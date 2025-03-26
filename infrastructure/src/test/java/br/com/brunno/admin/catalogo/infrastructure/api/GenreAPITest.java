package br.com.brunno.admin.catalogo.infrastructure.api;

import br.com.brunno.admin.catalogo.ApiTest;
import br.com.brunno.admin.catalogo.ControllerTest;
import br.com.brunno.admin.catalogo.application.genre.create.CreateGenreOutput;
import br.com.brunno.admin.catalogo.application.genre.create.CreateGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.retrieve.get.GenreOutput;
import br.com.brunno.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import br.com.brunno.admin.catalogo.application.genre.retrieve.list.GenreListOutput;
import br.com.brunno.admin.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.update.UpdateGenreOutput;
import br.com.brunno.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @Test
    void givenAValidRequest_whenCallsCreateGenre_shouldReturnGenreId() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;
        final var expectedId = "123";

        final var createCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        doReturn(new CreateGenreOutput(expectedId))
                .when(createGenreUseCase).execute(any());

        final var result = mockMvc.perform(post("/genres")
                        .with(ApiTest.GENRES_JWT)
                        .with(ApiTest.GENRES_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createCommand)))
                .andDo(print());

        result.andExpect(status().isCreated());
        result.andExpect(header().string("Location", "/genres/" + expectedId));
        result.andExpect(jsonPath("$.id").value(expectedId));
    }

    @Test
    void givenAnInvalidName_whenCallsCreateGenre_shouldReturnUnprocessableEntity() throws Exception {
        final String expectedName = null;
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;
        final var createCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error("Unknown error"))));

        final var result = mockMvc.perform(post("/genres")
                        .with(ApiTest.GENRES_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createCommand)))
                .andDo(print());

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].message").value("Unknown error"));
    }

    @Test
    void givenAValidGenreId_whenFindGenreById_shouldReturnTheGenre() throws Exception {

        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategoriesIDs = List.of("123", "456");
        final var aGenre = Genre.newGenre(
                expectedName,
                expectedIsActive,
                expectedCategoriesIDs.stream().map(CategoryId::from).toList()
        );
        final var expectedId = aGenre.getId().getValue();

        doReturn(GenreOutput.from(aGenre)).when(getGenreByIdUseCase).execute(any());

        final var resultActions = mockMvc.perform(get("/genres/{id}", expectedId)
                        .with(ApiTest.GENRES_JWT))
                .andDo(print());

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id", equalTo(expectedId)));
        resultActions.andExpect(jsonPath("$.name", equalTo(expectedName)));
        resultActions.andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)));
        resultActions.andExpect(jsonPath("$.created_at", equalTo(aGenre.getCreatedAt().toString())));
        resultActions.andExpect(jsonPath("$.updated_at", equalTo(aGenre.getUpdatedAt().toString())));
        resultActions.andExpect(jsonPath("$.deleted_at", equalTo(aGenre.getDeletedAt().toString())));
        resultActions.andExpect(jsonPath("$.categories_id", equalTo(expectedCategoriesIDs)));
    }

    @Test
    void givenANonExistentId_whenCallsGetGenreById_shouldReturnNotFound() throws Exception {
        final var aGenreId = GenreID.from("non-existent");

        Mockito.doThrow(NotFoundException.with(Genre.class, aGenreId))
                .when(getGenreByIdUseCase).execute(aGenreId);

        final var resultActions = mockMvc.perform(get("/genres/{id}", aGenreId.getValue())
                        .with(ApiTest.GENRES_JWT))
                .andDo(print());

        resultActions.andExpect(status().isNotFound());
        resultActions.andExpect(jsonPath("$.message").value("Genre with ID 'non-existent' not found"));
    }

    @Test
    void givenAValidRequest_whenCallsUpdateGenre_shouldReturnGenreId() throws Exception {
        final var aGenreId = "123";
        final var createCommand = new UpdateGenreRequest("Ação", List.of("123", "456"), true);

        doReturn(new UpdateGenreOutput(aGenreId))
                .when(updateGenreUseCase).execute(any());

        final var result = mockMvc.perform(put("/genres/{id}", aGenreId)
                        .with(ApiTest.GENRES_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createCommand)))
                .andDo(print());

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(aGenreId));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateGenre_shouldReturnUnprocessableEntity() throws Exception {
        final String nullName = null;
        final var aGenreId = "123";
        final var expectedErrorMessage = "Unknown error";
        final var updateCommand = new UpdateGenreRequest(nullName, List.of("123", "456"), true);

        when(updateGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var result = mockMvc.perform(put("/genres/{id}", aGenreId)
                        .with(ApiTest.GENRES_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateCommand)))
                .andDo(print());

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].message").value(expectedErrorMessage));
    }

    @Test
    void givenAnValidGenreId_whenCallsDeleteById_shouldReturnsOk() throws Exception {

        final var aGenreId = "123";

        doNothing().when(deleteGenreUseCase).execute(any());

        final var resultActions = mockMvc.perform(delete("/genres/{id}", aGenreId)
                        .with(ApiTest.GENRES_JWT))
                .andDo(print());

        resultActions.andExpect(status().isNoContent());

        Mockito.verify(deleteGenreUseCase).execute(argThat(
                command -> command.anID().getValue().equals(aGenreId))
        );
    }

    @Test
    void givenValidParams_whenCallsListGenres_shouldReturnGenres() throws Exception {
        final var aGenre = Genre.newGenre("Ação", true, Collections.emptyList());
        final var expectedItems = List.of(aGenre);
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTerms = "ac";
        final var expectedTotal = 1;
        final var expectedItemsCount = 1;

        when(listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(
                        expectedPage,
                        expectedPerPage,
                        expectedTotal,
                        expectedItems.stream().map(GenreListOutput::from).toList()
                ));

        final var result = mockMvc.perform(get("/genres")
                .with(ApiTest.GENRES_JWT)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("direction", expectedDirection)
                .queryParam("search", expectedTerms));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aGenre.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aGenre.getName())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(aGenre.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aGenre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(aGenre.getDeletedAt())))
        ;
    }
}
