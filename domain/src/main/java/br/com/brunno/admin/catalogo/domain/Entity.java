package br.com.brunno.admin.catalogo.domain;

import br.com.brunno.admin.catalogo.domain.event.DomainEvent;
import br.com.brunno.admin.catalogo.domain.event.DomainEventPublisher;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Entity<ID extends Identifier> {

    protected ID id;
    private final List<DomainEvent> domainEvents;

    protected Entity(ID id, List<DomainEvent> domainEvents) {
        Objects.requireNonNull(id, "'id' should not be null!");
        this.id = id;
        this.domainEvents = new ArrayList<>(Objects.isNull(domainEvents)? Collections.emptyList() : domainEvents);
    }

    public abstract void validate(ValidationHandler handler);

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    public void publishDomainEvents(final DomainEventPublisher<DomainEvent> publisher) {
        if (Objects.isNull(publisher)) return;

        this.getDomainEvents().forEach(publisher::publish);

        this.domainEvents.clear();
    }

    public void registerDomainEvent(DomainEvent event) {
        if (Objects.isNull(event)) return;
        this.domainEvents.add(event);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
