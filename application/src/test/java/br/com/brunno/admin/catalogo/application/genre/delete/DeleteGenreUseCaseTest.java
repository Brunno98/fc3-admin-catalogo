package br.com.brunno.admin.catalogo.application.genre.delete;

import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteGenreUseCaseTest {

    @InjectMocks
    private DeleteGenreUseCase deleteGenreUseCase;

    @Mock
    private GenreGateway genreGateway;

    @Test
    void givenAValidGenreId_whenCallsDeleteAGenre_thenShouldDeleteGenre() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var aGenreId = aGenre.getId();
        final var aCommand = new DeleteGenreCommand(aGenreId);

        deleteGenreUseCase.execute(aCommand);

        Mockito.verify(genreGateway, Mockito.times(1))
                .deleteById(Mockito.eq(aGenreId));
    }
}
