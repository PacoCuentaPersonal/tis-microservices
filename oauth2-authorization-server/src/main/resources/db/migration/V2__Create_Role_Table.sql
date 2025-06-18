CREATE TABLE oauth2.roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    public_id UNIQUEIDENTIFIER NOT NULL UNIQUE DEFAULT NEWID(),
    name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(200) NULL,
    active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 DEFAULT SYSDATETIME()
);

-- Índices para roles
CREATE INDEX IX_roles_name ON oauth2.roles(name);
CREATE INDEX IX_roles_active ON oauth2.roles(active);
