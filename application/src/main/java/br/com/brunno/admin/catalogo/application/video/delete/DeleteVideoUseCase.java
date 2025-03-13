package br.com.brunno.admin.catalogo.application.video.delete;

import br.com.brunno.admin.catalogo.application.UnitUseCase;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

public class DeleteVideoUseCase extends UnitUseCase<VideoID> {

    private final VideoGateway videoGateway;

    public DeleteVideoUseCase(VideoGateway videoGateway) {
        this.videoGateway = videoGateway;
    }

    @Override
    public void execute(VideoID anId) {
        this.videoGateway.deleteById(anId);
    }
}
