package br.com.brunno.admin.catalogo.application.castmember.create;

import br.com.brunno.admin.catalogo.application.Fixture;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class CreateCastMemberUseCaseTest {

    @InjectMocks
    private CreateCastMemberUseCase createCastMemberUseCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCommand_whenCallsCreateCastMember_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        Mockito.when(castMemberGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var output = createCastMemberUseCase.execute(aCommand);

        assertNotNull(output);
        assertNotNull(output.id());

        Mockito.verify(castMemberGateway).create(argThat(it -> Objects.nonNull(it.getId())
            && expectedName.equals(it.getName())
            && expectedType.equals(it.getType())
            && Objects.nonNull(it.getCreatedAt())
            && Objects.nonNull(it.getUpdatedAt())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsCreateCastMember_shouldThrowsNotificationException() {
        final String inputName = null;
        final var inputCastMemberType = Fixture.CastMember.type();
        final var aCommand = CreateCastMemberCommand.with(inputName, inputCastMemberType);

        final var actualException = assertThrows(NotificationException.class,
                () -> createCastMemberUseCase.execute(aCommand));

        assertEquals(1, actualException.getErrors().size());
        assertEquals("'name' should not be null", actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnNullCastMemberType_whenCallsCreateCastMember_shouldThrowsNotificationException() {
        final var inputName = Fixture.name();
        final CastMemberType inputCastMemberType = null;
        final var aCommand = CreateCastMemberCommand.with(inputName, inputCastMemberType);

        final var actualException = assertThrows(NotificationException.class,
                () -> createCastMemberUseCase.execute(aCommand));

        assertEquals(1, actualException.getErrors().size());
        assertEquals("'type' should not be null", actualException.getErrors().get(0).message());
    }
}
