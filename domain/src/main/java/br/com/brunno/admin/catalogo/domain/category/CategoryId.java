package br.com.brunno.admin.catalogo.domain.category;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.Identifier;

import java.util.Objects;

public class CategoryId extends Identifier {

    private final String value;

    private CategoryId(String value) {
        Objects.requireNonNull(value, "'value' should not be null!");
        this.value = value;
    }

    public static CategoryId unique() {
        return CategoryId.from(DomainId.generate());
    }

    public static CategoryId from(DomainId anId) {
        return CategoryId.from(anId.getValue());
    }

    public static CategoryId from(String anId) {
        return new CategoryId(anId);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryId that = (CategoryId) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
