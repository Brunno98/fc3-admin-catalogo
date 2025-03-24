package br.com.brunno.admin.catalogo.infrastructure.services.local;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.domain.UnitTest;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UnitTest
class InMemoryStorageServiceTest {

    private InMemoryStorageService storeService = new InMemoryStorageService();

    @Test
    void givenAValidResource_whenCallsStore_shouldStoreIt() {
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final var expectedName = "video";

        storeService.store(expectedName, expectedResource);

        assertEquals(expectedResource, storeService.getStorage().get(expectedName));
    }

    @Test
    void givenAValidResource_whenCallsGet_shouldRetrieveIt() {
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final var expectedName = "video";
        storeService.getStorage().put(expectedName, expectedResource);

        final var actualResource = storeService.get(expectedName).get();

        assertEquals(expectedResource, actualResource);
    }

    @Test
    void givenAnInvalidResource_whenCallsGet_shouldRetrieveIt() {
        final var expectedName = "video";

        final var actualResource = storeService.get(expectedName);

        assertTrue(actualResource.isEmpty());
    }

    @Test
    void givenAValidPrefix_whenCallsList_shouldRetrieveAll() {
        final var expectedPrefix = "video_";
        final var expectedNames = List.of(
                expectedPrefix + UUID.randomUUID(),
                expectedPrefix + UUID.randomUUID(),
                expectedPrefix + UUID.randomUUID()
        );

        Stream.concat(
                expectedNames.stream(),
                Stream.of("image_" + UUID.randomUUID(), "image_" + UUID.randomUUID())
        ).forEach(name -> storeService.getStorage().put(name, Fixture.Videos.resource(VideoMediaType.VIDEO)));

        final var actualResource = storeService.list(expectedPrefix);

        assertEquals(expectedNames.size(), actualResource.size());
        assertTrue(expectedNames.containsAll(actualResource));
    }

    @Test
    void givenAValidName_whenCallsDeleteAll_shouldDeleteAll() {
        final var resourcesToDelete = List.of(
                "video_" + UUID.randomUUID(),
                "video_" + UUID.randomUUID(),
                "video_" + UUID.randomUUID()
        );
        final var expectedNames = List.of(
                "video_" + UUID.randomUUID(),
                "video_" + UUID.randomUUID(),
                "video_" + UUID.randomUUID()
        );

        Stream.concat(
                expectedNames.stream(),
                resourcesToDelete.stream()
        ).forEach(name -> storeService.getStorage().put(name, Fixture.Videos.resource(VideoMediaType.VIDEO)));

        storeService.deleteAll(resourcesToDelete);

        assertEquals(expectedNames.size(), storeService.getStorage().size());
        assertTrue(expectedNames.containsAll(storeService.getStorage().keySet()));
    }
}