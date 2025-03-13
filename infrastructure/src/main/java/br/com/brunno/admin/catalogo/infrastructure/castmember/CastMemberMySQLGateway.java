package br.com.brunno.admin.catalogo.infrastructure.castmember;

import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import br.com.brunno.admin.catalogo.infrastructure.util.SpecificationUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CastMemberMySQLGateway implements CastMemberGateway {

    private final CastMemberRepository castMemberRepository;

    public CastMemberMySQLGateway(CastMemberRepository castMemberRepository) {
        this.castMemberRepository = castMemberRepository;
    }

    @Override
    public CastMember create(CastMember aCastMember) {
        return this.save(aCastMember);
    }

    @Override
    public CastMember update(CastMember aCastMember) {
        return this.save(aCastMember);
    }

    @Override
    public void deleteById(CastMemberID anId) {
        Objects.requireNonNull(anId, "Cannot delete an null CastMemberId");
        if (this.castMemberRepository.existsById(anId.getValue())) {
            this.castMemberRepository.deleteById(anId.getValue());
        }
    }

    @Override
    public Optional<CastMember> findById(CastMemberID anId) {
        return this.castMemberRepository.findById(anId.getValue())
                .map(CastMemberJpaEntity::toAggregate);
    }

    @Override
    public Pagination<CastMember> findAll(SearchQuery aQuery) {
        final var pageRequest = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var whereClause = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(str -> SpecificationUtil.<CastMemberJpaEntity>like("name", str))
                .orElse(null);

        final var pageResult = this.castMemberRepository.findAll(whereClause, pageRequest);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CastMemberJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public List<CastMemberID> existisByIds(List<CastMemberID> ids) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    private CastMember save(CastMember aCastMember) {
        return this.castMemberRepository.save(CastMemberJpaEntity.from(aCastMember)).toAggregate();
    }
}
