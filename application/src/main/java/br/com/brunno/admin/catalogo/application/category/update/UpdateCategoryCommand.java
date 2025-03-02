package br.com.brunno.admin.catalogo.application.category.update;

import br.com.brunno.admin.catalogo.domain.category.CategoryId;

public record UpdateCategoryCommand(
        CategoryId categoryId,
        String name,
        String description,
        boolean active
) {}
