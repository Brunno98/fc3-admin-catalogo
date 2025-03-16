package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import br.com.brunno.admin.catalogo.domain.video.MediaStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "AudioVideoMedia")
@Table(name = "audio_video_media")
public class AudioVideoMediaJpaEntity {

    @Id
    private String id;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "encoded_path", nullable = false)
    private String encodedPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "video_status", nullable = false)
    private MediaStatus status;

    public AudioVideoMediaJpaEntity() {}

    private AudioVideoMediaJpaEntity(
            String id,
            String checksum,
            String name,
            String filePath,
            String encodedPath,
            MediaStatus status
    ) {
        this.id = id;
        this.checksum = checksum;
        this.name = name;
        this.filePath = filePath;
        this.encodedPath = encodedPath;
        this.status = status;
    }

    public static AudioVideoMediaJpaEntity from(AudioVideoMedia media) {
        return new AudioVideoMediaJpaEntity(
                media.id().getValue(),
                media.checksum(),
                media.name(),
                media.rawLocation(),
                media.encodedLocation(),
                media.status()
        );
    }

    public AudioVideoMedia toDomain() {
        return AudioVideoMedia.with(
                DomainId.from(getId()),
                getChecksum(),
                getName(),
                getFilePath(),
                getEncodedPath(),
                getStatus()
        );
    }

    public String getId() {
        return id;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public MediaStatus getStatus() {
        return status;
    }
}
