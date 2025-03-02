package br.com.brunno.admin.catalogo.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCategoryAPIRequest(
        @JsonProperty(value = "name") String name,
        @JsonProperty(value = "description") String description,
        @JsonProperty(value = "is_active") Boolean active
) {}
