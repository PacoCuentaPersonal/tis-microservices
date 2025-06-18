CREATE TABLE IF NOT EXISTS app.person (
    document_type_id INTEGER NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    district_id INTEGER NULL,
    names VARCHAR(100) NOT NULL,
    maternal_surname VARCHAR(100) NOT NULL,
    paternal_surname VARCHAR(100) NOT NULL,
    birthdate DATE NULL,
    gender CHAR(1) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NULL,
    PRIMARY KEY (document_type_id, document_number),
    CONSTRAINT FK_person_district FOREIGN KEY (district_id) REFERENCES app.district (id),
    CONSTRAINT FK_person_document_type FOREIGN KEY (document_type_id) REFERENCES app.document_type (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS UQ_person_public_id ON app.person (public_id);
CREATE INDEX IF NOT EXISTS IX_person_names ON app.person (names);
CREATE INDEX IF NOT EXISTS IX_person_paternal_surname ON app.person (paternal_surname);
CREATE INDEX IF NOT EXISTS IX_person_maternal_surname ON app.person (maternal_surname);
CREATE INDEX IF NOT EXISTS IX_person_district_id ON app.person (district_id);