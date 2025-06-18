package com.jcs.jpa;

public final class PaginationConstants {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "20";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    public static final SortDirection DEFAULT_SORT_DIRECTION_ENUM = SortDirection.DESC;

    private PaginationConstants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }
}