IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'person' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.person (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        district_id INT NULL,
        name NVARCHAR(100) NOT NULL,
        maternal_surname NVARCHAR(100) NULL,
        paternal_surname NVARCHAR(100) NULL,
        birthdate DATE NULL,
        gender CHAR(1) NOT NULL,
        document_type_id INT NULL,
        document_number NVARCHAR(20) NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );
    CREATE UNIQUE INDEX UQ_person_public_id ON app.person (public_id);
    CREATE INDEX IX_person_name ON app.person (name);
    CREATE INDEX IX_person_paternal_surname ON app.person (paternal_surname);
    CREATE INDEX IX_person_maternal_surname ON app.person (maternal_surname);
    CREATE INDEX IX_person_document_number ON app.person (document_number);
    CREATE INDEX IX_person_district_id ON app.person (district_id);
    CREATE INDEX IX_person_document_type_id ON app.person (document_type_id);

    CREATE UNIQUE INDEX UQ_person_document ON app.person (document_type_id, document_number)
    WHERE document_type_id IS NOT NULL AND document_number IS NOT NULL;

    ALTER TABLE app.person
    ADD CONSTRAINT FK_person_district
    FOREIGN KEY (district_id) REFERENCES app.district (id);

    ALTER TABLE app.person
    ADD CONSTRAINT FK_person_document_type
    FOREIGN KEY (document_type_id) REFERENCES app.document_type (id);
END
GO