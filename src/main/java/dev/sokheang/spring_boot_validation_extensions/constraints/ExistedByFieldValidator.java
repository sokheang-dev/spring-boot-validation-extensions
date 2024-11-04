package dev.sokheang.spring_boot_validation_extensions.constraints;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.Assert;

import dev.sokheang.spring_boot_validation_extensions.ConstraintEntity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

final class ExistedByIdValidator implements ConstraintValidator<ExistedByField, Object> {
    private final ApplicationContext context;

    private JpaSpecificationExecutor<ConstraintEntity> executor;
    private String fieldName;

    public ExistedByIdValidator(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void initialize(ExistedByField constraintAnnotation) {
        final var bean = context.getBean(constraintAnnotation.jpaSpecificationExecutorClass());
        final var className = constraintAnnotation
                .jpaSpecificationExecutorClass()
                .getCanonicalName();
        Assert.notNull(bean, "No bean found for type %s".formatted(className));
        final var executorMessage = "%s did not extend JpaSpecificationExecutor<T>".formatted(className);
        Assert.isTrue(bean instanceof JpaSpecificationExecutor<ConstraintEntity>, executorMessage);
        Assert.notNull(constraintAnnotation.fieldName(), "fieldName must not be null");
        Assert.isTrue(constraintAnnotation.fieldName().isBlank(), "fieldName must not be empty or whitespace.");
        executor = bean;
        fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        constraintValidatorContext
                .unwrap(HibernateConstraintValidatorContext.class)
                .addMessageParameter("id", value);
        return executor
                .findOne(findByField(value))
                .isPresent();
    }

    private Specification<ConstraintEntity> findByField(Object value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(fieldName), value);
    }
}
