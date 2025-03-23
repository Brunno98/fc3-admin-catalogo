package br.com.brunno.admin.catalogo.infrastructure.api.controllers;

import br.com.brunno.admin.catalogo.application.video.create.CreateVideoCommand;
import br.com.brunno.admin.catalogo.application.video.create.CreateVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.retrieve.get.GetVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.retrieve.list.VideoListUseCase;
import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoCommand;
import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoUseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.resource.Resource;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoSearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.api.VideoAPI;
import br.com.brunno.admin.catalogo.infrastructure.util.HashingUtils;
import br.com.brunno.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import br.com.brunno.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import br.com.brunno.admin.catalogo.infrastructure.video.models.UpdateVideoResponse;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoListResponse;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class VideoController implements VideoAPI {

    private final CreateVideoUseCase createVideoUseCase;
    private final GetVideoUseCase getVideoUseCase;
    private final UpdateVideoUseCase updateVideoUseCase;
    private final DeleteVideoUseCase deleteVideoUseCase;
    private final VideoListUseCase videoListUseCase;

    public VideoController(
            CreateVideoUseCase createVideoUseCase,
            GetVideoUseCase getVideoUseCase,
            UpdateVideoUseCase updateVideoUseCase,
            DeleteVideoUseCase deleteVideoUseCase, VideoListUseCase videoListUseCase
    ) {
        this.createVideoUseCase = Objects.requireNonNull(createVideoUseCase);
        this.getVideoUseCase = Objects.requireNonNull(getVideoUseCase);
        this.updateVideoUseCase = Objects.requireNonNull(updateVideoUseCase);
        this.deleteVideoUseCase = Objects.requireNonNull(deleteVideoUseCase);
        this.videoListUseCase = Objects.requireNonNull(videoListUseCase);
    }

    @Override
    public Pagination<VideoListResponse> list(
            String search,
            int page,
            int perPage,
            String sort,
            String dir,
            Set<String> castMembers,
            Set<String> genres,
            Set<String> categories
    ) {
        final var aQuery = new VideoSearchQuery(
                page,
                perPage,
                search,
                sort,
                dir,
                categories.stream().map(CategoryId::from).collect(Collectors.toSet()),
                genres.stream().map(GenreID::from).collect(Collectors.toSet()),
                castMembers.stream().map(CastMemberID::from).collect(Collectors.toSet())
        );

        return this.videoListUseCase.execute(aQuery)
                .map(VideoListResponse::new);
    }

    @Override
    public ResponseEntity<?> createFull(
            String title,
            String description,
            Integer launchedYear,
            Double duration,
            Boolean isOpened,
            Boolean isPublished,
            String rating,
            Set<String> castMembers,
            Set<String> categories,
            Set<String> genres,
            MultipartFile videoFile,
            MultipartFile trailerFile,
            MultipartFile bannerFile,
            MultipartFile thumbnailFile,
            MultipartFile thumbnailHalfFile
    ) {
        final var aCommand = CreateVideoCommand.with(
                title,
                description,
                launchedYear,
                duration,
                isOpened,
                isPublished,
                rating,
                categories,
                genres,
                castMembers,
                resourceOf(videoFile),
                resourceOf(trailerFile),
                resourceOf(bannerFile),
                resourceOf(thumbnailFile),
                resourceOf(thumbnailHalfFile)
        );

        final var output = this.createVideoUseCase.execute(aCommand);

        return ResponseEntity.created(URI.create("/videos/" + output.id().getValue())).body(
                Map.of("id", output.id().getValue()) //TODO: criar classe de resposta de criação de video
        );
    }

    @Override
    public ResponseEntity<?> createFull(CreateVideoRequest createVideoRequest) {
        final var aCommand = CreateVideoCommand.with(
                createVideoRequest.title(),
                createVideoRequest.description(),
                createVideoRequest.yearLaunched(),
                createVideoRequest.duration(),
                createVideoRequest.isOpened(),
                createVideoRequest.isPublished(),
                createVideoRequest.rating(),
                createVideoRequest.categories(),
                createVideoRequest.genres(),
                createVideoRequest.castMembers()
        );

        final var output = this.createVideoUseCase.execute(aCommand);

        return ResponseEntity.created(URI.create("/videos/" + output.id().getValue())).body(
                Map.of("id", output.id().getValue()) //TODO: criar classe de resposta de criação de video
        );
    }

    @Override
    public VideoResponse getById(String id) {
        return VideoResponse.from(this.getVideoUseCase.execute(VideoID.from(id)));
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateVideoRequest updateVideoRequest) {
        final var aCommand = UpdateVideoCommand.with(
                VideoID.from(id),
                updateVideoRequest.title(),
                updateVideoRequest.description(),
                updateVideoRequest.yearLaunched(),
                updateVideoRequest.duration(),
                updateVideoRequest.isOpened(),
                updateVideoRequest.isPublished(),
                updateVideoRequest.rating(),
                updateVideoRequest.categories(),
                updateVideoRequest.genres(),
                updateVideoRequest.castMembers()
        );

        final var output = this.updateVideoUseCase.execute(aCommand);

        final var location = URI.create("/videos/" + output.id().getValue());
        return ResponseEntity
                .ok()
                .header("Location", location.toString())
                .body(new UpdateVideoResponse(output));
    }

    @Override
    public void delete(String id) {
        this.deleteVideoUseCase.execute(VideoID.from(id));
    }

    private Resource resourceOf(MultipartFile file) {
        if (Objects.isNull(file)) return null;

        try {
            return Resource.with(
                    HashingUtils.checksum(file.getBytes()),
                    file.getBytes(),
                    file.getContentType(),
                    file.getOriginalFilename()
            );
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
