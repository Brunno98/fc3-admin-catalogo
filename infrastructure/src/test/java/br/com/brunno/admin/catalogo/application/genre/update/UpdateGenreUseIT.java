package br.com.brunno.admin.catalogo.application.genre.update;

import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class UpdateGenreUseIT {

    @Autowired
    private UpdateGenreUseCase updateGenreUseCase;
    @Autowired
    private CategoryGateway categoryGateway;
    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidCommand_whenCallsUpdateAGenre_thenShouldReturnAGenreId() {
        final var aGenre =
                genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("acao", true)));

        final var expectedId = aGenre.getId();
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategoryIds = List.<String>of();

        final var aCommand = UpdateGenreCommand.with(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCategoryIds
        );

        final var actualOutput = updateGenreUseCase.execute(aCommand);

        assertNotNull(actualOutput);
        assertEquals(expectedId, actualOutput.id());

        final var actualGenre = genreRepository.findById(expectedId).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(actualGenre.getCategories().isEmpty());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsUpdateAGenre_thenShouldReturnAGenreId() {
        final var aGenre =
                genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("acao", true)));

        final var filmes =
                categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series =
                categoryGateway.create(Category.newCategory("Séries", null, true));

        final var expectedId = aGenre.getId();
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategoryIds = List.of(filmes.getId(), series.getId());

        final var aCommand = UpdateGenreCommand.with(
                expectedId,
                expectedName,
                expectedIsActive,
                asString(expectedCategoryIds)
        );

        final var actualOutput = updateGenreUseCase.execute(aCommand);

        assertNotNull(actualOutput);
        assertEquals(expectedId, actualOutput.id());

        final var actualGenre = genreRepository.findById(expectedId).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertTrue(expectedCategoryIds.size() == actualGenre.getCategories().size()
            && expectedCategoryIds.containsAll(actualGenre.getCategoriesIDs()));
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInvalidNullName_whenUpdatesAGenre_shouldReturnNotificationError() {
        final var aGenre =
                genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("acao", true)));
        final var genreId = aGenre.getId();
        final String nullName = null;
        final var aCommand = UpdateGenreCommand.with(genreId, nullName, true, List.<String>of());
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessage = "'name' should not be null";

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());

        final var actualGenre = genreRepository.findById(genreId).get();

        assertEquals(aGenre.getName(), actualGenre.getName());
        assertEquals(aGenre.isActive(), actualGenre.isActive());
        assertTrue(actualGenre.getCategories().isEmpty());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void givenAnInvalidEmptyName_whenUpdatesAGenre_shouldReturnNotificationError() {
        final var aGenre =
                genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("acao", true)));
        final var genreId = aGenre.getId();
        final var emptyName = "";
        final var aCommand = UpdateGenreCommand.with(genreId, emptyName, true, List.<String>of());
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessage = "'name' should not be blank";

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());

        final var actualGenre = genreRepository.findById(genreId).get();

        assertEquals(aGenre.getName(), actualGenre.getName());
        assertEquals(aGenre.isActive(), actualGenre.isActive());
        assertTrue(actualGenre.getCategories().isEmpty());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void givenAnInvalidNameGreaterThan255_whenUpdatesAGenre_shouldReturnNotificationError() {
        final var aGenre =
                genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("acao", true)));
        final var genreId = aGenre.getId();
        final var invalidName = "a".repeat(256);
        final var aCommand = UpdateGenreCommand.with(genreId, invalidName, true, List.<String>of());
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessage = "'name' length should be between 1 and 255";

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());

        final var actualGenre = genreRepository.findById(genreId).get();

        assertEquals(aGenre.getName(), actualGenre.getName());
        assertEquals(aGenre.isActive(), actualGenre.isActive());
        assertTrue(actualGenre.getCategories().isEmpty());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsUpdateAGenreWithSomeCategoriesThatNotExists_shouldReturnNotificationError() {
        final var aGenre =
                genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("acao", true)));
        final var filmes =
                categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series =
                categoryGateway.create(Category.newCategory("Séries", null, true));
        final var genreId = aGenre.getId();
        final String nullName = null;
        final var isActive = true;
        final var categoryIds = List.of(filmes.getId().getValue(), series.getId().getValue(), "nonExistentID");
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessageOne = "Some category IDs could not be found: nonExistentID";
        final var expectedValidationErroMessageTwo = "'name' should not be null";
        final var aCommand = UpdateGenreCommand.with(genreId, nullName, isActive, categoryIds);

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(2, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedValidationErroMessageTwo, actualException.getErrors().get(1).message());

        final var actualGenre = genreRepository.findById(genreId).get();

        assertEquals(aGenre.getName(), actualGenre.getName());
        assertEquals(aGenre.isActive(), actualGenre.isActive());
        assertTrue(actualGenre.getCategories().isEmpty());
        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());

    }

    private List<String> asString(List<CategoryId> ids) {
        return ids.stream().map(CategoryId::getValue).toList();
    }
}
