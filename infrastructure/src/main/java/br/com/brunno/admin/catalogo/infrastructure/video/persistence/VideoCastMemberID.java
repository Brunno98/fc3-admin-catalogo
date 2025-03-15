package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VideoCastMemberID implements Serializable {

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "cast_member_id", nullable = false)
    private UUID castMemberId;

    public VideoCastMemberID() {}

    private VideoCastMemberID(UUID videoId, UUID castMemberId) {
        this.videoId = videoId;
        this.castMemberId = castMemberId;
    }

    public static VideoCastMemberID from(VideoID videoID, CastMemberID castMemberID) {
        return new VideoCastMemberID(
                UUID.fromString(videoID.getValue()),
                UUID.fromString(castMemberID.getValue())
        );
    }

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public UUID getCastMemberId() {
        return castMemberId;
    }

    public void setCastMemberId(UUID castMemberId) {
        this.castMemberId = castMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VideoCastMemberID that = (VideoCastMemberID) o;
        return Objects.equals(getVideoId(), that.getVideoId()) && Objects.equals(getCastMemberId(), that.getCastMemberId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getCastMemberId());
    }
}
