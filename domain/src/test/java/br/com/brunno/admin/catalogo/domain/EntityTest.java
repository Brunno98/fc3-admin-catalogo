package br.com.brunno.admin.catalogo.domain;

import br.com.brunno.admin.catalogo.domain.event.DomainEvent;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class EntityTest {

    @Test
    void givenNullAsDomainEventsList_whenEntityIsCreated_thenDomainEventsShouldBeEmpty() {
        // given
        final List<DomainEvent> domainEvents = null;

        // when
        Entity<DummyEntityId> entity = new DummyEntity(new DummyEntityId(), domainEvents);


        assertNotNull(entity.getDomainEvents());
        assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    void givenDomainEvents_whenPassInConstructor_shouldReturnsADefensiveCopy() {
        //given
        final List<DomainEvent> domainEvents = new ArrayList<>();
        domainEvents.add(() -> null);

        // when
        Entity<DummyEntityId> entity = new DummyEntity(new DummyEntityId(), domainEvents);

        // then
        assertNotNull(entity.getDomainEvents());
        assertEquals(1, entity.getDomainEvents().size());
        assertThrows(RuntimeException.class,
                () -> entity.getDomainEvents().add(() -> null)
        );
    }

    @Test
    void givenADomainEvent_whenRegisterADomainEvent_shouldAddToDomainEventsList() {
        // given
        final List<DomainEvent> domainEvents = new ArrayList<>();
        Entity<DummyEntityId> entity = new DummyEntity(new DummyEntityId(), domainEvents);
        assertTrue(entity.getDomainEvents().isEmpty());

        // when
        entity.registerDomainEvent(() -> null);

        // then
        assertNotNull(entity.getDomainEvents());
        assertEquals(1, entity.getDomainEvents().size());
    }

    @Test
    void givenAFewDomainEvents_whenCallsPublishEvents_shouldPublishAllEventsAndClearList() {
        // given
        final List<DomainEvent> domainEvents = new ArrayList<>();
        domainEvents.add(() -> null);
        domainEvents.add(() -> null);
        domainEvents.add(() -> null);
        Entity<DummyEntityId> entity = new DummyEntity(new DummyEntityId(), domainEvents);
        assertEquals(3, entity.getDomainEvents().size());
        final var counter = new AtomicInteger(0);

        // when
        entity.publishDomainEvents(event -> counter.incrementAndGet());

        // then
        assertEquals(3, counter.get());
        assertTrue(entity.getDomainEvents().isEmpty());
    }

    static class DummyEntityId extends Identifier {
        @Override
        public Object getValue() {
            return DomainId.generate();
        }
    }
    static class DummyEntity extends Entity<DummyEntityId> {
        protected DummyEntity(DummyEntityId id, List<DomainEvent> domainEvents) {
            super(id, domainEvents);
        }

        @Override
        public void validate(ValidationHandler handler) {
            fail();
        }
    }
}