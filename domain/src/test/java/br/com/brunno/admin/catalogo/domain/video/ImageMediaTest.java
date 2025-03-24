package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.UnitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
class ImageMediaTest {

    @Test
    void givenAValidParams_whenCallsNewImage_shouldInstantiate() {
        final var expectedChecksum = "abc";
        final var expectedName = "image.png";
        final var expectedLocation = "/abc/image.png";

        final var actualImageMedia = ImageMedia.with(expectedChecksum, expectedName, expectedLocation);

        assertNotNull(actualImageMedia);
        assertEquals(expectedChecksum, actualImageMedia.checksum());
        assertEquals(expectedName, actualImageMedia.name());
        assertEquals(expectedLocation, actualImageMedia.location());
    }

    @Test
    void givenTwoImagensWithSameComparableAttributes_whenComparingBoth_shouldReturnEquals() {
        final var img1 = ImageMedia.with("abc", "random", "/abc/image.png");
        final var img2 = ImageMedia.with("abc", "single", "/abc/image.png");

        assertEquals(img1, img2);
        assertNotSame(img1, img2);
    }

    @Test
    void givenInvalidNullParams_whenCreatesAnImage_shouldThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> ImageMedia.with(null, "image.png", "/abc/image.png"));

        assertThrows(NullPointerException.class,
                () -> ImageMedia.with("abc", null, "/abc/image.png"));

        assertThrows(NullPointerException.class,
                () -> ImageMedia.with("abc", "image.png", null));
    }
}