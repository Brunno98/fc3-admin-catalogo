package br.com.brunno.admin.catalogo.domain.castmember;

import br.com.brunno.admin.catalogo.domain.AggregateRoot;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CastMember extends AggregateRoot<CastMemberID> {

    private CastMemberID id;
    private String name;
    private CastMemberType type;
    private Instant createdAt;
    private Instant updatedAt;

    private CastMember(
            CastMemberID id,
            String name,
            CastMemberType type,
            Instant createdAt,
            Instant updatedAt
    ) {
        super(id);
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        selfValidate();
    }

    private void selfValidate() {
        final var notification = Notification.create();
        this.validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Failed to create Aggregate Cast Member", notification);
        }
    }

    public static CastMember create(String name, CastMemberType type) {
        return CastMember.create(CastMemberID.unique(), name, type);
    }

    public static CastMember create(CastMemberID id, String name, CastMemberType type) {
        final var now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        return new CastMember(id, name, type, now, now);
    }

    public static CastMember create(
            CastMemberID id,
            String name,
            CastMemberType type,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new CastMember(id, name, type, createdAt, updatedAt);
    }

    @Override
    public void validate(ValidationHandler handler) {
        new CastMemberValidator(this, handler).validate();
    }

    public String getName() {
        return this.name;
    }

    public CastMemberType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt.truncatedTo(ChronoUnit.MICROS);
    }

    public Instant getUpdatedAt() {
        return updatedAt.truncatedTo(ChronoUnit.MICROS);
    }

    public CastMemberID getId() {
        return this.id;
    }

    public CastMember update(String aName, CastMemberType aType) {
        this.name = aName;
        this.type = aType;
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
        selfValidate();
        return this;
    }
}
