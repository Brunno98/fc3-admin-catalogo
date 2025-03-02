package br.com.brunno.admin.catalogo.infrastructure.category.models;

import br.com.brunno.admin.catalogo.application.category.retrive.list.CategoryListOutput;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ListCategoriesAPIResponse(
        @JsonProperty("current_page") int currentPage,
        @JsonProperty("per_page") int perPage,
        @JsonProperty("total") long total,
        @JsonProperty("categories") List<CategoryAPIOutput> categories
) {
    public static ListCategoriesAPIResponse from(Pagination<CategoryListOutput> pagination) {
        return new ListCategoriesAPIResponse(
                pagination.currentPage(),
                pagination.perPage(),
                pagination.total(),
                pagination.items().stream()
                        .map(CategoryAPIOutput::from)
                        .toList()
        );
    }
}
