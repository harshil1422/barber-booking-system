CREATE SCHEMA IF NOT EXISTS notification_db;

SET search_path TO notification_db;


-- ─────────────────────────────────────────────
-- Notification status
-- ─────────────────────────────────────────────

CREATE TYPE notification_status AS ENUM (
    'PENDING',
    'SENT',
    'FAILED'
);


-- ─────────────────────────────────────────────
-- Notifications
-- ─────────────────────────────────────────────

CREATE TABLE notifications (
                               id UUID PRIMARY KEY,

                               event_id UUID NOT NULL,

                               user_id UUID NOT NULL,

                               notification_type VARCHAR(50) NOT NULL,

                               message VARCHAR(1000) NOT NULL,

                               status notification_status NOT NULL DEFAULT 'PENDING',

                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                               sent_at TIMESTAMPTZ,

                               CONSTRAINT uq_notification_event_type
                                   UNIQUE (event_id, notification_type)
);

CREATE INDEX idx_notifications_user
    ON notifications (user_id, created_at);

CREATE INDEX idx_notifications_status
    ON notifications (status);


-- ─────────────────────────────────────────────
-- Processed Events (Inbox Pattern)
-- ─────────────────────────────────────────────

CREATE TABLE processed_events (

                                  id UUID PRIMARY KEY,

                                  event_id UUID NOT NULL,

                                  event_type VARCHAR(100) NOT NULL,

                                  processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                  CONSTRAINT uq_processed_event_id
                                      UNIQUE (event_id)
);

CREATE INDEX idx_processed_events_processed_at
    ON processed_events (processed_at);