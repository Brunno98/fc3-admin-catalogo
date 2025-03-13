package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.DomainException;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VideoValidatorTest {

    @Test
    void givenAInvalidNullTitle_whenCreateAVideo_shouldReceiveError() {
        final String inputName = null;
        final var inputDescription = "a".repeat(4000);
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be null";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );


        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidEmptyTitle_whenCreateAVideo_shouldReceiveError() {
        final var inputName = "";
        final var inputDescription = "a".repeat(4000);
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be blank";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var validator = new VideoValidator(actualVideo, new ThrowsValidationHandler());

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidTitleLesserThan3Characters_whenCreateAVideo_shouldReceiveError() {
        final String inputName = " aa ";
        final var inputDescription = "a".repeat(4000);
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' length should be between 3 and 255";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidTitleGreaterThan255Characters_whenCreateAVideo_shouldReceiveError() {
        final var inputName = "a".repeat(256);
        final var inputDescription = "a".repeat(4000);
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' length should be between 3 and 255";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullDescription_whenCreateAVideo_shouldReceiveError() {
        final var inputName = "a title";
        final String inputDescription = null;
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidEmptyDescription_whenCreateAVideo_shouldReceiveError() {
        final var inputName = "a title";
        final var inputDescription = "";
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be blank";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidDescriptionGreaterThan4000Characters_whenCreateAVideo_shouldReceiveError() {
        final var inputName = "a title";
        final var inputDescription = "a".repeat(4001);
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' length should be between 1 and 4000";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullLaunchedAt_whenCreateAVideo_shouldReceiveError() {
        final var inputName = "a title";
        final var inputDescription = "a".repeat(4000);
        final Year inputLaunchedAt = null;
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final var inputRating = Rating.L;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'launchedAt' should not be null";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullRating_whenCreateAVideo_shouldReceiveError() {
        final var inputName = "a title";
        final var inputDescription = "a".repeat(4000);
        final var inputLaunchedAt = Year.of(2022);
        final var inputDuration = 120.10;
        final var inputOpened = false;
        final var inputPublished = false;
        final Rating inputRating = null;
        final var inputCategories = Set.of(CategoryId.unique());
        final var inputGenres = Set.of(GenreID.unique());
        final var inputCastMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'rating' should not be null";

        final var actualVideo = Video.newVideo(
                inputName,
                inputDescription,
                inputLaunchedAt,
                inputDuration,
                inputOpened,
                inputPublished,
                inputRating,
                inputCategories,
                inputGenres,
                inputCastMembers
        );

        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}
