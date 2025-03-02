package br.com.brunno.admin.catalogo.domain.category;

import br.com.brunno.admin.catalogo.domain.AggregateRoot;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;

import java.time.Instant;

public class Category extends AggregateRoot<CategoryId> {

    private String name;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Category(
            CategoryId anId,
            String aName,
            String aDescription,
            boolean isActive,
            Instant aCreationDate,
            Instant anUpdatedDate,
            Instant aDeletionDate
    ) {
        super(anId);
        this.name = aName;
        this.description = aDescription;
        this.active = isActive;
        this.createdAt = aCreationDate;
        this.updatedAt = anUpdatedDate;
        this.deletedAt = aDeletionDate;
    }

    public static Category newCategory(String name, String description, boolean isActive) {
        var id = CategoryId.unique();
        var now = Instant.now();
        var deletedAt = isActive ? null : now;
        return new Category(id, name, description, isActive, now, now, deletedAt);
    }

    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    @Override
    public void validate(ValidationHandler handler) {
        new CategoryValidator(this, handler).validate();
    }

    public Category deactivate() {
        if (this.deletedAt != null) return this;

        Instant now = Instant.now();
        this.deletedAt = now;
        this.updatedAt = now;
        this.active = false;

        return this;
    }

    public Category activate() {
        if (this.active) return this; // Já ativado

        this.active = true;
        this.updatedAt = Instant.now();
        this.deletedAt = null;

        return this;
    }

    public Category update(
            String aName,
            String aDescription,
            boolean isActive
    ) {
        if (isActive) {
            this.activate();
        } else {
            this.deactivate();
        }

        this.name = aName;
        this.description = aDescription;
        this.updatedAt = Instant.now();

        return this;
    }
}
