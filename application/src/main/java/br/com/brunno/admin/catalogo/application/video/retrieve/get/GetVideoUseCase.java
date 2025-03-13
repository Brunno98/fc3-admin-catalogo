package br.com.brunno.admin.catalogo.application.video.retrieve.get;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

public class GetVideoUseCase extends UseCase<VideoID, VideoOutput> {

    private final VideoGateway videoGateway;

    public GetVideoUseCase(VideoGateway videoGateway) {
        this.videoGateway = videoGateway;
    }

    @Override
    public VideoOutput execute(VideoID anId) {
        return videoGateway.findById(anId)
                .map(VideoOutput::from)
                .orElseThrow(() -> NotFoundException.with(Video.class, anId));
    }
}
