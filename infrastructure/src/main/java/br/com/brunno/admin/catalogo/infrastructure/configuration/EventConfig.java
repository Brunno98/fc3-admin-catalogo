package br.com.brunno.admin.catalogo.infrastructure.configuration;

import br.com.brunno.admin.catalogo.infrastructure.configuration.properties.amqp.QueueProperties;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoCreatedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.services.EventService;
import br.com.brunno.admin.catalogo.infrastructure.services.impl.RabbitEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    @VideoCreatedQualifier
    public EventService createVideoEventService(
            @VideoCreatedQualifier QueueProperties props,
            RabbitOperations ops,
            ObjectMapper objectMapper
    ) {
        return new RabbitEventService(
                props.getExchange(),
                props.getRoutingKey(),
                ops,
                objectMapper
        );
    }
}
