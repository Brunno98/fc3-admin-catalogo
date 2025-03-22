package br.com.brunno.admin.catalogo.infrastructure.services.impl;

import br.com.brunno.admin.catalogo.infrastructure.services.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitOperations;

import java.util.Objects;

public class RabbitEventService implements EventService {

    private final String exchange;
    private final String routingKey;
    private final RabbitOperations ops;
    private final ObjectMapper objectMapper;

    public RabbitEventService(
            String exchange,
            String routingKey,
            RabbitOperations ops,
            ObjectMapper objectMapper
    ) {
        this.exchange = Objects.requireNonNull(exchange);
        this.routingKey = Objects.requireNonNull(routingKey);
        this.ops = Objects.requireNonNull(ops);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public void sendEvent(Object event) {
        try {
            this.ops.convertAndSend(this.exchange, this.routingKey, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
