package br.com.brunno.admin.catalogo.infrastructure.castmember;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.MySQLGatewayTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import br.com.brunno.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MySQLGatewayTest
class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberMySQLGateway castMemberMySQLGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;


    @Test
    void givenAValidCastMember_whenCreateACastMember_shouldCreateACastMember() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var castMember = CastMember.create(expectedName, expectedType);
        final var expectedId = castMember.getId();

        final var actualCastMember = castMemberMySQLGateway.create(castMember);

        assertEquals(expectedId, actualCastMember.getId());
        assertEquals(expectedName, actualCastMember.getName());
        assertEquals(expectedType, actualCastMember.getType());
        assertEquals(castMember.getCreatedAt(), actualCastMember.getCreatedAt());
        assertEquals(castMember.getUpdatedAt(), actualCastMember.getUpdatedAt());

        final var persistedCastMember = castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedCastMember.getName());
        assertEquals(expectedType, persistedCastMember.getType());
        assertEquals(castMember.getCreatedAt(), persistedCastMember.getCreatedAt());
        assertEquals(castMember.getUpdatedAt(), persistedCastMember.getUpdatedAt());
    }

    @Test
    void givenAValidCastMember_whenUpdateACastMember_shouldUpdateIt() {
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.DIRECTOR;
        final var castMember = CastMember.create(Fixture.name(), CastMemberType.ACTOR);
        final var expectedId = castMember.getId();

        assertNotEquals(expectedName, castMember.getName());
        assertNotEquals(expectedType, castMember.getType());

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(castMember));

        castMember.update(expectedName, expectedType);

        final var actualCastMember = castMemberMySQLGateway.update(castMember);

        assertEquals(expectedId, actualCastMember.getId());
        assertEquals(expectedName, actualCastMember.getName());
        assertEquals(expectedType, actualCastMember.getType());
        assertEquals(castMember.getCreatedAt(), actualCastMember.getCreatedAt());
        assertTrue(castMember.getUpdatedAt().isAfter(actualCastMember.getCreatedAt()));

        final var persistedCastMember = castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedCastMember.getName());
        assertEquals(expectedType, persistedCastMember.getType());
        assertEquals(castMember.getCreatedAt(), persistedCastMember.getCreatedAt());
        assertTrue(castMember.getUpdatedAt().isAfter(persistedCastMember.getCreatedAt()));
    }

    @Test
    void givenAValidCastMemberId_whenDeleteACastMember_shouldDeleteIt() {
        final var aCastMember = CastMember.create(Fixture.name(), Fixture.CastMembers.type());
        final var castMemberId = aCastMember.getId();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember));

        Assertions.assertEquals(1, castMemberRepository.count());

        castMemberMySQLGateway.deleteById(castMemberId);

        Assertions.assertEquals(0, castMemberRepository.count());
    }

    @Test
    void givenAnExistentCastMemberId_whenGetById_shouldRetrieveIt() {
        final var aCastMember = CastMember.create(Fixture.name(), Fixture.CastMembers.type());
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember));

        final var actualCastMember = castMemberMySQLGateway.findById(aCastMember.getId()).get();

        assertEquals(aCastMember.getId(), actualCastMember.getId());
        assertEquals(aCastMember.getName(), actualCastMember.getName());
        assertEquals(aCastMember.getType(), actualCastMember.getType());
        assertEquals(aCastMember.getCreatedAt(), actualCastMember.getCreatedAt());
        assertEquals(aCastMember.getUpdatedAt(), actualCastMember.getUpdatedAt());
    }

    @Test
    void givenANonExistentCastMemberId_whenGetById_shouldReturnEmpty() {
        final var actualCastMember = castMemberMySQLGateway.findById(CastMemberID.unique());

        assertTrue(actualCastMember.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "dies,Vin Diesel",
            "cha,Jack Chan",
            "nard,Leonardo DiCaprio"
    })
    void givenASearchFilterByName_whenCallsFindAll_shouldReturnAllCastMemberThatContainsTheTermOnName(
            final String termToSearch,
            final String expectedName
    ) {

        final var expectedPage = 0;
        final var expectedTotalItems = 1;

        final var vinDiesel = CastMember.create("Vin Diesel", Fixture.CastMembers.type());
        final var jackChan = CastMember.create("Jack Chan", Fixture.CastMembers.type());
        final var leonardoDiCaprio = CastMember.create("Leonardo DiCaprio", Fixture.CastMembers.type());

        assertEquals(0, castMemberRepository.count());

        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberJpaEntity.from(vinDiesel),
                CastMemberJpaEntity.from(jackChan),
                CastMemberJpaEntity.from(leonardoDiCaprio)
        ));

        assertEquals(3, castMemberRepository.count());

        final var searchQuery = new SearchQuery(expectedPage, 1, termToSearch, "name", "asc");
        final var queryResult = castMemberMySQLGateway.findAll(searchQuery);

        assertEquals(expectedPage, queryResult.currentPage());
        assertEquals(expectedTotalItems, queryResult.total());
        assertEquals(1, queryResult.items().size());
        assertEquals(expectedName, queryResult.items().get(0).getName());
    }

    @Test
    void givenEmptyCastMembers_whenCallsFindAll_shouldReturnEmpty() {

        final var searchQuery = new SearchQuery(0, 10, "", "name", "asc");
        final var queryResult = castMemberMySQLGateway.findAll(searchQuery);

        assertEquals(0, queryResult.currentPage());
        assertEquals(0, queryResult.total());
        assertTrue(queryResult.items().isEmpty());

    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,Jack Chan;Leonardo DiCaprio;Vin Diesel",
            "name,desc,Vin Diesel;Leonardo DiCaprio;Jack Chan"
    })
    void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnSorted(
            final String sortBy,
            final String direction,
            final String castMembers
    ) {
        final var expectedPage = 0;
        final var expectedTotalItems = 3;

        final var vinDiesel = CastMember.create("Vin Diesel", Fixture.CastMembers.type());
        final var jackChan = CastMember.create("Jack Chan", Fixture.CastMembers.type());
        final var leonardoDiCaprio = CastMember.create("Leonardo DiCaprio", Fixture.CastMembers.type());

        assertEquals(0, castMemberRepository.count());

        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberJpaEntity.from(vinDiesel),
                CastMemberJpaEntity.from(jackChan),
                CastMemberJpaEntity.from(leonardoDiCaprio)
        ));

        assertEquals(3, castMemberRepository.count());

        final var searchQuery = new SearchQuery(expectedPage, 10, "", sortBy, direction);
        final var queryResult = castMemberMySQLGateway.findAll(searchQuery);

        assertEquals(expectedPage, queryResult.currentPage());
        assertEquals(expectedTotalItems, queryResult.total());
        assertEquals(expectedTotalItems, queryResult.items().size());

        var count = 0;
        for (final String expectedCastMemberName : castMembers.split(";")) {
            assertEquals(expectedCastMemberName, queryResult.items().get(count++).getName());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,Jack Chan;Leonardo DiCaprio",
            "1,2,Tom Cruise;Vin Diesel",
            "2,2,Will Smith"
    })
    void givenAValidPagination_whenCallsFindAll_shouldReturnPaginated(
            final int page,
            final int perPage,
            final String castMembers
    ) {
        final var vinDiesel = CastMember.create("Vin Diesel", Fixture.CastMembers.type());
        final var jackChan = CastMember.create("Jack Chan", Fixture.CastMembers.type());
        final var leonardoDiCaprio = CastMember.create("Leonardo DiCaprio", Fixture.CastMembers.type());
        final var tomCruise = CastMember.create("Tom Cruise", Fixture.CastMembers.type());
        final var willSmith = CastMember.create("Will Smith", Fixture.CastMembers.type());

        assertEquals(0, castMemberRepository.count());

        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberJpaEntity.from(vinDiesel),
                CastMemberJpaEntity.from(jackChan),
                CastMemberJpaEntity.from(leonardoDiCaprio),
                CastMemberJpaEntity.from(tomCruise),
                CastMemberJpaEntity.from(willSmith)
        ));

        assertEquals(5, castMemberRepository.count());

        final var searchQuery = new SearchQuery(page, perPage, "", "name", "asc");
        final var queryResult = castMemberMySQLGateway.findAll(searchQuery);

        assertEquals(page, queryResult.currentPage());

        var count = 0;
        for (final String expectedCastMemberName : castMembers.split(";")) {
            assertEquals(expectedCastMemberName, queryResult.items().get(count++).getName());
        }
    }

    @Test
    void givenACastMemberList_whenCallsExistsByIds_shouldReturnTheExistentIds() {
        final var expectedExistentCategories = 2;

        final var cm1 = CastMember.create(Fixture.name(), Fixture.CastMembers.type());
        final var cm2 = CastMember.create(Fixture.name(), Fixture.CastMembers.type());

        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberJpaEntity.from(cm1),
                CastMemberJpaEntity.from(cm2)
        ));

        final var actualCategoryIds = castMemberMySQLGateway
                .existisByIds(List.of(cm1.getId(), cm2.getId(), CastMemberID.from("non-existent")));

        assertEquals(expectedExistentCategories, actualCategoryIds.size());
        assertTrue(actualCategoryIds.contains(cm1.getId()));
        assertTrue(actualCategoryIds.contains(cm2.getId()));
    }

}