package br.com.brunno.admin.catalogo.domain.castmember;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum CastMemberType {
    ACTOR,
    DIRECTOR,
    ;

    public static boolean isValue(String type) {
        if (Objects.isNull(type)) return false;

        for (CastMemberType castMemberType : CastMemberType.values()) {
            if (Objects.equals(type, castMemberType.name())) return true;
        }

        return false;
    }

    public static List<String> getValuesNames() {
        return Arrays.stream(CastMemberType.values())
                .map(Enum::name)
                .toList();
    }
}
