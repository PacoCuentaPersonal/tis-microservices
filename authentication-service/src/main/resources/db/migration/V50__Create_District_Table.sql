IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'district' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.district (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        province_id INT NOT NULL,
        ubigeo NVARCHAR(10) NULL,
        name NVARCHAR(100) NOT NULL,
        postal_code NVARCHAR(20) NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );
    CREATE UNIQUE INDEX UQ_district_public_id ON app.district (public_id);
    CREATE INDEX IX_district_name ON app.district (name);
    CREATE INDEX IX_district_province_id ON app.district (province_id);
    CREATE INDEX IX_district_ubigeo ON app.district (ubigeo);
    CREATE UNIQUE INDEX IX_district_postal_code ON app.district (postal_code);

    ALTER TABLE app.district
    ADD CONSTRAINT FK_district_province
    FOREIGN KEY (province_id) REFERENCES app.province (id);
END
GO