package br.com.brunno.admin.catalogo.application.genre.retrieve.get;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;

public class GetGenreByIdUseCase extends UseCase<GenreID, GenreOutput> {

    private final GenreGateway genreGateway;

    public GetGenreByIdUseCase(GenreGateway genreGateway) {
        this.genreGateway = genreGateway;
    }

    @Override
    public GenreOutput execute(GenreID anId) {
        final var aGenre = this.genreGateway.findById(anId)
                .orElseThrow(() -> NotFoundException.with(Genre.class, anId));

        return GenreOutput.from(aGenre);
    }


}
