package br.com.brunno.admin.catalogo.application.castmember.create;

import br.com.brunno.admin.catalogo.Fixture;
import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
public class CreateCastMemberUseCaseIT {

    @Autowired
    private CreateCastMemberUseCase createCastMemberUseCase;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCommand_whenCallsCreateCastMember_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        final var output = createCastMemberUseCase.execute(aCommand);

        assertNotNull(output);
        assertNotNull(output.id());

        final var aCastMember = castMemberGateway.findById(output.id()).get();

        assertEquals(expectedName, aCastMember.getName());
        assertEquals(expectedType, aCastMember.getType());
        assertNotNull(aCastMember.getCreatedAt());
        assertNotNull(aCastMember.getUpdatedAt());
        assertEquals(aCastMember.getUpdatedAt(), aCastMember.getCreatedAt());

    }

}
