package br.com.brunno.admin.catalogo.application.video.retrieve.list;

import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.video.VideoConsolidated;
import br.com.brunno.admin.catalogo.domain.video.VideoID;

import java.time.Instant;
import java.util.List;

public record VideoListOutput(
        VideoID id,
        String title,
        String description,
        Instant createdAt,
        Instant updatedAt,
        List<String> categories,
        List<String> genres,
        List<String> castMembers
) {

//    public static VideoListOutput from(Video videos) {
//        return new VideoListOutput(
//                videos.getId(),
//                videos.getTitle(),
//                videos.getDescription(),
//                videos.getCreatedAt(),
//                videos.getUpdatedAt()
//        );
//    }

    // metodo criado para tentar resolver o desafio de retornar, no response da listagem de videos, os nomes dos generos, categorias e castMembers
    public static VideoListOutput from(VideoConsolidated videoConsolidated) {
        return new VideoListOutput(
                videoConsolidated.getVideoID(),
                videoConsolidated.getTitle(),
                videoConsolidated.getDescription(),
                videoConsolidated.getCreatedAt(),
                videoConsolidated.getUpdatedAt(),
                videoConsolidated.getCategories().stream().map(Category::getName).toList(),
                videoConsolidated.getGenres().stream().map(Genre::getName).toList(),
                videoConsolidated.getCastMembers().stream().map(CastMember::getName).toList()
        );
    }


}
