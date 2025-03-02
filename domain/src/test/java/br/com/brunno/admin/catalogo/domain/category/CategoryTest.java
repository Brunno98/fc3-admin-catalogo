package br.com.brunno.admin.catalogo.domain.category;

import br.com.brunno.admin.catalogo.domain.exceptions.DomainException;
import br.com.brunno.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    @Test
    public void testNewCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "Uma categoria de filmes";
        final var expectedActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedActive);

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
        assertEquals(expectedActive, category.isActive());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNull(category.getDeletedAt());
    }

    @Test
    void givenAnInvalidNullName_whenCreateNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = null;
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedDescription = "Uma categoria de filmes";
        final var expectedActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedActive);

        final var actualException = assertThrows(DomainException.class,
                () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyName_whenCreateNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = "";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'name' should not be blank";
        final var expectedDescription = "Uma categoria de filmes";
        final var expectedActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedActive);

        final var actualException = assertThrows(DomainException.class,
                () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidBlankName_whenCreateNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = "        ";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'name' should not be blank";
        final var expectedDescription = "Uma categoria de filmes";
        final var expectedActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedActive);

        final var actualException = assertThrows(DomainException.class,
                () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3_whenCreateNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = "fi   ";
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'name' should not be less than 3";
        final var expectedDescription = "Uma categoria de filmes";
        final var expectedActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedActive);

        final var actualException = assertThrows(DomainException.class,
                () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameLengthGreaterThan255_whenCreateNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = """
                dfasdfasdjfhasjkfhaskjfhasjkfhajshfajkshfjakshfkjashfkashfafskfhaskfhaskjfhasjkfhakjshfkjasdhfaskhflskd
                hfjklashfkjasdhfaglçjdfoivbsadiofgjsodashfjkasdhfkajshfklasjdhfasjkdlfhjkasdfhjksahfklashfklasdhfkjlahsk
                adshfjasdhfjkaldhfklashfkfhsdkjfhalskdfhasjklfhlsknvfosvbnasofdugnuowrgnrunfverfvnoaehgauorfnwoafgjoweif
                """;
        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = "'name' should not be greater than 255";
        final var expectedDescription = "Uma categoria de filmes";
        final var expectedActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedActive);

        final var actualException = assertThrows(DomainException.class,
                () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorsCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAValidFalseIsActive_whenCreateNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = "Filmes";
        final var expectedDescription = "Uma categoria de filmes";
        final var expectedActive = false;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedActive);

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
        assertEquals(expectedActive, category.isActive());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNotNull(category.getDeletedAt());
    }
}