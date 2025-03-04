package br.com.brunno.admin.catalogo.infrastructure.castmember.models;

import br.com.brunno.admin.catalogo.application.castmember.retrieve.get.GetCastMemberOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CastMemberResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
    public static CastMemberResponse from(GetCastMemberOutput out) {
        return new CastMemberResponse(
                out.id().getValue(),
                out.name(),
                out.type().name(),
                out.createdAt(),
                out.updatedAt()
        );
    }
}
