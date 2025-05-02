IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'company' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.company (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        district_id INT NULL,
        ruc NVARCHAR(20) NOT NULL,
        company_name NVARCHAR(100) NOT NULL,
        trade_name NVARCHAR(100) NOT NULL ,
        company_type NVARCHAR(50) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );
    CREATE UNIQUE INDEX UQ_company_public_id ON app.company (public_id);
    CREATE INDEX IX_company_name ON app.company (company_name);
    CREATE INDEX IX_trade_name ON app.company (trade_name);
    CREATE INDEX IX_company_district_id ON app.company (district_id);
    CREATE INDEX IX_company_ruc ON app.company (ruc);

    ALTER TABLE app.company
    ADD CONSTRAINT FK_company_district
    FOREIGN KEY (district_id) REFERENCES app.district (id);

END
GO