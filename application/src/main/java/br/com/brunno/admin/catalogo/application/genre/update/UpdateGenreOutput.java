package br.com.brunno.admin.catalogo.application.genre.update;

import br.com.brunno.admin.catalogo.domain.genre.Genre;

public record UpdateGenreOutput(String id) {

    public static UpdateGenreOutput from(Genre genre) {
        return new UpdateGenreOutput(genre.getId().getValue());
    }

}
