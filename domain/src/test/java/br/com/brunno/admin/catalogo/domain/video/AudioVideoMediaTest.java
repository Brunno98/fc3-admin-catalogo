package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.UnitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
class AudioVideoMediaTest {

    @Test
    void givenAValidParams_whenCallsNewVideo_shouldInstantiate() {
        final var expectedId = DomainId.generate();
        final var expectedChecksum = "abc";
        final var expectedName = "video.mp4";
        final var expectedRawLocation = "/raw/video.mp4";
        final var expectedEncodedLocation = "/encoded/video.mp4";
        final var expectedStatus = MediaStatus.PENDING;

        final var actualImageMedia = AudioVideoMedia.with(
                expectedId,
                expectedChecksum,
                expectedName,
                expectedRawLocation,
                expectedEncodedLocation,
                expectedStatus
        );

        assertNotNull(actualImageMedia);
        assertEquals(expectedId, actualImageMedia.id());
        assertEquals(expectedChecksum, actualImageMedia.checksum());
        assertEquals(expectedName, actualImageMedia.name());
        assertEquals(expectedRawLocation, actualImageMedia.rawLocation());
        assertEquals(expectedEncodedLocation, actualImageMedia.encodedLocation());
        assertEquals(expectedStatus, actualImageMedia.status());
    }

    //TODO: Reavaliar esse teste apos incluisão do campo 'id' no dominio AudioVideoMedia
    @Test
    void givenTwoVideosWithSameComparableAttributes_whenComparingBoth_shouldReturnEquals() {
        final var video1 = AudioVideoMedia.with(
                DomainId.generate(),
                "abc",
                "random",
                "/raw/video.mp4",
                "/encoded/random.mp4",
                MediaStatus.PENDING
        );
        final var video2 = AudioVideoMedia.with(
                DomainId.generate(),
                "abc",
                "single",
                "/raw/video.mp4",
                "/encoded/single.mp4",
                MediaStatus.PENDING
        );;

        assertEquals(video1, video2);
        assertNotSame(video1, video2);
    }

    @Test
    void givenInvalidNullParams_whenCreatesAnImage_shouldThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with(
                        null,
                        "abc",
                        "random",
                        "/raw/video.mp4",
                        "/encoded/video.mp4",
                        MediaStatus.PENDING
                )
        );

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with(
                        DomainId.generate(),
                        null,
                        "random",
                        "/raw/video.mp4",
                        "/encoded/video.mp4",
                        MediaStatus.PENDING
                )
        );

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with(
                        DomainId.generate(),
                        "abc",
                        null,
                        "/raw/video.mp4",
                        "/encoded/video.mp4",
                        MediaStatus.PENDING
                )
        );

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with(
                        DomainId.generate(),
                        "abc",
                        "random",
                        null,
                        "/encoded/video.mp4",
                        MediaStatus.PENDING
                )
        );

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with(
                        DomainId.generate(),
                        "abc",
                        "random",
                        "/raw/random.png",
                        null,
                        MediaStatus.PENDING
                )
        );

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with(
                        DomainId.generate(),
                        "abc",
                        "random",
                        "/raw/random.png",
                        "/encoded/random.png",
                        null
                )
        );
    }
}