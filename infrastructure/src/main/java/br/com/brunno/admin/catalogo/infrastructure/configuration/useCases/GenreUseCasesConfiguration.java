package br.com.brunno.admin.catalogo.infrastructure.configuration.useCases;

import br.com.brunno.admin.catalogo.application.genre.create.CreateGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import br.com.brunno.admin.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import br.com.brunno.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import br.com.brunno.admin.catalogo.infrastructure.category.persistence.CategoryMySQLGateway;
import br.com.brunno.admin.catalogo.infrastructure.genre.persistence.GenreMySQLGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenreUseCasesConfiguration {

    private final GenreMySQLGateway genreMySQLGateway;
    private final CategoryMySQLGateway categoryMySQLGateway;

    public GenreUseCasesConfiguration(GenreMySQLGateway genreMySQLGateway, CategoryMySQLGateway categoryMySQLGateway) {
        this.genreMySQLGateway = genreMySQLGateway;
        this.categoryMySQLGateway = categoryMySQLGateway;
    }

    @Bean
    public CreateGenreUseCase createGenreUseCase() {
        return new CreateGenreUseCase(genreMySQLGateway, categoryMySQLGateway);
    }

    @Bean
    public DeleteGenreUseCase deleteGenreUseCase() {
        return new DeleteGenreUseCase(genreMySQLGateway);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase() {
        return new GetGenreByIdUseCase(genreMySQLGateway);
    }

    @Bean
    public ListGenreUseCase listGenreUseCase() {
        return new ListGenreUseCase(genreMySQLGateway);
    }

    @Bean
    public UpdateGenreUseCase updateGenreCommand() {
        return new UpdateGenreUseCase(categoryMySQLGateway, genreMySQLGateway);
    }
}
