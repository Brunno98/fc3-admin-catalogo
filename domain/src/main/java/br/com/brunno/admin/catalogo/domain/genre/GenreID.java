package br.com.brunno.admin.catalogo.domain.genre;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.Identifier;

import java.util.Objects;

public class GenreID extends Identifier {

    private final String value;

    private GenreID(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GenreID genreID = (GenreID) o;
        return Objects.equals(getValue(), genreID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    public static GenreID unique() {
        return from(DomainId.generate());
    }

    public static GenreID from(DomainId anId) {
        return from(anId.getValue());
    }

    public static GenreID from(String anId) {
        return new GenreID(anId);
    }
}
