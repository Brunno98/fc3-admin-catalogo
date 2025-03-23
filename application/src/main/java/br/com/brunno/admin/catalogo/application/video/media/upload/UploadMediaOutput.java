package br.com.brunno.admin.catalogo.application.video.media.upload;

import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;

public record UploadMediaOutput(
        VideoID anId,
        VideoMediaType type
) {

    public static UploadMediaOutput from(Video aVideo, VideoMediaType aType) {
        return UploadMediaOutput.from(aVideo.getId(), aType);
    }

    public static UploadMediaOutput from(VideoID anVideoId, VideoMediaType aType) {
        return new UploadMediaOutput(anVideoId, aType);
    }

}
