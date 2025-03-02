package br.com.brunno.admin.catalogo.domain.category;

import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;

import java.util.List;
import java.util.Optional;

public interface CategoryGateway {

    Category create(Category aCategory);

    void deleteById(CategoryId anId);

    Optional<Category> findById(CategoryId anId);

    Category update(Category update);

    Pagination<Category> findAll(SearchQuery aQuery);

    List<CategoryId> existisByIds(List<CategoryId> ids);
}
