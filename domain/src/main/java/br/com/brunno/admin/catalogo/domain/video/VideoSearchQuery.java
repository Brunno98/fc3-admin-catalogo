package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;

import java.util.Set;

public record VideoSearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction,
        Set<CategoryId> categories,
        Set<GenreID> genres,
        Set<CastMemberID> members
) {
    @Override
    public String toString() {
        return "VideoSearchQuery{" +
                "page=" + page +
                ", perPage=" + perPage +
                ", terms='" + terms + '\'' +
                ", sort='" + sort + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
