package br.com.brunno.admin.catalogo.infrastructure.video.models;

import br.com.brunno.admin.catalogo.JacksonTest;
import br.com.brunno.admin.catalogo.domain.DomainId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JacksonTest
class VideoEncoderResultTest {

    @Autowired
    private JacksonTester<VideoEncoderResult> json;

    @Test
    void testUnmarshallSuccessResult() throws IOException {
        // given
        final var expectedId = DomainId.generate().toString();
        final var expectedOutputBucket = "codeeducationtest";
        final var expectedStatus = "COMPLETED";
        final var expectedEncoderVideoFolder = "anyFolder";
        final var expectedResourceId = DomainId.generate().toString();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata =
                new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);

        final var json = """
                {
                    "id": "%s",
                    "output_bucket_path": "%s",
                    "status": "%s",
                    "video": {
                        "encoded_video_folder": "%s",
                        "resource_id": "%s",
                        "file_path": "%s"
                    }
                }
                """.formatted(expectedId, expectedOutputBucket, expectedStatus,
                expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);

        // when
        final var actualJson = this.json.parse(json);

        // then
        Assertions.assertThat(actualJson)
                .isInstanceOf(VideoEncoderCompleted.class)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("outputBucket", expectedOutputBucket)
                .hasFieldOrPropertyWithValue("status", expectedStatus)
                .hasFieldOrPropertyWithValue("video", expectedMetadata);
    }

    @Test
    void testMarshallSuccessResult() throws IOException {
        // given
        final var expectedId = DomainId.generate().toString();
        final var expectedOutputBucket = "codeeducationtest";
        final var expectedStatus = "COMPLETED";
        final var expectedEncoderVideoFolder = "anyFolder";
        final var expectedResourceId = DomainId.generate().toString();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata =
                new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);
        final var aResult = new VideoEncoderCompleted(expectedId, expectedOutputBucket, expectedMetadata);

        // when
        final var actualJson = this.json.write(aResult);

        // then
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.output_bucket_path", expectedOutputBucket)
                .hasJsonPathValue("$.status", expectedStatus)
                .hasJsonPathValue("$.video.encoded_video_folder", expectedEncoderVideoFolder)
                .hasJsonPathValue("$.video.resource_id", expectedResourceId)
                .hasJsonPathValue("$.video.file_path", expectedFilePath);
    }

    @Test
    void testUnmarshallErrorResult() throws IOException {
        // given
        final var expectedErrorMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedResourceId = DomainId.generate().toString();
        final var expectedFilePath = "any.mp4";
        final var expectedVideoMessage = new VideoMessage(expectedResourceId, expectedFilePath);
        final var json = """
                {
                    "status": "%s",
                    "error": "%s",
                    "message": {
                        "resource_id": "%s",
                        "file_path": "%s"
                    }
                }
                """.formatted(expectedStatus, expectedErrorMessage, expectedResourceId, expectedFilePath);

        // when
        final var actualJson = this.json.parse(json);

        // then
        Assertions.assertThat(actualJson)
                .isInstanceOf(VideoEncoderError.class)
                .hasFieldOrPropertyWithValue("error", expectedErrorMessage)
                .hasFieldOrPropertyWithValue("message", expectedVideoMessage);
    }

    @Test
    void testMarshallErrorResult() throws IOException {
        // given
        final var expectedErrorMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedResourceId = DomainId.generate().toString();
        final var expectedFilePath = "any.mp4";
        final var expectedVideoMessage = new VideoMessage(expectedResourceId, expectedFilePath);
        final var aResult = new VideoEncoderError(expectedVideoMessage, expectedErrorMessage);

        // when
        final var actualJson = this.json.write(aResult);

        // then
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.status", expectedStatus)
                .hasJsonPathValue("$.error", expectedErrorMessage)
                .hasJsonPathValue("$.message.resource_id", expectedResourceId)
                .hasJsonPathValue("$.message.file_path", expectedFilePath);
    }
}