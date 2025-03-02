package br.com.brunno.admin.catalogo.application.category.create;

import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;

public record CreateCategoryOutput(String id) {

    public static CreateCategoryOutput from(Category aCategory) {
        return CreateCategoryOutput.from(aCategory.getId());
    }

    public static CreateCategoryOutput from(CategoryId anId) {
        return new CreateCategoryOutput(anId.getValue());
    }
}
