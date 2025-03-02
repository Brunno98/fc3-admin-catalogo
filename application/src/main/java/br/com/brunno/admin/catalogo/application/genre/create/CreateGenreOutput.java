package br.com.brunno.admin.catalogo.application.genre.create;

import br.com.brunno.admin.catalogo.domain.genre.Genre;

public record CreateGenreOutput(String id) {
    public static CreateGenreOutput from(Genre genre) {
        return new CreateGenreOutput(genre.getId().getValue());
    }
}
