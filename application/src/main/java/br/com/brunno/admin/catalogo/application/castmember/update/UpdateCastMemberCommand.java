package br.com.brunno.admin.catalogo.application.castmember.update;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;

public record UpdateCastMemberCommand(
        CastMemberID id,
        String name,
        CastMemberType type
) {

    public static UpdateCastMemberCommand with(CastMemberID id, String name, CastMemberType type) {
        return new UpdateCastMemberCommand(id, name, type);
    }
}
