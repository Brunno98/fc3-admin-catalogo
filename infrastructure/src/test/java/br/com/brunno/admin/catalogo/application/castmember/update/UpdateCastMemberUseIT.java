package br.com.brunno.admin.catalogo.application.castmember.update;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class UpdateCastMemberUseIT {

    @Autowired
    private UpdateCastMemberUseCase updateCastMemberUseCase;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCastMember_shouldUpdate() {
        final var aCastMember = CastMember.create("Vin Diesel", CastMemberType.DIRECTOR);
        castMemberGateway.create(aCastMember);
        final var expectedId = aCastMember.getId();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;
        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);

        final var output = updateCastMemberUseCase.execute(aCommand);

        assertNotNull(output);
        assertNotNull(output.id());

        final var actualCastMember = castMemberGateway.findById(expectedId).get();

        assertEquals(expectedName, actualCastMember.getName());
        assertEquals(expectedType, actualCastMember.getType());
        assertEquals(aCastMember.getCreatedAt(), actualCastMember.getCreatedAt());
        assertTrue(actualCastMember.getUpdatedAt().isAfter(actualCastMember.getCreatedAt()));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var aCastMember = CastMember.create("Vin Diesel", CastMemberType.DIRECTOR);
        castMemberGateway.create(aCastMember);
        final var castMemberId = aCastMember.getId();
        final String inputName = null;
        final var inputType = Fixture.CastMembers.type();
        final var aCommand = UpdateCastMemberCommand.with(castMemberId, inputName, inputType);
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualException = assertThrows(NotificationException.class,
                () -> updateCastMemberUseCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidCastMemberType_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var aCastMember = CastMember.create("Vin Diesel", CastMemberType.DIRECTOR);
        castMemberGateway.create(aCastMember);
        final var castMemberId = aCastMember.getId();
        final var inputName = Fixture.name();
        final CastMemberType inputType = null;
        final var aCommand = UpdateCastMemberCommand.with(castMemberId, inputName, inputType);
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var actualException = assertThrows(NotificationException.class,
                () -> updateCastMemberUseCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenANonExistentId_whenCallsUpdateCastMember_shouldThrowsNotFoundException() {
        final var castMemberId = CastMemberID.from("123");
        final String inputName = Fixture.name();
        final var inputType = Fixture.CastMembers.type();
        final var aCommand = UpdateCastMemberCommand.with(castMemberId, inputName, inputType);
        final var expectedErrorMessage = "CastMember with ID '%s' not found".formatted(castMemberId.getValue());

        final var actualException = assertThrows(NotFoundException.class,
                () -> updateCastMemberUseCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
