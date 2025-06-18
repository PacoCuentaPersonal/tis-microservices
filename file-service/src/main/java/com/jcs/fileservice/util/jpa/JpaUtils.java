package com.jcs.fileservice.util.jpa;

import com.jcs.fileservice.exception.thrower.SortFieldInvalidException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;


public class JpaUtils {
    private List<String> allowedFields;
    private static final int MAX_SIZE = 100;

    public JpaUtils (List<String> allowedFields){
        this.allowedFields = allowedFields;
    }
    public Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = Math.max(0, page);
        int pageSize = Math.min(Math.max(1, size), MAX_SIZE);

        // Crear Sort
        Sort sort = createSort(sortBy, sortDirection);

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    /**
     * Crea un objeto Sort basado en los campos y dirección especificados
     */
    private Sort createSort(String sortBy, String sortDirection) {
        List<Sort.Order> orders = new ArrayList<>();

        // Validar dirección
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC;
        }

        // Procesar campos de ordenamiento (pueden ser múltiples separados por coma)
        String[] fields = sortBy.split(",");
        for (String field : fields) {
            String trimmedField = field.trim();
            if (trimmedField.isEmpty() && !isValidSortField(trimmedField)) {
                throw new SortFieldInvalidException("Invalid sort field: " + trimmedField);
            }
            orders.add(new Sort.Order(direction, trimmedField));

        }


        return Sort.by(orders);
    }

    /**
     * Valida si un campo es válido para ordenamiento
     */
    private boolean isValidSortField(String field) {

        return allowedFields.contains(field);
    }
}
