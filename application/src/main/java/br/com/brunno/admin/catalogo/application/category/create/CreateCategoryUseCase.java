package br.com.brunno.admin.catalogo.application.category.create;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;

public class CreateCategoryUseCase extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {

    private final CategoryGateway categoryGateway;

    public CreateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public Either<Notification, CreateCategoryOutput> execute(CreateCategoryCommand aCommand) {
        String name = aCommand.name();
        String description = aCommand.description();
        boolean active = aCommand.isActive();

        Notification notification = Notification.create();

        Category category = Category.newCategory(name, description, active);

        category.validate(notification);

        return notification.hasErrors() ? API.Left(notification) : create(category);
    }

    private Either<Notification, CreateCategoryOutput> create(Category category) {
        return Try.of(() -> this.categoryGateway.create(category))
                .toEither()
                .bimap(Notification::create, CreateCategoryOutput::from);
    }
}
