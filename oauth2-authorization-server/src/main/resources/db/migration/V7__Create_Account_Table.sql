CREATE TABLE oauth2.account (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    public_id UNIQUEIDENTIFIER NOT NULL UNIQUE DEFAULT NEWID(),
    email NVARCHAR(200) NOT NULL UNIQUE,
    password NVARCHAR(100) NOT NULL,
    username NVARCHAR(100) NOT NULL,
    email_verified BIT NOT NULL DEFAULT 0,
    profile_picture_url NVARCHAR(500) NULL,
    employee_public_id UNIQUEIDENTIFIER,
    role_id INT NOT NULL,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 DEFAULT SYSDATETIME(),
    last_login_at DATETIME2,
    active BIT DEFAULT 1,

    CONSTRAINT FK_account_roles FOREIGN KEY (role_id) REFERENCES oauth2.roles(id)
);
-- Índices para account
CREATE INDEX IX_account_email ON oauth2.account(email);
CREATE INDEX IX_account_created_at ON oauth2.account(created_at);
CREATE INDEX IX_account_updated_at ON oauth2.account(updated_at);
CREATE INDEX IX_account_roles_id ON oauth2.account(role_id);
CREATE INDEX IX_account_active ON oauth2.account(active);