package br.com.brunno.admin.catalogo.domain;

import java.util.Objects;
import java.util.UUID;

public class DomainId extends ValueObject {

    private final String value;

    private DomainId(String id) {
        if (Objects.isNull(id) || id.trim().isBlank()) {
            throw new IllegalArgumentException("value of DomainId cannot be null or blank");
        }

        this.value = format(id);
    }

    public static DomainId generate() {
        return new DomainId(UUID.randomUUID().toString());
    }

    public static DomainId from(String value) {
        return new DomainId(value);
    }

    private String format(String value) {
        return value.toLowerCase().replace("-", "");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DomainId domainId = (DomainId) o;
        return Objects.equals(getValue(), domainId.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return getValue();
    }
}
