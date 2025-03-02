package br.com.brunno.admin.catalogo.domain.category;

import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.Validator;

import java.util.Objects;

public class CategoryValidator extends Validator {

    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 255;
    
    private final Category category;

    public CategoryValidator(Category category, ValidationHandler aHandler) {
        super(aHandler);
        Objects.requireNonNull(category, "category should not be null");
        this.category = category;
    }

    @Override
    public void validate() {
        validateNameConstraints();
    }

    private void validateNameConstraints() {
        if (this.category.getName() == null) {
            this.validationHandler().append(new Error("'name' should not be null"));
            return;
        }

        final var name = this.category.getName().trim();

        if (name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be blank"));
            return;
        }

        if (name.length() < NAME_MIN_LENGTH) {
            this.validationHandler().append(new Error("'name' should not be less than 3"));
            return;
        }

        if (name.length() > NAME_MAX_LENGTH) {
            this.validationHandler().append(new Error("'name' should not be greater than 255"));
            return;
        }
    }

}
