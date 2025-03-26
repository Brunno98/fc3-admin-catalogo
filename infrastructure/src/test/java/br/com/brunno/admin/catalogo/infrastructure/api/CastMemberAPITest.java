package br.com.brunno.admin.catalogo.infrastructure.api;

import br.com.brunno.admin.catalogo.ApiTest;
import br.com.brunno.admin.catalogo.ControllerTest;
import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.application.castmember.create.CreateCastMemberOutput;
import br.com.brunno.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.get.GetCastMemberOutput;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.get.GetCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.list.CastMemberListOutput;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.list.ListCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.update.UpdateCastMemberOutput;
import br.com.brunno.admin.catalogo.application.castmember.update.UpdateCastMemberUseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.exceptions.DomainException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = CastMemberAPI.class)
public class CastMemberAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private UpdateCastMemberUseCase updateCastMemberUseCase;

    @MockBean
    private GetCastMemberUseCase getCastMemberUseCase;

    @MockBean
    private DeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private ListCastMemberUseCase listCastMemberUseCase;

    @Test
    void givenAValidParams_whenCreateACastMember_shouldReturnItsIdentifier() throws Exception {
        final var inputName = Fixture.name();
        final var inputType = Fixture.CastMembers.type();
        final var expectedId = CastMemberID.unique().getValue();
        final var createCastMemberRequest = new CreateCastMemberRequest(inputName, inputType.name());

        Mockito.doReturn(new CreateCastMemberOutput(CastMemberID.from(expectedId)))
                .when(createCastMemberUseCase).execute(any());

        final var response = mockMvc.perform(post("/cast_members")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(createCastMemberRequest)));

        response.andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/cast_members/" + expectedId))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));
    }

    @Test
    void givenAnInvalidParams_whenCreateACastMemberThrowsDomainException_shouldReturnsUnprocessableEntity() throws Exception {
        final var createCastMemberRequest = new CreateCastMemberRequest(Fixture.name(), Fixture.CastMembers.type().name());
        final var expectedErrorMessage = "Domain exception was thrown";

        Mockito.doThrow(DomainException.with(new Error(expectedErrorMessage)))
                .when(createCastMemberUseCase).execute(any());

        final var response = mockMvc.perform(post("/cast_members")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(createCastMemberRequest)));

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAnInvalidCastMemberType_whenCreateACastMember_shouldReturnsUnprocessableEntity() throws Exception {
        final var inputInvalidType = "invalidCastMemberType";
        final var createCastMemberRequest = new CreateCastMemberRequest(Fixture.name(), inputInvalidType);
        final var expectedErrorMessage = "Value '%s' is not a valid CastMemberType %s"
                .formatted(inputInvalidType, CastMemberType.getValuesNames());

        final var response = mockMvc.perform(post("/cast_members")
                        .with(ApiTest.CAST_MEMBERS_JWT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createCastMemberRequest)))
                .andDo(print());

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidId_whenCallsGetById_shouldReturnIt() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var aCastMember = CastMember.create(expectedName, expectedType);
        final var expectedId = aCastMember.getId();

        Mockito.doReturn(GetCastMemberOutput.from(aCastMember))
                .when(getCastMemberUseCase).execute(expectedId);

        final var response = mockMvc.perform(get("/cast_members/{id}", expectedId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.type", equalTo(expectedType.name())))
                .andExpect(jsonPath("$.created_at", equalTo(aCastMember.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aCastMember.getUpdatedAt().toString())));
    }

    @Test
    void givenANonExistentId_whenCallsGetById_shouldReturnNotFound() throws Exception {
        final var expectedId = CastMemberID.from("non existent");
        final var expectedMessage = "CastMember with ID 'non existent' not found";

        Mockito.doThrow(NotFoundException.with(CastMember.class, expectedId))
                .when(getCastMemberUseCase).execute(expectedId);

        final var response = mockMvc.perform(get("/cast_members/{id}", expectedId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedMessage)));
    }

    @Test
    void givenAValidParams_whenUpdateACastMember_shouldUpdateIt() throws Exception {
        final var expectedId = CastMemberID.unique().getValue();
        final var updateCastMemberRequest = new UpdateCastMemberRequest(Fixture.name(), Fixture.CastMembers.type().name());

        Mockito.doReturn(new UpdateCastMemberOutput(CastMemberID.from(expectedId)))
                .when(updateCastMemberUseCase).execute(any());

        final var response = mockMvc.perform(put("/cast_members/{id}", expectedId)
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateCastMemberRequest)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)));
    }

    @Test
    void givenAnInvalidParams_whenUpdateACastMemberThrowsDomainException_shouldReturnsUnprocessableEntity() throws Exception {
        final var updateCastMemberRequest = new UpdateCastMemberRequest(Fixture.name(), Fixture.CastMembers.type().name());
        final var expectedErrorMessage = "Domain exception was thrown";

        Mockito.doThrow(DomainException.with(new Error(expectedErrorMessage)))
                .when(updateCastMemberUseCase).execute(any());

        final var response = mockMvc.perform(put("/cast_members/{id}", "an id")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateCastMemberRequest)));

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAnInvalidCastMemberType_whenUpdateACastMember_shouldReturnsUnprocessableEntity() throws Exception {
        final var inputInvalidType = "invalidCastMemberType";
        final var updateCastMemberRequest = new UpdateCastMemberRequest(Fixture.name(), inputInvalidType);
        final var expectedErrorMessage = "Value '%s' is not a valid CastMemberType %s"
                .formatted(inputInvalidType, CastMemberType.getValuesNames());

        final var response = mockMvc.perform(put("/cast_members/{id}", "an id")
                        .with(ApiTest.CAST_MEMBERS_JWT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateCastMemberRequest)))
                .andDo(print());

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenANonExistentId_whenCallsUpdate_shouldReturnNotFound() throws Exception {
        final var expectedId = CastMemberID.from("non existent");
        final var expectedMessage = "CastMember with ID 'non existent' not found";
        final var updateCastMemberRequest = new UpdateCastMemberRequest(Fixture.name(), Fixture.CastMembers.type().name());

        Mockito.doThrow(NotFoundException.with(CastMember.class, expectedId))
                .when(updateCastMemberUseCase).execute(any());

        final var response = mockMvc.perform(put("/cast_members/{id}", expectedId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateCastMemberRequest)));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedMessage)));
    }

    @Test
    void givenAValidId_whenCallsDeleteById_shouldReturnsNoContent() throws Exception {
        final var anId = CastMemberID.unique();

        final var result = mockMvc.perform(delete("/cast_members/{id}", anId.getValue())
                .with(ApiTest.CAST_MEMBERS_JWT));

        result.andExpect(status().isNoContent());

        Mockito.verify(deleteCastMemberUseCase).execute(anId);
    }

    @Test
    void givenAValidParams_whenCallListCastMembers_shouldReturnIt() throws Exception {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotalItems = 1;
        final var aCastMember = CastMember.create(Fixture.name(), Fixture.CastMembers.type());
        final var expectedItems = List.of(CastMemberListOutput.from(aCastMember));
        final var expectedItemsCount = expectedItems.size();

        Mockito.doReturn(new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotalItems,
                expectedItems
        )).when(listCastMemberUseCase).execute(any());

        final var result = mockMvc.perform(get("/cast_members")
                .with(ApiTest.CAST_MEMBERS_JWT)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("per_page", String.valueOf(expectedPerPage))
                .queryParam("search", expectedTerms)
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotalItems)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aCastMember.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aCastMember.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(aCastMember.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aCastMember.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(aCastMember.getUpdatedAt().toString())));
    }

}
