CREATE TABLE IF NOT EXISTS app.department (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    code_inei VARCHAR(2) NOT NULL,  -- Código INEI de 2 dígitos
    clientName VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) NULL,
    CONSTRAINT UQ_department_code_inei UNIQUE (code_inei)
);
CREATE INDEX IX_department_name ON app.department (clientName);