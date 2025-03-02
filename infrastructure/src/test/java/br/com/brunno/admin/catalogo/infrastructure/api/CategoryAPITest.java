package br.com.brunno.admin.catalogo.infrastructure.api;

import br.com.brunno.admin.catalogo.ControllerTest;
import br.com.brunno.admin.catalogo.application.category.create.CreateCategoryOutput;
import br.com.brunno.admin.catalogo.application.category.create.CreateCategoryUseCase;
import br.com.brunno.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import br.com.brunno.admin.catalogo.application.category.retrive.get.GetCategoryByIdOutput;
import br.com.brunno.admin.catalogo.application.category.retrive.get.GetCategoryByIdUseCase;
import br.com.brunno.admin.catalogo.application.category.retrive.list.CategoryListOutput;
import br.com.brunno.admin.catalogo.application.category.retrive.list.ListCategoriesUseCase;
import br.com.brunno.admin.catalogo.application.category.update.UpdateCategoryOutput;
import br.com.brunno.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.DomainException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;
import br.com.brunno.admin.catalogo.infrastructure.category.models.CreateCategoryAPIRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.API;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper mapper;

    @MockBean
    public CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    public GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    public UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    public ListCategoriesUseCase listCategoriesUseCase;

    @MockBean
    public DeleteCategoryUseCase deleteCategoryUseCase;

    @Test
    void givenAValidInput_whenCreateANewCategory_shouldReturnCreatedStatusAndTheCategoryId () throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;

        Mockito.doReturn(API.Right(CreateCategoryOutput.from(CategoryId.from("123"))))
                .when(createCategoryUseCase).execute(Mockito.any());

        final var input = CreateCategoryAPIRequest.from(expectedName, expectedDescription, expectedIsActive);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/123"))
                .andExpect(jsonPath("$.id").value("123"));
    }

    @Test
    void givenAnInValidInput_whenCreateANewCategory_shouldReturnUnprocessableEntityAndErrorMessage () throws Exception {
        final String expectedName = null;
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        Mockito.doReturn(API.Left(Notification.create(new Error(expectedErrorMessage))))
                .when(createCategoryUseCase).execute(Mockito.any());

        final var input = CreateCategoryAPIRequest.from(expectedName, expectedDescription, expectedIsActive);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", Matchers.nullValue()))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message").value(expectedErrorMessage));
    }

    @Test
    void givenAnInValidInput_whenCreateANewCategoryThrowsDomainException_shouldReturnUnprocessableEntityAndErrorMessage () throws Exception {
        final String expectedName = null;
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        Mockito.doThrow(DomainException.with(new Error(expectedErrorMessage)))
                .when(createCategoryUseCase).execute(Mockito.any());

        final var input = CreateCategoryAPIRequest.from(expectedName, expectedDescription, expectedIsActive);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", Matchers.nullValue()))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message").value(expectedErrorMessage));
    }

    @Test
    void givenAnExistingCategoryId_whenGetCategoryById_shouldReturnOkStatusAndCategory() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        Mockito.doReturn(GetCategoryByIdOutput.from(aCategory))
                .when(getCategoryByIdUseCase).execute(Mockito.any());

        mockMvc.perform(get("/categories/{id}", aCategory.getId().getValue())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(aCategory.getId().getValue()))
                .andExpect(jsonPath("$.name").value(expectedName))
                .andExpect(jsonPath("$.description").value(expectedDescription))
                .andExpect(jsonPath("$.is_active").value(expectedIsActive));
    }

    @Test
    void givenAnInvalidCategoryId_whenGetCategoryById_shouldReturnNotFoundStatus() throws Exception {
        Mockito.doThrow(NotFoundException.with(Category.class, CategoryId.from("non-existent-id")))
                .when(getCategoryByIdUseCase).execute(CategoryId.from("non-existent-id"));

        mockMvc.perform(get("/categories/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category with ID 'non-existent-id' not found"));
    }

    @Test
    void givenAValidInput_whenUpdateCategory_shouldReturnOkStatus() throws Exception {
        final var expectedId = "123";

        Mockito.doReturn(API.Right(new UpdateCategoryOutput(CategoryId.from(expectedId))))
                .when(updateCategoryUseCase).execute(Mockito.any());

        final var input = CreateCategoryAPIRequest.from("Filmes", "Categoria de filmes", true);

        mockMvc.perform(put("/categories/{id}", expectedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId));
    }

    @Test
    void givenAnInvalidInput_whenUpdateCategory_shouldReturnUnprocessableEntity() throws Exception {
        final var expectedId = "123";
        final String expectedName = null;
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        Mockito.doReturn(API.Left(Notification.create(new Error(expectedErrorMessage))))
                .when(updateCategoryUseCase).execute(Mockito.any());

        final var input = CreateCategoryAPIRequest.from(expectedName, expectedDescription, expectedIsActive);

        mockMvc.perform(put("/categories/{id}", expectedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message").value(expectedErrorMessage));
    }

    @Test
    void givenAnInvalidInput_whenUpdateCategoryThrowsDomainException_shouldReturnUnprocessableEntityAndErrorMessage () throws Exception {
        final var expectedId = "123";
        final String expectedName = null;
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        Mockito.doThrow(DomainException.with(new Error(expectedErrorMessage)))
                .when(updateCategoryUseCase).execute(Mockito.any());

        final var input = CreateCategoryAPIRequest.from(expectedName, expectedDescription, expectedIsActive);

        mockMvc.perform(put("/categories/{id}", expectedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message").value(expectedErrorMessage));
    }

    @Test
    void givenANonExistentId_whenUpdateCategory_shouldReturnNotFoundAndErrorMessage () throws Exception {
        final var categoryId = "123";
        final var expectedErrorMessage = "Category with ID '123' not found";

        Mockito.doThrow(NotFoundException.with(Category.class, CategoryId.from(categoryId)))
                .when(updateCategoryUseCase).execute(Mockito.any());

        final var input = CreateCategoryAPIRequest.from(null,  "Categoria de filmes", true);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
    }

    @Test
    void givenAnExistingCategoryId_whenDeleteCategory_shouldReturnNoContentStatus() throws Exception {
        final var categoryId = "123";

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(deleteCategoryUseCase).execute(categoryId);
    }

    @Test
    void givenAValidTerm_whenListCategories_shouldReturnOkStatusAndAPageOfCategories() throws Exception {
        Category filmes = Category.newCategory("Filmes", "Categoria de filmes", true);
        Category series = Category.newCategory("Séries", "Categoria de séries", true);
        Category documentarios = Category.newCategory("Documentários", "Categoria de documentários", true);

        final var categoriesPage = new Pagination<CategoryListOutput>(
                0,
                10,
                3,
                Stream.of(filmes, series, documentarios).map(CategoryListOutput::from).toList()
        );
        Mockito.doReturn(categoriesPage)
                .when(listCategoriesUseCase).execute(Mockito.any());

        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page").value(0))
                .andExpect(jsonPath("$.per_page").value(10))
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.categories", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.categories[0].id").value(filmes.getId().getValue()))
                .andExpect(jsonPath("$.categories[0].name").value(filmes.getName()))
                .andExpect(jsonPath("$.categories[0].description").value(filmes.getDescription()))
                .andExpect(jsonPath("$.categories[0].is_active").value(filmes.isActive()));
    }
}
