package br.com.brunno.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record UpdateVideoRequest(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("year_launched") Integer yearLaunched,
        @JsonProperty("duration") Double duration,
        @JsonProperty("opened") Boolean isOpened,
        @JsonProperty("published") Boolean isPublished,
        @JsonProperty("rating") String rating,
        @JsonProperty("categories") Set<String> categories,
        @JsonProperty("genres") Set<String> genres,
        @JsonProperty("cast_members") Set<String> castMembers
) {
}

