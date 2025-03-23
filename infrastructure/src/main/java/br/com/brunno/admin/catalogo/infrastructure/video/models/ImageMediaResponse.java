package br.com.brunno.admin.catalogo.infrastructure.video.models;

import br.com.brunno.admin.catalogo.domain.video.ImageMedia;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record ImageMediaResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("checksum") String checksum,
        @JsonProperty("location") String location
) {
    public static ImageMediaResponse from(ImageMedia media) {
        if (Objects.isNull(media)) return null;
        return new ImageMediaResponse(
                media.id().getValue(),
                media.name(),
                media.checksum(),
                media.location()
        );
    }
}
