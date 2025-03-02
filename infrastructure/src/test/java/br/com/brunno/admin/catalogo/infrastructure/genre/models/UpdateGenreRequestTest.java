package br.com.brunno.admin.catalogo.infrastructure.genre.models;

import br.com.brunno.admin.catalogo.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class UpdateGenreRequestTest {

    @Autowired
    private JacksonTester<UpdateGenreRequest> json;

    @Test
    void testUnmarshall() throws IOException {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");

        final var json = """
        {
            "name": "%s",
            "is_active": %s,
            "categories_id": %s
        }
        """.formatted(
                expectedName,
                expectedIsActive,
                expectedCategories
        );

        final var actualJson = this.json.parse(json);

        assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("active", expectedIsActive)
                .hasFieldOrPropertyWithValue("categories", expectedCategories);
    }

}