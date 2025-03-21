package br.com.brunno.admin.catalogo.application.video.media.upload;

import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;

public record UploadMediaOutput(
        Video aVideo,
        VideoMediaType type
) {

    public static UploadMediaOutput from(Video aVideo, VideoMediaType aType) {
        return new UploadMediaOutput(aVideo, aType);
    }

}
