package br.com.brunno.admin.catalogo.application.castmember.update;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberGateway;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;

public class UpdateCastMemberUseCase extends UseCase<UpdateCastMemberCommand, UpdateCastMemberOutput> {

    private final CastMemberGateway castMemberGateway;

    public UpdateCastMemberUseCase(CastMemberGateway castMemberGateway) {
        this.castMemberGateway = castMemberGateway;
    }

    @Override
    public UpdateCastMemberOutput execute(UpdateCastMemberCommand anIn) {
        final var castMemberId = anIn.id();
        final var name = anIn.name();
        final var type = anIn.type();

        final var castMember = castMemberGateway.findById(castMemberId)
                .orElseThrow(() -> NotFoundException.with(CastMember.class, castMemberId));

        final var notification = Notification.create();
        notification.validate(() -> castMember.update(name, type));

        if (notification.hasErrors()) {
            throw new NotificationException("Failed to update Aggregate CastMember", notification);
        }

        this.castMemberGateway.update(castMember);

        return UpdateCastMemberOutput.from(castMember);
    }

}
