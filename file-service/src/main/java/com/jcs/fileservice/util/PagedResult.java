package com.jcs.fileservice.util;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResult<T> {
    private List<T> content;
    private String nextToken;
    private boolean hasMore;
    private int pageSize;
}
