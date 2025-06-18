CREATE TABLE IF NOT EXISTS app.company (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    district_id INTEGER NULL,
    ruc VARCHAR(20) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    trade_name VARCHAR(100) NOT NULL,
    company_type VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NULL,
    CONSTRAINT FK_company_district FOREIGN KEY (district_id) REFERENCES app.district (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS UQ_company_public_id ON app.company (public_id);
CREATE INDEX IF NOT EXISTS IX_company_name ON app.company (company_name);
CREATE INDEX IF NOT EXISTS IX_trade_name ON app.company (trade_name);
CREATE INDEX IF NOT EXISTS IX_company_district_id ON app.company (district_id);
CREATE INDEX IF NOT EXISTS IX_company_ruc ON app.company (ruc);