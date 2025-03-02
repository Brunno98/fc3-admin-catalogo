package br.com.brunno.admin.catalogo.application.category.update;

import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;

public record UpdateCategoryOutput(CategoryId anId) {

    public static UpdateCategoryOutput from(Category aCategory) {
        return new UpdateCategoryOutput(aCategory.getId());
    }

}
