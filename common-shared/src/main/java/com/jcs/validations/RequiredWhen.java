package com.jcs.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Valida que un campo sea requerido (no nulo) cuando otro campo tiene un valor específico.
 * Automáticamente valida que el campo sea nulo cuando la condición no se cumple.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredWhenValidator.class)
@Repeatable(RequiredWhen.List.class)
@Documented
public @interface RequiredWhen {

    String message() default "El campo '{field}' es requerido cuando '{condition}' es {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Campo que contiene la condición booleana
     */
    String condition();

    /**
     * Campo que debe ser validado
     */
    String field();

    /**
     * Valor de la condición para que el campo sea requerido (por defecto true)
     */
    boolean value() default true;

    /**
     * Mensaje cuando el campo no debería estar presente
     */
    String inverseMessage() default "El campo '{field}' no debe estar presente cuando '{condition}' es {inverseValue}";

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        RequiredWhen[] value();
    }
}
