package br.com.brunno.admin.catalogo.application.video.media.get;

import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;

import java.util.Objects;

public record GetMediaCommand(
        VideoID videoId,
        VideoMediaType type
) {

    public GetMediaCommand {
        Objects.requireNonNull(videoId);
        Objects.requireNonNull(type);
    }

    public static GetMediaCommand with (VideoID anId, VideoMediaType type) {
        return new GetMediaCommand(anId, type);
    }
}
