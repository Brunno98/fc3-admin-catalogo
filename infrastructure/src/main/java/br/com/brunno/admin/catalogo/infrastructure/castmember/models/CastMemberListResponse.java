package br.com.brunno.admin.catalogo.infrastructure.castmember.models;

import br.com.brunno.admin.catalogo.application.castmember.retrieve.list.CastMemberListOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CastMemberListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
    public static CastMemberListResponse from(CastMemberListOutput castMemberListOutput) {
        return new CastMemberListResponse(
                castMemberListOutput.id().getValue(),
                castMemberListOutput.name(),
                castMemberListOutput.type().name(),
                castMemberListOutput.createdAt(),
                castMemberListOutput.updatedAt()
        );
    }
}
