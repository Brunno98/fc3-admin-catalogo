package br.com.brunno.admin.catalogo.application.genre.delete;

import br.com.brunno.admin.catalogo.domain.genre.GenreID;

public record DeleteGenreCommand(GenreID anID) {
}
