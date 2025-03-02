package br.com.brunno.admin.catalogo.application.genre.create;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateGenreUseCase extends UseCase<CreateGenreCommand, CreateGenreOutput> {

    private final GenreGateway genreGateway;
    private final CategoryGateway categoryGateway;

    public CreateGenreUseCase(GenreGateway genreGateway, CategoryGateway categoryGateway) {
        this.genreGateway = genreGateway;
        this.categoryGateway = categoryGateway;
    }

    public CreateGenreOutput execute(CreateGenreCommand aCommand) {
        final var aName = aCommand.name();
        final var isActive = aCommand.active();
        final var categoryIds = toCategoryID(aCommand.categories());

        final var notification = Notification.create();
        notification.append(validateCategories(categoryIds));
        final var aGenre = notification.validate(() -> Genre.newGenre(aName, isActive, categoryIds));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not create a Genre", notification);
        }

        return CreateGenreOutput.from(genreGateway.create(aGenre));
    }

    private ValidationHandler validateCategories(List<CategoryId> ids) {
        final var categoryValidationNotification = Notification.create();
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return categoryValidationNotification;
        }

        final var retrievedIds = categoryGateway.existisByIds(ids);

        if (retrievedIds.size() != ids.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);
            final var missingIdsMessage = missingIds.stream()
                    .map(CategoryId::getValue)
                    .collect(Collectors.joining(", "));
            categoryValidationNotification.append(
                    new Error("Some category IDs could not be found: %s".formatted(missingIdsMessage))
            );
        }

        return categoryValidationNotification;
    }

    private List<CategoryId> toCategoryID(List<String> categories) {
        return categories.stream()
                .map(CategoryId::from)
                .collect(Collectors.toList());
    }

}
