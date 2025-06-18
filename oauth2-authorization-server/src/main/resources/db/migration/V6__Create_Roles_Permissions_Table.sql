CREATE TABLE oauth2.roles_permissions (
    permission_id INT NOT NULL,
    role_id INT NOT NULL,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 DEFAULT SYSDATETIME(),
    active BIT DEFAULT 1,
    CONSTRAINT PK_roles_permissions PRIMARY KEY (permission_id, role_id),
    CONSTRAINT FK_roles_permissions_permissions FOREIGN KEY (permission_id) REFERENCES oauth2.permissions(id),
    CONSTRAINT FK_roles_permissions_roles FOREIGN KEY (role_id) REFERENCES oauth2.roles(id)
);

-- Índices para roles_permissions
CREATE INDEX IX_roles_permissions_role_id ON oauth2.roles_permissions(role_id);
CREATE INDEX IX_roles_permissions_active ON oauth2.roles_permissions(active);
