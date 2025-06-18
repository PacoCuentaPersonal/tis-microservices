CREATE TABLE IF NOT EXISTS app.district (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    province_id INTEGER NOT NULL,
    ubigeo_inei VARCHAR(6) NOT NULL,  -- Código INEI completo de 6 dígitos
    ubigeo_reniec VARCHAR(6) NOT NULL,  -- Código RENIEC completo de 6 dígitos
    clientName VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) NULL,
    CONSTRAINT UQ_district_ubigeo_inei UNIQUE (ubigeo_inei),
    CONSTRAINT UQ_district_ubigeo_reniec UNIQUE (ubigeo_reniec),
    CONSTRAINT FK_district_province FOREIGN KEY (province_id) REFERENCES app.province (id)
);
CREATE INDEX IF NOT EXISTS IX_district_name ON app.district (clientName);
CREATE INDEX IF NOT EXISTS IX_district_province ON app.district (province_id);
CREATE INDEX IF NOT EXISTS IX_district_ubigeo_inei ON app.district (ubigeo_inei);
CREATE INDEX IF NOT EXISTS IX_district_ubigeo_reniec ON app.district (ubigeo_reniec);


