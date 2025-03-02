package br.com.brunno.admin.catalogo.application.category.retrive.list;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;

public class ListCategoriesUseCase extends UseCase<SearchQuery, Pagination<CategoryListOutput>> {

    private final CategoryGateway categoryGateway;

    public ListCategoriesUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public Pagination<CategoryListOutput> execute(SearchQuery anIn) {
        return this.categoryGateway.findAll(anIn)
                .map(CategoryListOutput::from);
    }
}
