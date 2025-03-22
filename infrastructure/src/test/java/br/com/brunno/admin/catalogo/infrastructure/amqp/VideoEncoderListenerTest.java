package br.com.brunno.admin.catalogo.infrastructure.amqp;

import br.com.brunno.admin.catalogo.AmqpTest;
import br.com.brunno.admin.catalogo.application.video.media.update.UpdateMediaStatusUseCase;
import br.com.brunno.admin.catalogo.infrastructure.configuration.properties.amqp.QueueProperties;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoCreatedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoEncodedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoEncoderCompleted;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoEncoderError;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoMessage;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@AmqpTest
class VideoEncoderListenerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TestRabbitTemplate rabbitTemplate;

    @Autowired
    RabbitListenerTestHarness harness;

    @Autowired
    @VideoEncodedQualifier
    QueueProperties queueProperties;

    @MockBean
    UpdateMediaStatusUseCase useCase;

    @Test
    void givenErrorResult_whenCallsListener_shouldProcess() throws Exception {
        // given
        final var expectedError = new VideoEncoderError(
                new VideoMessage("123", "abc"),
                "Video not found"
        );
        final var expectedMessage = objectMapper.writeValueAsString(expectedError);

        // when
        rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        // then
        final var invocationData =
                harness.getNextInvocationDataFor(VideoEncoderListener.LISTENER_ID, 1, TimeUnit.SECONDS);

        Assertions.assertNotNull(invocationData);
        Assertions.assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void givenCompletedResult_whenCallsListener_shouldCallUseCase() throws Exception {
        // given
        final var expectedError = new VideoEncoderCompleted(
                "123",
                "bucket",
                new VideoMetadata("folder", "resourceId", "filePath")
        );
        final var expectedMessage = objectMapper.writeValueAsString(expectedError);

        // when
        rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        // then
        final var invocationData =
                harness.getNextInvocationDataFor(VideoEncoderListener.LISTENER_ID, 1, TimeUnit.SECONDS);

        Assertions.assertNotNull(invocationData);
        Assertions.assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];

        assertEquals(expectedMessage, actualMessage);

        Mockito.verify(useCase).execute(Mockito.any());
    }
}