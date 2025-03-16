package br.com.brunno.admin.catalogo.application.castmember.retrieve.get;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
class GetCastMemberUseIT {

    @Autowired
    private GetCastMemberUseCase getCastMemberUseCase;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidId_whenCallsGetById_shouldReturnTheCastMember() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var castMember = CastMember.create(expectedName, expectedType);
        castMemberGateway.create(castMember);
        final var expectedId = castMember.getId();

        final var castMemberOutput = getCastMemberUseCase.execute(expectedId);

        assertEquals(expectedId, castMemberOutput.id());
        assertEquals(expectedName, castMemberOutput.name());
        assertEquals(expectedType, castMemberOutput.type());
        assertEquals(castMember.getCreatedAt(), castMemberOutput.createdAt());
        assertEquals(castMember.getUpdatedAt(), castMemberOutput.updatedAt());
    }

    @Test
    void givenANonExistentId_whenCallsGetById_shouldThrowNotFoundException() {
        final var nonExistentId = CastMemberID.from("123");

        final var actualException = assertThrows(NotFoundException.class,
                () -> getCastMemberUseCase.execute(nonExistentId));

        assertEquals("CastMember with ID '123' not found", actualException.getMessage());
    }

}