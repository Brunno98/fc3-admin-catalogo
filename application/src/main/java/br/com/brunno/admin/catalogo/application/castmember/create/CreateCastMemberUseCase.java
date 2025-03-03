package br.com.brunno.admin.catalogo.application.castmember.create;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;

public class CreateCastMemberUseCase extends UseCase<CreateCastMemberCommand, CreateCastMemberOutput> {

    private final CastMemberGateway castMemberGateway;

    public CreateCastMemberUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = castMemberGateway;
    }

    @Override
    public CreateCastMemberOutput execute(CreateCastMemberCommand anIn) {
        final var aName = anIn.name();
        final var aType = anIn.type();

        final var notification = Notification.create();

        final var aCastMember = notification.validate(() -> CastMember.create(aName, aType));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not create Aggregate CastMember", notification);
        }

        return CreateCastMemberOutput.from(castMemberGateway.create(aCastMember));
    }

}
