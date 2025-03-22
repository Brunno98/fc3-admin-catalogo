package br.com.brunno.admin.catalogo.infrastructure.configuration.useCases;

import br.com.brunno.admin.catalogo.application.video.create.CreateVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.media.get.GetMediaUseCase;
import br.com.brunno.admin.catalogo.application.video.media.update.UpdateMediaStatusUseCase;
import br.com.brunno.admin.catalogo.application.video.media.upload.MediaUploadUseCase;
import br.com.brunno.admin.catalogo.application.video.retrieve.get.GetVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.retrieve.list.VideoListUseCase;
import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoUseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.video.MediaResourceGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class VideoUseCasesConfiguration {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;
    private final CastMemberGateway castMemberGateway;
    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public VideoUseCasesConfiguration(
            CategoryGateway categoryGateway,
            GenreGateway genreGateway,
            CastMemberGateway castMemberGateway,
            VideoGateway videoGateway,
            MediaResourceGateway mediaResourceGateway
    ) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Bean
    public CreateVideoUseCase createVideoUseCase() {
        return new CreateVideoUseCase(
                categoryGateway,
                genreGateway,
                castMemberGateway,
                videoGateway,
                mediaResourceGateway
        );
    }

    @Bean
    public UpdateVideoUseCase updateVideoUseCase() {
        return new UpdateVideoUseCase(
                categoryGateway,
                genreGateway,
                castMemberGateway,
                videoGateway,
                mediaResourceGateway
        );
    }

    @Bean
    public GetVideoUseCase getVideoUseCase() {
        return new GetVideoUseCase(videoGateway);
    }

    @Bean
    public VideoListUseCase videoListUseCase() {
        return new VideoListUseCase(videoGateway);
    }

    @Bean
    public DeleteVideoUseCase deleteVideoUseCase() {
        return new DeleteVideoUseCase(videoGateway, mediaResourceGateway);
    }

    @Bean
    public GetMediaUseCase getMediaUseCase() {
        return new GetMediaUseCase(mediaResourceGateway);
    }

    @Bean
    public UpdateMediaStatusUseCase updateMediaStatusUseCase() {
        return new UpdateMediaStatusUseCase(videoGateway);
    }

    @Bean
    public MediaUploadUseCase mediaUploadUseCase() {
        return new MediaUploadUseCase(mediaResourceGateway, videoGateway);
    }

}
