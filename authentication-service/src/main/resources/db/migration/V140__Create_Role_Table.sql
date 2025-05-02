IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'role' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.[role] (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        name NVARCHAR(100) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        is_system_role BIT NOT NULL DEFAULT 0,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );
    CREATE UNIQUE INDEX UQ_role_public_id ON app.[role] (public_id);
    CREATE UNIQUE INDEX UQ_role_name ON app.[role] (name);
END
GO