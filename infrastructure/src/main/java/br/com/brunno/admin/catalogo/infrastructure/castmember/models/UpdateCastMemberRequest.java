package br.com.brunno.admin.catalogo.infrastructure.castmember.models;

public record UpdateCastMemberRequest(
        String name,
        String type
) {}
