package br.com.brunno.admin.catalogo.application.castmember.delete;

import br.com.brunno.admin.catalogo.domain.UnitTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class DeleteCastMemberUseCaseTest {

    @InjectMocks
    private DeleteCastMemberUseCase deleteCastMemberUseCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCastMemberId_whenCallsDelete_shouldDeleteId() {
        final var castMemberId = CastMemberID.from("123");

        deleteCastMemberUseCase.execute(castMemberId);

        Mockito.verify(castMemberGateway).deleteById(argThat(it ->
                Objects.equals(it, castMemberId)));
    }

}
