IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = 'oauth2')
    BEGIN
        EXEC('CREATE SCHEMA oauth2');
    END;
GO