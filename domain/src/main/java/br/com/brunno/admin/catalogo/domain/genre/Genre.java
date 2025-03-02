package br.com.brunno.admin.catalogo.domain.genre;

import br.com.brunno.admin.catalogo.domain.AggregateRoot;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Genre extends AggregateRoot<GenreID> {

    private GenreID id;
    private String name;
    private boolean active;
    private List<CategoryId> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Genre(
            GenreID genreID,
            String name,
            boolean isActive,
            List<CategoryId> categories,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        super(genreID);
        this.id = genreID;
        this.name = name;
        this.active = isActive;
        this.categories = new ArrayList<>(categories);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;

        selfValidate("failed to create AggregateRoot Genre");
    }

    public static Genre newGenre(String aName, boolean isActive) {
        return newGenre(aName, isActive, Collections.emptyList());
    }

    public static Genre newGenre(String aName, boolean isActive, List<CategoryId> categories) {
        final var now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        final var deletedAt = isActive ? null : now;
        return new Genre(GenreID.unique(), aName, isActive, categories, now, now, deletedAt);
    }

    public static Genre with(
            GenreID anId,
            String name,
            boolean active,
            List<CategoryId> categories,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new Genre(anId, name, active, categories, createdAt, updatedAt, deletedAt);
    }

    public static Genre with(Genre aGenre) {
        return Genre.with(
                aGenre.getId(),
                aGenre.getName(),
                aGenre.isActive(),
                aGenre.getCategories(),
                aGenre.getCreatedAt(),
                aGenre.getUpdatedAt(),
                aGenre.getDeletedAt()
        );
    }

    public GenreID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isActive() {
        return this.active;
    }

    public List<CategoryId> getCategories() {
        return Collections.unmodifiableList(this.categories);
    }

    public Instant getCreatedAt() {
        return this.createdAt.truncatedTo(ChronoUnit.MICROS);
    }

    public Instant getUpdatedAt() {
        return this.updatedAt.truncatedTo(ChronoUnit.MICROS);
    }

    public Instant getDeletedAt() {
        if (Objects.isNull(this.deletedAt)) return null;
        return this.deletedAt.truncatedTo(ChronoUnit.MICROS);
    }

    public void validate(ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    public void deactivate() {
        if (!this.active) return;

        this.active = false;

        final var now = Instant.now();
        this.deletedAt = now;
        this.updatedAt = now;
    }

    public void activate() {
        if (active) return;

        this.active = true;

        this.deletedAt = null;
        this.updatedAt = Instant.now();
    }

    public Genre update(String aName, boolean isActive, List<CategoryId> categories) {
        if (isActive) {
            this.activate();
        } else {
            this.deactivate();
        }
        this.name = aName;
        this.categories = new ArrayList<>(Objects.isNull(categories) ? Collections.emptyList() : categories);
        this.updatedAt = Instant.now();

        selfValidate("failed to update Genre");
        return this;
    }

    private void selfValidate(String errorMessage) {
        final var notification = Notification.create();
        this.validate(notification);
        if (notification.hasErrors()) {
            throw new NotificationException(errorMessage, notification);
        }
    }

    public void addCategory(CategoryId aCategoryId) {
        if (Objects.isNull(aCategoryId)) return;

        this.categories.add(aCategoryId);
        this.updatedAt = Instant.now();
        selfValidate("failed to add category to Genre " + this.id);
    }

    public void addCategories(List<CategoryId> categoriesIds) {
        if (Objects.isNull(categoriesIds) || categoriesIds.isEmpty()) return;

        this.categories.addAll(categoriesIds);
        this.updatedAt = Instant.now();
        selfValidate("failed to add categories to Genre " + this.id);
    }

    public void removeCategory(CategoryId aCategoryId) {
        if (Objects.isNull(aCategoryId)) return;

        this.categories.remove(aCategoryId);
        this.updatedAt = Instant.now();
        selfValidate("failed to remove category to Genre " + this.id);
    }
}
