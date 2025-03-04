package br.com.brunno.admin.catalogo.e2e.castmember;

import br.com.brunno.admin.catalogo.E2ETest;
import br.com.brunno.admin.catalogo.Fixture;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.e2e.MockDSL;
import br.com.brunno.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CastMemberE2ETest implements MockDSL {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Override
    public MockMvc mvc() {
        return this.mockMvc;
    }

    @Override
    public ObjectMapper mapper() {
        return this.mapper;
    }

    @Container
    private static MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:8.0")
            .withUsername("root")
            .withPassword("123456")
            .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    void givenAValidParams_shouldCreateACastMember() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        assertEquals(0, castMemberRepository.count());

        final var castMemberId = this.givenACastMember(expectedName, expectedType);

        assertEquals(1, castMemberRepository.count());

        final var actualCastMember = castMemberRepository.findById(castMemberId.getValue()).get();

        assertEquals(castMemberId.getValue(), actualCastMember.getId());
        assertEquals(expectedName, actualCastMember.getName());
        assertEquals(expectedType, actualCastMember.getType());
        assertNotNull(actualCastMember.getCreatedAt());
        assertNotNull(actualCastMember.getUpdatedAt());
    }

    @Test
    void givenAValidCastMemberId_whenCallsGetById_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var castMemberId = this.givenACastMember(expectedName, expectedType);

        final var actualCastMember = this.retrieveCastMember(castMemberId);

        assertEquals(castMemberId.getValue(), actualCastMember.id());
        assertEquals(expectedName, actualCastMember.name());
        assertEquals(expectedType.name(), actualCastMember.type());
        assertNotNull(actualCastMember.createdAt());
        assertNotNull(actualCastMember.updatedAt());
    }

    @Test
    void givenAValidParams_whenCallsUpdate_shouldUpdateIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var castMemberId = this.givenACastMember("Vin Diesel", CastMemberType.ACTOR);

        this.updateCastMember(castMemberId, expectedName, expectedType);

        final var actualCastMember = this.retrieveCastMember(castMemberId);

        assertEquals(castMemberId.getValue(), actualCastMember.id());
        assertEquals(expectedName, actualCastMember.name());
        assertEquals(expectedType.name(), actualCastMember.type());
        assertNotNull(actualCastMember.createdAt());
        assertNotNull(actualCastMember.updatedAt());
        assertTrue(actualCastMember.createdAt().isBefore(actualCastMember.updatedAt()));
    }

    @Test
    void givenAExistentId_whenCallsDelete_shouldDeleteIt() {
        final var castMemberId = this.givenACastMember(Fixture.name(), Fixture.CastMember.type());

        assertTrue(this.castMemberRepository.findById(castMemberId.getValue()).isPresent());

        this.deleteCastMember(castMemberId);

        assertTrue(this.castMemberRepository.findById(castMemberId.getValue()).isEmpty());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateThruAllMembers() throws Exception {
        givenACastMember("Vin Diesel", CastMemberType.ACTOR);
        givenACastMember("Quentin Tarantino", CastMemberType.DIRECTOR);
        givenACastMember("Jason Momoa", CastMemberType.ACTOR);

        listCastMembers("0", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Jason Momoa")));

        listCastMembers("1", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Quentin Tarantino")));

        listCastMembers("2", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Vin Diesel")));

        listCastMembers("3", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(3)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchThruAllMembers() throws Exception {
        givenACastMember("Vin Diesel", CastMemberType.ACTOR);
        givenACastMember("Quentin Tarantino", CastMemberType.DIRECTOR);
        givenACastMember("Jason Momoa", CastMemberType.ACTOR);

        listCastMembers("0", "10", "name", "asc", "Vin")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(10)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Vin Diesel")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllMembersByNameDesc() throws Exception {
        givenACastMember("Vin Diesel", CastMemberType.ACTOR);
        givenACastMember("Quentin Tarantino", CastMemberType.DIRECTOR);
        givenACastMember("Jason Momoa", CastMemberType.ACTOR);

        listCastMembers("0", "10", "name", "asc", "")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(10)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Jason Momoa")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Quentin Tarantino")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Vin Diesel")));

        listCastMembers("0", "10", "name", "desc", "")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(10)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Vin Diesel")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Quentin Tarantino")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Jason Momoa")));
    }

}
