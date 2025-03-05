package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.AggregateRoot;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;

import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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
            Set<CastMemberID> castMembers
    ) {
        super(id);
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
                castMembers
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
                new HashSet<>(video.getCastMembers())
        );
    }

    @Override
    public void validate(ValidationHandler handler) {

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
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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

    public void setBanner(ImageMedia banner) {
        this.banner = banner;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void setThumbnail(ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void setThumbnailHalf(ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void setTrailer(AudioVideoMedia trailer) {
        this.trailer = trailer;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void setVideo(AudioVideoMedia video) {
        this.video = video;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
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
}
