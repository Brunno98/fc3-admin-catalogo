package br.com.brunno.admin.catalogo.domain.genre;

import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.Validator;

public class GenreValidator extends Validator {

    private final Genre genre;

    public GenreValidator(Genre genre, ValidationHandler aHandler) {
        super(aHandler);
        this.genre = genre;
    }

    @Override
    public void validate() {
        final var name = genre.getName();
        if (name == null) {
            validationHandler().append(new br.com.brunno.admin.catalogo.domain.validation.Error("'name' should not be null"));
        } else if (name.isBlank()) {
            validationHandler().append(new br.com.brunno.admin.catalogo.domain.validation.Error("'name' should not be blank"));
        } else if (name.length() > 255) {
            validationHandler().append(new Error("'name' length should be between 1 and 255"));
        }
    }
}
