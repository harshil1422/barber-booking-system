-- V2__seed_roles.sql

INSERT INTO roles (name) VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN'),
    ('ROLE_MODERATOR')
ON CONFLICT (name) DO NOTHING;
