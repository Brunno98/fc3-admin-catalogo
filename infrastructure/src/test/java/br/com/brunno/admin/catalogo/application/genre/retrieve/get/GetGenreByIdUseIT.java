package br.com.brunno.admin.catalogo.application.genre.retrieve.get;

import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class GetGenreByIdUseIT {

    @Autowired
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                filmes.getId(),
                series.getId()
        );

        final var aGenre =  genreGateway.create(
                Genre.newGenre(expectedName, expectedIsActive, expectedCategories)
        );

        final var actualGenre = getGenreByIdUseCase.execute(aGenre.getId());

        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(expectedCategories.size() == actualGenre.categories().size()
                && asString(expectedCategories).containsAll(actualGenre.categories()));
        assertEquals(aGenre.getCreatedAt(), actualGenre.createdAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.updatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.deletedAt());
    }

    private List<String> asString(List<CategoryId> ids) {
        return ids.stream().map(CategoryId::getValue).collect(Collectors.toList());
    }

}
