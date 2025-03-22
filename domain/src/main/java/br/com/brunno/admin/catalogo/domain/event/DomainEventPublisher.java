package br.com.brunno.admin.catalogo.domain.event;

@FunctionalInterface
public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T event);

}
