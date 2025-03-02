package br.com.brunno.admin.catalogo.application.genre.create;

import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.Identifier;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
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
public class CreateGenreUseCaseIT {

    @Autowired
    private CreateGenreUseCase useCase;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidCommandWithCategories_whenCallsCreateAGenre_thenShouldReturnAGenreId() {
        final var filmes = categoryGateway.create(
                Category.newCategory("Filmes", null, true)
        );

        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());
        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var createGenreOutput = useCase.execute(aCommand);

        assertNotNull(createGenreOutput);
        assertNotNull(createGenreOutput.id());

        final var aGenre = genreRepository.findById(createGenreOutput.id()).get();

        assertEquals(expectedName, aGenre.getName());
        assertEquals(expectedIsActive, aGenre.isActive());
        assertTrue(expectedCategories.size() == aGenre.getCategories().size() &&
                expectedCategories.containsAll(aGenre.getCategoriesIDs()));
        assertNotNull(aGenre.getCreatedAt());
        assertNotNull(aGenre.getUpdatedAt());
        assertNull(aGenre.getDeletedAt());
    }

    @Test
    void givenAValidCommandWithoutCategories_whenCallsCreateAGenre_thenShouldReturnAGenreId() {
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryId>of();
        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var createGenreOutput = useCase.execute(aCommand);

        assertNotNull(createGenreOutput);
        assertNotNull(createGenreOutput.id());

        final var actualGenre = genreRepository.findById(createGenreOutput.id()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(0, actualGenre.getCategories().size());
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsCreateAGenre_thenShouldReturnAGenreId() {
        final var filmes = categoryGateway.create(
                Category.newCategory("Filmes", null, true)
        );

        final var expectedName = "Action";
        final var expectedIsActive = false;
        final var expectedCategories = List.of(filmes.getId());
        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var createGenreOutput = useCase.execute(aCommand);

        assertNotNull(createGenreOutput);
        assertNotNull(createGenreOutput.id());

        final var aGenre = genreRepository.findById(createGenreOutput.id()).get();

        assertEquals(expectedName, aGenre.getName());
        assertEquals(expectedIsActive, aGenre.isActive());
        assertTrue(expectedCategories.size() == aGenre.getCategories().size() &&
                expectedCategories.containsAll(aGenre.getCategoriesIDs()));
        assertNotNull(aGenre.getCreatedAt());
        assertNotNull(aGenre.getUpdatedAt());
        assertNotNull(aGenre.getDeletedAt());
    }

    @Test
    public void givenAnInvalidNullName_whenCreateAGenre_shouldReturnNotificationError() {
        final String aName = null;
        final var isActive = true;
        final var categoryIds = List.<String>of();
        final var expectedGeneralErrorMessage = "Could not create a Genre";
        final var expectedValidationErroMessage = "'name' should not be null";
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCreateAGenre_shouldReturnNotificationError() {
        final String aName = " ";
        final var isActive = true;
        final var categoryIds = List.<String>of();
        final var expectedGeneralErrorMessage = "Could not create a Genre";
        final var expectedValidationErroMessage = "'name' should not be blank";
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameGreaterThan255_whenCreateAGenre_shouldReturnNotificationError() {
        final String aName = "a".repeat(256);
        final var isActive = true;
        final var categoryIds = List.<String>of();
        final var expectedGeneralErrorMessage = "Could not create a Genre";
        final var expectedValidationErroMessage = "'name' length should be between 1 and 255";
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAValidCommand_whenCallsCreateAGenreWithSomeCategoriesThatNotExists_shouldReturnNotificationError() {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);

        categoryGateway.create(filmes);
        categoryGateway.create(series);

        final String aName = "Ação";
        final var isActive = true;
        final var categoryIds = List.of(filmes.getId().getValue(), series.getId().getValue(), "nonExistentID");
        final var expectedGeneralErrorMessage = "Could not create a Genre";
        final var expectedValidationErroMessage = "Some category IDs could not be found: nonExistentID";
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateAGenreWithSomeCategoriesThatNotExists_shouldReturnNotificationError() {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);

        categoryGateway.create(filmes);
        categoryGateway.create(series);

        final String aName = null;
        final var isActive = true;
        final var categoryIds = List.of(filmes.getId().getValue(), series.getId().getValue(), "nonExistentID");
        final var expectedGeneralErrorMessage = "Could not create a Genre";
        final var expectedValidationErroMessageOne = "Some category IDs could not be found: nonExistentID";
        final var expectedValidationErroMessageTwo = "'name' should not be null";;
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(2, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedValidationErroMessageTwo, actualException.getErrors().get(1).message());
    }

    private List<String> asString(List<? extends Identifier> ids) {
        return ids.stream()
                .map(Identifier::getValue)
                .map(Object::toString)
                .toList();
    }

}
