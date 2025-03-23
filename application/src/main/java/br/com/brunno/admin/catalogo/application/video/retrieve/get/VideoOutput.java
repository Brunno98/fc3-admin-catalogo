package br.com.brunno.admin.catalogo.application.video.retrieve.get;

import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.video.AudioVideoMedia;
import br.com.brunno.admin.catalogo.domain.video.ImageMedia;
import br.com.brunno.admin.catalogo.domain.video.Video;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record VideoOutput(
        String id,
        String title,
        String description,
        Integer LaunchedAt,
        Double duration,
        Boolean opened,
        Boolean published,
        String rating,
        Instant createdAt,
        Instant updatedAt,
        Set<String> categories,
        Set<String> genres,
        Set<String> members,
        Optional<ImageMedia> banner,
        Optional<ImageMedia> thumbnail,
        Optional<ImageMedia> thumbnailHalf,
        Optional<AudioVideoMedia> video,
        Optional<AudioVideoMedia> trailer
) {

    public static VideoOutput from(Video aVideo) {
        return new VideoOutput(
                aVideo.getId().getValue(),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getLaunchedAt().getValue(),
                aVideo.getDuration(),
                aVideo.isOpened(),
                aVideo.isPublished(),
                aVideo.getRating().getName(),
                aVideo.getCreatedAt(),
                aVideo.getUpdatedAt(),
                aVideo.getCategories().stream().map(CategoryId::getValue).collect(Collectors.toSet()),
                aVideo.getGenres().stream().map(GenreID::getValue).collect(Collectors.toSet()),
                aVideo.getCastMembers().stream().map(CastMemberID::getValue).collect(Collectors.toSet()),
                aVideo.getBanner(),
                aVideo.getThumbnail(),
                aVideo.getThumbnailHalf(),
                aVideo.getVideo(),
                aVideo.getTrailer()
        );
    }
}
