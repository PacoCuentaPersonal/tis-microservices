IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'province' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.province (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        department_id INT NOT NULL,
        name NVARCHAR(100) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );
    CREATE UNIQUE INDEX UQ_province_public_id ON app.province (public_id);
    CREATE INDEX IX_province_name ON app.province (name);
    CREATE INDEX IX_province_department_id ON app.province (department_id);

    ALTER TABLE app.province
    ADD CONSTRAINT FK_province_department
    FOREIGN KEY (department_id) REFERENCES app.department (id);

END
GO