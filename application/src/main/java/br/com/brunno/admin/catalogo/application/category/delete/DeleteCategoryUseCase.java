package br.com.brunno.admin.catalogo.application.category.delete;

import br.com.brunno.admin.catalogo.application.UnitUseCase;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;

public class DeleteCategoryUseCase extends UnitUseCase<String> {

    private final CategoryGateway categoryGateway;

    public DeleteCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public void execute(String anId) {
        categoryGateway.deleteById(CategoryId.from(anId));
    }

}
