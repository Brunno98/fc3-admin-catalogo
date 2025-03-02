package br.com.brunno.admin.catalogo.application.genre.create;

import java.util.List;

public record CreateGenreCommand(
        String name,
        boolean active,
        List<String> categories
) {
    public static CreateGenreCommand with(String aName, boolean isActive, List<String> categoryIds) {
        return new CreateGenreCommand(aName, isActive, categoryIds);
    }
}
