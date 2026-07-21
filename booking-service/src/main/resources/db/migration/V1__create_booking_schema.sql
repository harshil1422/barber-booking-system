-- ═══════════════════════════════════════════════════════════════
-- V1__create_booking_schema.sql
-- New Look – Booking Service
-- ═══════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS booking_db;
SET search_path TO booking_db;

-- ── Enums ──────────────────────────────────────────────────────

CREATE TYPE slot_status AS ENUM (
    'AVAILABLE',
    'BOOKED',
    'BLOCKED',      -- blocked by barber manually or walk-in mode
    'WALK_IN',      -- occupied by a walk-in customer
    'BREAK'
);

CREATE TYPE booking_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE walkin_status AS ENUM (
    'WAITING',
    'IN_SERVICE',
    'SERVED',
    'LEFT'
);

-- ── chair_slots ────────────────────────────────────────────────
-- ChairSlot is OWNED by Booking service (not Chair service).
-- Chair service owns only chair metadata (name, barber, active flag).
-- This separation means: booking conflict detection lives here,
-- using @Version for JPA optimistic locking (409 on concurrent book).

CREATE TABLE chair_slots (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    chair_id     UUID        NOT NULL,          -- FK to chair_db.chairs (cross-service: no FK constraint)
    shop_id      UUID        NOT NULL,          -- denormalised for fast shop-level queries
    service_id   UUID,                          -- service booked for this slot (nullable = open slot)
    start_time   TIMESTAMPTZ NOT NULL,
    end_time     TIMESTAMPTZ NOT NULL,
    status       slot_status NOT NULL DEFAULT 'AVAILABLE',
    version      INTEGER     NOT NULL DEFAULT 0, -- JPA @Version optimistic lock
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_slot_times CHECK (end_time > start_time),
    CONSTRAINT uq_chair_slot_time UNIQUE (chair_id, start_time)  -- prevent overlapping slots per chair
);

-- Fast slot availability lookup: shop × time range
CREATE INDEX idx_slots_shop_time
    ON chair_slots (shop_id, start_time, end_time)
    WHERE status = 'AVAILABLE';

-- Per-chair slot grid lookup
CREATE INDEX idx_slots_chair_time
    ON chair_slots (chair_id, start_time);

-- Walk-in blocked slots
CREATE INDEX idx_slots_walkin
    ON chair_slots (chair_id, status)
    WHERE status IN ('WALK_IN', 'BLOCKED');

-- ── bookings ───────────────────────────────────────────────────

CREATE TABLE bookings (
    id                 UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID           NOT NULL,
    shop_id            UUID           NOT NULL,
    chair_id           UUID           NOT NULL,
    chair_slot_id      UUID           NOT NULL REFERENCES chair_slots(id),
    service_id         UUID           NOT NULL,
    status             booking_status NOT NULL DEFAULT 'PENDING',

    -- Denormalised fields for display without cross-service calls
    shop_name          VARCHAR(255)   NOT NULL,
    barber_name        VARCHAR(255),
    service_name       VARCHAR(255)   NOT NULL,
    duration_min       INTEGER        NOT NULL,
    user_phone         VARCHAR(15)    NOT NULL,
    user_name          VARCHAR(255)   NOT NULL,

    -- Pricing (snapshot at booking time — price may change later)
    amount_paise       BIGINT         NOT NULL,   -- amount in paise (₹ × 100)
    currency           VARCHAR(3)     NOT NULL DEFAULT 'INR',

    -- Payment
    payment_id         UUID,                      -- set by Payment service via Kafka

    -- Timing
    start_time         TIMESTAMPTZ    NOT NULL,
    end_time           TIMESTAMPTZ    NOT NULL,

    -- Cancellation policy (copied from shop at booking time)
    cancellable_until  TIMESTAMPTZ,               -- NULL = non-cancellable

    -- State transition timestamps
    confirmed_at       TIMESTAMPTZ,
    in_progress_at     TIMESTAMPTZ,
    completed_at       TIMESTAMPTZ,
    cancelled_at       TIMESTAMPTZ,
    cancel_reason      TEXT,

    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bookings_user        ON bookings (user_id, created_at DESC);
CREATE INDEX idx_bookings_shop_date   ON bookings (shop_id, start_time);
CREATE INDEX idx_bookings_slot        ON bookings (chair_slot_id);
CREATE INDEX idx_bookings_status      ON bookings (status, start_time)
    WHERE status IN ('PENDING','CONFIRMED','IN_PROGRESS');

-- ── walk_in_queue ──────────────────────────────────────────────
-- Physical walk-in customers waiting at the shop.
-- When chair is in WALK_IN mode, app bookings for that chair are blocked.

CREATE TABLE walk_in_queue (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    chair_id          UUID         NOT NULL,
    shop_id           UUID         NOT NULL,
    customer_name     VARCHAR(255) NOT NULL,
    phone             VARCHAR(15),
    service_requested VARCHAR(255),
    position          INTEGER      NOT NULL,     -- queue position (1-based)
    est_wait_min      INTEGER,                   -- estimated wait in minutes
    status            walkin_status NOT NULL DEFAULT 'WAITING',
    joined_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    served_at         TIMESTAMPTZ,
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_walkin_chair_active
    ON walk_in_queue (chair_id, position ASC)
    WHERE status = 'WAITING';

-- ── booking_outbox ────────────────────────────────────────────
-- Outbox pattern: domain events written here in the SAME transaction
-- as the booking state change. Debezium reads WAL and publishes to Kafka.

CREATE TABLE booking_outbox (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id   UUID        NOT NULL,   -- bookingId or chairId
    aggregate_type VARCHAR(64) NOT NULL,   -- 'BOOKING' | 'SLOT'
    event_type     VARCHAR(64) NOT NULL,   -- 'CONFIRMED' | 'CANCELLED' etc.
    payload        JSONB       NOT NULL,
    published      BOOLEAN     NOT NULL DEFAULT FALSE,
    retry_count    INTEGER     NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    published_at   TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished
    ON booking_outbox (created_at ASC)
    WHERE published = FALSE;

-- Required for Debezium CDC
ALTER TABLE booking_outbox REPLICA IDENTITY FULL;

-- ── idempotency_keys ──────────────────────────────────────────
-- Stores client-provided Idempotency-Key header with the response body.
-- Duplicate POST /bookings with same key returns original response
-- without creating a second booking. TTL managed by cleanup scheduler.

CREATE TABLE idempotency_keys (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    booking_id      UUID        REFERENCES bookings(id),
    http_status     INTEGER     NOT NULL,
    response_body   JSONB       NOT NULL,
    expires_at      TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_idempotency_user_key UNIQUE (user_id, idempotency_key)
);

CREATE INDEX idx_idempotency_expires
    ON idempotency_keys (expires_at)
    WHERE expires_at < NOW();

-- ── Triggers ──────────────────────────────────────────────────

CREATE OR REPLACE FUNCTION booking_db.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION booking_db.set_updated_at();

CREATE TRIGGER trg_slots_updated_at
    BEFORE UPDATE ON chair_slots
    FOR EACH ROW EXECUTE FUNCTION booking_db.set_updated_at();

CREATE TRIGGER trg_walkin_updated_at
    BEFORE UPDATE ON walk_in_queue
    FOR EACH ROW EXECUTE FUNCTION booking_db.set_updated_at();