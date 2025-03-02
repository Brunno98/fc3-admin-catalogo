package br.com.brunno.admin.catalogo.domain.exceptions;

import br.com.brunno.admin.catalogo.domain.validation.Error;

import java.util.List;

public class DomainException extends NoStackTraceException {

    private final List<Error> errors;

    protected DomainException(String aMessage, List<Error> anErros) {
        super(aMessage);
        this.errors = anErros;
    }

    public static DomainException with(List<Error> anErrors) {
        return new DomainException("", anErrors);
    }

    public static DomainException with(Error anError) {
        return new DomainException(anError.message(), List.of(anError));
    }

    public List<Error> getErrors() {
        return errors;
    }
}
