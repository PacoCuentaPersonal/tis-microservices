CREATE TABLE IF NOT EXISTS app.document_type (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    clientName VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS UQ_document_type_public_id ON app.document_type (public_id);
CREATE INDEX IF NOT EXISTS IX_document_type_name ON app.document_type (clientName);