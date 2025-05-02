IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'submodule' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.submodule (
        id INT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        module_id INT NOT NULL,
        name NVARCHAR(100) NOT NULL,
        icon NVARCHAR(50) NULL,
        route NVARCHAR(100) NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL
    );

    CREATE UNIQUE INDEX UQ_submodule_public_id ON app.submodule (public_id);
    CREATE INDEX IX_submodule_name ON app.submodule (name);
    CREATE INDEX IX_submodule_module_id ON app.submodule (module_id);
    CREATE INDEX IX_submodule_route ON app.submodule (route);
    CREATE INDEX IX_submodule_active ON app.submodule (active) WHERE active = 1;

    ALTER TABLE app.submodule
    ADD CONSTRAINT FK_submodule_module
    FOREIGN KEY (module_id) REFERENCES app.module (id);
END
GO