CREATE TABLE oauth2.submodules (
    id INT IDENTITY(1,1) PRIMARY KEY,
    public_id UNIQUEIDENTIFIER NOT NULL UNIQUE DEFAULT NEWID(),
    module_id INT NOT NULL,
    code NVARCHAR(50) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 DEFAULT SYSDATETIME(),
    CONSTRAINT FK_submodules_modules FOREIGN KEY (module_id) REFERENCES oauth2.modules(id)
);

-- Índices para submodules
CREATE INDEX IX_submodules_module_id ON oauth2.submodules(module_id);
CREATE INDEX IX_submodules_code ON oauth2.submodules(code);
CREATE INDEX IX_submodules_active ON oauth2.submodules(active);
