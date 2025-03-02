package br.com.brunno.admin.catalogo.domain.genre;

import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenreTest {

    @Test
    void givenAValidParams_whenCallNewGenre_shouldInstantiateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 0;

        final var genre = Genre.newGenre(expectedName, expectedIsActivate);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertNotNull(genre.getUpdatedAt());
        assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAnInvalidNullName_whenCallsNewGenre_shouldReturnAnError() {
        final String nullName = null;
        final var expectedIsActivate = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualException = assertThrows(NotificationException.class,
                () -> Genre.newGenre(nullName, expectedIsActivate));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyName_whenCallsNewGenre_shouldReturnAnError() {
        final String emptyName = " ";
        final var expectedIsActivate = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be blank";

        final var actualException = assertThrows(NotificationException.class,
                () -> Genre.newGenre(emptyName, expectedIsActivate));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameGreaterThan255_whenCallsNewGenre_shouldReturnAnError() {
        final String bigName = "a".repeat(256);
        final var expectedIsActivate = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' length should be between 1 and 255";

        final var actualException = assertThrows(NotificationException.class,
                () -> Genre.newGenre(bigName, expectedIsActivate));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnActiveGenre_whenCallDeactivate_shouldDeactivateTheGenre() {
        final var expectedName = "Ação";
        final var expectedIsActivate = false;
        final var expectedQuantityOfCategories = 0;
        final var genre = Genre.newGenre(expectedName, true);

        assertNotNull(genre);
        assertTrue(genre.isActive());
        assertNull(genre.getDeletedAt());

        genre.deactivate();

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNotNull(genre.getDeletedAt());
    }

    @Test
    void givenAnDeactivatedGenre_whenCallActivate_shouldActivateTheGenre() {
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 0;
        final var genre = Genre.newGenre(expectedName, false);

        assertNotNull(genre);
        assertFalse(genre.isActive());
        assertNotNull(genre.getDeletedAt());

        genre.activate();

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAnActiveGenre_whenCallUpdateWithDeactivate_shouldReceiveAnUpdatedGenre() {
        final var expectedName = "Ação";
        final var expectedIsActivate = false;
        final var expectedQuantityOfCategories = 0;
        final var genre = Genre.newGenre("acao", true);

        assertNotNull(genre);
        assertTrue(genre.isActive());
        assertNull(genre.getDeletedAt());

        genre.update(expectedName, expectedIsActivate, null);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNotNull(genre.getDeletedAt());
    }

    @Test
    void givenAnDeactivatedGenre_whenCallUpdateWithActivate_shouldReceiveAGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 0;
        final var genre = Genre.newGenre("acao", false);

        assertNotNull(genre);
        assertFalse(genre.isActive());
        assertNotNull(genre.getDeletedAt());

        genre.update(expectedName, expectedIsActivate, null);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidGenre_whenCallsUpdateWithNullName_shouldReturnAnError() {
        final String nullName = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var actualGenre = Genre.newGenre("acao", true);

        final var actualException = assertThrows(NotificationException.class,
                () -> actualGenre.update(nullName, true, null));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAValidGenre_whenCallsUpdateWithEmptyCategories_shouldRemoveAllCategories() {
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 0;
        final var genre = Genre.newGenre(expectedName, true, List.of(CategoryId.unique()));

        assertEquals(1, genre.getCategories().size());

        genre.update(expectedName, expectedIsActivate, null);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidEmptyCategoriesGenre_whenCallsAddCategory_shouldAddTheCategory() {
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 1;
        final var expectedCategoryId = CategoryId.unique();
        final var genre = Genre.newGenre(expectedName, true);

        assertEquals(0, genre.getCategories().size());

        genre.addCategory(expectedCategoryId);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertEquals(expectedCategoryId, genre.getCategories().get(0));
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithCategories_whenCallsRemoveCategory_shouldRemoveTheCategory() {
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 1;
        final var expectedCategoryIdToRemain = CategoryId.unique();
        final var categoryToRemove = CategoryId.unique();
        final var genre = Genre.newGenre(expectedName, true, List.of(categoryToRemove, expectedCategoryIdToRemain));

        assertEquals(2, genre.getCategories().size());

        genre.removeCategory(categoryToRemove);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertEquals(expectedCategoryIdToRemain, genre.getCategories().get(0));
        assertNotNull(genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(genre.getCreatedAt()));
        assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAnInvalidNullAsCategoryId_whenCallAddCategory_shouldDoNothing(){
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 0;
        final var genre = Genre.newGenre(expectedName, true);

        assertEquals(0, genre.getCategories().size());

        genre.addCategory(null);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), genre.getCreatedAt());
        assertNull(genre.getDeletedAt());
    }

    @Test
    void givenAnInvalidNullAsCategoryId_whenCallRemoveCategory_shouldDoNothing() {
        final var expectedName = "Ação";
        final var expectedIsActivate = true;
        final var expectedQuantityOfCategories = 1;
        final var genre = Genre.newGenre(expectedName, true, List.of(CategoryId.unique()));

        assertEquals(1, genre.getCategories().size());

        genre.removeCategory(null);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActivate, genre.isActive());
        assertEquals(expectedQuantityOfCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), genre.getCreatedAt());
        assertNull(genre.getDeletedAt());
    }
}
