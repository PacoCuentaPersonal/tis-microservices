IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'provider_oauth' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.provider_oauth (
        id INT IDENTITY(1,1) PRIMARY KEY,
        code VARCHAR(50) NOT NULL,
        name VARCHAR(100) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        CONSTRAINT UQ_provider_oauth_code UNIQUE (code)
    );
END
GO