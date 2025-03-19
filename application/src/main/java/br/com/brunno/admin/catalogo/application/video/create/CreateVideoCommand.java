package br.com.brunno.admin.catalogo.application.video.create;

import br.com.brunno.admin.catalogo.domain.resource.Resource;

import java.util.Optional;
import java.util.Set;

public record CreateVideoCommand(
        String title,
        String description,
        Integer launchedAt,
        Double duration,
        boolean opened,
        boolean published,
        String rating,
        Set<String> categories,
        Set<String> genres,
        Set<String> members,
        Resource video,
        Resource trailer,
        Resource banner,
        Resource thumbnail,
        Resource thumbnailHalf
) {
    public static CreateVideoCommand with(
            String aTitle,
            String aDescription,
            int aLaunchAt,
            double aDuration,
            boolean isOpened,
            boolean issPublished,
            String aRating,
            Set<String> categories,
            Set<String> genres,
            Set<String> castMembers,
            Resource aVideo,
            Resource aTrailer,
            Resource aBanner,
            Resource aThumbnail,
            Resource aThumbnailHalf
    ) {
        return new CreateVideoCommand(
                aTitle,
                aDescription,
                aLaunchAt,
                aDuration,
                isOpened,
                issPublished,
                aRating,
                categories,
                genres,
                castMembers,
                aVideo,
                aTrailer,
                aBanner,
                aThumbnail,
                aThumbnailHalf
        );
    }

    public Optional<Resource> getVideo() {
        return Optional.ofNullable(this.video);
    }

    public Optional<Resource> getTrailer() {
        return Optional.ofNullable(this.trailer);
    }

    public Optional<Resource> getBanner() {
        return Optional.ofNullable(this.banner);
    }

    public Optional<Resource> getThumbnail() {
        return Optional.ofNullable(this.thumbnail);
    }

    public Optional<Resource> getThumbnailHalf() {
        return Optional.ofNullable(this.thumbnailHalf);
    }
}
