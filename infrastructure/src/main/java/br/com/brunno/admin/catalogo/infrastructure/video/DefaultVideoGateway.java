package br.com.brunno.admin.catalogo.infrastructure.video;

import br.com.brunno.admin.catalogo.domain.Identifier;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.video.Video;
import br.com.brunno.admin.catalogo.domain.video.VideoGateway;
import br.com.brunno.admin.catalogo.domain.video.VideoID;
import br.com.brunno.admin.catalogo.domain.video.VideoPreview;
import br.com.brunno.admin.catalogo.domain.video.VideoSearchQuery;
import br.com.brunno.admin.catalogo.infrastructure.configuration.qualifiers.VideoCreatedQualifier;
import br.com.brunno.admin.catalogo.infrastructure.services.EventService;
import br.com.brunno.admin.catalogo.infrastructure.util.SqlUtils;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoJpaEntity;
import br.com.brunno.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final VideoRepository videoRepository;
    private final EventService eventService;

    public DefaultVideoGateway(
            VideoRepository videoRepository,
            @VideoCreatedQualifier EventService eventService
    ) {
        this.videoRepository = Objects.requireNonNull(videoRepository);
        this.eventService = Objects.requireNonNull(eventService);
    }

    @Transactional
    @Override
    public Video create(Video aVideo) {
        return save(aVideo);
    }

    @Override
    public void deleteById(VideoID anId) {
        final var aVideoId = anId.getValue();
        if (this.videoRepository.existsById(aVideoId)) {
            this.videoRepository.deleteById(aVideoId);
        }
    }

    @Transactional(readOnly = true) // https://vladmihalcea.com/spring-read-only-transaction-hibernate-optimization/
    @Override
    public Optional<Video> findById(VideoID anId) {
        return this.videoRepository.findById(anId.getValue())
                .map(VideoJpaEntity::toAggregate);
    }

    @Override
    public Video update(Video aVideo) {
        return save(aVideo);
    }

    @Override
    public Pagination<VideoPreview> findAll(VideoSearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var actualPage = this.videoRepository.findAll(
                SqlUtils.like(aQuery.terms()),
                nullIfEmpty(asString(aQuery.members())),
                nullIfEmpty(asString(aQuery.genres())),
                nullIfEmpty(asString(aQuery.categories())),
                page
        );

        return new Pagination<>(
                actualPage.getNumber(),
                actualPage.getSize(),
                actualPage.getTotalElements(),
                actualPage.toList()
        );
    }

    private Video save(Video aVideo) {
        final var video = this.videoRepository.save(VideoJpaEntity.from(aVideo))
                .toAggregate();

        video.publishDomainEvents(this.eventService::sendEvent);

        return video;
    }

    private List<String> asString(Set<? extends Identifier> ids) {
        if (Objects.isNull(ids)) return null;

        return ids.stream()
                .map(Identifier::getValue)
                .map(Object::toString)
                .toList();
    }

    private <T extends Collection<?>> T nullIfEmpty(T collection) {
        if (Objects.isNull(collection) || collection.isEmpty()) return null;
        return collection;
    }
}
