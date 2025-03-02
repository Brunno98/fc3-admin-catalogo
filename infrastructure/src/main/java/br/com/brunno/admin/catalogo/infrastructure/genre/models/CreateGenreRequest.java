package br.com.brunno.admin.catalogo.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CreateGenreRequest(
        @JsonProperty("name") String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") boolean active
) {

    public List<String> categories() {
        return Objects.isNull(this.categories) ? new ArrayList<>() : this.categories;
    }
}
