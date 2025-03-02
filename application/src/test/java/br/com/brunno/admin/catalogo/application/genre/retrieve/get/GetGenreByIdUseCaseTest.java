package br.com.brunno.admin.catalogo.application.genre.retrieve.get;

import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class GetGenreByIdUseCaseTest {

    @InjectMocks
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @Mock
    private GenreGateway genreGateway;

    @Test
    void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryId.from("123"),
                CategoryId.from("456")
        );

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive, expectedCategories);

        Mockito.when(genreGateway.findById(any()))
                .thenReturn(Optional.of(aGenre));

        final var actualGenre = getGenreByIdUseCase.execute(aGenre.getId());

        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(toString(expectedCategories), actualGenre.categories());
        assertEquals(aGenre.getCreatedAt(), actualGenre.createdAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.updatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.deletedAt());
    }

    private List<String> toString(List<CategoryId> ids) {
        return ids.stream().map(CategoryId::getValue).collect(Collectors.toList());
    }

    @Test
    void givenAValidId_whenCallsGetGenreThatDoesNotExists_shouldThrowsNotFound() {
        final var expectedErrorMessage = "Genre with ID 'non-existent-id' not found";

        Mockito.when(genreGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var actualException = assertThrows(NotFoundException.class,
                () -> getGenreByIdUseCase.execute(GenreID.from("non-existent-id")));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
