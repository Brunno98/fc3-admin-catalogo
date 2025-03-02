package br.com.brunno.admin.catalogo.infrastructure.genre.models;

import br.com.brunno.admin.catalogo.application.genre.retrieve.get.GenreOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GenreResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
    public static GenreResponse from(GenreOutput genreOutput) {
        return new GenreResponse(
                genreOutput.id(),
                genreOutput.name(),
                genreOutput.categories(),
                genreOutput.isActive(),
                genreOutput.createdAt(),
                genreOutput.updatedAt(),
                genreOutput.deletedAt()
        );
    }
}
