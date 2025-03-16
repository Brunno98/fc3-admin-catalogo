package br.com.brunno.admin.catalogo.application.castmember.update;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class UpdateCastMemberUseCaseTest {

    @InjectMocks
    private UpdateCastMemberUseCase updateCastMemberUseCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCastMember_shouldUpdate() {
        final var aCastMember = CastMember.create("Vin Diesel", CastMemberType.DIRECTOR);
        final var expectedId = aCastMember.getId();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;
        final var aCommand = UpdateCastMemberCommand.with(expectedId, expectedName, expectedType);

        Mockito.when(castMemberGateway.findById(expectedId))
                .thenReturn(Optional.of(aCastMember));

        final var output = updateCastMemberUseCase.execute(aCommand);

        assertNotNull(output);
        assertNotNull(output.id());

        Mockito.verify(castMemberGateway).update(argThat(it ->
                Objects.equals(it.getId(), expectedId)
                && Objects.equals(expectedName, it.getName())
                && Objects.equals(expectedType, it.getType())
                && Objects.nonNull(it.getCreatedAt())
                && Objects.nonNull(it.getUpdatedAt())
                && it.getCreatedAt().isBefore(it.getUpdatedAt())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var aCastMember = CastMember.create("Vin Diesel", CastMemberType.DIRECTOR);
        final var castMemberId = aCastMember.getId();
        final String inputName = null;
        final var inputType = Fixture.CastMembers.type();
        final var aCommand = UpdateCastMemberCommand.with(castMemberId, inputName, inputType);
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        Mockito.when(castMemberGateway.findById(castMemberId))
                .thenReturn(Optional.of(aCastMember));

        final var actualException = assertThrows(NotificationException.class,
                () -> updateCastMemberUseCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidCastMemberType_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var aCastMember = CastMember.create("Vin Diesel", CastMemberType.DIRECTOR);
        final var castMemberId = aCastMember.getId();
        final var inputName = Fixture.name();
        final CastMemberType inputType = null;
        final var aCommand = UpdateCastMemberCommand.with(castMemberId, inputName, inputType);
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        Mockito.when(castMemberGateway.findById(castMemberId))
                .thenReturn(Optional.of(aCastMember));

        final var actualException = assertThrows(NotificationException.class,
                () -> updateCastMemberUseCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenANonExistentId_whenCallsUpdateCastMember_shouldThrowsNotFoundException() {
        final var aCastMember = CastMember.create("Vin Diesel", CastMemberType.DIRECTOR);
        final var castMemberId = aCastMember.getId();
        final String inputName = Fixture.name();
        final var inputType = Fixture.CastMembers.type();
        final var aCommand = UpdateCastMemberCommand.with(castMemberId, inputName, inputType);
        final var expectedErrorMessage = "CastMember with ID '%s' not found".formatted(castMemberId.getValue());

        Mockito.when(castMemberGateway.findById(castMemberId))
                .thenReturn(Optional.empty());

        final var actualException = assertThrows(NotFoundException.class,
                () -> updateCastMemberUseCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
