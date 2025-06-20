package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.util;

import com.oauth2.app.oauth2_authorization_server.application.exception.throwers.SortFieldInvalidException; // Path corregido
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class JpaUtils {
    private final List<String> allowedSortFields;
    private static final int MAX_PAGE_SIZE = 100;

    public JpaUtils(List<String> allowedSortFields) {
        this.allowedSortFields = allowedSortFields != null ? new ArrayList<>(allowedSortFields) : new ArrayList<>();
    }

    public Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = (page != null && page > 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE) : 20; // Default size 20 if not provided

        Sort sort = createSort(sortBy, sortDirection);
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Direction direction = Sort.Direction.ASC; // Default direction

        if (sortDirection != null && !sortDirection.trim().isEmpty()) {
            try {
                direction = Sort.Direction.fromString(sortDirection.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Log warning or keep default if direction is invalid
                System.err.println("Invalid sort direction provided: " + sortDirection + ". Defaulting to ASC.");
            }
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String[] fields = sortBy.split(",");
            for (String field : fields) {
                String trimmedField = field.trim();
                if (!trimmedField.isEmpty()) {
                    if (!isValidSortField(trimmedField)) {
                        throw new SortFieldInvalidException("Invalid sort field: " + trimmedField + ". Allowed fields: " + allowedSortFields);
                    }
                    orders.add(new Sort.Order(direction, trimmedField));
                }
            }
        }

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    private boolean isValidSortField(String field) {
        if (allowedSortFields.isEmpty()) {
            // If no specific fields are allowed, perhaps allow any valid field name (less safe)
            // or disallow sorting altogether by returning false always unless list is populated.
            // For now, if not configured, assume no field is valid for sorting to be safe.
            return false; 
        }
        return allowedSortFields.contains(field);
    }
}
