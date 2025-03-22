package br.com.brunno.admin.catalogo.infrastructure.configuration;

import br.com.brunno.admin.catalogo.infrastructure.configuration.properties.amqp.QueueProperties;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoCreatedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoEncodedQualifier;
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
}
