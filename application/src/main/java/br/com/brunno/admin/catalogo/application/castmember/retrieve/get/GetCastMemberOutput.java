package br.com.brunno.admin.catalogo.application.castmember.retrieve.get;

import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;

import java.time.Instant;

public record GetCastMemberOutput(
        CastMemberID id,
        String name,
        CastMemberType type,
        Instant createdAt,
        Instant updatedAt
) {

    public static GetCastMemberOutput from(CastMember castMember) {
        return new GetCastMemberOutput(
                castMember.getId(),
                castMember.getName(),
                castMember.getType(),
                castMember.getCreatedAt(),
                castMember.getUpdatedAt()
        );
    }

}
