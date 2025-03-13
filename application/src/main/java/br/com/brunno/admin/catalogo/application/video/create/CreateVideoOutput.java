package br.com.brunno.admin.catalogo.application.video.create;

import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

public record CreateVideoOutput(VideoID id) {

    public static CreateVideoOutput with(Video video) {
        return new CreateVideoOutput(video.getId());
    }

}
