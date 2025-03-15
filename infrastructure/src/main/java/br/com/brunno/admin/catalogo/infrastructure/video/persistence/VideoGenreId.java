package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VideoGenreId implements Serializable {

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "genre_id", nullable = false)
    private UUID genreId;

    public VideoGenreId() {}

    private VideoGenreId(UUID videoId, UUID genreId) {
        this.videoId = videoId;
        this.genreId = genreId;
    }

    public static VideoGenreId from (VideoID videoId, GenreID genreId) {
        return new VideoGenreId(
                UUID.fromString(videoId.getValue()),
                UUID.fromString(genreId.getValue())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VideoGenreId that = (VideoGenreId) o;
        return Objects.equals(getVideoId(), that.getVideoId()) && Objects.equals(getGenreId(), that.getGenreId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getGenreId());
    }

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public UUID getGenreId() {
        return genreId;
    }

    public void setGenreId(UUID genreId) {
        this.genreId = genreId;
    }
}
