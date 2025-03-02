package br.com.brunno.admin.catalogo.infrastructure.configuration.useCases;

import br.com.brunno.admin.catalogo.application.category.create.CreateCategoryUseCase;
import br.com.brunno.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import br.com.brunno.admin.catalogo.application.category.retrive.get.GetCategoryByIdUseCase;
import br.com.brunno.admin.catalogo.application.category.retrive.list.ListCategoriesUseCase;
import br.com.brunno.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import br.com.brunno.admin.catalogo.infrastructure.category.persistence.CategoryMySQLGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCasesConfiguration {

    private final CategoryMySQLGateway categoryMySQLGateway;

    public CategoryUseCasesConfiguration(CategoryMySQLGateway categoryMySQLGateway) {
        this.categoryMySQLGateway = categoryMySQLGateway;
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new GetCategoryByIdUseCase(categoryMySQLGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new UpdateCategoryUseCase(categoryMySQLGateway);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new CreateCategoryUseCase(categoryMySQLGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new ListCategoriesUseCase(categoryMySQLGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DeleteCategoryUseCase(categoryMySQLGateway);
    }
}
