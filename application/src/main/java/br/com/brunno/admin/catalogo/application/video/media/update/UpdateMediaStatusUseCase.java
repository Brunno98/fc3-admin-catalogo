package br.com.brunno.admin.catalogo.application.video.media.update;

import br.com.brunno.admin.catalogo.application.UnitUseCase;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;

import java.util.Objects;

public class UpdateMediaStatusUseCase extends UnitUseCase<UpdateMediaStatusCommand> {

    private final VideoGateway videoGateway;

    public UpdateMediaStatusUseCase(VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(UpdateMediaStatusCommand aCommand) {
        final var anId = aCommand.videoID();
        final var resourceId = aCommand.resourceId(); //TODO: resourceId deveria ser um domainId?
        final var folder = aCommand.folder();
        final var filename = aCommand.filename();

        final var aVideo = this.videoGateway.findById(anId)
                .orElseThrow(() -> NotFoundException.with(Video.class, anId));

        final var encodedPath = "%s/%s".formatted(folder, filename);

        if (matches(resourceId, aVideo.getVideo().orElse(null))) {
            switch (aCommand.status()) {
                case PROCESSING -> this.videoGateway.update(aVideo.processing(VideoMediaType.VIDEO));
                case COMPLETED -> this.videoGateway.update(aVideo.completed(VideoMediaType.VIDEO, encodedPath));
            }
        } else if (matches(resourceId, aVideo.getTrailer().orElse(null))) {
            switch (aCommand.status()) {
                case PROCESSING -> this.videoGateway.update(aVideo.processing(VideoMediaType.TRAILER));
                case COMPLETED -> this.videoGateway.update(aVideo.completed(VideoMediaType.TRAILER, encodedPath));
            }
        }

    }

    private boolean matches(String aResourceId, AudioVideoMedia media) {
        if (Objects.isNull(media)) return false;
        return media.id().getValue().equals(aResourceId); //TODO: resourceId deveria ser um DomainId?
    }
}
