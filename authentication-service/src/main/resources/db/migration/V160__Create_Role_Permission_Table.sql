IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'role_permission' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.role_permission (
        permission_id INT NOT NULL,
        role_id INT NOT NULL,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL,
        active BIT NOT NULL DEFAULT 1,
        CONSTRAINT Pk_role_permission PRIMARY KEY (permission_id, role_id)
    );

    CREATE UNIQUE INDEX UQ_role_permission_public_id ON app.role_permission (public_id);
    CREATE INDEX IX_role_permission_permission_id ON app.role_permission (permission_id);
    CREATE INDEX IX_role_permission_role_id ON app.role_permission (role_id);

    ALTER TABLE app.role_permission
    ADD CONSTRAINT FK_role_permission_permission
    FOREIGN KEY (permission_id) REFERENCES app.permission (id);

    ALTER TABLE app.role_permission
    ADD CONSTRAINT FK_role_permission_role
    FOREIGN KEY (role_id) REFERENCES app.[role] (id);
END
GO