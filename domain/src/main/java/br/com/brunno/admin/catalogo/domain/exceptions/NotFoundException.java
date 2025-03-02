package br.com.brunno.admin.catalogo.domain.exceptions;

import br.com.brunno.admin.catalogo.domain.AggregateRoot;
import br.com.brunno.admin.catalogo.domain.Identifier;
import br.com.brunno.admin.catalogo.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {

    protected NotFoundException(String aMessage, List<Error> anErros) {
        super(aMessage, anErros);
    }

    public static NotFoundException with(Class<? extends AggregateRoot<?>> aAggregateRoot, Identifier anId) {
        final var notFoundMessage = "%s with ID '%s' not found".formatted(aAggregateRoot.getSimpleName(), anId.getValue());
        return new NotFoundException(notFoundMessage, Collections.emptyList());
    }

}
