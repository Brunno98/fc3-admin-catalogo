package br.com.brunno.admin.catalogo.application.castmember.update;

import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;

public record UpdateCastMemberOutput(CastMemberID id) {

    public static UpdateCastMemberOutput from(CastMember castMember) {
        return new UpdateCastMemberOutput(castMember.getId());
    }

}
