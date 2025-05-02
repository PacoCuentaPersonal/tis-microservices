IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'document_type' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.document_type (
        id INT IDENTITY(1,1) PRIMARY KEY,
        code VARCHAR(50) NOT NULL,
        name VARCHAR(100) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        CONSTRAINT UQ_document_type_code UNIQUE (code)
    );
END
GO