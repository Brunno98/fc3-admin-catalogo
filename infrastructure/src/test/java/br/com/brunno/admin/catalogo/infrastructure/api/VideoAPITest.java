package br.com.brunno.admin.catalogo.infrastructure.api;

import br.com.brunno.admin.catalogo.ControllerTest;
import br.com.brunno.admin.catalogo.application.video.create.CreateVideoOutput;
import br.com.brunno.admin.catalogo.application.video.create.CreateVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.retrieve.get.GetVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.retrieve.get.VideoOutput;
import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoOutput;
import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoUseCase;
import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.video.Rating;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;
import br.com.brunno.admin.catalogo.infrastructure.api.controllers.VideoController;
import br.com.brunno.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import br.com.brunno.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = VideoController.class)
class VideoAPITest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CreateVideoUseCase createVideoUseCase;

    @MockBean
    GetVideoUseCase getVideoUseCase;

    @MockBean
    UpdateVideoUseCase updateVideoUseCase;

    @Test
    void givenAValidCommand_whenCallsCreateFull_shouldReturnAnId() throws Exception {
        // given
        final var category = Category.newCategory("Category", null, true);
        final var genre = Genre.newGenre("Genre", true);
        final var actor = CastMember.create("Actor", CastMemberType.ACTOR);

        final var expectedVideoId = VideoID.unique();
        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedVideo =
                new MockMultipartFile("video_file", "video.mp4", "video/mp4", "VIDEO".getBytes());
        final var expectedTrailer =
                new MockMultipartFile("trailer_file", "trailer.mp4", "video/mp4", "TRAILER".getBytes());
        final var expectedBanner =
                new MockMultipartFile("banner_file", "banner.jpg", "image/jpg", "BANNER".getBytes());
        final var expectedThumbnail =
                new MockMultipartFile("thumbnail_file", "thumbnail.jpg", "image/jpg", "THUMBNAIL".getBytes());
        final var expectedThumbnailHalf =
                new MockMultipartFile("thumbnail_half_file", "thumbnail_half.jpg", "image/jpg", "THUMBNAIL_HALF".getBytes());
        doReturn(new CreateVideoOutput(expectedVideoId))
                .when(createVideoUseCase).execute(Mockito.any());

        // when
        final var aRequest = multipart("/videos")
                .file(expectedVideo)
                .file(expectedTrailer)
                .file(expectedBanner)
                .file(expectedThumbnail)
                .file(expectedThumbnailHalf)
                .param("title", expectedTitle)
                .param("description", expectedDescription)
                .param("year_launched", expectedLaunchYear.toString())
                .param("duration", String.valueOf(expectedDuration))
                .param("opened", String.valueOf(expectedOpened))
                .param("published", String.valueOf(expectedPublished))
                .param("rating", expectedRating.getName())
                .param("cast_member_id", actor.getId().getValue())
                .param("categories_id", category.getId().getValue())
                .param("genres_id", genre.getId().getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isCreated(),
                header().string("Location", "/videos/" + expectedVideoId.getValue()),
                header().string("content-type", MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.id", equalTo(expectedVideoId.getValue()))
        );
    }


    @Test
    void givenAValidCommand_whenCallsCreatePartial_shouldReturnId() throws Exception {
        // given
        final var category = Category.newCategory("Category", null, true);
        final var genre = Genre.newGenre("Genre", true);
        final var actor = CastMember.create("Actor", CastMemberType.ACTOR);

        final var expectedVideoId = VideoID.unique();
        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(category.getId().getValue());
        final var expectedGenres = Set.of(genre.getId().getValue());
        final var expectedCastMembers = Set.of(actor.getId().getValue());

        final var requestBody = new CreateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );

        doReturn(new CreateVideoOutput(expectedVideoId))
                .when(createVideoUseCase).execute(Mockito.any());

        // when
        final var aRequest = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isCreated(),
                header().string("Location", "/videos/" + expectedVideoId.getValue()),
                header().string("content-type", MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.id", equalTo(expectedVideoId.getValue()))
        );
    }

    @Test
    void givenAValidId_whenCallsGetById_shouldReturnVideo() throws Exception {
        // given
        final var category = Category.newCategory("Category", null, true);
        final var genre = Genre.newGenre("Genre", true);
        final var actor = CastMember.create("Actor", CastMemberType.ACTOR);

        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = List.of(category.getId().getValue());
        final var expectedGenres = List.of(genre.getId().getValue());
        final var expectedCastMembers = List.of(actor.getId().getValue());
        final var expectedVideo = Fixture.Videos.audioVideo(VideoMediaType.VIDEO);
        final var expectedTrailer = Fixture.Videos.audioVideo(VideoMediaType.TRAILER);
        final var expectedBanner = Fixture.Videos.image(VideoMediaType.BANNER);
        final var expectedThumbnail = Fixture.Videos.image(VideoMediaType.THUMBNAIL);
        final var expectedThumbnailHalf = Fixture.Videos.image(VideoMediaType.THUMBNAIL_HALF);

        final var aVideo = Video.newVideo(
                expectedTitle, expectedDescription, expectedLaunchYear, expectedDuration, expectedOpened,
                expectedPublished, expectedRating,
                expectedCategories.stream().map(CategoryId::from).collect(Collectors.toSet()),
                expectedGenres.stream().map(GenreID::from).collect(Collectors.toSet()),
                expectedCastMembers.stream().map(CastMemberID::from).collect(Collectors.toSet())
        );
        aVideo.updateVideoMedia(expectedVideo);
        aVideo.updateTrailerMedia(expectedTrailer);
        aVideo.updateBannerMedia(expectedBanner);
        aVideo.updateThumbnailMedia(expectedThumbnail);
        aVideo.updateThumbnailHalfMedia(expectedThumbnailHalf);

        final var expectedId = aVideo.getId();

        doReturn(VideoOutput.from(aVideo)).when(getVideoUseCase).execute(expectedId);

        // when
        final var aRequest = get("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON);

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isOk(),
                header().string("content-type", MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.id", equalTo(expectedId.getValue())),
                jsonPath("$.title", equalTo(expectedTitle)),
                jsonPath("$.year_launched", equalTo(expectedLaunchYear.getValue())),
                jsonPath("$.duration", equalTo(expectedDuration)),
                jsonPath("$.opened", equalTo(expectedOpened)),
                jsonPath("$.published", equalTo(expectedPublished)),
                jsonPath("$.rating", equalTo(expectedRating.getName())),
                jsonPath("$.created_at", equalTo(aVideo.getCreatedAt().toString())),
                jsonPath("$.updated_at", equalTo(aVideo.getUpdatedAt().toString())),

                jsonPath("$.banner.id", equalTo(expectedBanner.id().getValue())),
                jsonPath("$.banner.name", equalTo(expectedBanner.name())),
                jsonPath("$.banner.location", equalTo(expectedBanner.location())),
                jsonPath("$.banner.checksum", equalTo(expectedBanner.checksum())),

                jsonPath("$.thumbnail.id", equalTo(expectedThumbnail.id().getValue())),
                jsonPath("$.thumbnail.name", equalTo(expectedThumbnail.name())),
                jsonPath("$.thumbnail.location", equalTo(expectedThumbnail.location())),
                jsonPath("$.thumbnail.checksum", equalTo(expectedThumbnail.checksum())),

                jsonPath("$.thumbnail_half.id", equalTo(expectedThumbnailHalf.id().getValue())),
                jsonPath("$.thumbnail_half.name", equalTo(expectedThumbnailHalf.name())),
                jsonPath("$.thumbnail_half.location", equalTo(expectedThumbnailHalf.location())),
                jsonPath("$.thumbnail_half.checksum", equalTo(expectedThumbnailHalf.checksum())),

                jsonPath("$.video.id", equalTo(expectedVideo.id().getValue())),
                jsonPath("$.video.name", equalTo(expectedVideo.name())),
                jsonPath("$.video.location", equalTo(expectedVideo.rawLocation())),
                jsonPath("$.video.encoded_location", equalTo(expectedVideo.encodedLocation())),
                jsonPath("$.video.checksum", equalTo(expectedVideo.checksum())),
                jsonPath("$.video.status", equalTo(expectedVideo.status().name())),

                jsonPath("$.trailer.id", equalTo(expectedTrailer.id().getValue())),
                jsonPath("$.trailer.name", equalTo(expectedTrailer.name())),
                jsonPath("$.trailer.location", equalTo(expectedTrailer.rawLocation())),
                jsonPath("$.trailer.encoded_location", equalTo(expectedTrailer.encodedLocation())),
                jsonPath("$.trailer.checksum", equalTo(expectedTrailer.checksum())),
                jsonPath("$.trailer.status", equalTo(expectedTrailer.status().name())),

                jsonPath("$.categories_id", equalTo(expectedCategories)),
                jsonPath("$.genres_id", equalTo(expectedGenres)),
                jsonPath("$.cast_members_id", equalTo(expectedCastMembers))

        );
    }

    @Test
    void givenAValidCommand_whenCallsUpdate_shouldReturnVideoId() throws Exception {
        // given
        final var category = Category.newCategory("Category", null, true);
        final var genre = Genre.newGenre("Genre", true);
        final var actor = CastMember.create("Actor", CastMemberType.ACTOR);

        final var expectedVideoId = VideoID.unique();
        final var expectedTitle = "a title";
        final var expectedDescription = "a description";
        final var expectedLaunchYear = Year.of(2022);
        final var expectedDuration = 120.1;
        final var expectedOpened = true;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(category.getId().getValue());
        final var expectedGenres = Set.of(genre.getId().getValue());
        final var expectedCastMembers = Set.of(actor.getId().getValue());

        final var requestBody = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedCategories,
                expectedGenres,
                expectedCastMembers
        );

        doReturn(new UpdateVideoOutput(expectedVideoId))
                .when(updateVideoUseCase).execute(Mockito.any());

        // when
        final var aRequest = put("/videos/{id}", expectedVideoId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isOk(),
                header().string("Location", "/videos/" + expectedVideoId.getValue()),
                header().string("content-type", MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.id", equalTo(expectedVideoId.getValue()))
        );
    }

    @Test
    void givenAnInvalidCommand_whenCallsUpdate_shouldReturnNotification() throws Exception {
        // given
        final var expectedErrorMessage = "Error was threw to test";
        final var expectedErrorCount = 1;

        final var requestBody = new UpdateVideoRequest(
                "a title",
                "a description",
                2022,
                120.1,
                true,
                true,
                "L",
                Set.of(CategoryId.unique().getValue()),
                Set.of(GenreID.unique().getValue()),
                Set.of(CastMemberID.unique().getValue())
        );

        doThrow(NotificationException.with(new Error(expectedErrorMessage)))
                .when(updateVideoUseCase).execute(Mockito.any());

        // when
        final var aRequest = put("/videos/{id}", VideoID.unique().getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isUnprocessableEntity(),
                header().string("content-type", MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.message", equalTo(expectedErrorMessage)),
                jsonPath("$.errors", hasSize(expectedErrorCount)),
                jsonPath("$.errors[0].message", equalTo(expectedErrorMessage))
        );
    }
}












