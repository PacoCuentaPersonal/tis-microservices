CREATE TABLE application.image_categories (
    id          BIGINT IDENTITY(1,1)         NOT NULL PRIMARY KEY,
    public_id   UNIQUEIDENTIFIER             NOT NULL DEFAULT NEWSEQUENTIALID(),
    name        NVARCHAR(50)                 NOT NULL,
    description NVARCHAR(255)                NULL,
    active      BIT                          NOT NULL DEFAULT 1,
    created_at  DATETIME2                    NOT NULL DEFAULT SYSDATETIME(),
    updated_at  DATETIME2                    NOT NULL DEFAULT SYSDATETIME(),
    -- Restricciones de unicidad
    CONSTRAINT  UQ_image_categories_name     UNIQUE (name),
    CONSTRAINT  UQ_image_categories_publicId UNIQUE (public_id)
);
GO