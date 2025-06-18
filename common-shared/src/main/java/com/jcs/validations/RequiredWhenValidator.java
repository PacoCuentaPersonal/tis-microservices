package com.jcs.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class RequiredWhenValidator implements ConstraintValidator<RequiredWhen, Object> {

    private String conditionField;
    private String targetField;
    private boolean expectedValue;
    private String message;
    private String inverseMessage;

    @Override
    public void initialize(RequiredWhen annotation) {
        this.conditionField = annotation.condition();
        this.targetField = annotation.field();
        this.expectedValue = annotation.value();
        this.message = annotation.message();
        this.inverseMessage = annotation.inverseMessage();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }

        try {
            Boolean conditionValue = (Boolean) getFieldValue(object, conditionField);
            Object targetValue = getFieldValue(object, targetField);

            if (conditionValue == null) {
                return true; // Si no hay condición, no validamos
            }

            boolean conditionMet = conditionValue.equals(expectedValue);
            boolean isValid = true;
            String errorMessage = null;

            if (conditionMet && targetValue == null) {
                // La condición se cumple pero el campo es nulo
                isValid = false;
                errorMessage = buildMessage(message, conditionValue);
            } else if (!conditionMet && targetValue != null) {
                // La condición NO se cumple pero el campo tiene valor
                isValid = false;
                errorMessage = buildMessage(inverseMessage, conditionValue);
            }

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorMessage)
                        .addPropertyNode(targetField)
                        .addConstraintViolation();
            }

            return isValid;

        } catch (Exception e) {
            throw new RuntimeException("Error en validación: " + e.getMessage(), e);
        }
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();

        // Para records, intentar el método accessor
        if (clazz.isRecord()) {
            try {
                return clazz.getMethod(fieldName).invoke(object);
            } catch (NoSuchMethodException e) {
                // Si no funciona, intentar con reflection normal
            }
        }

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    private String buildMessage(String template, boolean actualValue) {
        return template
                .replace("{field}", targetField)
                .replace("{condition}", conditionField)
                .replace("{value}", String.valueOf(expectedValue))
                .replace("{inverseValue}", String.valueOf(!expectedValue));
    }
}
