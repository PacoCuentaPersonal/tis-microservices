IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'action' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.action (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        name NVARCHAR(100) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );
    CREATE UNIQUE INDEX UQ_action_public_id ON app.action (public_id);
    CREATE INDEX IX_action_name ON app.action (name);
END
GO