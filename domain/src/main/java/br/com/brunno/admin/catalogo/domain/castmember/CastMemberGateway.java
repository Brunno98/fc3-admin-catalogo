package br.com.brunno.admin.catalogo.domain.castmember;

import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;

import java.util.Optional;

public interface CastMemberGateway {

    CastMember create (CastMember aCastMember);

    CastMember update (CastMember aCastMember);

    void deleteById(CastMemberID anId);

    Optional<CastMember> findById(CastMemberID anId);

    Pagination<CastMember> findAll(SearchQuery aQuery);

}
