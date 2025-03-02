package br.com.brunno.admin.catalogo.infrastructure.genre.persistence;

import br.com.brunno.admin.catalogo.MySQLGatewayTest;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.category.persistence.CategoryMySQLGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {

    @Autowired
    CategoryMySQLGateway categoryGateway;

    @Autowired
    GenreMySQLGateway genreGateway;

    @Autowired
    GenreRepository genreRepository;

    @Test
    void test() {
        Assertions.assertNotNull(categoryGateway);
        Assertions.assertNotNull(genreGateway);
        Assertions.assertNotNull(genreRepository);
    }

    @Test
    void givenAValidGenre_whenCallsCreateAGenre_shouldPersistTheGenre() {
        final var filmes = Category.newCategory("Filmes", null, true);

        categoryGateway.create(filmes);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());
        final var aGenre = Genre.newGenre(expectedName, expectedIsActive, expectedCategories);
        final var expectedId = aGenre.getId();

        assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.create(aGenre);

        assertEquals(1, genreRepository.count());


        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var genreJpa = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), genreJpa.getId());
        assertEquals(expectedName, genreJpa.getName());
        assertEquals(expectedIsActive, genreJpa.isActive());
        assertEquals(expectedCategories, genreJpa.getCategoriesIDs());
        assertEquals(aGenre.getCreatedAt(), genreJpa.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), genreJpa.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), genreJpa.getDeletedAt());
        assertNull(genreJpa.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithoutCategories_whenCallsCreateAGenre_shouldPersistTheGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryId>of();
        final var aGenre = Genre.newGenre(expectedName, expectedIsActive, expectedCategories);
        final var expectedId = aGenre.getId();

        assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.create(aGenre);

        assertEquals(1, genreRepository.count());


        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var genreJpa = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), genreJpa.getId());
        assertEquals(expectedName, genreJpa.getName());
        assertEquals(expectedIsActive, genreJpa.isActive());
        assertEquals(expectedCategories, genreJpa.getCategoriesIDs());
        assertEquals(aGenre.getCreatedAt(), genreJpa.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), genreJpa.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), genreJpa.getDeletedAt());
        assertNull(genreJpa.getDeletedAt());
    }

    @Test
    void givenAValidGenre_whenCallsUpdateAGenre_shouldUpdateTheGenre() {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);

        categoryGateway.create(filmes);
        categoryGateway.create(series);


        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());
        final var aGenre = Genre.newGenre("ac", false, List.of());
        final var expectedId = aGenre.getId();

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        final var actualGenre = genreGateway.update(
            Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories)
        );

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertIterableEquals(sorted(expectedCategories), sorted(actualGenre.getCategories()));
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());

        final var genreJpa = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), genreJpa.getId());
        assertEquals(expectedName, genreJpa.getName());
        assertEquals(expectedIsActive, genreJpa.isActive());
        assertIterableEquals(sorted(expectedCategories), sorted(genreJpa.getCategoriesIDs()));
        assertEquals(aGenre.getCreatedAt(), genreJpa.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(genreJpa.getUpdatedAt()));
        assertNull(genreJpa.getDeletedAt());
    }

    @Test
    void givenAPrePersistedGenre_whenCallsDeleteById_shouldDeleteGenre() {
        final var aGenre = Genre.newGenre("Filmes", true);
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertEquals(1, genreRepository.count());

        genreGateway.deleteById(aGenre.getId());

        assertEquals(0, genreRepository.count());
    }

    @Test
    void givenAPrePersistedGenre_whenCallsFindById_shouldReturnGenre() {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        categoryGateway.create(filmes);
        categoryGateway.create(series);

        final var aGenre = Genre.newGenre("Filmes", true, List.of(filmes.getId(), series.getId()));
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        assertEquals(1, genreRepository.count());

        final var actualGenre = genreGateway.findById(aGenre.getId()).get();

        assertNotNull(actualGenre);
        assertEquals(aGenre.getName(), actualGenre.getName());
        assertEquals(aGenre.isActive(), actualGenre.isActive());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        assertEquals(sorted(aGenre.getCategories()), sorted(actualGenre.getCategories()));
    }

    @Test
    void givenAnNonExistentGenreId_whenCallsFindById_shouldReturnEmpty() {
        assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.findById(GenreID.from("non-existent"));

        assertTrue(actualGenre.isEmpty());
    }

    @Test
    void givenAnEmptyGenre_whenCallsFindAll_shouldReturnEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "aç,0,10,1,1,Ação",
            "dr,0,10,1,1,Drama",
            "rom,0,10,1,1,Comédia romântica",
            "cien,0,10,1,1,Ficção científica",
            "ter,0,10,1,1,Terror"
    })
    void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final int expectedTotal,
        final String expectedGenreName
    ) {
        genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Ação", true)),
                GenreJpaEntity.from(Genre.newGenre("Drama", true)),
                GenreJpaEntity.from(Genre.newGenre("Comédia romântica", true)),
                GenreJpaEntity.from(Genre.newGenre("Ficção científica", true)),
                GenreJpaEntity.from(Genre.newGenre("Terror", true))
        ));

        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedGenreName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Ação",
            "name,desc,0,10,5,5,Terror",
            "createdAt,asc,0,10,5,5,Comédia romântica",
            "createdAt,desc,0,10,5,5,Ficção científica",
    })
    void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnFiltered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedGenreName
    ) {
        genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Comédia romântica", true)),
                GenreJpaEntity.from(Genre.newGenre("Ação", true)),
                GenreJpaEntity.from(Genre.newGenre("Drama", true)),
                GenreJpaEntity.from(Genre.newGenre("Terror", true)),
                GenreJpaEntity.from(Genre.newGenre("Ficção científica", true))
        ));

        final var expectedTerms = "";
        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedGenreName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Ação;Comédia romântica",
            "1,2,2,5,Drama;Ficção científica",
            "2,2,1,5,Terror",
    })
    void givenAValidPagination_whenCallsFindAll_shouldReturnFiltered(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedGenres
    ) {
        genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Comédia romântica", true)),
                GenreJpaEntity.from(Genre.newGenre("Ação", true)),
                GenreJpaEntity.from(Genre.newGenre("Drama", true)),
                GenreJpaEntity.from(Genre.newGenre("Terror", true)),
                GenreJpaEntity.from(Genre.newGenre("Ficção científica", true))
        ));

        final var expectedTerms = "";
        final var expectedSort = "name";
        final String expectedDirection = "asc";
        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = genreGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedItemsCount, actualResult.items().size());
        var index = 0;
        for (String expectedGenreName : expectedGenres.split(";")) {
            assertEquals(expectedGenreName, actualResult.items().get(index++).getName());
        }
    }

    private List<CategoryId> sorted(List<CategoryId> categories) {
        return categories.stream()
                .sorted(Comparator.comparing(CategoryId::getValue))
                .toList();
    }

}
