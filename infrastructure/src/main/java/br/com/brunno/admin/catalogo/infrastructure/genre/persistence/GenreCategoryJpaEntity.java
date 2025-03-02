package br.com.brunno.admin.catalogo.infrastructure.genre.persistence;

import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "genres_categories")
public class GenreCategoryJpaEntity {

    @EmbeddedId
    private GenreCategoryId id;

    @ManyToOne
    @MapsId("genreId")
    private GenreJpaEntity genre;

//    @ManyToOne
//    @MapsId("categoryId")
//    private CategoryJpaEntity category;

    public GenreCategoryJpaEntity() {}

    private GenreCategoryJpaEntity(GenreJpaEntity genre, CategoryId categoryId) {
        this.id = GenreCategoryId.from(genre.getId(), categoryId.getValue());
        this.genre = genre;
    }

    public static GenreCategoryJpaEntity from (GenreJpaEntity genre, CategoryId categoryId) {
        return new GenreCategoryJpaEntity(genre, categoryId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GenreCategoryJpaEntity that = (GenreCategoryJpaEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public GenreCategoryId getId() {
        return id;
    }

    public void setId(GenreCategoryId id) {
        this.id = id;
    }

    public GenreJpaEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreJpaEntity genre) {
        this.genre = genre;
    }
}
