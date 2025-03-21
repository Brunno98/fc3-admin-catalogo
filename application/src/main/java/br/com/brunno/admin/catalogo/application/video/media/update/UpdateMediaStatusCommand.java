package br.com.brunno.admin.catalogo.application.video.media.update;

import br.com.brunno.admin.catalogo.domain.video.MediaStatus;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

public record UpdateMediaStatusCommand(
        MediaStatus status,
        VideoID videoID,
        String resourceId,
        String folder,
        String filename
) {

    public static UpdateMediaStatusCommand with(
            MediaStatus status,
            VideoID videoID,
            String resourceId,
            String folder,
            String filename
    ) {
        return new UpdateMediaStatusCommand(
                status,
                videoID,
                resourceId,
                folder,
                filename
        );
    }
}
