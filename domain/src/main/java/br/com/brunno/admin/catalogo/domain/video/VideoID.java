package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class VideoID extends Identifier {

    private final String value;

    private VideoID(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static VideoID unique() {
        return VideoID.from(UUID.randomUUID());
    }

    public static VideoID from(UUID anId) {
        return VideoID.from(anId.toString().toLowerCase());
    }

    public static VideoID from(String anId) {
        return new VideoID(anId);
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
