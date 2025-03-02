package br.com.brunno.admin.catalogo.application.category.retrive.list;

import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;

import java.time.Instant;
import java.util.Optional;

public record CategoryListOutput(
        CategoryId id,
        String name,
        String description,
        boolean isActive,
        Instant createdDate,
        Instant updatedDate,
        Optional<Instant> deletedAt
) {

    public static CategoryListOutput from(Category aCategory) {
        return new CategoryListOutput(
                aCategory.getId(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.isActive(),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt(),
                Optional.ofNullable(aCategory.getDeletedAt())
        );
    }
}
