package br.com.brunno.admin.catalogo.infrastructure.services.impl;

import br.com.brunno.admin.catalogo.AmqpTest;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaCreated;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoCreatedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.services.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@AmqpTest
class RabbitEventServiceTest {

    private static final String LISTENER = "video.created";

    @Autowired
    @VideoCreatedQualifier
    private EventService eventService;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSendMessage() throws InterruptedException, JsonProcessingException {
        final var notification = new VideoMediaCreated("resource", "filepath");
        final var expectedMessage = objectMapper.writeValueAsString(notification);

        eventService.sendEvent(notification);

        final var invocationData = harness.getNextInvocationDataFor(LISTENER, 1, TimeUnit.SECONDS);

        assertNotNull(invocationData);
        assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];
        assertEquals(expectedMessage, actualMessage);
    }

    @Component
    static class VideoCreatedListener {
        @RabbitListener(id = LISTENER, queues = "${amqp.queues.video-created.routing-key}")
        void onVideoCreated(@Payload String message) {
            System.out.println(message);
        }
    }
}