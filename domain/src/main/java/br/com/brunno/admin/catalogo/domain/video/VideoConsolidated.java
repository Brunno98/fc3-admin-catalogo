package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.genre.Genre;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// Arquivo criado para tentar resolver o desafio de retornar, no response da listagem de videos, os nomes dos generos, categorias e castMembers

public class VideoConsolidated {

    private VideoID videoID;
    private String title;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Category> categories;
    private List<Genre> genres;
    private List<CastMember> castMembers;

    public VideoConsolidated(Video video, List<Category> categories, List<Genre> genres, List<CastMember> castMembers) {
        Objects.requireNonNull(video);
        Objects.requireNonNull(categories);
        Objects.requireNonNull(genres);
        Objects.requireNonNull(castMembers);
        this.videoID = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.createdAt = video.getCreatedAt();
        this.updatedAt = video.getUpdatedAt();
        this.categories = new ArrayList<>(categories);
        this.genres = new ArrayList<>(genres);
        this.castMembers = new ArrayList<>(castMembers);
    }

    public VideoID getVideoID() {
        return videoID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public List<Genre> getGenres() {
        return new ArrayList<>(genres);
    }

    public List<CastMember> getCastMembers() {
        return new ArrayList<>(castMembers);
    }
}
