package br.com.brunno.admin.catalogo.application.castmember.delete;

import br.com.brunno.admin.catalogo.application.UnitUseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;

public class DeleteCastMemberUseCase extends UnitUseCase<CastMemberID> {

    private final CastMemberGateway castMemberGateway;

    public DeleteCastMemberUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = castMemberGateway;
    }

    @Override
    public void execute(CastMemberID anId) {
        this.castMemberGateway.deleteById(anId);
    }
}
