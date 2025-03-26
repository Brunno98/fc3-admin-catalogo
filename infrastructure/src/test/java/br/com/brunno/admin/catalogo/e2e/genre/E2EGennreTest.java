package br.com.brunno.admin.catalogo.e2e.genre;

import br.com.brunno.admin.catalogo.ApiTest;
import br.com.brunno.admin.catalogo.E2ETest;
import br.com.brunno.admin.catalogo.e2e.MockDSL;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class E2EGennreTest implements MockDSL {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public MockMvc mvc() {
        return this.mockMvc;
    }

    @Override
    public ObjectMapper mapper() {
        return this.mapper;
    }

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
    public void asAdminIShouldBeAbleToCreateUpdateAndDeleteAGenreWithCategories() throws Exception {

        final var filmeId = givenAnCategory("Filmes", null, true);
        final var seriesId = givenAnCategory("Series", null, true);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmeId.getValue(), seriesId.getValue());

        final var genreId = this.givenAGenre(expectedName, expectedCategories, expectedIsActive);

        var genreResponses = this.retrieveGenre(genreId);

        Assertions.assertEquals(expectedName, genreResponses.name());
        Assertions.assertTrue(expectedCategories.size() == genreResponses.categories().size()
            && expectedCategories.containsAll(genreResponses.categories()));
        Assertions.assertEquals(expectedIsActive, genreResponses.active());
        Assertions.assertNotNull(genreResponses.createdAt());
        Assertions.assertNotNull(genreResponses.updatedAt());
        Assertions.assertNull(genreResponses.deletedAt());


        final var documentarioId = this.givenAnCategory("Documentarios", null, true);
        final var expectedUpdatedName = "Ação Atualizado";
        final var expectedUpdatedCategoriesList = Stream.concat(expectedCategories.stream(), Stream.of(documentarioId.getValue())).toList();
        final var expectedUpdatedIsActive = false;

        this.updateGenre(genreId, expectedUpdatedName, expectedUpdatedCategoriesList, expectedUpdatedIsActive);

        genreResponses = this.retrieveGenre(genreId);

        Assertions.assertEquals(expectedUpdatedName, genreResponses.name());
        Assertions.assertTrue(expectedUpdatedCategoriesList.size() == genreResponses.categories().size()
                && expectedUpdatedCategoriesList.containsAll(genreResponses.categories()));
        Assertions.assertEquals(expectedUpdatedIsActive, genreResponses.active());
        Assertions.assertNotNull(genreResponses.createdAt());
        Assertions.assertNotNull(genreResponses.updatedAt());
        Assertions.assertNotNull(genreResponses.deletedAt());

        this.deleteGenre(genreId);

        mockMvc.perform(get("/genre/{id}", genreId.getValue())
                        .with(ApiTest.ADMIN_JWT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToListCategories() throws Exception {
        this.givenAGenre("Ação", List.of(), true);
        this.givenAGenre("Esportes", List.of(), true);
        this.givenAGenre("Drama", List.of(), true);

        this.listGenres("0", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Ação"));

        this.listGenres("1", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Drama"));

        this.listGenres("2", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Esportes"));

        //FIXME: Não funciona se passar o valor recebido no json 'created_at' (em snake case), precisando ser o nome
        // do atributo na entidade JPA. Estudar como corrigir esse comportamento para usar o mesmo campo fornecido no
        // JSON de resosta
        this.listGenres("0", "1", "createdAt", "desc", "")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Drama"));

    }

}
