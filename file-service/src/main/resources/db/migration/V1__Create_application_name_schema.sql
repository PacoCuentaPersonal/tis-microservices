IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = N'application')
    EXEC('CREATE SCHEMA application');
GO