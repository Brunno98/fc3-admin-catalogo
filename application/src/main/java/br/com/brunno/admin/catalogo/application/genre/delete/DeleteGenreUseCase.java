package br.com.brunno.admin.catalogo.application.genre.delete;

import br.com.brunno.admin.catalogo.application.UnitUseCase;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;

public class DeleteGenreUseCase extends UnitUseCase<DeleteGenreCommand> {

    private final GenreGateway genreGateway;

    public DeleteGenreUseCase(GenreGateway genreGateway) {
        this.genreGateway = genreGateway;
    }

    @Override
    public void execute(DeleteGenreCommand aCommand) {
        this.genreGateway.deleteById(aCommand.anID());
    }

}
