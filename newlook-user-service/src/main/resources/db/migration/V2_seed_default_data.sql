-- ============================================================
-- New Look · User Service · V2 — Additional Indexes & Constraints
-- ============================================================

SET search_path TO user_db;

-- Partial index: active users only (most queries filter on ACTIVE)
CREATE INDEX idx_user_profiles_active
    ON user_profiles(created_at DESC)
    WHERE status = 'ACTIVE';

-- Composite index for admin queries: status + created_at range scans
CREATE INDEX idx_user_profiles_status_created
    ON user_profiles(status, created_at DESC);

-- Covering index for profile card queries (avoid heap fetch)
CREATE INDEX idx_user_profiles_covering
    ON user_profiles(user_id)
    INCLUDE (name, phone, avatar_url, status);

-- Total bookings tracking: partial index for active bookers
CREATE INDEX idx_user_profiles_bookings
    ON user_profiles(total_bookings DESC)
    WHERE total_bookings > 0;

-- ── Comments on columns for documentation ─────────────────────
COMMENT ON TABLE user_profiles      IS 'Core user record. user_id is the cross-service identifier shared with auth_db.';
COMMENT ON TABLE user_roles         IS 'Mutable role list. A single user can hold USER + BARBER simultaneously.';
COMMENT ON TABLE shop_owner_profiles IS 'Created on shop registration. shop_id is null until Shop service confirms creation.';
COMMENT ON TABLE user_preferences   IS 'Notification and UX preferences. Default values are set on user creation.';

COMMENT ON COLUMN user_profiles.user_id         IS 'FK to auth_db.users.id — cross-service, no DB constraint, enforced in application layer.';
COMMENT ON COLUMN user_profiles.total_bookings  IS 'Denormalized counter. Incremented via internal event from Booking service.';
COMMENT ON COLUMN shop_owner_profiles.bank_account IS 'Encrypted at rest using AES-256 before INSERT. Never returned in API responses.';
COMMENT ON COLUMN shop_owner_profiles.shop_id   IS 'FK to shop_db.shops.id — cross-service, set by Shop service callback after shop creation.';