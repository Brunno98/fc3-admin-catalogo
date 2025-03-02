package br.com.brunno.admin.catalogo.infrastructure.api.controllers;

import br.com.brunno.admin.catalogo.application.genre.create.CreateGenreCommand;
import br.com.brunno.admin.catalogo.application.genre.create.CreateGenreOutput;
import br.com.brunno.admin.catalogo.application.genre.create.CreateGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.delete.DeleteGenreCommand;
import br.com.brunno.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.retrieve.get.GenreOutput;
import br.com.brunno.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import br.com.brunno.admin.catalogo.application.genre.retrieve.list.GenreListOutput;
import br.com.brunno.admin.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.update.UpdateGenreCommand;
import br.com.brunno.admin.catalogo.application.genre.update.UpdateGenreOutput;
import br.com.brunno.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.api.GenreAPI;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.GenreResponse;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final ListGenreUseCase listGenreUseCase;

    public GenreController(CreateGenreUseCase createGenreUseCase, GetGenreByIdUseCase getGenreByIdUseCase, UpdateGenreUseCase updateGenreUseCase, DeleteGenreUseCase deleteGenreUseCase, ListGenreUseCase listGenreUseCase) {
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
        this.getGenreByIdUseCase = getGenreByIdUseCase;
        this.updateGenreUseCase = updateGenreUseCase;
        this.deleteGenreUseCase = deleteGenreUseCase;
        this.listGenreUseCase = listGenreUseCase;
    }

    @Override
    public ResponseEntity<?> create(CreateGenreRequest createGenreRequest) {
        final var aCommand = CreateGenreCommand.with(
                createGenreRequest.name(),
                createGenreRequest.active(),
                createGenreRequest.categories()
        );

        final var createGenreOutput = createGenreUseCase.execute(aCommand);

        return ResponseEntity
                .created(URI.create("/genres/"+createGenreOutput.id()))
                .body(createGenreOutput);
    }

    @Override
    public Pagination<GenreListResponse> list(String search, int page, int perPage, String sort, String direction) {
        final var searchQuery = new SearchQuery(
                page, perPage, search, sort, direction
        );

        System.out.println(searchQuery);

        return this.listGenreUseCase.execute(searchQuery)
                .map(GenreListResponse::from);

    }

    @Override
    public ResponseEntity<GenreResponse> getById(String id) {
        final var genreOutput = getGenreByIdUseCase.execute(GenreID.from(id));
        return ResponseEntity.ok(GenreResponse.from(genreOutput));
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateGenreRequest updateGenreRequest) {
        final var aCommand = new UpdateGenreCommand(
                id,
                updateGenreRequest.name(),
                updateGenreRequest.active(),
                updateGenreRequest.categories()
        );

        final var output = updateGenreUseCase.execute(aCommand);

        return ResponseEntity.ok(output);
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        this.deleteGenreUseCase.execute(new DeleteGenreCommand(GenreID.from(id)));
        return ResponseEntity.noContent().build();
    }

}
