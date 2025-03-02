package br.com.brunno.admin.catalogo.infrastructure.api.controllers;

import br.com.brunno.admin.catalogo.application.category.create.CreateCategoryCommand;
import br.com.brunno.admin.catalogo.application.category.create.CreateCategoryUseCase;
import br.com.brunno.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import br.com.brunno.admin.catalogo.application.category.retrive.get.GetCategoryByIdUseCase;
import br.com.brunno.admin.catalogo.application.category.retrive.list.ListCategoriesUseCase;
import br.com.brunno.admin.catalogo.application.category.update.UpdateCategoryCommand;
import br.com.brunno.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.api.CategoryAPI;
import br.com.brunno.admin.catalogo.infrastructure.category.models.CategoryAPIOutput;
import br.com.brunno.admin.catalogo.infrastructure.category.models.CreateCategoryAPIRequest;
import br.com.brunno.admin.catalogo.infrastructure.category.models.ListCategoriesAPIResponse;
import br.com.brunno.admin.catalogo.infrastructure.category.models.UpdateCategoryAPIRequest;
import br.com.brunno.admin.catalogo.infrastructure.category.models.UpdateCategoryAPIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase, GetCategoryByIdUseCase getCategoryByIdUseCase, ListCategoriesUseCase listCategoriesUseCase, UpdateCategoryUseCase updateCategoryUseCase, DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getCategoryByIdUseCase = getCategoryByIdUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryAPIRequest createCategoryAPIRequest) {
        var createCommand = CreateCategoryCommand.with(
                createCategoryAPIRequest.name(),
                createCategoryAPIRequest.description(),
                createCategoryAPIRequest.active() != null && createCategoryAPIRequest.active()
        );
        return createCategoryUseCase.execute(createCommand)
                .fold(anError -> ResponseEntity.unprocessableEntity().body(anError),
                        createdCategoryOutput -> ResponseEntity
                                .created(URI.create("/categories/" + createdCategoryOutput.id()))
                                .body(createdCategoryOutput));
    }

    @Override
    public ListCategoriesAPIResponse listCategories(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction
    ) {
        final var searchQuery = new SearchQuery(
                page,
                perPage,
                search,
                sort,
                direction
        );
        return ListCategoriesAPIResponse.from(this.listCategoriesUseCase.execute(searchQuery));
    }

    @Override
    public CategoryAPIOutput getById(String id) {
        final var getCategoryByIdOutput = getCategoryByIdUseCase.execute(CategoryId.from(id));
        return CategoryAPIOutput.from(getCategoryByIdOutput);
    }

    @Override
    public ResponseEntity<?> updateCategory(String id, UpdateCategoryAPIRequest createCategoryAPIRequest) {
        UpdateCategoryCommand updateCategoryCommand = new UpdateCategoryCommand(
                CategoryId.from(id),
                createCategoryAPIRequest.name(),
                createCategoryAPIRequest.description(),
                createCategoryAPIRequest.active() != null && createCategoryAPIRequest.active()
        );
        return this.updateCategoryUseCase.execute(updateCategoryCommand)
                .fold(anError -> ResponseEntity.unprocessableEntity().body(anError),
                        updateCategoryOutput -> ResponseEntity.ok(UpdateCategoryAPIResponse.from(updateCategoryOutput)));
    }

    @Override
    public ResponseEntity<Void> deleteCategory(String id) {
        deleteCategoryUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}
