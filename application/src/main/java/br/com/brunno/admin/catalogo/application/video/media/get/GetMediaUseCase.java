package br.com.brunno.admin.catalogo.application.video.media.get;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.video.MediaResourceGateway;

import java.util.Objects;

public class GetMediaUseCase extends UseCase<GetMediaCommand, MediaOutput> {

    private final MediaResourceGateway mediaResourceGateway;

    public GetMediaUseCase(MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public MediaOutput execute(GetMediaCommand aCommand) {
        final var videoID = aCommand.videoId();
        final var type = aCommand.type();

        final var aResource = this.mediaResourceGateway.getResource(videoID, type)
                .orElseThrow(() -> NotFoundException.with(
                        new Error("Resource with from videoId %s and type %s not found"
                                .formatted(videoID.getValue(), type.name())))
                );

        return new MediaOutput(
                aResource.name(),
                aResource.contentType(),
                aResource.content()
        );
    }

}
