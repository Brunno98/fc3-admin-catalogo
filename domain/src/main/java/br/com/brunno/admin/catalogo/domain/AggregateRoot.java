package br.com.brunno.admin.catalogo.domain;

import br.com.brunno.admin.catalogo.domain.event.DomainEvent;

import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    public AggregateRoot(ID id) {
        super(id, Collections.emptyList());
    }

    public AggregateRoot(ID id, List<DomainEvent> domainEvents) {
        super(id, domainEvents);
    }

}
