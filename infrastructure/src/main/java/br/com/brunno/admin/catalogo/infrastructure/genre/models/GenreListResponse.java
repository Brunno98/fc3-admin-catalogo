package br.com.brunno.admin.catalogo.infrastructure.genre.models;

import br.com.brunno.admin.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GenreListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("is_active") boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
    public static GenreListResponse from(GenreListOutput genreListOutput) {
        return new GenreListResponse(
                genreListOutput.id(),
                genreListOutput.name(),
                genreListOutput.isActive(),
                genreListOutput.createdAt(),
                genreListOutput.deletedAt()
        );
    }
}
