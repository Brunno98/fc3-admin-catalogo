package br.com.brunno.admin.catalogo.infrastructure.castmember.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CastMemberRepository extends JpaRepository<CastMemberJpaEntity, String> {

    Page<CastMemberJpaEntity> findAll(Specification<CastMemberJpaEntity> whereClause, Pageable page);

    @Query("SELECT cm.id FROM CastMember cm WHERE cm.id IN :ids")
    Collection<String> existsByIds(@Param("ids") List<String> castMemberIds);
}
