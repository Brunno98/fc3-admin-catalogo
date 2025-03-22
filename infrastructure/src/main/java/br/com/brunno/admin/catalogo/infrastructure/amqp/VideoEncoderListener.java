package br.com.brunno.admin.catalogo.infrastructure.amqp;

import br.com.brunno.admin.catalogo.application.video.media.update.UpdateMediaStatusCommand;
import br.com.brunno.admin.catalogo.application.video.media.update.UpdateMediaStatusUseCase;
import br.com.brunno.admin.catalogo.domain.video.MediaStatus;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoEncoderCompleted;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoEncoderError;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoEncoderResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class VideoEncoderListener {

    private static final Logger log = LoggerFactory.getLogger(VideoEncoderListener.class);

    public static final String LISTENER_ID = "videoEncoded";

    private final UpdateMediaStatusUseCase updateMediaStatusUseCase;
    private final ObjectMapper objectMapper;

    public VideoEncoderListener(UpdateMediaStatusUseCase updateMediaStatusUseCase, ObjectMapper objectMapper) {
        this.updateMediaStatusUseCase = Objects.requireNonNull(updateMediaStatusUseCase);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @RabbitListener(id = LISTENER_ID, queues = "${amqp.queues.video-encoded.queue}")
    public void onVideoEncodedMessage(@Payload String message) {
        VideoEncoderResult videoEncoderResult;
        try {
            videoEncoderResult = this.objectMapper.readValue(message, VideoEncoderResult.class);
        } catch (JsonProcessingException e) {
            log.error("[message:video.listener.income] [status: unknown] [error: JsonParserError] [payload: {}]", message);
            return;
        }

        if (videoEncoderResult instanceof VideoEncoderCompleted dto) {
            var command = new UpdateMediaStatusCommand(
                    MediaStatus.COMPLETED,
                    VideoID.from(dto.id()),
                    dto.video().resourceId(),
                    dto.video().encodedVideoFolder(),
                    dto.video().filePath()
            );
            this.updateMediaStatusUseCase.execute(command);
        } else if (videoEncoderResult instanceof VideoEncoderError) {
            log.error("[message:video.listener.income] [status: error] [payload: {}]", message);
        } else {
            log.error("[message:video.listener.income] [status: unknown] [payload: {}]", message);
        }
    }
}
