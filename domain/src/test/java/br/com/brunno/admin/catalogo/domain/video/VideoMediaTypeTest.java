package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.UnitTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UnitTest
class VideoMediaTypeTest {

    @ParameterizedTest
    @EnumSource(VideoMediaType.class)
    void givenAValidValue_whenGetVideoMediaTypeFromStringValue_shouldReturnIt(VideoMediaType expectedType) {
        final var actualType = VideoMediaType.of(expectedType.name());
        assertTrue(actualType.isPresent());
        assertEquals(expectedType, actualType.get());
    }

    @ParameterizedTest
    @EnumSource(VideoMediaType.class)
    void givenAValidLowerCaseValue_whenGetVideoMediaTypeFromStringValue_shouldReturnIt(VideoMediaType expectedType) {
        final var actualType = VideoMediaType.of(expectedType.name().toLowerCase());
        assertTrue(actualType.isPresent());
        assertEquals(expectedType, actualType.get());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid"})
    void givenAnInvalidValue_whenGetVideoMediaTypeFromStringValue_shouldReturnEmpty(String invalidValue) {
        assertTrue(VideoMediaType.of(invalidValue).isEmpty());
    }
}