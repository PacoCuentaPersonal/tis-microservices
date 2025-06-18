CREATE TABLE oauth2.oauth2_authorization_consent (
    registered_client_id NVARCHAR(100) NOT NULL,
    principal_name NVARCHAR(200) NOT NULL,
    authorities NVARCHAR(1000) NOT NULL,
    CONSTRAINT PK_oauth2_authorization_consent PRIMARY KEY (registered_client_id, principal_name)
);

-- Índice adicional para búsquedas por principal_name
CREATE INDEX IX_oauth2_authorization_consent_principal_name ON oauth2.oauth2_authorization_consent(principal_name);
