package br.com.brunno.admin.catalogo.domain.castmember;

import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.Validator;

import java.util.Objects;

public class CastMemberValidator extends Validator {

    private final CastMember castMember;

    public CastMemberValidator(CastMember castMember, ValidationHandler aHandler) {
        super(aHandler);
        this.castMember = castMember;
    }

    @Override
    public void validate() {
        validateNameConstraints();
        validateCastMemberType();
    }

    private void validateNameConstraints() {
        var name = this.castMember.getName();
        if (Objects.isNull(name)) {
            this.validationHandler().append(new Error("'name' should not be null"));
            return;
        }
        name = name.trim();
        if (name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be blank"));
            return;
        }
        if (name.length() < 3 || name.length() > 255) {
            this.validationHandler().append(new Error("'name' length should be between 3 and 255"));
        }
    }

    private void validateCastMemberType() {
        final var castMembertype = this.castMember.getType();
        if (Objects.isNull(castMembertype)) {
            this.validationHandler().append(new Error("'type' should not be null"));
            return;
        }
    }

}
