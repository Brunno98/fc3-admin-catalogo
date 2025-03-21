package br.com.brunno.admin.catalogo.application.video.media.upload;

import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoResource;

import java.util.Objects;

public record UploadMediaCommand(
        VideoID videoID,
        VideoResource resource
) {
    public UploadMediaCommand {
        Objects.requireNonNull(videoID);
        Objects.requireNonNull(resource);
    }

    public static UploadMediaCommand with(VideoID anId, VideoResource aResource) {
        return new UploadMediaCommand(anId, aResource);
    }

}
