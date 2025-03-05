package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.domain.validation.ValidationHandler;
import br.com.brunno.admin.catalogo.domain.validation.Validator;

import java.util.Objects;

public class VideoValidator extends Validator {

    public static final int TITLE_MIN_LENGTH = 3;
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int DESCRIPTION_MIN_LENGTH = 1;
    public static final int DESCRIPTION_MAX_LENGTH = 4_000;

    private final Video video;

    public VideoValidator(Video video, ValidationHandler aHandler) {
        super(aHandler);
        this.video = video;
    }

    @Override
    public void validate() {
        validateTitleConstraints();
        validateDescriptionConstraints();
        validateLaunchedAtConstraints();
        validateRatingConstraints();
    }

    private void validateTitleConstraints() {
        if (Objects.isNull(this.video.getTitle())) {
            this.validationHandler().append(new Error("'title' should not be null"));
            return;
        }

        final var title = this.video.getTitle().trim();

        if (title.isBlank()) {
            this.validationHandler().append(new Error("'title' should not be blank"));
            return;
        }

        if (title.length() < TITLE_MIN_LENGTH || title.length() > TITLE_MAX_LENGTH) {
            final var errorMessage = "'title' length should be between %d and %d"
                    .formatted(TITLE_MIN_LENGTH, TITLE_MAX_LENGTH);
            this.validationHandler().append(new Error(errorMessage));
            return;
        }
    }

    private void validateDescriptionConstraints() {
        final var description = this.video.getDescription();
        if (Objects.isNull(description)) {
            this.validationHandler().append(new Error("'description' should not be null"));
            return;
        }

        if (description.isBlank()) {
            this.validationHandler().append(new Error("'description' should not be blank"));
            return;
        }

        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            final var errorMessage = "'description' length should be between %d and %d"
                    .formatted(DESCRIPTION_MIN_LENGTH, DESCRIPTION_MAX_LENGTH);
            this.validationHandler().append(new Error(errorMessage));
            return;
        }
    }

    private void validateLaunchedAtConstraints() {
        if (Objects.isNull(this.video.getLaunchedAt())) {
            this.validationHandler().append(new Error("'launchedAt' should not be null"));return;
        }
    }

    private void validateRatingConstraints() {
        if (Objects.isNull(this.video.getRating())) {
            this.validationHandler().append(new Error("'rating' should not be null"));return;
        }
    }
}
