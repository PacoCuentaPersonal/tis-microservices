IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'module' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.module (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        name NVARCHAR(50) NOT NULL,
        icon NVARCHAR(50) NULL,
        route NVARCHAR(100) NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );

    CREATE UNIQUE INDEX UQ_module_public_id ON app.module (public_id);
    CREATE INDEX IX_module_name ON app.module (name);
    CREATE INDEX IX_module_route ON app.module (route);
    CREATE INDEX IX_module_active ON app.module (active) WHERE active = 1;
END
GO