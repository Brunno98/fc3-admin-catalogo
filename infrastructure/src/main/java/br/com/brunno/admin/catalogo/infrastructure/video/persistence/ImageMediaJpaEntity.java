package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.video.ImageMedia;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "ImageMedia")
@Table(name = "videos_image_media")
public class ImageMediaJpaEntity {

    @Id
    private String id;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    public ImageMediaJpaEntity() {
    }

    public ImageMediaJpaEntity(
            String id,
            String checksum,
            String name,
            String filePath
    ) {
        this.id = id;
        this.checksum = checksum;
        this.name = name;
        this.filePath = filePath;
    }

    public static ImageMediaJpaEntity from(ImageMedia media) {
        return new ImageMediaJpaEntity(
                media.id().getValue(),
                media.checksum(),
                media.name(),
                media.location()
        );
    }

    public ImageMedia toDomain() {
        return ImageMedia.with(
                DomainId.from(getId()),
                getChecksum(),
                getName(),
                getFilePath()
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
}
