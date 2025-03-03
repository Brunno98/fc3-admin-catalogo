package br.com.brunno.admin.catalogo.application.castmember.retrieve.list;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;

public class ListCastMemberUseCase extends UseCase<SearchQuery, Pagination<CastMemberListOutput>> {

    private final CastMemberGateway castMemberGateway;

    public ListCastMemberUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = castMemberGateway;
    }

    @Override
    public Pagination<CastMemberListOutput> execute(SearchQuery anIn) {
        final var castMemberPages = this.castMemberGateway.findAll(anIn);

        return castMemberPages
                .map(CastMemberListOutput::from);
    }
}
