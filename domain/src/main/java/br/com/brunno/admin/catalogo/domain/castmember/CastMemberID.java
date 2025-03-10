package br.com.brunno.admin.catalogo.domain.castmember;

import br.com.brunno.admin.catalogo.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class CastMemberID extends Identifier {

    private String value;

    private CastMemberID(String value) {
        this.value = value;
    }

    public static CastMemberID unique() {
        return CastMemberID.from(UUID.randomUUID().toString());
    }

    public static CastMemberID from(String anId) {
        return new CastMemberID(anId);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CastMemberID that = (CastMemberID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
