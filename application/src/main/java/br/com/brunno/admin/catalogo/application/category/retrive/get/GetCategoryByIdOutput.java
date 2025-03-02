package br.com.brunno.admin.catalogo.application.category.retrive.get;

import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;

import java.time.Instant;
import java.util.Optional;

public record GetCategoryByIdOutput(
        CategoryId id,
        String name,
        String description,
        boolean isActive,
        Instant createdDate,
        Instant updatedDate,
        Optional<Instant> deletedAt
) {

    public static GetCategoryByIdOutput from(Category aCategory) {
        return new GetCategoryByIdOutput(
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
