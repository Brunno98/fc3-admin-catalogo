package br.com.brunno.admin.catalogo.application.genre.update;

import br.com.brunno.admin.catalogo.domain.UnitTest;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

@UnitTest
@ExtendWith(MockitoExtension.class)
class UpdateGenreUseCaseTest {

    @InjectMocks
    private UpdateGenreUseCase updateGenreUseCase;
    @Mock
    private CategoryGateway categoryGateway;
    @Mock
    private GenreGateway genreGateway;

    @BeforeEach
    void resetMock() {
        Mockito.reset(categoryGateway, genreGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateAGenre_thenShouldReturnAGenreId() {
        final var aGenre = Genre.newGenre("acao", true);

        final var expectedId = aGenre.getId();
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategoryIds = List.<String>of();

        final var aCommand = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                expectedCategoryIds
        );

        Mockito.when(genreGateway.findById(any()))
                .thenReturn(Optional.of(aGenre));

        Mockito.when(genreGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = updateGenreUseCase.execute(aCommand);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        Mockito.verify(genreGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(genreGateway, times(1)).update(argThat(genre -> Objects.nonNull(genre.getId()) &&
                Objects.equals(genre.getName(), expectedName) &&
                genre.isActive() &&
                genre.getCategories().isEmpty()
        ));
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsUpdateAGenre_thenShouldReturnAGenreId() {
        final var aGenre = Genre.newGenre("acao", true);

        final var expectedId = aGenre.getId();
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategoryIds = List.of(CategoryId.from("123"), CategoryId.from("456"));

        final var aCommand = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                expectedCategoryIds.stream().map(CategoryId::getValue).toList()
        );

        Mockito.when(genreGateway.findById(any()))
                .thenReturn(Optional.of(aGenre));

        Mockito.doReturn(expectedCategoryIds)
                .when(categoryGateway).existisByIds(any());

        Mockito.when(genreGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = updateGenreUseCase.execute(aCommand);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        Mockito.verify(genreGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).existisByIds(eq(expectedCategoryIds));
        Mockito.verify(genreGateway, times(1)).update(argThat(genre -> Objects.nonNull(genre.getId()) &&
                Objects.equals(genre.getName(), expectedName) &&
                genre.isActive() &&
                genre.getCategories().size() == expectedCategoryIds.size()
        ));
    }

    @Test
    public void givenAnInvalidNullName_whenUpdatesAGenre_shouldReturnNotificationError() {
        final var aGenre = Genre.newGenre("acao", true);
        final var genreId = aGenre.getId();
        final String nullName = null;
        final var aCommand = UpdateGenreCommand.with(genreId.getValue(), nullName, true, List.<String>of());
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessage = "'name' should not be null";

        doReturn(Optional.of(aGenre)).when(genreGateway).findById(genreId);

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyName_whenUpdatesAGenre_shouldReturnNotificationError() {
        final var aGenre = Genre.newGenre("acao", true);
        final var genreId = aGenre.getId();
        final var emptyName = "";
        final var aCommand = UpdateGenreCommand.with(genreId.getValue(), emptyName, true, List.<String>of());
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessage = "'name' should not be blank";

        doReturn(Optional.of(aGenre)).when(genreGateway).findById(genreId);

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameGreaterThan255_whenUpdatesAGenre_shouldReturnNotificationError() {
        final var aGenre = Genre.newGenre("acao", true);
        final var genreId = aGenre.getId();
        final var invalidName = "a".repeat(256);
        final var aCommand = UpdateGenreCommand.with(genreId.getValue(), invalidName, true, List.<String>of());
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessage = "'name' length should be between 1 and 255";

        doReturn(Optional.of(aGenre)).when(genreGateway).findById(genreId);

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(1, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsUpdateAGenreWithSomeCategoriesThatNotExists_shouldReturnNotificationError() {
        final var aGenre = Genre.newGenre("acao", true);
        final var genreId = aGenre.getId();
        final String nullName = null;
        final var isActive = true;
        final var categoryIds = List.of("123", "456", "nonExistentID");
        final var expectedGeneralErrorMessage = "Could not update a Genre";
        final var expectedValidationErroMessageOne = "Some category IDs could not be found: nonExistentID";
        final var expectedValidationErroMessageTwo = "'name' should not be null";;
        final var aCommand = UpdateGenreCommand.with(genreId.getValue(), nullName, isActive, categoryIds);

        doReturn(Optional.of(aGenre)).when(genreGateway).findById(genreId);

        Mockito.doReturn(List.of(CategoryId.from("123"),CategoryId.from("456")))
                .when(categoryGateway).existisByIds(any());

        final var actualException = assertThrows(NotificationException.class, () -> updateGenreUseCase.execute(aCommand));

        assertEquals(expectedGeneralErrorMessage, actualException.getMessage());
        assertEquals(2, actualException.getErrors().size());
        assertEquals(expectedValidationErroMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedValidationErroMessageTwo, actualException.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1))
                .existisByIds(argThat(list -> list.size() == categoryIds.size()));
    }
}