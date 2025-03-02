package br.com.brunno.admin.catalogo.domain.pagination;

public record SearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction
) {
    @Override
    public String toString() {
        return "SearchQuery{" +
                "page=" + page +
                ", perPage=" + perPage +
                ", terms='" + terms + '\'' +
                ", sort='" + sort + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
