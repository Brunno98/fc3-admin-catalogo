package br.com.brunno.admin.catalogo.infrastructure.api;

import br.com.brunno.admin.catalogo.ApiTest;
import br.com.brunno.admin.catalogo.ControllerTest;
import br.com.brunno.admin.catalogo.application.video.create.CreateVideoOutput;
import br.com.brunno.admin.catalogo.application.video.create.CreateVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.media.get.GetMediaUseCase;
import br.com.brunno.admin.catalogo.application.video.media.get.MediaOutput;
import br.com.brunno.admin.catalogo.application.video.media.upload.MediaUploadUseCase;
import br.com.brunno.admin.catalogo.application.video.media.upload.UploadMediaOutput;
import br.com.brunno.admin.catalogo.application.video.retrieve.get.GetVideoUseCase;
import br.com.brunno.admin.catalogo.application.video.retrieve.get.VideoOutput;
import br.com.brunno.admin.catalogo.application.video.retrieve.list.VideoListOutput;
import br.com.brunno.admin.catalogo.application.video.retrieve.list.VideoListUseCase;
import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoOutput;
import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoUseCase;
import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.video.Rating;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoMediaType;
import br.com.brunno.admin.catalogo.domain.video.VideoPreview;
import br.com.brunno.admin.catalogo.infrastructure.api.controllers.VideoController;
import br.com.brunno.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import br.com.brunno.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @MockBean
    DeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    VideoListUseCase videoListUseCase;

    @MockBean
    GetMediaUseCase getMediaUseCase;

    @MockBean
    MediaUploadUseCase mediaUploadUseCase;

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
                .when(createVideoUseCase).execute(any());

        // when
        final var aRequest = multipart("/videos")
                .file(expectedVideo)
                .file(expectedTrailer)
                .file(expectedBanner)
                .file(expectedThumbnail)
                .file(expectedThumbnailHalf)
                .with(ApiTest.VIDEOS_JWT)
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
    void givenANotificationException_whenCallsCreateVideoFull_shouldReturnUnprocessableEntity() throws Exception {
        // given
        final var expectedErrorMessage = "Some error message";

        final var inputVideo =
                new MockMultipartFile("video_file", "video.mp4", "video/mp4", "VIDEO".getBytes());
        final var inputTrailer =
                new MockMultipartFile("trailer_file", "trailer.mp4", "video/mp4", "TRAILER".getBytes());
        final var inputBanner =
                new MockMultipartFile("banner_file", "banner.jpg", "image/jpg", "BANNER".getBytes());
        final var inputThumbnail =
                new MockMultipartFile("thumbnail_file", "thumbnail.jpg", "image/jpg", "THUMBNAIL".getBytes());
        final var inputThumbnailHalf =
                new MockMultipartFile("thumbnail_half_file", "thumbnail_half.jpg", "image/jpg", "THUMBNAIL_HALF".getBytes());

        when(createVideoUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = multipart("/videos")
                .file(inputVideo)
                .file(inputTrailer)
                .file(inputBanner)
                .file(inputThumbnail)
                .file(inputThumbnailHalf)
                .with(ApiTest.VIDEOS_JWT)
                .param("title", Fixture.title())
                .param("description", "some description")
                .param("year_launched", Fixture.year().toString())
                .param("duration", String.valueOf(Fixture.duration()))
                .param("opened", String.valueOf(Fixture.bool()))
                .param("published", String.valueOf(Fixture.bool()))
                .param("rating", Fixture.Videos.rating().getName())
                .param("cast_member_id", CastMemberID.unique().getValue())
                .param("categories_id", CategoryId.unique().getValue())
                .param("genres_id", GenreID.unique().getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isUnprocessableEntity(),
                jsonPath("$.message", equalTo(expectedErrorMessage))
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
                .when(createVideoUseCase).execute(any());

        // when
        final var aRequest = post("/videos")
                .with(ApiTest.VIDEOS_JWT)
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
    void givenANotificationException_whenCallsCreateVideoPartial_shouldReturnUnprocessableEntity() throws Exception {
        // given
        final var expectedErrorMessage = "Some error message";

        final var requestBody = new CreateVideoRequest(
                Fixture.title(),
                "some description",
                Fixture.year(),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                Fixture.Videos.rating().getName(),
                Set.of(CastMemberID.unique().getValue()),
                Set.of(CategoryId.unique().getValue()),
                Set.of(GenreID.unique().getValue())
        );

        when(createVideoUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = post("/videos")
                .with(ApiTest.VIDEOS_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isUnprocessableEntity(),
                jsonPath("$.message", equalTo(expectedErrorMessage))
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
                .with(ApiTest.VIDEOS_JWT)
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
    void givenANotFoundException_whenCallsGetById_shouldReturnNotFound() throws Exception {
        //given
        final var inputId = "nonExistentId";
        final var expectedErrorMessage = "Video with ID 'nonExistentId' not found";

        when(getVideoUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Video.class, VideoID.from(inputId)));

        // when
        final var aRequest = get("/videos/{id}", inputId)
                .with(ApiTest.VIDEOS_JWT)
                .accept(MediaType.APPLICATION_JSON);

        final var resultActions = mockMvc.perform(aRequest)
                .andDo(print());

        // then
        resultActions.andExpectAll(
                status().isNotFound(),
                jsonPath("$.message", equalTo(expectedErrorMessage))
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
                .when(updateVideoUseCase).execute(any());

        // when
        final var aRequest = put("/videos/{id}", expectedVideoId.getValue())
                .with(ApiTest.VIDEOS_JWT)
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
                .when(updateVideoUseCase).execute(any());

        // when
        final var aRequest = put("/videos/{id}", VideoID.unique().getValue())
                .with(ApiTest.VIDEOS_JWT)
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

    @Test
    void givenAValidId_whenCallsDelete_shouldReturnNoContent() throws Exception {
        // given
        final var expectedId = VideoID.unique().toString();

        // when
        final var aRequest = delete("/videos/{id}", expectedId)
                .with(ApiTest.VIDEOS_JWT);

        final var resultActions = this.mockMvc.perform(aRequest).andDo(print());

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void givenValidParams_whenCallsListVideos_shouldReturnPagination() throws Exception {
        // given
        final var aVideo = new VideoPreview(Fixture.video());
        final var expectedPage = 0;
        final var expectePerPage = 10;
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(VideoListOutput.from(aVideo));
        final var inputTerms = "foo";
        final var inputSort = "name";
        final var inputDirection = "asc";
        final var inputCastMembers = "cast1";
        final var inputCategories = "cat1";
        final var inputGenres = "genre1";

        when(videoListUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectePerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/videos")
                .with(ApiTest.VIDEOS_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectePerPage))
                .queryParam("sort", inputSort)
                .queryParam("dir", inputDirection)
                .queryParam("search", inputTerms)
                .queryParam("cast_members_ids", inputCastMembers)
                .queryParam("genres_ids", inputGenres)
                .queryParam("categories_ids", inputCategories);
        final var resultActions = this.mockMvc.perform(aRequest);

        // then
        resultActions.andExpectAll(
                status().isOk(),
                jsonPath("$.current_page", equalTo(expectedPage)),
                jsonPath("$.per_page", equalTo(expectePerPage)),
                jsonPath("$.total", equalTo(expectedTotal)),
                jsonPath("$.items", hasSize(expectedItemsCount)),
                jsonPath("$.items[0].id", equalTo(aVideo.id())),
                jsonPath("$.items[0].title", equalTo(aVideo.title())),
                jsonPath("$.items[0].description", equalTo(aVideo.description())),
                jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())),
                jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString()))
        );
    }

    @Test
    void givenEmptyParams_whenCallsListVideos_shouldReturnPagination() throws Exception {
        // given
        final var aVideo = new VideoPreview(Fixture.video());
        final var expectedPage = 0;
        final var expectePerPage = 25;
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        when(videoListUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectePerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/videos")
                .with(ApiTest.VIDEOS_JWT)
                .accept(MediaType.APPLICATION_JSON);
        final var resultActions = this.mockMvc.perform(aRequest);

        // then
        resultActions.andExpectAll(
                status().isOk(),
                jsonPath("$.current_page", equalTo(expectedPage)),
                jsonPath("$.per_page", equalTo(expectePerPage)),
                jsonPath("$.total", equalTo(expectedTotal)),
                jsonPath("$.items", hasSize(expectedItemsCount)),
                jsonPath("$.items[0].id", equalTo(aVideo.id())),
                jsonPath("$.items[0].title", equalTo(aVideo.title())),
                jsonPath("$.items[0].description", equalTo(aVideo.description())),
                jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())),
                jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString()))
        );
    }

    @Test
    void givenAValidVideoIdAndFileType_whenCallsGetMediaById_shouldReturnContent() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedMediaType);

        final var expectedMedia = new MediaOutput(
                expectedResource.name(),
                expectedResource.contentType(),
                expectedResource.content()
        );

        when(getMediaUseCase.execute(any()))
                .thenReturn(expectedMedia);

        // when
        final var aRequest = get("/videos/{id}/medias/{type}", expectedId.getValue(), expectedMediaType.name())
                .with(ApiTest.VIDEOS_JWT);
        final var response = mockMvc.perform(aRequest);

        // then
        response.andExpectAll(
                status().isOk(),
                header().string(HttpHeaders.CONTENT_TYPE, expectedMedia.contentType()),
                header().string(HttpHeaders.CONTENT_LENGTH, String.valueOf(expectedMedia.content().length)),
                header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(expectedResource.name())),
                content().bytes(expectedMedia.content())
        );
    }

    @Test
    void givenAValidVideoIdAndFile_whenCallsUploadMedia_shouldStoreIt() throws Exception {
        // given
        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);

        final var expectedVideo = new MockMultipartFile(
                "media_file",
                expectedResource.name(),
                expectedResource.contentType(),
                expectedResource.content()
        );

        when(mediaUploadUseCase.execute(any()))
                .thenReturn(new UploadMediaOutput(expectedVideoId, expectedType));

        // when
        final var aRequest = multipart("/videos/{id}/medias/{type}", expectedVideoId.getValue(), expectedType.name())
                .file(expectedVideo)
                .with(ApiTest.VIDEOS_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        final var response = this.mockMvc.perform(aRequest).andDo(print());

        // then
        response.andExpectAll(
                status().isCreated(),
                header().string(HttpHeaders.LOCATION, "/videos/%s/medias/%s".formatted(expectedVideoId.getValue(), expectedType.name())),
                header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.video_id", equalTo(expectedVideoId.getValue())),
                jsonPath("$.media_type", equalTo(expectedType.name()))
        );
    }

    @Test
    void givenAnInvalidType_whenCallsUploadMedia_shouldReturnError() throws Exception {
        // given
        final var inputInvalidType = "SOME_INVALID_TYPE";
        final var expectedErrorMessage = "Invalid value '"+inputInvalidType+"' for mediaType";
        final var inputVideoId = VideoID.unique();
        final var inputResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var inputMedia = new MockMultipartFile(
                "media_file",
                inputResource.name(),
                inputResource.contentType(),
                inputResource.content()
        );

        // when
        final var aRequest = multipart("/videos/{id}/medias/{type}", inputVideoId.getValue(), inputInvalidType)
                .file(inputMedia)
                .with(ApiTest.VIDEOS_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        final var response = this.mockMvc.perform(aRequest).andDo(print());

        // then
        response.andExpectAll(
                status().isUnprocessableEntity(),
                jsonPath("$.message", equalTo(expectedErrorMessage))
        );
    }
}