package br.com.brunno.admin.catalogo.infrastructure.video.models;

import br.com.brunno.admin.catalogo.application.video.retrieve.get.VideoOutput;
import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public record VideoResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("year_launched") int yearLaunched ,
        @JsonProperty("duration") double duration,
        @JsonProperty("opened") boolean opened,
        @JsonProperty("published") boolean published,
        @JsonProperty("rating") String rating,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("banner") ImageMediaResponse banner,
        @JsonProperty("thumbnail") ImageMediaResponse thumbnail,
        @JsonProperty("thumbnail_half") ImageMediaResponse thumbnailHalf,
        @JsonProperty("video") AudioVideoMediaResponse video,
        @JsonProperty("trailer") AudioVideoMediaResponse trailer,
        @JsonProperty("categories_id") Set<String> categoriesId,
        @JsonProperty("genres_id") Set<String> genresId,
        @JsonProperty("cast_members_id") Set<String> castMembersId

) {
    public static VideoResponse from(VideoOutput videoOutput) {
        if (Objects.isNull(videoOutput)) return null;
        return new VideoResponse(
                videoOutput.id(),
                videoOutput.title(),
                videoOutput.description(),
                videoOutput.LaunchedAt(),
                videoOutput.duration(),
                videoOutput.opened(),
                videoOutput.published(),
                videoOutput.rating(),
                videoOutput.createdAt(),
                videoOutput.updatedAt(),
                videoOutput.banner().map(ImageMediaResponse::from).orElse(null),
                videoOutput.thumbnail().map(ImageMediaResponse::from).orElse(null),
                videoOutput.thumbnailHalf().map(ImageMediaResponse::from).orElse(null),
                videoOutput.video().map(AudioVideoMediaResponse::from).orElse(null),
                videoOutput.trailer().map(AudioVideoMediaResponse::from).orElse(null),
                videoOutput.categories(),
                videoOutput.genres(),
                videoOutput.members()
        );
    }
}
