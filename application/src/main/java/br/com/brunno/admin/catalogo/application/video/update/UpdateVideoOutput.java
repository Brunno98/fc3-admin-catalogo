package br.com.brunno.admin.catalogo.application.video.update;

import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

public record UpdateVideoOutput(VideoID id) {

    public static UpdateVideoOutput from(Video aVideo) {
        return new UpdateVideoOutput(aVideo.getId());
    }

}
