package br.com.brunno.admin.catalogo.application;

import br.com.brunno.admin.catalogo.IntegrationTest;
import br.com.brunno.admin.catalogo.application.category.create.CreateCategoryUseCase;
import br.com.brunno.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class SampleIT {

    @Autowired
    private CreateCategoryUseCase createCategoryUseCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testInjection() {
        Assertions.assertNotNull(createCategoryUseCase);
        Assertions.assertNotNull(categoryRepository);
    }
}
