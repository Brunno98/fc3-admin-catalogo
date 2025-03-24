package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.UnitTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UnitTest
public class VideoTest {

    @Test
    void givenValidParams_whenCreateAVideo_shouldInstantiate() {
        final var expectedTitle = "Some title";
        final var expectedDescription = "a".repeat(4000);
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );

        assertNotNull(actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertNotNull(actualVideo.getCreatedAt());
        assertNotNull(actualVideo.getUpdatedAt());
        assertEquals(actualVideo.getCreatedAt(), actualVideo.getUpdatedAt());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertTrue(actualVideo.getDomainEvents().isEmpty());

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidVideo_whenCallsUpdate_shouldReturnsUpdated() {
        final var expectedTitle = "Some title";
        final var expectedDescription = "a".repeat(4000);
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());
        final var expectedEvent = new VideoMediaCreated("ID", "FILE");

        final var aVideo = Video.newVideo(
                "Test update title",
                "Test update description",
                Year.of(1900),
                30.1,
                true,
                true,
                Rating.AGE_10,
                Set.of(),
                Set.of(),
                Set.of()
        );
        aVideo.registerDomainEvent(expectedEvent);

        final var actualVideo = Video.with(aVideo);
        actualVideo.update(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );


        assertEquals(aVideo.getId(), actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
        assertTrue(aVideo.getUpdatedAt().isBefore(actualVideo.getUpdatedAt()));
        assertEquals(1, actualVideo.getDomainEvents().size());
        assertEquals(expectedEvent, actualVideo.getDomainEvents().get(0));

        Assertions.assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidVideo_whenCallsUpdateVideoMedia_shouldUpdateIt() {
        // given
        final var expectedTitle = "Some title";
        final var expectedDescription = "a".repeat(4000);
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var initialVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        final var actualVideo = Video.with(initialVideo);
        final var aVideoMedia = AudioVideoMedia
                .with("abc", "Video.mp4", "/123/video.mp4");
        final var expectedEvent = new VideoMediaCreated(actualVideo.getId().getValue(), aVideoMedia.rawLocation());

        // when
        actualVideo.updateVideoMedia(aVideoMedia);

        // then
        assertEquals(aVideoMedia, actualVideo.getVideo().get());
        assertNotEquals(initialVideo.getVideo(), actualVideo.getVideo());
        assertTrue(initialVideo.getUpdatedAt().isBefore(actualVideo.getUpdatedAt()));
        assertEquals(1, actualVideo.getDomainEvents().size());
        assertEquals(expectedEvent, actualVideo.getDomainEvents().get(0)); //TODO: essa asserção esta tornando o teste Flaky
    }

    @Test
    void givenAValidTrailer_whenCallsUpdateTrailer_Media_shouldUpdateIt() {
        // given
        final var expectedTitle = "Some title";
        final var expectedDescription = "a".repeat(4000);
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());
        final var initialVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        final var actualVideo = Video.with(initialVideo);
        final var aTrailerMedia = AudioVideoMedia
                .with("abc", "Trailer.mp4", "/123/trailer.mp4");
        final var expectedEvent = new VideoMediaCreated(actualVideo.getId().getValue(), aTrailerMedia.rawLocation());

        // when
        actualVideo.updateTrailerMedia(aTrailerMedia);

        // then
        assertEquals(aTrailerMedia, actualVideo.getTrailer().get());
        assertNotEquals(initialVideo.getTrailer(), actualVideo.getTrailer());
        assertTrue(initialVideo.getUpdatedAt().isBefore(actualVideo.getUpdatedAt()));
        assertEquals(1, actualVideo.getDomainEvents().size());
        assertEquals(expectedEvent, actualVideo.getDomainEvents().get(0));
    }

    @Test
    void givenAValidBanner_whenCallsUpdateBanner_Media_shouldUpdateIt() {
        final var expectedTitle = "Some title";
        final var expectedDescription = "a".repeat(4000);
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var initialVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        final var actualVideo = Video.with(initialVideo);

        final var aBannerImage = ImageMedia
                .with("abc", "Banner.jpg", "/123/banner.mp4");
        actualVideo.updateBannerMedia(aBannerImage);

        assertEquals(aBannerImage, actualVideo.getBanner().get());
        assertNotEquals(initialVideo.getBanner(), actualVideo.getBanner());
        assertTrue(initialVideo.getUpdatedAt().isBefore(actualVideo.getUpdatedAt()));
    }

    @Test
    void givenAValidThumbnail_whenCallsUpdateThumbnail_Media_shouldUpdateIt() {
        final var expectedTitle = "Some title";
        final var expectedDescription = "a".repeat(4000);
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var initialVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        final var actualVideo = Video.with(initialVideo);

        final var aThumbnailImage = ImageMedia
                .with("abc", "Thumbnail.jpg", "/123/thumbnail.mp4");
        actualVideo.updateThumbnailMedia(aThumbnailImage);

        assertEquals(aThumbnailImage, actualVideo.getThumbnail().get());
        assertNotEquals(initialVideo.getThumbnail(), actualVideo.getThumbnail());
        assertTrue(initialVideo.getUpdatedAt().isBefore(actualVideo.getUpdatedAt()));
    }

    @Test
    void givenAValidThumbnailHalf_whenCallsUpdateThumbnailHalf_Media_shouldUpdateIt() {
        final var expectedTitle = "Some title";
        final var expectedDescription = "a".repeat(4000);
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var initialVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        final var actualVideo = Video.with(initialVideo);

        final var aThumbnailHalfImage = ImageMedia
                .with("abc", "Thumbnail.jpg", "/123/thumbnail.mp4");
        actualVideo.updateThumbnailHalfMedia(aThumbnailHalfImage);

        assertEquals(aThumbnailHalfImage, actualVideo.getThumbnailHalf().get());
        assertNotEquals(initialVideo.getThumbnailHalf(), actualVideo.getThumbnailHalf());
        assertTrue(initialVideo.getUpdatedAt().isBefore(actualVideo.getUpdatedAt()));
    }
}
