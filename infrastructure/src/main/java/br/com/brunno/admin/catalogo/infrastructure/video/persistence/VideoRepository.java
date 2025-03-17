package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.video.VideoPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, String> {

    @Query("""
            SELECT DISTINCT new br.com.brunno.admin.catalogo.domain.video.VideoPreview(
                v.id as id,
                v.title as title,
                v.description as description,
                v.createdAt as createdAt,
                v.updatedAt as updatedAt
            )
            FROM Video v
                LEFT JOIN v.castMembers members
                LEFT JOIN v.genres genres
                LEFT JOIN v.categories categories
            WHERE
                ( :terms is null or UPPER(v.title) LIKE :terms)
            AND
                ( :genres is null or genres.id.genreId in :genres)
            AND
                ( :categories is null or categories.id.categoryId in :categories)
            AND
                ( :members is null or members.id.castMemberId in :members)
            """)
    Page<VideoPreview> findAll(
            @Param("terms") String terms,
            @Param("members") List<String> members,
            @Param("genres") List<String> genres,
            @Param("categories") List<String> categories,
            Pageable page
    );

}
