CREATE TABLE oauth2.oauth2_registered_client (
    id NVARCHAR(100) NOT NULL PRIMARY KEY,
    client_id NVARCHAR(100) NOT NULL,
    client_id_issued_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    client_secret NVARCHAR(200) DEFAULT NULL,
    client_secret_expires_at DATETIME2 DEFAULT NULL,
    client_name NVARCHAR(200) NOT NULL,
    client_authentication_methods NVARCHAR(1000) NOT NULL,
    authorization_grant_types NVARCHAR(1000) NOT NULL,
    redirect_uris NVARCHAR(1000) DEFAULT NULL,
    post_logout_redirect_uris NVARCHAR(1000) DEFAULT NULL,
    client_type NVARCHAR(20) NOT NULL DEFAULT 'CONFIDENTIAL',
    scopes NVARCHAR(1000) NOT NULL,
    client_settings NVARCHAR(2000) NOT NULL,
    token_settings NVARCHAR(2000) NOT NULL
);

-- Índices para oauth2_registered_client
CREATE INDEX IX_oauth2_registered_client_client_id ON oauth2.oauth2_registered_client(client_id);
