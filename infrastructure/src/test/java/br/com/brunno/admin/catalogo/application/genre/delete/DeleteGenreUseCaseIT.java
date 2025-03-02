package br.com.brunno.admin.catalogo.application.genre.delete;

import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class DeleteGenreUseCaseIT {

    @Autowired
    private DeleteGenreUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAValidGenreId_whenCallsDeleteAGenre_thenShouldDeleteGenre() {
        final var aGenre =
                genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("Ação", true)));
        final var aGenreId = aGenre.getId();
        assertTrue(genreRepository.findById(aGenreId).isPresent());

        final var aCommand = new DeleteGenreCommand(GenreID.from(aGenreId));

        useCase.execute(aCommand);

        assertTrue(genreRepository.findById(aGenreId).isEmpty());
    }

    @Test
    void givenAnInvalidGenreId_whenCallsDeleteAGenre_thenShouldDoNothing() {
        genreRepository.saveAndFlush(GenreJpaEntity.from(Genre.newGenre("Ação", true)));

        assertEquals(1, genreRepository.count());

        final var aCommand = new DeleteGenreCommand(GenreID.from("non-existent"));

        assertDoesNotThrow(() -> useCase.execute(aCommand));

        assertEquals(1, genreRepository.count());
    }

}
