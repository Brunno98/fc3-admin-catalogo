package br.com.brunno.admin.catalogo.application.category.update;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.exceptions.DomainException;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;

public class UpdateCategoryUseCase extends UseCase<UpdateCategoryCommand, Either<Notification, UpdateCategoryOutput>> {

    private final CategoryGateway categoryGateway;

    public UpdateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(UpdateCategoryCommand aCommand) {
        final var anId = aCommand.categoryId();
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var isActive = aCommand.active();

        final var aCategory = categoryGateway.findById(anId)
                .orElseThrow(() -> DomainException.with(new Error("Category with ID: " + anId.getValue() + " not found")));

        final var notification = Notification.create();

        aCategory.update(aName, aDescription, isActive);

        aCategory.validate(notification);

        return notification.hasErrors()? API.Left(notification) : update(aCategory);
    }

    private Either<Notification, UpdateCategoryOutput> update(Category aCategory) {
        return Try.of(() -> categoryGateway.update(aCategory))
                .toEither()
                .bimap(Notification::create, UpdateCategoryOutput::from);
    }
}
