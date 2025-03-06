package br.com.brunno.admin.catalogo.domain.video;

public record VideoSearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction
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
