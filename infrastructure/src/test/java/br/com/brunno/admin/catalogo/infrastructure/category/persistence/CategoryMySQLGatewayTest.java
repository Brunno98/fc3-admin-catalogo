package br.com.brunno.admin.catalogo.infrastructure.category.persistence;


import br.com.brunno.admin.catalogo.MySQLGatewayTest;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    public CategoryMySQLGateway categoryMySQLGateway;

    @Autowired
    public CategoryRepository categoryRepository;

    @Test
    public void testDependencyInjection() {
        assertNotNull(categoryMySQLGateway);
        assertNotNull(categoryRepository);
    }

    @Test
    void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryMySQLGateway.create(aCategory);

        assertEquals(1, categoryRepository.count());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        assertNull(actualCategory.getDeletedAt());

        final var persistedCategory = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(expectedDescription, persistedCategory.getDescription());
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertEquals(aCategory.getId().getValue(), persistedCategory.getId());
        assertEquals(aCategory.getCreatedAt(), persistedCategory.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), persistedCategory.getUpdatedAt());
        assertNull(persistedCategory.getDeletedAt());
    }

    @Test
    void givenAEmptySearchFilter_whenCallsFindAll_shouldReturnAllCategories() {

        final var expectedPage = 2;
        final var expectedTotalItems = 3;

        final var filmes = Category.newCategory("Filmes", "Categoria de filmes", true);
        final var series = Category.newCategory("Series", "Categoria de séries", true);
        final var documentarios = Category.newCategory("Documentarios", "Categoria de documentarios", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(filmes));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(series));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(documentarios));

        assertEquals(3, categoryRepository.count());

        final var searchQuery = new SearchQuery(expectedPage, 1, "", "name", "asc");
        final var queryResult = categoryMySQLGateway.findAll(searchQuery);

        assertEquals(expectedPage, queryResult.currentPage());
        assertEquals(expectedTotalItems, queryResult.total());
        assertEquals(1, queryResult.items().size());
        assertEquals(series.getId(), queryResult.items().get(0).getId());
    }

    @Test
    void givenASearchFilterByName_whenCallsFindAll_shouldReturnAllCategoriesThatContainsTheTermOnName() {

        final var expectedPage = 0;
        final var expectedTotalItems = 1;

        final var filmes = Category.newCategory("Filmes", "Categoria de filmes", true);
        final var series = Category.newCategory("Series", "Categoria de séries", true);
        final var documentarios = Category.newCategory("Documentarios", "Categoria de documentarios", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(filmes));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(series));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(documentarios));

        assertEquals(3, categoryRepository.count());

        final var searchQuery = new SearchQuery(expectedPage, 1, "doc", "name", "desc");
        final var queryResult = categoryMySQLGateway.findAll(searchQuery);

        assertEquals(expectedPage, queryResult.currentPage());
        assertEquals(expectedTotalItems, queryResult.total());
        assertEquals(1, queryResult.items().size());
        assertEquals(documentarios.getId(), queryResult.items().get(0).getId());
    }

    @Test
    void givenASearchFilterByDescription_whenCallsFindAll_shouldReturnAllCategoriesThatContainsTheTermOnDescription() {

        final var expectedPage = 0;
        final var expectedTotalItems = 1;

        final var filmes = Category.newCategory("Filmes", "Categoria de filmes", true);
        final var series = Category.newCategory("Series", "Categoria de séries XPTO", true);
        final var documentarios = Category.newCategory("Documentarios", "Categoria de documentarios", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(filmes));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(series));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(documentarios));

        assertEquals(3, categoryRepository.count());

        final var searchQuery = new SearchQuery(expectedPage, 1, "xpto", "name", "asc");
        final var queryResult = categoryMySQLGateway.findAll(searchQuery);

        assertEquals(expectedPage, queryResult.currentPage());
        assertEquals(expectedTotalItems, queryResult.total());
        assertEquals(1, queryResult.items().size());
        assertEquals(series.getId(), queryResult.items().get(0).getId());
    }

    @Test
    void givenASearchFilterByNotExistsName_whenCallsFindAll_shouldReturnAnEmptyResultPage() {

        final var expectedPage = 0;
        final var expectedTotalItems = 0;

        final var filmes = Category.newCategory("Filmes", "Categoria de filmes", true);
        final var series = Category.newCategory("Series", "Categoria de séries", true);
        final var documentarios = Category.newCategory("Documentarios", "Categoria de documentarios", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(filmes));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(series));
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(documentarios));

        assertEquals(3, categoryRepository.count());

        final var searchQuery = new SearchQuery(expectedPage, 1, "xpto", "name", "asc");
        final var queryResult = categoryMySQLGateway.findAll(searchQuery);

        assertEquals(expectedPage, queryResult.currentPage());
        assertEquals(0, queryResult.total());
        assertEquals(expectedTotalItems, queryResult.items().size());
    }

    @Test
    void givenACategoriesList_whenCallsExistsByIds_shouldReturnTheExistentIds() {
        final var expectedExistentCategories = 2;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Series", null, true);

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series)
        ));

        final var actualCategoryIds = categoryMySQLGateway
                .existisByIds(List.of(filmes.getId(), series.getId(), CategoryId.from("non-existent")));

        assertEquals(expectedExistentCategories, actualCategoryIds.size());
        assertTrue(actualCategoryIds.contains(filmes.getId()));
        assertTrue(actualCategoryIds.contains(series.getId()));
    }
}