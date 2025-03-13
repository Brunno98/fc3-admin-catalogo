package br.com.brunno.admin.catalogo.application.video.retrieve.list;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoSearchQuery;

public class VideoListUseCase extends UseCase<VideoSearchQuery, Pagination<VideoListOutput>> {

    private final VideoGateway videoGateway;

    public VideoListUseCase(VideoGateway videoGateway) {
        this.videoGateway = videoGateway;
    }

    @Override
    public Pagination<VideoListOutput> execute(VideoSearchQuery aQuery) {
        return videoGateway.findAll(aQuery)
                .map(VideoListOutput::from);
    }
}
