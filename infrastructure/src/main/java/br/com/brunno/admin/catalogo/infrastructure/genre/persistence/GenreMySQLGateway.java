package br.com.brunno.admin.catalogo.infrastructure.genre.persistence;

import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.util.SpecificationUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre create(Genre aGenre) {
        return genreRepository.save(GenreJpaEntity.from(aGenre)).toAggregate();
    }

    @Override
    public void deleteById(GenreID anId) {
        Objects.requireNonNull(anId, "Cannot delete an genre with null ID");
        if (this.genreRepository.existsById(anId.getValue())){
            this.genreRepository.deleteById(anId.getValue());
        }
    }

    @Override
    public Optional<Genre> findById(GenreID anId) {
        Objects.requireNonNull(anId, "Cannot find an genre with null ID");
        return this.genreRepository.findById(anId.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(Genre aGenre) {
        return this.genreRepository.save(GenreJpaEntity.from(aGenre)).toAggregate();
    }

    @Override
    public Pagination<Genre> findAll(SearchQuery aQuery) {
        final var pageRequest = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final Specification<GenreJpaEntity> whereClause = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(str -> SpecificationUtil.<GenreJpaEntity>like("name", str))
                .orElse(null);

        final var pageResult = this.genreRepository.findAll(whereClause, pageRequest);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(GenreJpaEntity::toAggregate).toList()
        );
    }

}
