package br.com.brunno.admin.catalogo.application.genre.create;

import br.com.brunno.admin.catalogo.domain.UnitTest;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

@UnitTest
@ExtendWith(MockitoExtension.class)
class CreateGenreUseCaseTest {

    @InjectMocks
    private CreateGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsCreateAGenre_thenShouldReturnAGenreId() {
        final var aName = "Action";
        final var isActive = true;
        final var categoryIds = List.<String>of();
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        Mockito.when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var createGenreOutput = useCase.execute(aCommand);

        assertNotNull(createGenreOutput);
        assertNotNull(createGenreOutput.id());
        Mockito.verify(genreGateway, Mockito.times(1))
                .create(Mockito.argThat(genre -> Objects.equals(genre.getName(), aName) &&
                        genre.isActive() == isActive &&
                        genre.getCreatedAt() != null &&
                        genre.getUpdatedAt() != null &&
                        genre.getCategories().isEmpty()));
    }

    @Test
    public void givenAValidCommandWithCategoriesIds_whenCallsCreateAGenre_shouldReturnGenreID() {
        final var aName = "Action";
        final var isActive = true;
        final var categoryIds = List.of(
                CategoryId.from("123"),
                CategoryId.from("456")
        );
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds.stream().map(CategoryId::toString).toList());

        Mockito.doReturn(categoryIds).when(categoryGateway).existisByIds(any());
        Mockito.when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var createGenreOutput = useCase.execute(aCommand);

        assertNotNull(createGenreOutput);
        assertNotNull(createGenreOutput.id());

        Mockito.verify(genreGateway, Mockito.times(1))
                .create(Mockito.argThat(genre -> Objects.equals(genre.getName(), aName) &&
                        genre.isActive() == isActive &&
                        genre.getCreatedAt() != null &&
                        genre.getUpdatedAt() != null &&
                        genre.getCategories().size() == 2));
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
        final String aName = "Ação";
        final var isActive = true;
        final var categoryIds = List.of("123", "456", "nonExistentID");
        final var expectedGeneralErrorMessage = "Could not create a Genre";
        final var expectedValidationErroMessage = "Some category IDs could not be found: nonExistentID";
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        Mockito.doReturn(List.of(CategoryId.from("123"),CategoryId.from("456")))
                .when(categoryGateway).existisByIds(any());

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1))
                .existisByIds(argThat(list -> list.size() == categoryIds.size()));
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateAGenreWithSomeCategoriesThatNotExists_shouldReturnNotificationError() {
        final String aName = null;
        final var isActive = true;
        final var categoryIds = List.of("123", "456", "nonExistentID");
        final var expectedGeneralErrorMessage = "Could not create a Genre";
        final var expectedValidationErroMessageOne = "Some category IDs could not be found: nonExistentID";
        final var expectedValidationErroMessageTwo = "'name' should not be null";;
        final var aCommand = CreateGenreCommand.with(aName, isActive, categoryIds);

        Mockito.doReturn(List.of(CategoryId.from("123"),CategoryId.from("456")))
                .when(categoryGateway).existisByIds(any());

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(2, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedValidationErroMessageTwo, actualException.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1))
                .existisByIds(argThat(list -> list.size() == categoryIds.size()));
    }
}