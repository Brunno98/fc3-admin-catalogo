package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.video.Rating;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(name = "videos")
@Entity(name = "Video")
public class VideoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "launched_year", nullable = false)
    private int launchedYear;

    @Column(name = "duration", nullable = false, precision = 2)
    private double duration;

    @Column(name = "opened", nullable = false)
    private boolean opened;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "rating", nullable = false)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "video_id")
    private AudioVideoMediaJpaEntity video;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "trailer_id")
    private AudioVideoMediaJpaEntity trailer;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "banner_id")
    private ImageMediaJpaEntity banner;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private ImageMediaJpaEntity thumbnail;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_half_id")
    private ImageMediaJpaEntity thumbnailHalf;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCategoryJpaEntity> categories;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoGenreJpaEntity> genres;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCastMemberJpaEntity> castMembers;

    public VideoJpaEntity() {}

    public VideoJpaEntity(
            UUID id,
            String title,
            String description,
            int launchedYear,
            double duration,
            boolean opened,
            boolean published,
            Rating rating,
            Instant createdAt,
            Instant updatedAt, AudioVideoMediaJpaEntity video,
            AudioVideoMediaJpaEntity trailer,
            ImageMediaJpaEntity banner,
            ImageMediaJpaEntity thumbnail,
            ImageMediaJpaEntity thumbnailHalf
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.launchedYear = launchedYear;
        this.duration = duration;
        this.opened = opened;
        this.published = published;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.video = video;
        this.trailer = trailer;
        this.banner = banner;
        this.thumbnail = thumbnail;
        this.thumbnailHalf = thumbnailHalf;
        this.categories = new HashSet<>(3);
        this.genres = new HashSet<>(3);
    }

    public static VideoJpaEntity from(Video aVideo) {
        final var entity = new VideoJpaEntity(
                UUID.fromString(aVideo.getId().getValue()),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getLaunchedAt().getValue(),
                aVideo.getDuration(),
                aVideo.isOpened(),
                aVideo.isPublished(),
                aVideo.getRating(),
                aVideo.getCreatedAt(),
                aVideo.getUpdatedAt(),
                aVideo.getVideo().map(AudioVideoMediaJpaEntity::from).orElse(null),
                aVideo.getTrailer().map(AudioVideoMediaJpaEntity::from).orElse(null),
                aVideo.getBanner().map(ImageMediaJpaEntity::from).orElse(null),
                aVideo.getThumbnail().map(ImageMediaJpaEntity::from).orElse(null),
                aVideo.getThumbnailHalf().map(ImageMediaJpaEntity::from).orElse(null)
        );

        aVideo.getCategories().forEach(entity::addCategory);
        aVideo.getGenres().forEach(entity::addGenre);
        aVideo.getCastMembers().forEach(entity::addCastMember);

        return entity;
    }

    public Video toAggregate() {
        return Video.with(
                VideoID.from(this.getId()),
                this.getTitle(),
                this.getDescription(),
                Year.of(this.getLaunchedYear()),
                this.getDuration(),
                this.isOpened(),
                this.isPublished(),
                this.getRating(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                Optional.ofNullable(getBanner()).map(ImageMediaJpaEntity::toDomain).orElse(null),
                Optional.ofNullable(getThumbnail()).map(ImageMediaJpaEntity::toDomain).orElse(null),
                Optional.ofNullable(getThumbnailHalf()).map(ImageMediaJpaEntity::toDomain).orElse(null),
                Optional.ofNullable(getVideo()).map(AudioVideoMediaJpaEntity::toDomain).orElse(null),
                Optional.ofNullable(getTrailer()).map(AudioVideoMediaJpaEntity::toDomain).orElse(null),
                getCategories().stream()
                        .map(it -> it.getId().getCategoryId())
                        .map(CategoryId::from)
                        .collect(Collectors.toSet()),
                getGenres().stream()
                        .map(it -> it.getId().getGenreId())
                        .map(GenreID::from)
                        .collect(Collectors.toSet()),
                getCastMembers().stream()
                        .map(it -> it.getId().getCastMemberId())
                        .map(CastMemberID::from)
                        .collect(Collectors.toSet())
        );
    }

    private void addGenre(GenreID genreID) {
        this.genres.add(VideoGenreJpaEntity.from(this, genreID));
    }

    private void addCategory(CategoryId categoryId) {
        this.categories.add(VideoCategoryJpaEntity.from(this, categoryId));
    }

    private void addCastMember(CastMemberID castMemberID) {
        this.castMembers.add(VideoCastMemberJpaEntity.from(this, castMemberID));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLaunchedYear() {
        return launchedYear;
    }

    public void setLaunchedYear(int launchedYear) {
        this.launchedYear = launchedYear;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AudioVideoMediaJpaEntity getVideo() {
        return video;
    }

    public void setVideo(AudioVideoMediaJpaEntity video) {
        this.video = video;
    }

    public AudioVideoMediaJpaEntity getTrailer() {
        return trailer;
    }

    public void setTrailer(AudioVideoMediaJpaEntity trailer) {
        this.trailer = trailer;
    }

    public ImageMediaJpaEntity getBanner() {
        return banner;
    }

    public void setBanner(ImageMediaJpaEntity banner) {
        this.banner = banner;
    }

    public ImageMediaJpaEntity getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageMediaJpaEntity thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ImageMediaJpaEntity getThumbnailHalf() {
        return thumbnailHalf;
    }

    public void setThumbnailHalf(ImageMediaJpaEntity thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
    }

    public Set<VideoCategoryJpaEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<VideoCategoryJpaEntity> categories) {
        this.categories = categories;
    }

    public Set<VideoGenreJpaEntity> getGenres() {
        return genres;
    }

    public void setGenres(Set<VideoGenreJpaEntity> genres) {
        this.genres = genres;
    }

    public Set<VideoCastMemberJpaEntity> getCastMembers() {
        return castMembers;
    }

    public void setCastMembers(Set<VideoCastMemberJpaEntity> castMembers) {
        this.castMembers = castMembers;
    }
}
