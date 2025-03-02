package br.com.brunno.admin.catalogo.application.genre.update;

import br.com.brunno.admin.catalogo.domain.category.CategoryId;

import java.util.List;

public record UpdateGenreCommand(
        String id,
        String name,
        boolean isActive,
        List<String> categories
) {
    public static UpdateGenreCommand with(
            String id,
            String aName,
            boolean isActive,
            List<String> categoryIds
    ) {
        return new UpdateGenreCommand(id, aName, isActive, categoryIds);
    }
}
