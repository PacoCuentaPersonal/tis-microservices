IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'department' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.department (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        name NVARCHAR(100) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );
    CREATE UNIQUE INDEX UQ_department_public_id ON app.department (public_id);
    CREATE INDEX IX_department_name ON app.department (name);
END
GO