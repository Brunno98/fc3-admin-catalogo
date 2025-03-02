package br.com.brunno.admin.catalogo.e2e.category;

import br.com.brunno.admin.catalogo.E2ETest;
import br.com.brunno.admin.catalogo.infrastructure.category.models.CategoryAPIOutput;
import br.com.brunno.admin.catalogo.infrastructure.category.models.CreateCategoryAPIRequest;
import br.com.brunno.admin.catalogo.infrastructure.category.models.UpdateCategoryAPIRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class E2ECategoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:8.0")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        final var mappedPort = MYSQL_CONTAINER.getMappedPort(3306);
        registry.add("mysql.port", () -> mappedPort);
    }

    @Test
    public void testWorks() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;

        final var input = CreateCategoryAPIRequest.from(expectedName, expectedDescription, expectedIsActive);

        final var categoryId = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getHeader("Location")
                .replace("/categories/", "");

        var contentAsString = mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var categoryAPIOutput = mapper.readValue(contentAsString, CategoryAPIOutput.class);
        Assertions.assertEquals(expectedName, categoryAPIOutput.name());
        Assertions.assertEquals(expectedDescription, categoryAPIOutput.description());
        Assertions.assertEquals(expectedIsActive, categoryAPIOutput.active());

        final var expectedUpdatedName = "Filme Atualizado";
        final var expectedUpdatedDescription = "Categoria de filmes atualizada";
        final var expectedUpdatedIsActive = false;

        final var updateCategoryAPIRequest = new UpdateCategoryAPIRequest(expectedUpdatedName, expectedUpdatedDescription, expectedUpdatedIsActive);

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateCategoryAPIRequest)))
                .andExpect(status().isOk());

        contentAsString = mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        categoryAPIOutput = mapper.readValue(contentAsString, CategoryAPIOutput.class);
        Assertions.assertEquals(expectedUpdatedName, categoryAPIOutput.name());
        Assertions.assertEquals(expectedUpdatedDescription, categoryAPIOutput.description());
        Assertions.assertEquals(expectedUpdatedIsActive, categoryAPIOutput.active());

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToListCategories() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                CreateCategoryAPIRequest.from("Filme", "F", true)
                        )))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                CreateCategoryAPIRequest.from("Séries", "A", true)
                        )))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                CreateCategoryAPIRequest.from("Documentarios", "Z", true)
                        )))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mockMvc.perform(get("/categories")
                        .queryParam("page", "0")
                        .queryParam("perPage", "1")
                        .queryParam("sort", "name")
                        .queryParam("direction", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(1))
                .andExpect(jsonPath("$.categories[0].name").value("Documentarios"));

        mockMvc.perform(get("/categories")
                        .queryParam("page", "2")
                        .queryParam("perPage", "1")
                        .queryParam("sort", "name")
                        .queryParam("direction", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(1))
                .andExpect(jsonPath("$.categories[0].name").value("Séries"));

        mockMvc.perform(get("/categories")
                        .queryParam("page", "0")
                        .queryParam("perPage", "1")
                        .queryParam("sort", "name")
                        .queryParam("direction", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(1))
                .andExpect(jsonPath("$.categories[0].name").value("Séries"));

        mockMvc.perform(get("/categories")
                        .queryParam("page", "0")
                        .queryParam("perPage", "1")
                        .queryParam("sort", "description")
                        .queryParam("direction", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(1))
                .andExpect(jsonPath("$.categories[0].name").value("Séries"));

    }

}
