package br.com.brunno.admin.catalogo.infrastructure.util;

import java.util.Objects;

public final class SqlUtils {

    private SqlUtils() {}

    public static String like(String term) {
        if (Objects.isNull(term)) return null;
        return "%" + term.toUpperCase() + "%";
    }

}
