package br.com.brunno.admin.catalogo.infrastructure.video.models;

import br.com.brunno.admin.catalogo.application.video.retrieve.list.VideoListOutput;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record VideoListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
    public VideoListResponse(VideoListOutput anOutput) {
        this(
                anOutput.id().getValue(),
                anOutput.title(),
                anOutput.description(),
                anOutput.createdAt(),
                anOutput.updatedAt()
        );
    }
}
