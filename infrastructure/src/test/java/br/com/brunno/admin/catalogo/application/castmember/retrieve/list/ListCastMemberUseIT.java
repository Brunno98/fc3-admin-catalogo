package br.com.brunno.admin.catalogo.application.castmember.retrieve.list;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class ListCastMemberUseIT {

    @Autowired
    private ListCastMemberUseCase listCastMemberUseCase;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidQuery_whenCallsListCastMember_shouldReturnCastMembers() {
        final var vinDiesel = CastMember.create("Vin Diesel", Fixture.CastMembers.type());
        final var tomHanks = CastMember.create("Tom Hanks", Fixture.CastMembers.type());
        final var castMembers = List.of(vinDiesel,tomHanks);
        castMembers.forEach(castMemberGateway::create);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;
        final var expectedItems = castMembers.stream()
                .map(CastMemberListOutput::from)
                .toList();
        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                castMembers
        );

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualOutput = listCastMemberUseCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertTrue(expectedItems.size() == actualOutput.items().size()
            && expectedItems.containsAll(actualOutput.items()));
        assertEquals(tomHanks.getId(), actualOutput.items().get(0).id());
        assertEquals(vinDiesel.getId(), actualOutput.items().get(1).id());

    }

    @Test
    void givenAValidQuery_whenCallsListCastMemberAndResultIsEmpty_shouldReturnEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var expectedItems = List.<CastMemberListOutput>of();

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualOutput = listCastMemberUseCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems, actualOutput.items());

    }

}
