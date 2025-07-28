package com.dollop.app.annotation.impl;

import java.lang.reflect.Field;

import com.dollop.app.annotation.Trimmed;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrimValidator implements ConstraintValidator<Trimmed, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true; 
        }

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.getType().equals(String.class)) { 
                    field.setAccessible(true);
                    String value = (String) field.get(obj);
                    if (value != null) {
                        field.set(obj, value.trim()); 
                    }
                }
            }
        } catch (IllegalAccessException e) {
            return false;
        }

        return true;
    }
}
