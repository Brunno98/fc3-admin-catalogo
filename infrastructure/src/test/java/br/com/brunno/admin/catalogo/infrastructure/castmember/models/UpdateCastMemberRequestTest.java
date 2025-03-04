package br.com.brunno.admin.catalogo.infrastructure.castmember.models;

import br.com.brunno.admin.catalogo.Fixture;
import br.com.brunno.admin.catalogo.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class UpdateCastMemberRequestTest {

    @Autowired
    private JacksonTester<UpdateCastMemberRequest> json;

    @Test
    void testUnmarshall() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type().name();

        final var json = """
        {
            "name": "%s",
            "type": "%s"
        }
        """.formatted(
                expectedName,
                expectedType
        );

        final var actualRequest = this.json.parse(json);

        assertThat(actualRequest)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("type", expectedType);
    }
}