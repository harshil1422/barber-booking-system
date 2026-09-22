ALTER TABLE refresh_tokens
    ALTER COLUMN ip_address TYPE inet
    USING NULLIF(TRIM(ip_address), '')::inet;
