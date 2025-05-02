IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'oauth_auth' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.oauth_auth (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        provider_id INT NOT NULL,
        sub NVARCHAR(255) NOT NULL,
        email NVARCHAR(255) NOT NULL,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL,
        active BIT NOT NULL DEFAULT 1
    );
    CREATE UNIQUE INDEX UQ_oauth_auth_provider_sub ON app.oauth_auth (provider_id, sub);
    CREATE INDEX IX_oauth_auth_email ON app.oauth_auth (email);
    CREATE INDEX IX_oauth_auth_provider_id ON app.oauth_auth (provider_id);
    ALTER TABLE app.oauth_auth
    ADD CONSTRAINT FK_oauth_auth_provider
    FOREIGN KEY (provider_id) REFERENCES app.provider_oauth (id);
END
GO