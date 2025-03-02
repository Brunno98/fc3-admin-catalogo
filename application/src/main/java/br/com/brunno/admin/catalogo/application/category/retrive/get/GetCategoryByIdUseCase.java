package br.com.brunno.admin.catalogo.application.category.retrive.get;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.DomainException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.validation.Error;

public class GetCategoryByIdUseCase extends UseCase<CategoryId, GetCategoryByIdOutput> {

    private final CategoryGateway categoryGateway;

    public GetCategoryByIdUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public GetCategoryByIdOutput execute(CategoryId anIn) {
        final var aCategory = categoryGateway.findById(anIn)
                .orElseThrow(() -> NotFoundException.with(Category.class, anIn));

        return GetCategoryByIdOutput.from(aCategory);
    }
}
