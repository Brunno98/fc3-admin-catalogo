package br.com.brunno.admin.catalogo.infrastructure.category.models;

import br.com.brunno.admin.catalogo.application.category.retrive.get.GetCategoryByIdOutput;
import br.com.brunno.admin.catalogo.application.category.retrive.list.CategoryListOutput;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryAPIOutput(
        @JsonProperty(value = "id") String id,
        @JsonProperty(value = "name") String name,
        @JsonProperty(value = "description") String description,
        @JsonProperty(value = "is_active") Boolean active,
        @JsonProperty(value = "created_at") String createdAt,
        @JsonProperty(value = "updated_at") String updatedAt,
        @JsonProperty(value = "deleted_at") String deletedAt
) {

    public static CategoryAPIOutput from(GetCategoryByIdOutput getCategoryByIdOutput) {
        return new CategoryAPIOutput(
                getCategoryByIdOutput.id().getValue(),
                getCategoryByIdOutput.name(),
                getCategoryByIdOutput.description(),
                getCategoryByIdOutput.isActive(),
                getCategoryByIdOutput.createdDate().toString(),
                getCategoryByIdOutput.updatedDate().toString(),
                getCategoryByIdOutput.deletedAt().map(Instant::toString).orElse(null)
        );
    }


    public static CategoryAPIOutput from(CategoryListOutput category) {
        return new CategoryAPIOutput(
                category.id().getValue(),
                category.name(),
                category.description(),
                category.isActive(),
                category.createdDate().toString(),
                category.updatedDate().toString(),
                category.deletedAt().map(Instant::toString).orElse(null)
        );
    }
}
