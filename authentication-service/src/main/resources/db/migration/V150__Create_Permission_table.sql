IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'permission' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.permission (
        id INT IDENTITY(1,1) NOT NULL,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        submodule_id INT NOT NULL,
        action_id INT NOT NULL,
        name NVARCHAR(100) NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL,
        CONSTRAINT Pk_permission PRIMARY KEY (id),
        CONSTRAINT Uq_permission_submodule_action UNIQUE (submodule_id, action_id)
    );

    CREATE UNIQUE INDEX UQ_permission_public_id ON app.permission (public_id);
    CREATE INDEX IX_permission_name ON app.permission (name);
    CREATE INDEX IX_permission_submodule_id ON app.permission (submodule_id);
    CREATE INDEX IX_permission_action_id ON app.permission (action_id);

    ALTER TABLE app.permission
    ADD CONSTRAINT FK_permission_submodule
    FOREIGN KEY (submodule_id) REFERENCES app.submodule (id);

    ALTER TABLE app.permission
    ADD CONSTRAINT FK_permission_action
    FOREIGN KEY (action_id) REFERENCES app.action (id);

END
GO