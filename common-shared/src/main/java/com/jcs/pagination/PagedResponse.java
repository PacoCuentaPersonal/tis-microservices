package com.jcs.pagination;

import java.util.List;

public record PagedResponse<T>(
        List<T> data,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean isFirst,
        boolean isLast
) {}

