package br.com.brunno.admin.catalogo.infrastructure.configuration;

import br.com.brunno.admin.catalogo.infrastructure.configuration.properties.amqp.QueueProperties;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoCreatedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoEncodedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoEventsQualifier;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class amqpConfig {

    @Bean
    @ConfigurationProperties("amqp.queues.video-created")
    @VideoCreatedQualifier
    public QueueProperties videoCreateQueueProperties() {
        return new QueueProperties();
    }

    @Bean
    @ConfigurationProperties("amqp.queues.video-encoded")
    @VideoEncodedQualifier
    public QueueProperties videoEncodedQueueProperties() {
        return new QueueProperties();
    }

    @Configuration
    static class Admin {

        @Bean
        @VideoEventsQualifier
        public Exchange videoEventsExchange(@VideoCreatedQualifier QueueProperties props) {
            return new DirectExchange(props.getExchange());
        }

        @Bean
        @VideoCreatedQualifier
        public Queue videoCreatedQueue(@VideoCreatedQualifier QueueProperties props) {
            return new Queue(props.getQueue());
        }

        @Bean
        @VideoEncodedQualifier
        public Queue videoEncodedQueue(@VideoEncodedQualifier QueueProperties props) {
            return new Queue(props.getQueue());
        }

        @Bean
        @VideoCreatedQualifier
        public Binding videCreatedBinding(
                @VideoEventsQualifier DirectExchange exchange,
                @VideoCreatedQualifier Queue queue,
                @VideoCreatedQualifier QueueProperties props
        ) {
            return BindingBuilder.bind(queue).to(exchange).with(props.getRoutingKey());
        }

        @Bean
        @VideoEncodedQualifier
        public Binding videEncodedBinding(
                @VideoEventsQualifier DirectExchange exchange,
                @VideoEncodedQualifier Queue queue,
                @VideoEncodedQualifier QueueProperties props
        ) {
            return BindingBuilder.bind(queue).to(exchange).with(props.getRoutingKey());
        }
    }
}
