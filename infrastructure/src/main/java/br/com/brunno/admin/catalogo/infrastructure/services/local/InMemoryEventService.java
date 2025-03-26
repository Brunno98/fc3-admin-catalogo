package br.com.brunno.admin.catalogo.infrastructure.services.local;

import br.com.brunno.admin.catalogo.infrastructure.services.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class InMemoryEventService implements EventService {

    private static final Logger log = LoggerFactory.getLogger(InMemoryEventService.class);

    private final ObjectMapper om;

    public InMemoryEventService(ObjectMapper om) {
        this.om = Objects.requireNonNull(om);
    }

    @Override
    public void sendEvent(Object event) {
        try {
            log.info("An event was observed: {}", om.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            if (Objects.nonNull(event)) {
                log.error("Error to marshall object {}", event.toString());
            }
            throw new RuntimeException(e);
        }
    }
}
