package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.pagination.Pagination;

// Arquivo criado para tentar resolver o desafio de retornar, no response da listagem de videos, os nomes dos generos, categorias e castMembers

public interface VideoConsolidatedGateway {

    VideoConsolidated findById(VideoID videoID);

    Pagination<VideoConsolidated> findAll(VideoSearchQuery aQuery);

}
