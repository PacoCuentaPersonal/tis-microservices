IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_role' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.user_role (
        user_id BIGINT NOT NULL,
        role_id INT NOT NULL,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL,
        active BIT NOT NULL DEFAULT 1,
        CONSTRAINT PK_user_role PRIMARY KEY (user_id, role_id)
    );

    CREATE INDEX IX_user_role_user_id ON app.user_role (user_id);
    CREATE INDEX IX_user_role_role_id ON app.user_role (role_id);

    ALTER TABLE app.user_role
    ADD CONSTRAINT FK_user_role_user
    FOREIGN KEY (user_id) REFERENCES app.[user] (id);

    ALTER TABLE app.user_role
    ADD CONSTRAINT FK_user_role_role
    FOREIGN KEY (role_id) REFERENCES app.[role] (id);
END
GO