IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'email_auth' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.email_auth (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        email NVARCHAR(255) NOT NULL,
        password NVARCHAR(255) NOT NULL,
        email_verified BIT NOT NULL DEFAULT 0,
        recovery_email NVARCHAR(255) NULL,
        attempts INT NOT NULL DEFAULT 0,
        last_failed_attempt DATETIME2(7) NULL,
        lockout_end DATETIME2(7) NULL,
        account_locked BIT NOT NULL DEFAULT 0,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL,
        active BIT NOT NULL DEFAULT 1
    );
    CREATE UNIQUE INDEX UQ_email_auth_email ON app.email_auth (email);
    CREATE INDEX IX_email_auth_recovery_email ON app.email_auth (recovery_email);
END
GO