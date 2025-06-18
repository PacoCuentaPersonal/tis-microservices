CREATE TABLE IF NOT EXISTS app.province (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    department_id INTEGER NOT NULL,
    code_inei VARCHAR(4) NOT NULL,  -- Código INEI de 4 dígitos
    clientName VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) NULL,
    CONSTRAINT UQ_province_code_inei UNIQUE (code_inei),
    CONSTRAINT FK_province_department FOREIGN KEY (department_id) REFERENCES app.department (id)
);
CREATE INDEX IX_province_name ON app.province (clientName);
CREATE INDEX IX_province_department ON app.province (department_id);