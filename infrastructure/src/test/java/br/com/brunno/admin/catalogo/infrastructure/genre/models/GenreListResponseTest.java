package br.com.brunno.admin.catalogo.infrastructure.genre.models;

import br.com.brunno.admin.catalogo.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class GenreListResponseTest {

    @Autowired
    private JacksonTester<GenreListResponse> json;

    @Test
    void testMarshall() throws IOException {
        final var now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedActive = true;
        final var expectedCreatedAt = now;
        final Instant expectedDeletedAt = null;

        final var genreListResponse = new GenreListResponse(
                expectedId,
                expectedName,
                expectedActive,
                expectedCreatedAt,
                expectedDeletedAt
        );

        final var actualJson = this.json.write(genreListResponse);

        assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.is_active", expectedActive)
                .hasJsonPathValue("$.created_at", expectedCreatedAt)
                .hasEmptyJsonPathValue("$.deleted_at");
    }
}