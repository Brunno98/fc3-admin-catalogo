package br.com.brunno.admin.catalogo.application.castmember.delete;

import br.com.brunno.admin.catalogo.Fixture;
import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;

@IntegrationTest
public class DeleteCastMemberUseIT {

    @Autowired
    private DeleteCastMemberUseCase deleteCastMemberUseCase;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCastMemberId_whenCallsDelete_shouldDeleteId() {
        final var aCastMember = CastMember.create(Fixture.name(), Fixture.CastMember.type());
        final var castMemberId = aCastMember.getId();

        castMemberGateway.create(aCastMember);

        assertTrue(castMemberGateway.findById(castMemberId).isPresent());

        deleteCastMemberUseCase.execute(castMemberId);

        assertTrue(castMemberGateway.findById(castMemberId).isEmpty());
    }

    @Test
    void givenANonExistentCastMemberId_whenCallsDelete_shouldDoNothing() {
        final var existentCastMember = CastMember.create(Fixture.name(), Fixture.CastMember.type());
        castMemberGateway.create(existentCastMember);
        assertTrue(castMemberGateway.findById(existentCastMember.getId()).isPresent());

        final var nonExistentId = CastMemberID.from("1234");
        assertTrue(castMemberGateway.findById(nonExistentId).isEmpty());

        deleteCastMemberUseCase.execute(nonExistentId);

        assertTrue(castMemberGateway.findById(nonExistentId).isEmpty());
        assertTrue(castMemberGateway.findById(existentCastMember.getId()).isPresent());
    }

}
