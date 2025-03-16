package br.com.brunno.admin.catalogo.application.video.create;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.Identifier;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.InternalErroException;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import br.com.brunno.admin.catalogo.domain.video.ImageMedia;
import br.com.brunno.admin.catalogo.domain.video.MediaResourceGateway;
import br.com.brunno.admin.catalogo.domain.video.MediaStatus;
import br.com.brunno.admin.catalogo.domain.video.Rating;
import br.com.brunno.admin.catalogo.domain.video.Resource;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateVideoUseCaseTest {

    @InjectMocks
    private CreateVideoUseCase createVideoUseCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Test
    void givenAValidCommand_whenCallsCreateAVideo_shouldReturnVideoId() {
        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf
        );

        when(categoryGateway.existisByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existisByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(castMemberGateway.existisByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        when(mediaResourceGateway.storeAudioVideo(any(), any()))
                .thenReturn(AudioVideoMedia.with(
                        DomainId.generate().getValue(),
                        "audioVideoMedia",
                        "/location",
                        "",
                        MediaStatus.PENDING
                ));

        when(mediaResourceGateway.storeImage(any(), any()))
                .thenReturn(ImageMedia.with(
                        DomainId.generate().getValue(),
                        "audioVideoMedia",
                        "/location"
                ));

        final var actualResult = createVideoUseCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(it ->
                            Objects.equals(expectedTitle, it.getTitle())
                            && Objects.equals(expectedDescription, it.getDescription())
                            && Objects.equals(expectedLaunchYear, it.getLaunchedAt())
                            && Objects.equals(expectedDuration, it.getDuration())
                            && Objects.equals(expectedOpened, it.isOpened())
                            && Objects.equals(expectedPublished, it.isPublished())
                            && Objects.equals(expectedRating, it.getRating())
                            && Objects.equals(expectedCategories, it.getCategories())
                            && Objects.equals(expectedGenres, it.getGenres())
                            && Objects.equals(expectedCastMembers, it.getCastMembers())
                            && it.getVideo().isPresent()
                            && it.getTrailer().isPresent()
                            && it.getBanner().isPresent()
                            && it.getThumbnail().isPresent()
                            && it.getThumbnailHalf().isPresent()
//                            && Objects.equals(expectedVideo.name(), it.getVideo().get().name())
//                            && Objects.equals(expectedTrailer.name(), it.getTrailer().get().name())
//                            && Objects.equals(expectedBanner.name(), it.getBanner().get().name())
//                            && Objects.equals(expectedThumbnail.name(), it.getThumbnail().get().name())
//                            && Objects.equals(expectedThumbnailHalf.name(), it.getThumbnailHalf().get().name())
        ));
    }

    @Test
    void givenAValidCommand_whenCallsCreateAVideoThrowsException_shouldClearResource() {
        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryId.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf
        );

        when(categoryGateway.existisByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existisByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(castMemberGateway.existisByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(mediaResourceGateway.storeAudioVideo(any(), any()))
                .thenReturn(AudioVideoMedia.with(
                        DomainId.generate().getValue(),
                        "audioVideoMedia",
                        "/location",
                        "",
                        MediaStatus.PENDING
                ));

        when(mediaResourceGateway.storeImage(any(), any()))
                .thenReturn(ImageMedia.with(
                        DomainId.generate().getValue(),
                        "audioVideoMedia",
                        "/location"
                ));

        when(videoGateway.create(any()))
                .thenThrow(new RuntimeException("Internal Server Error"));

        final var actualError = assertThrows(InternalErroException.class, () -> createVideoUseCase.execute(aCommand));

        assertTrue(actualError.getMessage().contains("An error was observed when create a video id"));

        verify(mediaResourceGateway).clearResources(any());
    }

    private Set<String> asString(Collection<? extends Identifier> id) {
        return id.stream()
                .map(Identifier::getValue)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
