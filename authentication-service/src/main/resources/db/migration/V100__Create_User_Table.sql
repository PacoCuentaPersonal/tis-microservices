IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.[user] (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        public_id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        username NVARCHAR(100) NULL,
        profile_picture NVARCHAR(MAX) NULL,
        profile_completed BIT NOT NULL DEFAULT 0,
        last_login_at DATETIME2(7) NULL,
        created_at DATETIME2(7) NOT NULL DEFAULT SYSDATETIME(),
        updated_at DATETIME2(7) NULL,
        created_by NVARCHAR(100) NOT NULL,
        updated_by NVARCHAR(100) NULL,
        active BIT NOT NULL DEFAULT 1,
        account_non_expired BIT NOT NULL DEFAULT 1,
        account_non_locked BIT NOT NULL DEFAULT 1,
        credentials_non_expired BIT NOT NULL DEFAULT 1,
        person_id INT NULL,
        company_id INT NULL ,
        oauth_auth_id BIGINT NULL,
        email_auth_id BIGINT NULL
    );
    CREATE UNIQUE INDEX UQ_user_public_id ON app.[user] (public_id);
    CREATE UNIQUE INDEX UQ_user_username ON app.[user] (username);
    CREATE INDEX IX_user_person_id ON app.[user] (person_id);
    CREATE INDEX IX_user_email_auth_id ON app.[user] (email_auth_id);
    CREATE INDEX IX_user_oauth_auth_id ON app.[user] (oauth_auth_id);

    ALTER TABLE app.[user]
    ADD CONSTRAINT FK_user_person
    FOREIGN KEY (person_id) REFERENCES app.person (id);

    ALTER TABLE app.[user]
    ADD CONSTRAINT Fk_user_company
    FOREIGN KEY (company_id) REFERENCES app.company (id);

    ALTER TABLE app.[user]
    ADD CONSTRAINT FK_user_email_auth
    FOREIGN KEY (email_auth_id) REFERENCES app.email_auth (id);

    ALTER TABLE app.[user]
    ADD CONSTRAINT FK_user_oauth_auth
    FOREIGN KEY (oauth_auth_id) REFERENCES app.oauth_auth (id);

END
GO