package br.com.brunno.admin.catalogo.infrastructure.video;

import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.Fixture;
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
import br.com.brunno.admin.catalogo.domain.video.VideoSearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCastMemberID;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCastMemberJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCategoryID;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoCategoryJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoGenreId;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoGenreJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.Year;
import java.util.List;
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
    
    private final Category filme = Category.newCategory("Filme", null, true);
    private final Category serie = Category.newCategory("Serie", null, true);
    private final Genre aventura = Genre.newGenre("Aventura", true, List.of());
    private final Genre suspense = Genre.newGenre("Suspense", true, List.of());
    private final CastMember tomHanks = CastMember.create("Tom Hanks", CastMemberType.ACTOR);
    private final CastMember vinDiesel = CastMember.create("Vin Diesel", CastMemberType.ACTOR);

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
        aVideo.updateVideoMedia(anAudioVideo);
        aVideo.updateTrailerMedia(aTrailer);
        aVideo.updateBannerMedia(aBanner);
        aVideo.updateThumbnailMedia(aThumbnail);
        aVideo.updateThumbnailHalfMedia(aThumbnailHalf);

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
        updatedVideo.updateVideoMedia(anAudioVideo);
        updatedVideo.updateTrailerMedia(aTrailer);
        updatedVideo.updateBannerMedia(aBanner);
        updatedVideo.updateThumbnailMedia(aThumbnail);
        updatedVideo.updateThumbnailHalfMedia(aThumbnailHalf);

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
        aVideo.updateVideoMedia(anAudioVideo);
        aVideo.updateTrailerMedia(aTrailer);
        aVideo.updateBannerMedia(aBanner);
        aVideo.updateThumbnailMedia(aThumbnail);
        aVideo.updateThumbnailHalfMedia(aThumbnailHalf);

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

    @Test
    @Transactional
    void givenEmptyParams_whenCallFindAll_shouldReturnAllList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final String expectedTerms = null;
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 4;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualResult = videoGateway.findAll(aQuery);

        System.out.println(
                actualResult.items().stream().map(it -> it.title())
                        .collect(Collectors.joining(", "))
        );

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
    }

    @Test
    void givenEmptyVideos_whenCallFindAll_shouldReturnEmptyList() {

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualResult = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
    }

    @Test
    void givenAValidCategories_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(filme.getId()),
                Set.of(),
                Set.of()
        );

        final var actualResult = videoGateway.findAll(aQuery);

        System.out.println(
                actualResult.items().stream().map(it -> it.title())
                        .collect(Collectors.joining(", "))
        );

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
        assertEquals("Sombras do Amanhã", actualResult.items().get(0).title());
        assertEquals("Tempestade Lunar", actualResult.items().get(1).title());
    }

    @Test
    void givenAValidCastMember_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of(tomHanks.getId())
        );

        final var actualResult = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
        assertEquals("Ruínas do Destino", actualResult.items().get(0).title());
        assertEquals("Sombras do Amanhã", actualResult.items().get(1).title());

    }

    @Test
    void givenAValidGenre_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(suspense.getId()),
                Set.of()
        );

        final var actualResult = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
        assertEquals("Sombras do Amanhã", actualResult.items().get(0).title());
        assertEquals("Tempestade Lunar", actualResult.items().get(1).title());
    }

    @Test
    void givenAllParams_whenCallFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(filme.getId()),
                Set.of(suspense.getId()),
                Set.of(vinDiesel.getId())
        );

        final var actualResult = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
        assertEquals("Sombras do Amanhã", actualResult.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,4,O Último Código;Ruínas do Destino",
            "1,2,2,4,Sombras do Amanhã;Tempestade Lunar",
    })
    void givenAValidPaging_whenCallFindAll_shouldReturnPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedVideos
    ) {
        mockVideos();
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualResult = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedItemsCount, actualResult.items().size());
        var index = 0;
        for (String expectedGenreName : expectedVideos.split(";")) {
            assertEquals(expectedGenreName, actualResult.items().get(index++).title());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "somb,0,10,1,1,Sombras do Amanhã",
            "ódig,0,10,1,1,O Último Código",
            "nar,0,10,1,1,Tempestade Lunar",
            "uín,0,10,1,1,Ruínas do Destino",
    })
    void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedGenreName
    ) {
        mockVideos();

        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualResult = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedGenreName, actualResult.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "title,asc,0,10,4,4,O Último Código",
            "title,desc,0,10,4,4,Tempestade Lunar",
            "createdAt,asc,0,10,4,4,Sombras do Amanhã",
            "createdAt,desc,0,10,4,4,Ruínas do Destino",
    })
    void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnFiltered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedGenreName
    ) {
        mockVideos();

        final var expectedTerms = "";
        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var actualResult = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedGenreName, actualResult.items().get(0).title());
    }

    private void mockVideos() {
        categoryGateway.create(this.filme);
        categoryGateway.create(this.serie);


        genreGateway.create(this.aventura);
        genreGateway.create(this.suspense);

        castMemberGateway.create(this.tomHanks);
        castMemberGateway.create(this.vinDiesel);

        final var sombrasDoAmanha = Video.newVideo(
                "Sombras do Amanhã",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                Fixture.Videos.rating(),
                Set.of(filme.getId()),
                Set.of(suspense.getId()),
                Set.of(tomHanks.getId(), vinDiesel.getId())
        );

        final var oUltimoCodigo = Video.newVideo(
                "O Último Código",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                Fixture.Videos.rating(),
                Set.of(),
                Set.of(),
                Set.of()
        );

        final var tempestadeLunar = Video.newVideo(
                "Tempestade Lunar",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                Fixture.Videos.rating(),
                Set.of(filme.getId()),
                Set.of(suspense.getId()),
                Set.of(vinDiesel.getId())
        );

        final var ruinasDoDestino = Video.newVideo(
                "Ruínas do Destino",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                Fixture.Videos.rating(),
                Set.of(serie.getId()),
                Set.of(aventura.getId()),
                Set.of(tomHanks.getId())
        );

        videoRepository.saveAllAndFlush(List.of(
                VideoJpaEntity.from(sombrasDoAmanha),
                VideoJpaEntity.from(oUltimoCodigo),
                VideoJpaEntity.from(tempestadeLunar),
                VideoJpaEntity.from(ruinasDoDestino)
        ));
    }
}