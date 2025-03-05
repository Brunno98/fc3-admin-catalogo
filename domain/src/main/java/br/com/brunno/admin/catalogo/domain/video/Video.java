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
            Year launchedAt,
            double duration,
            boolean opened,
            boolean published,
            Rating rating,
            Instant createdAt,
            Instant updatedAt,
            Set<CategoryId> categories,
            Set<GenreID> genres,
            Set<CastMemberID> castMembers
    ) {
        super(id);
        this.id = id;
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.opened = opened;
        this.published = published;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categories = categories;
        this.genres = genres;
        this.castMembers = castMembers;
    }

    public static Video create(
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
        final var now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        return new Video(
                VideoID.unique(),
                title,
                description,
                launchedAt,
                duration,
                opened,
                published,
                rating,
                now,
                now,
                categories,
                genres,
                castMembers
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

}
