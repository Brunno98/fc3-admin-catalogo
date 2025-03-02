package br.com.brunno.admin.catalogo.application.genre.update;

import br.com.brunno.admin.catalogo.application.UseCase;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotFoundException;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import br.com.brunno.admin.catalogo.domain.genre.Genre;
import br.com.brunno.admin.catalogo.domain.genre.GenreGateway;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateGenreUseCase extends UseCase<UpdateGenreCommand, UpdateGenreOutput> {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public UpdateGenreUseCase(CategoryGateway categoryGateway, GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public UpdateGenreOutput execute(UpdateGenreCommand aCommand) {
        final var anId = GenreID.from(aCommand.id());
        final var aName = aCommand.name();
        final var isActive = aCommand.isActive();
        final var categories = toCategoryId(aCommand.categories());

        final var aGenre = this.genreGateway.findById(anId)
                .orElseThrow(() -> NotFoundException.with(Genre.class, anId));

        final var notification = Notification.create();
        notification.append(validateCategories(categories));
        notification.validate(() -> aGenre.update(aName, isActive, categories));
        if (notification.hasErrors()) {
            throw new NotificationException("Could not update a Genre", notification);
        }
        genreGateway.update(aGenre);

        return UpdateGenreOutput.from(aGenre);
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

    private List<CategoryId> toCategoryId(List<String> categories) {
        return categories.stream().map(CategoryId::from).collect(Collectors.toList());
    }

}
