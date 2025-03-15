package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import io.vavr.Lazy;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "VideoGenre")
@Table(name = "videos_genres")
public class VideoGenreJpaEntity {

    @EmbeddedId
    private VideoGenreId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoGenreJpaEntity() {}

    private VideoGenreJpaEntity(VideoGenreId id, VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoGenreJpaEntity from(VideoJpaEntity video, GenreID genreID) {
        return new VideoGenreJpaEntity(
                VideoGenreId.from(VideoID.from(video.getId()), genreID),
                video
        );
    }

    public VideoGenreId getId() {
        return id;
    }

    public void setId(VideoGenreId id) {
        this.id = id;
    }

    public VideoJpaEntity getVideo() {
        return video;
    }

    public void setVideo(VideoJpaEntity video) {
        this.video = video;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VideoGenreJpaEntity that = (VideoGenreJpaEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getVideo(), that.getVideo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVideo());
    }
}
