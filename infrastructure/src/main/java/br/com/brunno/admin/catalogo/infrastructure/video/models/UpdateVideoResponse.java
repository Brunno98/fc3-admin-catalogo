package br.com.brunno.admin.catalogo.infrastructure.video.models;

import br.com.brunno.admin.catalogo.application.video.update.UpdateVideoOutput;

public record UpdateVideoResponse(String id) {

    public UpdateVideoResponse(UpdateVideoOutput output) {
        this(output.id().getValue());
    }

}
