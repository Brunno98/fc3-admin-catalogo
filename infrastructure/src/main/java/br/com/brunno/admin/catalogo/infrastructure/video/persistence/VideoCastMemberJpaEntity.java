package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "VideoCastMember")
@Table(name = "videos_cast_members")
public class VideoCastMemberJpaEntity {

    @EmbeddedId
    private VideoCastMemberID id;

    @OneToMany(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoCastMemberJpaEntity() {}

    private VideoCastMemberJpaEntity(VideoCastMemberID id, VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoCastMemberJpaEntity from(VideoJpaEntity video, CastMemberID castMemberID) {
        return new VideoCastMemberJpaEntity(
                VideoCastMemberID.from(VideoID.from(video.getId()), castMemberID),
                video
        );
    }

    public VideoCastMemberID getId() {
        return id;
    }

    public void setId(VideoCastMemberID id) {
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
        VideoCastMemberJpaEntity that = (VideoCastMemberJpaEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getVideo(), that.getVideo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVideo());
    }
}
