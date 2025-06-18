CREATE TABLE oauth2.permissions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    public_id UNIQUEIDENTIFIER NOT NULL UNIQUE DEFAULT NEWID(),
    submodule_id INT,
    code NVARCHAR(100) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 DEFAULT SYSDATETIME(),
    CONSTRAINT FK_permissions_submodules FOREIGN KEY (submodule_id) REFERENCES oauth2.submodules(id)
);

-- Índices para permissions
CREATE INDEX IX_permissions_name ON oauth2.permissions(name);
CREATE INDEX IX_permissions_public_id ON oauth2.permissions(public_id);
CREATE INDEX IX_permissions_submodule_id ON oauth2.permissions(submodule_id);
CREATE INDEX IX_permissions_code ON oauth2.permissions(code);
CREATE INDEX IX_permissions_active ON oauth2.permissions(active);
