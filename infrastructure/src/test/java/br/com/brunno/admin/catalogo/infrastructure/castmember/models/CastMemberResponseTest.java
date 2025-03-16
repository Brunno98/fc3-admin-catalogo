package br.com.brunno.admin.catalogo.infrastructure.castmember.models;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.JacksonTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;

@JacksonTest
class CastMemberResponseTest {

    @Autowired
    private JacksonTester<CastMemberResponse> json;

    @Test
    void testMarshall() throws Exception{
        final var expectedId = CastMemberID.unique();
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var expectedCreatedDate = Instant.now();
        final var expectedUpdatedDate = Instant.now();

        final var castMemberListResponse = new CastMemberResponse(
                expectedId.getValue(),
                expectedName,
                expectedType.name(),
                expectedCreatedDate,
                expectedUpdatedDate
        );

        final var json = this.json.write(castMemberListResponse);

        Assertions.assertThat(json)
                .hasJsonPathValue("$.id", expectedId.getValue())
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.type", expectedType.name())
                .hasJsonPathValue("$.created_at", expectedCreatedDate)
                .hasJsonPathValue("$.updated_at", expectedUpdatedDate);
    }

}