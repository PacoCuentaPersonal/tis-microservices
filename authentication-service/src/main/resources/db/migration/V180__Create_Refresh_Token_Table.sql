IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'refresh_token' AND schema_id = SCHEMA_ID('app'))
BEGIN
    CREATE TABLE app.refresh_token (
        id INT IDENTITY(1,1) PRIMARY KEY,
        refresh_token UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
        expiration_time DATETIME2(7) NOT NULL,
        user_id BIGINT NOT NULL
    );
    CREATE UNIQUE INDEX UQ_refresh_token ON app.refresh_token (refresh_token);
    CREATE INDEX IX_refresh_token_user_id ON app.refresh_token (user_id);
    CREATE INDEX IX_refresh_token_expiration_time ON app.refresh_token (expiration_time);

    ALTER TABLE app.refresh_token
    ADD CONSTRAINT FK_refresh_token_user
    FOREIGN KEY (user_id) REFERENCES app.[user] (id);
END
GO