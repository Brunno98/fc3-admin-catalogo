package br.com.brunno.admin.catalogo.infrastructure.video.models;

import br.com.brunno.admin.catalogo.application.video.media.upload.UploadMediaOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UploadMediaResponse(
        @JsonProperty("video_id") String id,
        @JsonProperty("media_type") String type
) {

    public static UploadMediaResponse from(UploadMediaOutput output) {
        return new UploadMediaResponse(
                output.anId().getValue(),
                output.type().name()
        );
    }

}
