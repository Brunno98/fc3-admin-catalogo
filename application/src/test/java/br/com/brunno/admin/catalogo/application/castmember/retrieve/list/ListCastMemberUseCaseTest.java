package br.com.brunno.admin.catalogo.application.castmember.retrieve.list;

import br.com.brunno.admin.catalogo.domain.Fixture;
import br.com.brunno.admin.catalogo.domain.UnitTest;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class ListCastMemberUseCaseTest {

    @InjectMocks
    private ListCastMemberUseCase listCastMemberUseCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidQuery_whenCallsListCastMember_shouldReturnCastMembers() {
        final var castMembers = List.of(
            CastMember.create(Fixture.name(), Fixture.CastMembers.type()),
            CastMember.create(Fixture.name(), Fixture.CastMembers.type())
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
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

        Mockito.when(castMemberGateway.findAll(aQuery))
                .thenReturn(expectedPagination);

        final var actualOutput = listCastMemberUseCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems, actualOutput.items());

        Mockito.verify(castMemberGateway, Mockito.times(1)).findAll(any());
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
        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                Collections.<CastMember>emptyList()
        );

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        Mockito.when(castMemberGateway.findAll(aQuery))
                .thenReturn(expectedPagination);

        final var actualOutput = listCastMemberUseCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems, actualOutput.items());

        Mockito.verify(castMemberGateway, Mockito.times(1)).findAll(any());
    }

    @Test
    void givenAValidQuery_whenCallsListCastMemberAndGatewaysThrowsUnknownError_shouldReturnEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var expectedErrorMessage = "Gateway Error";

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        Mockito.when(castMemberGateway.findAll(aQuery))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var actualOutput = Assertions.assertThrows(IllegalStateException.class,
                () -> listCastMemberUseCase.execute(aQuery));

        assertEquals(expectedErrorMessage, actualOutput.getMessage());

        Mockito.verify(castMemberGateway, Mockito.times(1)).findAll(any());
    }
}
