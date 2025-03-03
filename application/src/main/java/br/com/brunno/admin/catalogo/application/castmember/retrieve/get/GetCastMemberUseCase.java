package br.com.brunno.admin.catalogo.application.castmember.retrieve.get;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;

public class GetCastMemberUseCase extends UseCase<CastMemberID, GetCastMemberOutput> {

    private final CastMemberGateway castMemberGateway;

    public GetCastMemberUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = castMemberGateway;
    }

    @Override
    public GetCastMemberOutput execute(CastMemberID anIn) {
        final var castMember = this.castMemberGateway.findById(anIn)
                .orElseThrow(() -> NotFoundException.with(CastMember.class, anIn));

        return GetCastMemberOutput.from(castMember);
    }
}
