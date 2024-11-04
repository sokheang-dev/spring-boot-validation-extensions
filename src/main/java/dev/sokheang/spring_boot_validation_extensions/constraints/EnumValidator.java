package dev.sokheang.spring_boot_validation_extensions.constraints;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

final class EnumValidator implements ConstraintValidator<Enum, String> {
    private Class<? extends java.lang.Enum<?>> enumClass;
    private List<String> excludeValues;

    @Override
    public void initialize(Enum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        excludeValues = List.of(constraintAnnotation.excludeValues());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return true;
        }

        final var values = List
                .of(enumClass.getEnumConstants())
                .stream()
                .filter(v -> excludeValues.stream().noneMatch(ev -> ev.equalsIgnoreCase(v.name())))
                .toList();
        final var enums = values
                .stream()
                .map(e -> e.name().toLowerCase())
                .collect(Collectors.toList());
        var content = String.join(", ", enums);
        final var stringBuilder = new StringBuilder(content);
        final var index = content.lastIndexOf(", ");
        content = stringBuilder
                .replace(index, index + 2, ", or ")
                .toString();
        context.unwrap(HibernateConstraintValidatorContext.class)
                .addMessageParameter("values", content);
        return enums.contains(value.toLowerCase());
    }
}