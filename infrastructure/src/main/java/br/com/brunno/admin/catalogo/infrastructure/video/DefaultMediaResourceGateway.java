package br.com.brunno.admin.catalogo.infrastructure.video;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import br.com.brunno.admin.catalogo.domain.video.ImageMedia;
import br.com.brunno.admin.catalogo.domain.video.MediaResourceGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;
import br.com.brunno.admin.catalogo.domain.video.VideoResource;
import br.com.brunno.admin.catalogo.infrastructure.configuration.properties.StorageProperties;
import br.com.brunno.admin.catalogo.infrastructure.services.StorageService;
import org.springframework.stereotype.Component;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    private final String filenamePattern;
    private final String locationPattern;
    private final StorageService storageService;

    public DefaultMediaResourceGateway(StorageProperties storageProperties, StorageService storageService) {
        this.filenamePattern = storageProperties.getFilenamePattern();
        this.locationPattern = storageProperties.getLocationPattern();
        this.storageService = storageService;
    }

    @Override
    public AudioVideoMedia storeAudioVideo(VideoID anId, VideoResource videoResource) {
        final var filepath = filepath(videoResource, anId);
        final var aResource = videoResource.resource();
        this.storageService.store(filepath, aResource);
        return AudioVideoMedia.with(aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public ImageMedia storeImage(VideoID anId, VideoResource videoResource) {
        final var filepath = filepath(videoResource, anId);
        final var aResource = videoResource.resource();
        this.storageService.store(filepath, aResource);
        return ImageMedia.with(DomainId.from(anId.getValue()), aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public void clearResources(VideoID anId) {
        final var ids = this.storageService.list(folder(anId));
        this.storageService.deleteAll(ids);
    }

    private String filename(final VideoMediaType type) {
        return filenamePattern.replace("{type}", type.name());
    }

    private String folder(VideoID id) {
        return locationPattern.replace("{videoId}", id.getValue());
    }

    private String filepath(VideoResource resource, VideoID id) {
        return folder(id)
                .concat("/")
                .concat(filename(resource.type()));
    }
}
