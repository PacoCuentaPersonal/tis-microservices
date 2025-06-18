CREATE TABLE oauth2.oauth2_authorization (
    id NVARCHAR(100) NOT NULL PRIMARY KEY,
    registered_client_id NVARCHAR(100) NOT NULL,
    principal_name NVARCHAR(200) NOT NULL,
    authorization_grant_type NVARCHAR(100) NOT NULL,
    authorized_scopes NVARCHAR(1000) DEFAULT NULL,
    attributes NVARCHAR(MAX) DEFAULT NULL,
    state NVARCHAR(500) DEFAULT NULL,
    authorization_code_value NVARCHAR(MAX) DEFAULT NULL,
    authorization_code_issued_at DATETIME2 DEFAULT NULL,
    authorization_code_expires_at DATETIME2 DEFAULT NULL,
    authorization_code_metadata NVARCHAR(MAX) DEFAULT NULL,
    access_token_value NVARCHAR(MAX) DEFAULT NULL,
    access_token_issued_at DATETIME2 DEFAULT NULL,
    access_token_expires_at DATETIME2 DEFAULT NULL,
    access_token_metadata NVARCHAR(MAX) DEFAULT NULL,
    access_token_type NVARCHAR(100) DEFAULT NULL,
    access_token_scopes NVARCHAR(1000) DEFAULT NULL,
    refresh_token_value NVARCHAR(MAX) DEFAULT NULL,
    refresh_token_issued_at DATETIME2 DEFAULT NULL,
    refresh_token_expires_at DATETIME2 DEFAULT NULL,
    refresh_token_metadata NVARCHAR(MAX) DEFAULT NULL,
    oidc_id_token_value NVARCHAR(MAX) DEFAULT NULL,
    oidc_id_token_issued_at DATETIME2 DEFAULT NULL,
    oidc_id_token_expires_at DATETIME2 DEFAULT NULL,
    oidc_id_token_metadata NVARCHAR(MAX) DEFAULT NULL,
    user_code_value NVARCHAR(MAX) DEFAULT NULL,
    user_code_issued_at DATETIME2 DEFAULT NULL,
    user_code_expires_at DATETIME2 DEFAULT NULL,
    user_code_metadata NVARCHAR(MAX) DEFAULT NULL,
    device_code_value NVARCHAR(MAX) DEFAULT NULL,
    device_code_issued_at DATETIME2 DEFAULT NULL,
    device_code_expires_at DATETIME2 DEFAULT NULL,
    device_code_metadata NVARCHAR(MAX) DEFAULT NULL
);

-- Índices para oauth2_authorization
CREATE INDEX IX_oauth2_authorization_registered_client_id ON oauth2.oauth2_authorization(registered_client_id);
CREATE INDEX IX_oauth2_authorization_principal_name ON oauth2.oauth2_authorization(principal_name);
CREATE INDEX IX_oauth2_authorization_access_token_expires_at ON oauth2.oauth2_authorization(access_token_expires_at);
CREATE INDEX IX_oauth2_authorization_refresh_token_expires_at ON oauth2.oauth2_authorization(refresh_token_expires_at);
