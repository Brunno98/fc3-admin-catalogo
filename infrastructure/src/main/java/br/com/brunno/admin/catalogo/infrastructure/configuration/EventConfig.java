package br.com.brunno.admin.catalogo.infrastructure.configuration;

import br.com.brunno.admin.catalogo.infrastructure.configuration.properties.amqp.QueueProperties;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoCreatedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.services.EventService;
import br.com.brunno.admin.catalogo.infrastructure.services.impl.RabbitEventService;
import br.com.brunno.admin.catalogo.infrastructure.services.local.InMemoryEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EventConfig {

    @Bean
    @Profile({"development"})
    public EventService createInMemoryEventService(ObjectMapper om) {
        return new InMemoryEventService(om);
    }

    @Bean
    @VideoCreatedQualifier
    @ConditionalOnMissingBean
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
