package br.com.brunno.admin.catalogo.infrastructure.video;

import br.com.brunno.admin.catalogo.CleanupMySQLExtension;
import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import br.com.brunno.admin.catalogo.domain.video.ImageMedia;
import br.com.brunno.admin.catalogo.domain.video.Rating;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCastMemberID;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCastMemberJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCategoryID;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCategoryJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoGenreId;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoGenreJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.Year;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
class DefaultVideoGatewayTest  {

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    void testInjection() {
        assertNotNull(videoGateway);
        assertNotNull(categoryGateway);
        assertNotNull(genreGateway);
        assertNotNull(castMemberGateway);
        assertNotNull(videoRepository);
    }

    @Test
    @Transactional
    void givenAValidVideoWithRelationships_whenCallsCreate_shouldPersistIt() {

        final var aCategory = categoryGateway.create(Category.newCategory("A Category", "description", true));
        final var aGenre = genreGateway.create(Genre.newGenre("A Genre", true));
        final var aMember = castMemberGateway.create(CastMember.create("A member", CastMemberType.ACTOR));

        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(aCategory.getId());
        final var expectedGenres = Set.of(aGenre.getId());
        final var expectedCastMembers = Set.of(aMember.getId());

        final var anAudioVideo = AudioVideoMedia.with("123", "video", "/raw/location/video");
        final var aTrailer = AudioVideoMedia.with("123", "trailer", "/raw/location/trailer");
        final var aBanner = ImageMedia.with("123", "banner", "/raw/location/banner");
        final var aThumbnail = ImageMedia.with("123", "thumbnail", "/raw/location/thumbnail");
        final var aThumbnailHalf = ImageMedia.with("123", "thumbnail half", "/raw/location/thumbnail_half");

        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        aVideo.setVideo(anAudioVideo);
        aVideo.setTrailer(aTrailer);
        aVideo.setBanner(aBanner);
        aVideo.setThumbnail(aThumbnail);
        aVideo.setThumbnailHalf(aThumbnailHalf);

        final var actualVideo = videoGateway.create(aVideo);

        assertNotNull(actualVideo);
        assertEquals(aVideo.getId(), actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertEquals(anAudioVideo, actualVideo.getVideo().get());
        assertEquals(aTrailer, actualVideo.getTrailer().get());
        assertEquals(aBanner, actualVideo.getBanner().get());
        assertEquals(aThumbnail, actualVideo.getThumbnail().get());
        assertEquals(aThumbnailHalf, actualVideo.getThumbnailHalf().get());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertNotNull(persistedVideo);
        assertEquals(aVideo.getId().getValue(), persistedVideo.getId());
        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchYear, Year.of(persistedVideo.getLaunchedYear()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertTrue(expectedCategories.size() == persistedVideo.getCategories().size()
                && expectedCategories.containsAll(persistedVideo.getCategories().stream()
                .map(VideoCategoryJpaEntity::getId)
                .map(VideoCategoryID::getCategoryId)
                .map(CategoryId::from)
                .collect(Collectors.toSet())));
        assertTrue(expectedGenres.size() == persistedVideo.getGenres().size()
                && expectedGenres.containsAll(persistedVideo.getGenres().stream()
                .map(VideoGenreJpaEntity::getId)
                .map(VideoGenreId::getGenreId)
                .map(GenreID::from)
                .collect(Collectors.toSet())));
        assertTrue(expectedCastMembers.size() == persistedVideo.getCastMembers().size()
                && expectedCastMembers.containsAll(persistedVideo.getCastMembers().stream()
                .map(VideoCastMemberJpaEntity::getId)
                .map(VideoCastMemberID::getCastMemberId)
                .map(CastMemberID::from)
                .collect(Collectors.toSet())));
        assertEquals(anAudioVideo, persistedVideo.getVideo().toDomain());
        assertEquals(aTrailer, persistedVideo.getTrailer().toDomain());
        assertEquals(aBanner, persistedVideo.getBanner().toDomain());
        assertEquals(aThumbnail, persistedVideo.getThumbnail().toDomain());
        assertEquals(aThumbnailHalf, persistedVideo.getThumbnailHalf().toDomain());
    }


    @Test
    @Transactional
    void givenAValidVideoWithoutRelationships_whenCallsCreate_shouldPersistIt() {

        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.<CategoryId>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );

        final var actualVideo = videoGateway.create(aVideo);

        assertNotNull(actualVideo);
        assertEquals(aVideo.getId(), actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertTrue(actualVideo.getVideo().isEmpty());
        assertTrue(actualVideo.getTrailer().isEmpty());
        assertTrue(actualVideo.getBanner().isEmpty());
        assertTrue(actualVideo.getThumbnail().isEmpty());
        assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertNotNull(persistedVideo);
        assertEquals(aVideo.getId().getValue(), persistedVideo.getId());
        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchYear, Year.of(persistedVideo.getLaunchedYear()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertTrue(persistedVideo.getCategories().isEmpty());
        assertTrue(persistedVideo.getGenres().isEmpty());
        assertTrue(persistedVideo.getCastMembers().isEmpty());
        assertNull(persistedVideo.getVideo());
        assertNull(persistedVideo.getTrailer());
        assertNull(persistedVideo.getBanner());
        assertNull(persistedVideo.getThumbnail());
        assertNull(persistedVideo.getThumbnailHalf());
    }


    @Test
    @Transactional
    void givenAValidVideo_whenCallsUpdate_shouldPersistIt() {
        final var aVideo = Video.newVideo(
                "a title",
                "a description",
                Year.of(1900),
                1.1,
                false,
                false,
                Rating.ER,
                Set.of(),
                Set.of(),
                Set.of()
        );
        videoGateway.create(aVideo);
        final var aCategory = categoryGateway.create(Category.newCategory("A Category", "description", true));
        final var aGenre = genreGateway.create(Genre.newGenre("A Genre", true));
        final var aMember = castMemberGateway.create(CastMember.create("A member", CastMemberType.ACTOR));

        final var expectedTitle = "an updated title";
        final var expectedDescription = "an updated description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(aCategory.getId());
        final var expectedGenres = Set.of(aGenre.getId());
        final var expectedCastMembers = Set.of(aMember.getId());

        final var anAudioVideo = AudioVideoMedia.with("123", "video", "/raw/location/video");
        final var aTrailer = AudioVideoMedia.with("123", "trailer", "/raw/location/trailer");
        final var aBanner = ImageMedia.with("123", "banner", "/raw/location/banner");
        final var aThumbnail = ImageMedia.with("123", "thumbnail", "/raw/location/thumbnail");
        final var aThumbnailHalf = ImageMedia.with("123", "thumbnail half", "/raw/location/thumbnail_half");

        final var updatedVideo = Video.with(aVideo);
        updatedVideo.update(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        updatedVideo.setVideo(anAudioVideo);
        updatedVideo.setTrailer(aTrailer);
        updatedVideo.setBanner(aBanner);
        updatedVideo.setThumbnail(aThumbnail);
        updatedVideo.setThumbnailHalf(aThumbnailHalf);

        final var actualVideo = videoGateway.update(updatedVideo);

        assertNotNull(actualVideo);
        assertEquals(aVideo.getId(), actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertEquals(anAudioVideo, actualVideo.getVideo().get());
        assertEquals(aTrailer, actualVideo.getTrailer().get());
        assertEquals(aBanner, actualVideo.getBanner().get());
        assertEquals(aThumbnail, actualVideo.getThumbnail().get());
        assertEquals(aThumbnailHalf, actualVideo.getThumbnailHalf().get());
        assertTrue(aVideo.getUpdatedAt().isBefore(actualVideo.getUpdatedAt()));

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertNotNull(persistedVideo);
        assertEquals(aVideo.getId().getValue(), persistedVideo.getId());
        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchYear, Year.of(persistedVideo.getLaunchedYear()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertTrue(expectedCategories.size() == persistedVideo.getCategories().size()
                && expectedCategories.containsAll(persistedVideo.getCategories().stream()
                .map(VideoCategoryJpaEntity::getId)
                .map(VideoCategoryID::getCategoryId)
                .map(CategoryId::from)
                .collect(Collectors.toSet())));
        assertTrue(expectedGenres.size() == persistedVideo.getGenres().size()
                && expectedGenres.containsAll(persistedVideo.getGenres().stream()
                .map(VideoGenreJpaEntity::getId)
                .map(VideoGenreId::getGenreId)
                .map(GenreID::from)
                .collect(Collectors.toSet())));
        assertTrue(expectedCastMembers.size() == persistedVideo.getCastMembers().size()
                && expectedCastMembers.containsAll(persistedVideo.getCastMembers().stream()
                .map(VideoCastMemberJpaEntity::getId)
                .map(VideoCastMemberID::getCastMemberId)
                .map(CastMemberID::from)
                .collect(Collectors.toSet())));
        assertEquals(anAudioVideo, persistedVideo.getVideo().toDomain());
        assertEquals(aTrailer, persistedVideo.getTrailer().toDomain());
        assertEquals(aBanner, persistedVideo.getBanner().toDomain());
        assertEquals(aThumbnail, persistedVideo.getThumbnail().toDomain());
        assertEquals(aThumbnailHalf, persistedVideo.getThumbnailHalf().toDomain());
        assertTrue(aVideo.getUpdatedAt().isBefore(persistedVideo.getUpdatedAt()));

    }

    @Test
    void givenAValidVideoId_whenCallsDelete_shouldDeleteIt() {
        final var aVideo = Video.newVideo(
                "a title",
                "a description",
                Year.of(1900),
                1.1,
                false,
                false,
                Rating.ER,
                Set.of(),
                Set.of(),
                Set.of()
        );

        assertTrue(videoGateway.findById(aVideo.getId()).isEmpty());

        videoGateway.create(aVideo);

        System.out.println("=================");
        System.out.println(aVideo.getGenres().stream().map(it -> it.getValue()).collect(Collectors.joining(", ")));
        System.out.println("=================");

        assertEquals(1, videoRepository.count());

        videoGateway.deleteById(aVideo.getId());

        assertEquals(0, videoRepository.count());
    }

    @Test
    void givenAnInvalidVideoId_whenCallsDelete_shouldDoNothing() {
        final var aVideo = Video.newVideo(
                "a title",
                "a description",
                Year.of(1900),
                1.1,
                false,
                false,
                Rating.AGE_16,
                Set.of(),
                Set.of(),
                Set.of()
        );
        videoGateway.create(aVideo);

        assertEquals(1, videoRepository.count());

        videoGateway.deleteById(VideoID.unique());

        assertEquals(1, videoRepository.count());
    }


    @Test
    void givenAValidVideoId_whenCallsFindById_shouldReturnIt() {

        final var aCategory = categoryGateway.create(Category.newCategory("A Category", "description", true));
        final var aGenre = genreGateway.create(Genre.newGenre("A Genre", true));
        final var aMember = castMemberGateway.create(CastMember.create("A member", CastMemberType.ACTOR));

        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(aCategory.getId());
        final var expectedGenres = Set.of(aGenre.getId());
        final var expectedCastMembers = Set.of(aMember.getId());

        final var anAudioVideo = AudioVideoMedia.with("123", "video", "/raw/location/video");
        final var aTrailer = AudioVideoMedia.with("123", "trailer", "/raw/location/trailer");
        final var aBanner = ImageMedia.with("123", "banner", "/raw/location/banner");
        final var aThumbnail = ImageMedia.with("123", "thumbnail", "/raw/location/thumbnail");
        final var aThumbnailHalf = ImageMedia.with("123", "thumbnail half", "/raw/location/thumbnail_half");

        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );
        aVideo.setVideo(anAudioVideo);
        aVideo.setTrailer(aTrailer);
        aVideo.setBanner(aBanner);
        aVideo.setThumbnail(aThumbnail);
        aVideo.setThumbnailHalf(aThumbnailHalf);

        videoGateway.create(aVideo);

        final var actualVideo = videoGateway.findById(aVideo.getId()).get();

        assertNotNull(actualVideo);
        assertEquals(aVideo.getId(), actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertEquals(anAudioVideo, actualVideo.getVideo().get());
        assertEquals(aTrailer, actualVideo.getTrailer().get());
        assertEquals(aBanner, actualVideo.getBanner().get());
        assertEquals(aThumbnail, actualVideo.getThumbnail().get());
        assertEquals(aThumbnailHalf, actualVideo.getThumbnailHalf().get());
    }


    @Test
    void givenANonExistentVideoId_whenCallsFindById_shouldReturnEmpty() {
        final var aVideo = Video.newVideo(
                "a title",
                "a description",
                Year.of(1900),
                1.1,
                false,
                false,
                Rating.ER,
                Set.of(),
                Set.of(),
                Set.of()
        );
        videoGateway.create(aVideo);

        final var nonExistentId = VideoID.unique();

        final var actualVideo = videoGateway.findById(nonExistentId);

        assertTrue(actualVideo.isEmpty());
    }
}