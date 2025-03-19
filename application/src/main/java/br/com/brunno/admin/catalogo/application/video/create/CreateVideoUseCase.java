package br.com.brunno.admin.catalogo.application.video.create;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.Identifier;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.InternalErroException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;
import br.com.brunno.admin.catalogo.domain.video.MediaResourceGateway;
import br.com.brunno.admin.catalogo.domain.video.Rating;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;
import br.com.brunno.admin.catalogo.domain.video.VideoResource;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CreateVideoUseCase extends UseCase<CreateVideoCommand, CreateVideoOutput> {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;
    private final CastMemberGateway castMemberGateway;
    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public CreateVideoUseCase(
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

    @Override
    public CreateVideoOutput execute(CreateVideoCommand aCommand) {
        final var aRating = Rating.of(aCommand.rating()).orElse(null);
        final var aYear = Objects.nonNull(aCommand.launchedAt()) ? Year.of(aCommand.launchedAt()) : null;

        final var categories = aCommand.categories().stream().map(CategoryId::from).collect(Collectors.toSet());
        final var genres = aCommand.genres().stream().map(GenreID::from).collect(Collectors.toSet());
        final var castMembers = aCommand.members().stream().map(CastMemberID::from).collect(Collectors.toSet());

        final var notification = Notification.create();
        notification.append(validateIds(categories, categoryGateway::existisByIds));
        notification.append(validateIds(genres, genreGateway::existisByIds));
        notification.append(validateIds(castMembers, castMemberGateway::existisByIds));

        final var aVideo = Video.newVideo(
                aCommand.title(),
                aCommand.description(),
                aYear,
                aCommand.duration(),
                aCommand.opened(),
                aCommand.published(),
                aRating,
                categories,
                genres,
                castMembers
        );

        aVideo.validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Could not create a Video", notification);
        }

        return CreateVideoOutput.with(create(aCommand, aVideo));
    }

    private Video create(CreateVideoCommand aCommand, Video aVideo) {
        final var anId = aVideo.getId();

        try {
            final var anAudioVideo = aCommand.getVideo()
                    .map(it -> mediaResourceGateway.storeAudioVideo(anId, VideoResource.with(it, VideoMediaType.VIDEO)))
                    .orElse(null);

            final var aTrailer = aCommand.getTrailer()
                    .map(it -> mediaResourceGateway.storeAudioVideo(anId, VideoResource.with(it, VideoMediaType.TRAILER)))
                    .orElse(null);

            final var aBanner = aCommand.getBanner()
                    .map(it -> mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.BANNER)))
                    .orElse(null);

            final var aThumbnail = aCommand.getThumbnail()
                    .map(it -> mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.THUMBNAIL)))
                    .orElse(null);

            final var aThumbnailHalf = aCommand.getThumbnailHalf()
                    .map(it -> mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.THUMBNAIL_HALF)))
                    .orElse(null);

            aVideo.setVideo(anAudioVideo);
            aVideo.setTrailer(aTrailer);
            aVideo.setBanner(aBanner);
            aVideo.setThumbnail(aThumbnail);
            aVideo.setThumbnailHalf(aThumbnailHalf);

            return videoGateway.create(aVideo);
        } catch (Throwable t) {
            this.mediaResourceGateway.clearResources(anId);
            throw InternalErroException.with(
                    "An error was observed when create a video id:[%s]".formatted(anId.getValue()),
                    t
            );
        }
    }

    private <T extends Identifier> ValidationHandler validateIds(
            Collection<T> ids,
            Function<List<T>, List<T>> validator
    ) {
        final var notification = Notification.create();

        final var validIds = validator.apply(ids.stream().toList());

        if (validIds.size() != ids.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(validIds);
            final var missingIdsMessage = missingIds.stream()
                    .map(Identifier::getValue)
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));
            notification.append(
                    new Error("Some category IDs could not be found: %s".formatted(missingIdsMessage))
            );
        }

        return notification;
    }
}
