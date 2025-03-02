package br.com.brunno.admin.catalogo.infrastructure.category.persistence;

import br.com.brunno.admin.catalogo.MySQLGatewayTest;
import br.com.brunno.admin.catalogo.domain.category.Category;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MySQLGatewayTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAnInvalidNullName_whenPersistCategory_shouldReturnsError() {
        final var expectedName = "Filme";
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;
        final var expectedPropertyName = "name";

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var entityCategory = CategoryJpaEntity.from(aCategory);
        entityCategory.setName(null);
        final var actualException = assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepository.save(entityCategory));

        final var actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());
        assertEquals(expectedPropertyName, actualCause.getPropertyName());
    }

    @Test
    public void givenAnInvalidNullCreatedAt_whenPersistCategory_shouldReturnsError() {
        final var expectedName = "Filme";
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;
        final var expectedPropertyName = "createdAt";

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var entityCategory = CategoryJpaEntity.from(aCategory);
        entityCategory.setCreatedAt(null);
        final var actualException = assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepository.save(entityCategory));

        final var actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());
        assertEquals(expectedPropertyName, actualCause.getPropertyName());
    }

    @Test
    public void givenAnInvalidNullUpdatedAt_whenPersistCategory_shouldReturnsError() {
        final var expectedName = "Filme";
        final var expectedDescription = "Categoria de filmes";
        final var expectedIsActive = true;
        final var expectedPropertyName = "updatedAt";

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var entityCategory = CategoryJpaEntity.from(aCategory);
        entityCategory.setUpdatedAt(null);
        final var actualException = assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepository.save(entityCategory));

        final var actualCause = assertInstanceOf(PropertyValueException.class, actualException.getCause());
        assertEquals(expectedPropertyName, actualCause.getPropertyName());
    }
}