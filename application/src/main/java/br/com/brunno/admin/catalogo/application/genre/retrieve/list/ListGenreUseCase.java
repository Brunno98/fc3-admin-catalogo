package br.com.brunno.admin.catalogo.application.genre.retrieve.list;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;

public class ListGenreUseCase extends UseCase<SearchQuery, Pagination<GenreListOutput>> {

    private final GenreGateway genreGateway;

    public ListGenreUseCase(GenreGateway genreGateway) {
        this.genreGateway = genreGateway;
    }

    @Override
    public Pagination<GenreListOutput> execute(SearchQuery anIn) {
        return this.genreGateway.findAll(anIn)
                .map(GenreListOutput::from);
    }
}
