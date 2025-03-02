package br.com.brunno.admin.catalogo.infrastructure.category.models;

import br.com.brunno.admin.catalogo.application.category.update.UpdateCategoryOutput;

public record UpdateCategoryAPIResponse(String id) {

    public static UpdateCategoryAPIResponse from(UpdateCategoryOutput updateCategoryOutput) {
        return new UpdateCategoryAPIResponse(updateCategoryOutput.anId().getValue());
    }
}
