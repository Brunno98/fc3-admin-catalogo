package br.com.brunno.admin.catalogo.application.video.delete;

import br.com.brunno.admin.catalogo.application.UnitUseCase;
import br.com.brunno.admin.catalogo.domain.video.MediaResourceGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

import java.util.Objects;

public class DeleteVideoUseCase extends UnitUseCase<VideoID> {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DeleteVideoUseCase(VideoGateway videoGateway, MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public void execute(VideoID anId) {
        this.videoGateway.deleteById(anId);
        this.mediaResourceGateway.clearResources(anId);
    }
}
