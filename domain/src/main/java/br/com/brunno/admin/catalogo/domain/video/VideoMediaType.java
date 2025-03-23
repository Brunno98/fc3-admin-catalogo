package br.com.brunno.admin.catalogo.domain.video;

import java.util.Optional;

public enum VideoMediaType {
    VIDEO,
    TRAILER,
    BANNER,
    THUMBNAIL,
    THUMBNAIL_HALF;

    public static Optional<VideoMediaType> of(String value) {
        for (VideoMediaType type : VideoMediaType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

}
