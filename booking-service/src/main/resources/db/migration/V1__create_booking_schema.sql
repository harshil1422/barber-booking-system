-- ═══════════════════════════════════════════════════════════════
-- V1__create_booking_schema.sql
-- New Look – Booking Service
-- ═══════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS booking_db;
SET search_path TO booking_db;

-- ── Extensions ────────────────────────────────────────────────
-- Required for gen_random_uuid()

CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- ── Enums ──────────────────────────────────────────────────────

CREATE TYPE slot_status AS ENUM (
    'AVAILABLE',
    'BOOKED',
    'BLOCKED',
    'WALK_IN',
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

-- Appointment entity enum
CREATE TYPE appointment_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED'
);


-- ── chair_slots ────────────────────────────────────────────────
-- ChairSlot is owned by Booking service.

CREATE TABLE chair_slots (
                             id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                             chair_id     UUID        NOT NULL,
                             shop_id      UUID        NOT NULL,
                             service_id   UUID,
                             start_time   TIMESTAMPTZ NOT NULL,
                             end_time     TIMESTAMPTZ NOT NULL,
                             status       slot_status NOT NULL DEFAULT 'AVAILABLE',
                             version      INTEGER     NOT NULL DEFAULT 0,
                             created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                             updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                             CONSTRAINT chk_slot_times
                                 CHECK (end_time > start_time),

                             CONSTRAINT uq_chair_slot_time
                                 UNIQUE (chair_id, start_time)
);

CREATE INDEX idx_slots_shop_time
    ON chair_slots (shop_id, start_time, end_time)
    WHERE status = 'AVAILABLE';

CREATE INDEX idx_slots_chair_time
    ON chair_slots (chair_id, start_time);

CREATE INDEX idx_slots_walkin
    ON chair_slots (chair_id, status)
    WHERE status IN ('WALK_IN', 'BLOCKED');


-- ── appointments ───────────────────────────────────────────────
-- Maps to:
-- com.newlook.booking.booking.domain.Booking

CREATE TABLE appointments (
                              appointment_id UUID PRIMARY KEY,

                              user_id        UUID NOT NULL,
                              barber_id      UUID NOT NULL,
                              shop_id        UUID NOT NULL,
                              service_id     UUID NOT NULL,

                              start_time     TIMESTAMPTZ NOT NULL,
                              end_time       TIMESTAMPTZ NOT NULL,

                              status         appointment_status NOT NULL DEFAULT 'PENDING',

                              CONSTRAINT chk_appointment_times
                                  CHECK (end_time > start_time)
);

CREATE INDEX idx_appointments_user
    ON appointments (user_id, start_time);

CREATE INDEX idx_appointments_barber_time
    ON appointments (barber_id, start_time, end_time);

CREATE INDEX idx_appointments_shop_time
    ON appointments (shop_id, start_time);

CREATE INDEX idx_appointments_status
    ON appointments (status, start_time);


-- ── walk_in_queue ──────────────────────────────────────────────

CREATE TABLE walk_in_queue (
                               id                UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                               chair_id          UUID          NOT NULL,
                               shop_id           UUID          NOT NULL,
                               customer_name     VARCHAR(255)  NOT NULL,
                               phone             VARCHAR(15),
                               service_requested VARCHAR(255),
                               position          INTEGER       NOT NULL,
                               est_wait_min      INTEGER,
                               status            walkin_status NOT NULL DEFAULT 'WAITING',
                               joined_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
                               served_at         TIMESTAMPTZ,
                               updated_at        TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_walkin_chair_active
    ON walk_in_queue (chair_id, position ASC)
    WHERE status = 'WAITING';


-- ── outbox_events ──────────────────────────────────────────────
-- Maps to:
-- com.newlook.booking.event.outbox.OutboxEvent
--
-- Domain events are persisted in the same transaction as the
-- aggregate state change.
--
-- If Debezium is used, it can capture this table from PostgreSQL WAL.

CREATE TABLE outbox_events (
                               id                    BIGINT GENERATED BY DEFAULT AS IDENTITY
                                   PRIMARY KEY,

                               event_id              UUID         NOT NULL UNIQUE,

                               event_type            VARCHAR(100) NOT NULL,

                               event_version         VARCHAR(20)  NOT NULL,

                               occurred_at           TIMESTAMPTZ  NOT NULL,

                               producer              VARCHAR(100) NOT NULL,

                               correlation_id        VARCHAR(100),

                               payload               JSONB        NOT NULL,

                               status                VARCHAR(20)  NOT NULL,

                               created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

                               processing_started_at TIMESTAMPTZ,

                               published_at          TIMESTAMPTZ,

                               retry_count            INTEGER      NOT NULL DEFAULT 0,

                               next_attempt_at        TIMESTAMPTZ,

                               last_error             VARCHAR(2000),

                               CONSTRAINT chk_outbox_retry_count
                                   CHECK (retry_count >= 0)
);

CREATE INDEX idx_outbox_status_next_attempt
    ON outbox_events (status, next_attempt_at);

CREATE INDEX idx_outbox_created_at
    ON outbox_events (created_at);


-- ── idempotency_keys ──────────────────────────────────────────

CREATE TABLE idempotency_keys (
                                  id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                                  user_id         UUID         NOT NULL,
                                  idempotency_key VARCHAR(255) NOT NULL,
                                  booking_id      UUID         REFERENCES appointments(appointment_id),
                                  http_status     INTEGER      NOT NULL,
                                  response_body   JSONB        NOT NULL,
                                  expires_at      TIMESTAMPTZ  NOT NULL,
                                  created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

                                  CONSTRAINT uq_idempotency_user_key
                                      UNIQUE (user_id, idempotency_key)
);

CREATE INDEX idx_idempotency_expires
    ON idempotency_keys (expires_at);


-- ── Triggers ──────────────────────────────────────────────────

CREATE OR REPLACE FUNCTION booking_db.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_slots_updated_at
    BEFORE UPDATE ON chair_slots
    FOR EACH ROW
    EXECUTE FUNCTION booking_db.set_updated_at();


CREATE TRIGGER trg_walkin_updated_at
    BEFORE UPDATE ON walk_in_queue
    FOR EACH ROW
    EXECUTE FUNCTION booking_db.set_updated_at();


-- ============================================================
-- Remove old idempotency foreign key dependency
-- ============================================================

ALTER TABLE idempotency_keys
DROP CONSTRAINT IF EXISTS idempotency_keys_booking_id_fkey;


-- ============================================================
-- Drop old appointment table
-- ============================================================

DROP TABLE IF EXISTS appointments;


-- ============================================================
-- Create bookings table
-- ============================================================

CREATE TABLE bookings (

                          booking_id UUID PRIMARY KEY,

                          user_id UUID NOT NULL,

                          barber_id UUID NOT NULL,

                          shop_id UUID NOT NULL,

                          start_time TIMESTAMPTZ NOT NULL,

                          end_time TIMESTAMPTZ NOT NULL,

                          total_duration_minutes INTEGER NOT NULL,

                          total_amount NUMERIC(19, 2) NOT NULL,

                          status booking_status NOT NULL DEFAULT 'PENDING',

                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                          CONSTRAINT chk_booking_times
                              CHECK (end_time > start_time),

                          CONSTRAINT chk_booking_duration
                              CHECK (total_duration_minutes > 0),

                          CONSTRAINT chk_booking_amount
                              CHECK (total_amount >= 0)
);


-- ============================================================
-- Create booking_items table
-- ============================================================

CREATE TABLE booking_items (

                               booking_item_id UUID PRIMARY KEY,

                               booking_id UUID NOT NULL,

                               service_catalog_id UUID NOT NULL,

                               service_name VARCHAR(100) NOT NULL,

                               duration_minutes INTEGER NOT NULL,

                               price NUMERIC(19, 2) NOT NULL,

                               CONSTRAINT fk_booking_items_booking
                                   FOREIGN KEY (booking_id)
                                       REFERENCES bookings(booking_id)
                                       ON DELETE CASCADE,

                               CONSTRAINT chk_booking_item_duration
                                   CHECK (duration_minutes > 0),

                               CONSTRAINT chk_booking_item_price
                                   CHECK (price >= 0)
);


-- ============================================================
-- Booking indexes
-- ============================================================

CREATE INDEX idx_bookings_user_time
    ON bookings (user_id, start_time);

CREATE INDEX idx_bookings_barber_time
    ON bookings (barber_id, start_time, end_time);

CREATE INDEX idx_bookings_shop_time
    ON bookings (shop_id, start_time, end_time);

CREATE INDEX idx_bookings_status
    ON bookings (status, start_time);


-- ============================================================
-- Booking item indexes
-- ============================================================

CREATE INDEX idx_booking_items_booking
    ON booking_items (booking_id);

CREATE INDEX idx_booking_items_service
    ON booking_items (service_catalog_id);


-- ============================================================
-- Update idempotency_keys relationship
-- ============================================================

ALTER TABLE idempotency_keys
    ADD CONSTRAINT fk_idempotency_booking
        FOREIGN KEY (booking_id)
            REFERENCES bookings(booking_id);


-- ============================================================
-- Updated timestamp trigger
-- ============================================================

CREATE TRIGGER trg_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW
    EXECUTE FUNCTION booking_db.set_updated_at();
