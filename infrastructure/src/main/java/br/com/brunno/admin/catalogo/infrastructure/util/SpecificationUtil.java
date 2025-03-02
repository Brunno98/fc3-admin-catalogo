package br.com.brunno.admin.catalogo.infrastructure.util;

import br.com.brunno.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import org.springframework.data.jpa.domain.Specification;

public final class SpecificationUtil {

    private SpecificationUtil() {}

    public static <T> Specification<T> like(String prop, String term) {
        return (root, query, cb) -> {
            final var name = root.<String>get(prop);
            return cb.like(cb.upper(name), "%" + term.toUpperCase() + "%");
        };
    }
}
