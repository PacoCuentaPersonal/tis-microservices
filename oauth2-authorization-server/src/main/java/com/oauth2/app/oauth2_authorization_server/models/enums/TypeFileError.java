package com.oauth2.app.oauth2_authorization_server.models.enums;

public enum TypeFileError {
    EMPTY_FILE("EMPTY_FILE", "El archivo está vacío"),
    FILE_TOO_SMALL("FILE_TOO_SMALL", "El archivo es demasiado pequeño"),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "El archivo excede el tamaño máximo permitido"),
    INVALID_MIME_TYPE("INVALID_MIME_TYPE", "Tipo de archivo no soportado"),
    INVALID_EXTENSION("INVALID_EXTENSION", "Extensión de archivo no permitida"),
    INVALID_FILENAME("INVALID_FILENAME", "Nombre de archivo inválido"),
    IMAGE_TOO_SMALL("IMAGE_TOO_SMALL", "Las dimensiones de la imagen son muy pequeñas"),
    IMAGE_TOO_LARGE("IMAGE_TOO_LARGE", "Las dimensiones de la imagen son muy grandes"),
    CORRUPTED_FILE("CORRUPTED_FILE", "El archivo está corrupto o dañado"),
    TOO_MANY_FILES("TOO_MANY_FILES", "Se excedió el número máximo de archivos"),
    TOTAL_SIZE_EXCEEDED("TOTAL_SIZE_EXCEEDED", "El tamaño total excede el límite"),
    PROCESSING_ERROR("PROCESSING_ERROR", "Error al procesar el archivo");

    private final String code;
    private final String defaultMessage;

    TypeFileError(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    public String getCode() { return code; }
    public String getDefaultMessage() { return defaultMessage; }
}