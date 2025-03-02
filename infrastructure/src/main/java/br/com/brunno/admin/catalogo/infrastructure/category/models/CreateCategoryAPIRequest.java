package br.com.brunno.admin.catalogo.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCategoryAPIRequest(
    @JsonProperty(value = "name") String name,
    @JsonProperty(value = "description") String description,
    @JsonProperty(value = "is_active") Boolean active
) {

    public static CreateCategoryAPIRequest from(String name, String description, boolean active) {
        return new CreateCategoryAPIRequest(name, description, active);
    }

}
