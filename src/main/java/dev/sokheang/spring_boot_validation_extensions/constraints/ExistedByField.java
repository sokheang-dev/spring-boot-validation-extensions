package dev.sokheang.spring_boot_validation_extensions.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dev.sokheang.spring_boot_validation_extensions.ConstraintEntity;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistedByIdValidator.class)
public @interface ExistedByField {
    String message() default "{validations.existed-by-field}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldName();

    Class<? extends JpaSpecificationExecutor<ConstraintEntity>> jpaSpecificationExecutorClass();
}