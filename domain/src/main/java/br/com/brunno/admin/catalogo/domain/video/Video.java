package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.AggregateRoot;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.event.DomainEvent;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;

import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Video extends AggregateRoot<VideoID> {

    private String title;
    private String description;
    private Year launchedAt;
    private double duration;
    private boolean opened;
    private boolean published;
    private Rating rating;
    private Instant createdAt;
    private Instant updatedAt;

    private ImageMedia banner;
    private ImageMedia thumbnail;
    private ImageMedia thumbnailHalf;

    private AudioVideoMedia trailer;
    private AudioVideoMedia video;

    private Set<CategoryId> categories;
    private Set<GenreID> genres;
    private Set<CastMemberID> castMembers;

    public Video(
            VideoID id,
            String title,
            String description,
            Year launchedYear,
            double duration,
            boolean opened,
            boolean published,
            Rating rating,
            Instant createdAt,
            Instant updatedAt,
            ImageMedia banner,
            ImageMedia thumbnail,
            ImageMedia thumbnailHalf,
            AudioVideoMedia trailer,
            AudioVideoMedia video,
            Set<CategoryId> categories,
            Set<GenreID> genres,
            Set<CastMemberID> castMembers,
            List<DomainEvent> domainEvents
    ) {
        super(id, domainEvents);
        this.id = id;
        this.title = title;
        this.description = description;
        this.launchedAt = launchedYear;
        this.duration = duration;
        this.opened = opened;
        this.published = published;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categories = categories;
        this.genres = genres;
        this.castMembers = castMembers;
        this.banner = banner;
        this.thumbnail = thumbnail;
        this.thumbnailHalf = thumbnailHalf;
        this.trailer = trailer;
        this.video = video;
    }

    public static Video newVideo(
            String title,
            String description,
            Year launchedYear,
            double duration,
            boolean opened,
            boolean published,
            Rating rating,
            Set<CategoryId> categories,
            Set<GenreID> genres,
            Set<CastMemberID> castMembers
    ) {
        final var now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        return new Video(
                VideoID.unique(),
                title,
                description,
                launchedYear,
                duration,
                opened,
                published,
                rating,
                now,
                now,
                null,
                null,
                null,
                null,
                null,
                categories,
                genres,
                castMembers,
                null
        );
    }

    public static Video with(
            VideoID id,
            String title,
            String description,
            Year launchedYear,
            double duration,
            boolean opened,
            boolean published,
            Rating rating,
            Instant createdAt,
            Instant updatedAt,
            ImageMedia banner,
            ImageMedia thumbnail,
            ImageMedia thumbnailHalf,
            AudioVideoMedia video,
            AudioVideoMedia trailer,
            Set<CategoryId> categories,
            Set<GenreID> genres,
            Set<CastMemberID> castMembers
    ) {
        return new Video(
                id,
                title,
                description,
                launchedYear,
                duration,
                opened,
                published,
                rating,
                createdAt,
                updatedAt,
                banner,
                thumbnail,
                thumbnailHalf,
                trailer,
                video,
                categories,
                genres,
                castMembers,
                null
        );
    }

    public static Video with(Video video) {
        return new Video(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getLaunchedAt(),
                video.getDuration(),
                video.isOpened(),
                video.isPublished(),
                video.getRating(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                video.getBanner().orElse(null),
                video.getThumbnail().orElse(null),
                video.getThumbnailHalf().orElse(null),
                video.getTrailer().orElse(null),
                video.getVideo().orElse(null),
                new HashSet<>(video.getCategories()),
                new HashSet<>(video.getGenres()),
                new HashSet<>(video.getCastMembers()),
                new ArrayList<>(video.getDomainEvents())
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new VideoValidator(this, handler).validate();
    }

    public VideoID getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isPublished() {
        return published;
    }

    public Rating getRating() {
        return rating;
    }

    public Instant getCreatedAt() {
        if (Objects.isNull(createdAt)) return null;
        return createdAt.truncatedTo(ChronoUnit.MICROS);
    }

    public Instant getUpdatedAt() {
        if (Objects.isNull(updatedAt)) return null;
        return updatedAt.truncatedTo(ChronoUnit.MICROS);
    }

    public Set<CategoryId> getCategories() {
        if (Objects.isNull(this.categories)) return new HashSet<>();
        return new HashSet<>(this.categories);
    }

    public Set<GenreID> getGenres() {
        if (Objects.isNull(this.genres)) return new HashSet<>();
        return new HashSet<>(this.genres);
    }

    public Set<CastMemberID> getCastMembers() {
        if (Objects.isNull(this.castMembers)) return new HashSet<>();
        return new HashSet<>(this.castMembers);
    }

    public Optional<ImageMedia> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Optional<ImageMedia> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Optional<ImageMedia> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public Optional<AudioVideoMedia> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Optional<AudioVideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }

    public void updateBannerMedia(ImageMedia banner) {
        this.banner = banner;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void updateThumbnailMedia(ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void updateThumbnailHalfMedia(ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void updateTrailerMedia(AudioVideoMedia trailer) {
        this.trailer = trailer;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);

        if (Objects.nonNull(trailer) && trailer.isProcessing()) {
            this.registerDomainEvent(new VideoMediaCreated(this.getId().getValue(), trailer.rawLocation()));
        }
    }

    public void updateVideoMedia(AudioVideoMedia video) {
        this.video = video;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);

        if (Objects.nonNull(video) && video.isProcessing()) {
            this.registerDomainEvent(new VideoMediaCreated(this.getId().getValue(), video.rawLocation()));
        }
    }

    private void setCategories(Set<CategoryId> categories) {
        this.categories = categories;
    }

    private void setGenres(Set<GenreID> genres) {
        this.genres = genres;
    }

    private void setCastMembers(Set<CastMemberID> castMembers) {
        this.castMembers = castMembers;
    }

    public void update(
            String title,
            String description,
            Year launchedAt,
            double duration,
            boolean opened,
            boolean published,
            Rating rating,
            Set<CategoryId> categories,
            Set<GenreID> genres,
            Set<CastMemberID> castMembers
    ) {
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.opened = opened;
        this.published = published;
        this.rating = rating;
        this.setCategories(categories);
        this.setGenres(genres);
        this.setCastMembers(castMembers);
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    //TODO: Sugestão da aula: Implementar o pattern Visitor
    public Video processing(VideoMediaType type) {
        if (VideoMediaType.VIDEO == type) {
            getVideo().ifPresent(media -> updateVideoMedia(media.processing()));
        } else if (VideoMediaType.TRAILER == type) {
            getTrailer().ifPresent(media -> updateTrailerMedia(media.processing()));
        }
        return this;
    }

    //TODO: Sugestão da aula: Implementar o pattern Visitor
    public Video completed(final VideoMediaType type, final String encodedLocation) {
        if (VideoMediaType.VIDEO == type) {
            getVideo().ifPresent(media -> updateVideoMedia(media.completed(encodedLocation)));
        } else if (VideoMediaType.TRAILER == type) {
            getTrailer().ifPresent(media -> updateTrailerMedia(media.completed(encodedLocation)));
        }
        return this;
    }
}
