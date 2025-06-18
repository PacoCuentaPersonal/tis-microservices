CREATE TABLE oauth2.modules (
    id INT IDENTITY(1,1) PRIMARY KEY,
    public_id UNIQUEIDENTIFIER NOT NULL UNIQUE DEFAULT NEWID(),
    code NVARCHAR(50) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT SYSDATETIME(),
    updated_at DATETIME2 DEFAULT SYSDATETIME()
);

-- Índices para modules
CREATE INDEX IX_modules_code ON oauth2.modules(code);
CREATE INDEX IX_modules_active ON oauth2.modules(active);
