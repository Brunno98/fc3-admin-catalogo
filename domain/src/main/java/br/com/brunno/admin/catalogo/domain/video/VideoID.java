package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.Identifier;

import java.util.Objects;

public class VideoID extends Identifier {

    private final String value;

    private VideoID(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static VideoID unique() {
        return VideoID.from(DomainId.generate());
    }

    public static VideoID from(DomainId anId) {
        return VideoID.from(anId.toString().toLowerCase());
    }

    public static VideoID from(String anId) {
        return new VideoID(anId);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VideoID videoID = (VideoID) o;
        return Objects.equals(getValue(), videoID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
