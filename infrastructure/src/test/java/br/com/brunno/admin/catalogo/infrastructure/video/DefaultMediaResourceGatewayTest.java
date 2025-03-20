package br.com.brunno.admin.catalogo.infrastructure.video;

import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.domain.resource.Resource;
import br.com.brunno.admin.catalogo.domain.video.MediaStatus;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;
import br.com.brunno.admin.catalogo.domain.video.VideoResource;
import br.com.brunno.admin.catalogo.infrastructure.services.StorageService;
import br.com.brunno.admin.catalogo.infrastructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertTrue;

@IntegrationTest
class DefaultMediaResourceGatewayTest {

    @Autowired
    private DefaultMediaResourceGateway defaultMediaResourceGateway;

    @Autowired
    private StorageService storageService;

    @BeforeEach
    public void setUp() {
        ((InMemoryStorageService) storageService).reset();
    }

    @Test
    void givenAValidResource_whenCallsStorageAudioVideo_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());
        final var expectedStatus = MediaStatus.PENDING;
        final var expectedEncodedLocation = "";

        final var actualMedia =
                this.defaultMediaResourceGateway.storeAudioVideo(expectedVideoId, VideoResource.with(expectedResource, expectedType));

        assertEquals(expectedLocation, actualMedia.rawLocation());
        assertEquals(expectedResource.name(), actualMedia.name());
        assertEquals(expectedResource.checksum(), actualMedia.checksum());
        assertEquals(expectedStatus, actualMedia.status());
        assertEquals(expectedEncodedLocation, actualMedia.encodedLocation());

        final var persistedResource = ((InMemoryStorageService) this.storageService).getStorage().get(expectedLocation);

        assertEquals(persistedResource, expectedResource);
    }

    @Test
    void givenAValidResource_whenCallsStorageImage_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.BANNER;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());

        final var actualMedia =
                this.defaultMediaResourceGateway.storeImage(expectedVideoId, VideoResource.with(expectedResource, expectedType));

        assertEquals(expectedLocation, actualMedia.location());
        assertEquals(expectedResource.name(), actualMedia.name());
        assertEquals(expectedResource.checksum(), actualMedia.checksum());
        assertEquals(expectedVideoId.getValue(), actualMedia.id().getValue());

        final var persistedResource = ((InMemoryStorageService) this.storageService).getStorage().get(expectedLocation);

        assertEquals(persistedResource, expectedResource);
    }

    @Test
    void givenAValidId_whenCallsClear_shouldDeleteIt() {
        final var idToBeDeleted = VideoID.unique();
        final var toBeDeleted = List.of(
                VideoResource.with(Fixture.Videos.resource(VideoMediaType.VIDEO), VideoMediaType.VIDEO),
                VideoResource.with(Fixture.Videos.resource(VideoMediaType.TRAILER), VideoMediaType.TRAILER),
                VideoResource.with(Fixture.Videos.resource(VideoMediaType.BANNER), VideoMediaType.BANNER)
        );
        final var expectedResources = List.of(
                VideoResource.with(Fixture.Videos.resource(VideoMediaType.VIDEO), VideoMediaType.VIDEO),
                VideoResource.with(Fixture.Videos.resource(VideoMediaType.TRAILER), VideoMediaType.TRAILER)
        );
        toBeDeleted.forEach(videoResource ->
                this.defaultMediaResourceGateway.storeAudioVideo(idToBeDeleted, videoResource));
        expectedResources.forEach(videoResource ->
                this.defaultMediaResourceGateway.storeAudioVideo(VideoID.unique(), videoResource));
        final var storeService = (InMemoryStorageService) this.storageService;
        assertEquals(5, storeService.getStorage().size());

        this.defaultMediaResourceGateway.clearResources(idToBeDeleted);

        assertEquals(storeService.getStorage().size(), expectedResources.size());
        assertTrue(storeService.getStorage().values().containsAll(expectedResources.stream().map(VideoResource::resource).toList()));
    }


}