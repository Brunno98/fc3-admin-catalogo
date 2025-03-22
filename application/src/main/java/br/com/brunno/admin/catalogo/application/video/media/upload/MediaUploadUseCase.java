package br.com.brunno.admin.catalogo.application.video.media.upload;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.video.MediaResourceGateway;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;

import java.util.Objects;

public class MediaUploadUseCase extends UseCase<UploadMediaCommand, UploadMediaOutput> {

    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public MediaUploadUseCase(MediaResourceGateway mediaResourceGateway, VideoGateway videoGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public UploadMediaOutput execute(UploadMediaCommand aCommand) {
        final var anId = aCommand.videoID();
        final var aVideoResource = aCommand.resource();

        final var aVideo = videoGateway.findById(anId)
                .orElseThrow(() -> NotFoundException.with(Video.class, anId));

        switch (aVideoResource.type()) {
            case VIDEO -> aVideo.updateVideoMedia(mediaResourceGateway.storeAudioVideo(anId, aVideoResource));
            case TRAILER -> aVideo.updateTrailerMedia(mediaResourceGateway.storeAudioVideo(anId, aVideoResource));
            case BANNER -> aVideo.updateBannerMedia(mediaResourceGateway.storeImage(anId, aVideoResource));
            case THUMBNAIL -> aVideo.updateThumbnailMedia(mediaResourceGateway.storeImage(anId, aVideoResource));
            case THUMBNAIL_HALF -> aVideo.updateThumbnailHalfMedia(mediaResourceGateway.storeImage(anId, aVideoResource));
        }

        return UploadMediaOutput.from(videoGateway.update(aVideo), aVideoResource.type());
    }
}
