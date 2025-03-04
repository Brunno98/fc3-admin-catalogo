package br.com.brunno.admin.catalogo.infrastructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCastMemberRequest(
        @JsonProperty("name") String name,
        @JsonProperty("type") String type
) {}
