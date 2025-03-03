package br.com.brunno.admin.catalogo.application.castmember.create;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;

public record CreateCastMemberCommand(String name, CastMemberType type) {
    public static CreateCastMemberCommand with(String name, CastMemberType castMemberType) {
        return new CreateCastMemberCommand(name, castMemberType);
    }
}
