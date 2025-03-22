package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.event.DomainEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record VideoMediaCreated(
        String resourceId,
        String filePath,
        Instant occurredOn
) implements DomainEvent {
    public VideoMediaCreated(final String resourceId, final String filePath) {
        this(resourceId, filePath, Instant.now().truncatedTo(ChronoUnit.MILLIS));
    }
}
