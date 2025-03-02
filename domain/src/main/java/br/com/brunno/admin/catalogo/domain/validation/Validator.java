package br.com.brunno.admin.catalogo.domain.validation;

public abstract class Validator {

    private final ValidationHandler handler;

    public Validator(ValidationHandler aHandler) {
        this.handler = aHandler;
    }

    public abstract void validate();

    protected ValidationHandler validationHandler() {
        return this.handler;
    }
}
