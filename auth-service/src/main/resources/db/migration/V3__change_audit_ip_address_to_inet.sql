ALTER TABLE audit_log
ALTER COLUMN ip_address TYPE inet
    USING NULLIF(TRIM(ip_address), '')::inet;