package br.com.brunno.admin.catalogo.application.castmember.create;

import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;

public record CreateCastMemberOutput(CastMemberID id) {

    public static CreateCastMemberOutput from(CastMember aCastMember) {
        return new CreateCastMemberOutput(aCastMember.getId());
    }

}
