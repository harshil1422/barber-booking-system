-- ============================================================
-- New Look · User Service · V1 — Initial Schema
-- Schema: user_db
-- Runs once on service startup via Flyway
-- ============================================================

CREATE SCHEMA IF NOT EXISTS user_db;

SET search_path TO user_db;

-- ── ENUM types ──────────────────────────────────────────────
CREATE TYPE user_role AS ENUM ('USER', 'BARBER', 'ADMIN');

CREATE TYPE user_status AS ENUM ('ACTIVE', 'SUSPENDED', 'DELETED');

-- ── user_profiles ────────────────────────────────────────────
-- Core user record. user_id references auth_db.users (cross-service,
-- no FK constraint — enforced at application layer).
CREATE TABLE user_profiles (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID            NOT NULL UNIQUE,
    name                VARCHAR(100)    NOT NULL,
    phone               VARCHAR(15)     NOT NULL UNIQUE,
    email               VARCHAR(255)    UNIQUE,
    avatar_url          VARCHAR(500),
    status              user_status     NOT NULL DEFAULT 'ACTIVE',
    preferred_language  VARCHAR(10)     NOT NULL DEFAULT 'en',
    total_bookings      INTEGER         NOT NULL DEFAULT 0 CHECK (total_bookings >= 0),
    last_booking_at     TIMESTAMPTZ,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- ── user_roles ───────────────────────────────────────────────
-- Mutable role assignment. A user can hold USER + BARBER simultaneously.
-- Architecture rule: roles stored as a list, JWT claim reflects this list.
CREATE TABLE user_roles (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    role        user_role   NOT NULL,
    granted_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    granted_by  UUID,                          -- admin user_id, null = self-assigned
    UNIQUE (user_id, role)                     -- no duplicate roles per user
);

-- ── shop_owner_profiles ──────────────────────────────────────
-- Created when a user registers a shop. Links user_id → shop_id.
-- shop_id references shop_db.shops (cross-service, no FK constraint).
CREATE TABLE shop_owner_profiles (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL UNIQUE REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    shop_id         UUID            UNIQUE,    -- null until shop is created in Shop service
    business_name   VARCHAR(200),
    gstin           VARCHAR(15)     UNIQUE,    -- GST Identification Number
    bank_account    VARCHAR(20),               -- encrypted at application layer
    ifsc_code       VARCHAR(11),
    kyc_verified    BOOLEAN         NOT NULL DEFAULT FALSE,
    kyc_verified_at TIMESTAMPTZ,
    kyc_verified_by UUID,                      -- admin user_id
    registered_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- ── user_preferences ─────────────────────────────────────────
-- Notification and UX preferences per user.
CREATE TABLE user_preferences (
    id                          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                     UUID        NOT NULL UNIQUE REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    sms_notifications           BOOLEAN     NOT NULL DEFAULT TRUE,
    push_notifications          BOOLEAN     NOT NULL DEFAULT TRUE,
    booking_reminders           BOOLEAN     NOT NULL DEFAULT TRUE,
    marketing_notifications     BOOLEAN     NOT NULL DEFAULT FALSE,
    default_radius_km           INTEGER     NOT NULL DEFAULT 5 CHECK (default_radius_km BETWEEN 1 AND 50),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── INDEXES ──────────────────────────────────────────────────

-- user_profiles — lookup by phone (login + search)
CREATE INDEX idx_user_profiles_phone    ON user_profiles(phone);
CREATE INDEX idx_user_profiles_email    ON user_profiles(email) WHERE email IS NOT NULL;
CREATE INDEX idx_user_profiles_status   ON user_profiles(status);
CREATE INDEX idx_user_profiles_user_id  ON user_profiles(user_id);

-- user_roles — lookup all roles for a user (JWT claim construction)
CREATE INDEX idx_user_roles_user_id     ON user_roles(user_id);
CREATE INDEX idx_user_roles_role        ON user_roles(role);

-- shop_owner_profiles — lookup by shop_id (called by Shop service)
CREATE INDEX idx_shop_owner_shop_id     ON shop_owner_profiles(shop_id) WHERE shop_id IS NOT NULL;
CREATE INDEX idx_shop_owner_user_id     ON shop_owner_profiles(user_id);

-- ── TRIGGERS ─────────────────────────────────────────────────

-- Auto-update updated_at on every UPDATE
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_user_profiles_updated_at
    BEFORE UPDATE ON user_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_shop_owner_updated_at
    BEFORE UPDATE ON shop_owner_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_user_preferences_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();