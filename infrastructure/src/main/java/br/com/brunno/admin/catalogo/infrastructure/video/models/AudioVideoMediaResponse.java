package br.com.brunno.admin.catalogo.infrastructure.video.models;

import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record AudioVideoMediaResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("checksum") String checksum,
        @JsonProperty("location") String rawLocation,
        @JsonProperty("encoded_location") String encodedLocation,
        @JsonProperty("status") String status
) {
    public static AudioVideoMediaResponse from(AudioVideoMedia media) {
        if (Objects.isNull(media)) return null;
        return new AudioVideoMediaResponse(
                media.id().getValue(),
                media.name(),
                media.checksum(),
                media.rawLocation(),
                media.encodedLocation(),
                media.status().name()
        );
    }
}
