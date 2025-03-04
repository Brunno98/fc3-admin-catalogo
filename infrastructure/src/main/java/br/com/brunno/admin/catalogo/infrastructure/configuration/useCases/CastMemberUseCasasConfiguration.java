package br.com.brunno.admin.catalogo.infrastructure.configuration.useCases;

import br.com.brunno.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.get.GetCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.list.ListCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.update.UpdateCastMemberUseCase;
import br.com.brunno.admin.catalogo.infrastructure.castmember.CastMemberMySQLGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CastMemberUseCasasConfiguration {

    private final CastMemberMySQLGateway castMemberMySQLGateway;

    public CastMemberUseCasasConfiguration(final CastMemberMySQLGateway castMemberMySQLGateway) {
        this.castMemberMySQLGateway = Objects.requireNonNull(castMemberMySQLGateway);
    }

    @Bean
    public CreateCastMemberUseCase createCastMemberUseCase() {
        return new CreateCastMemberUseCase(castMemberMySQLGateway);
    }

    @Bean
    public UpdateCastMemberUseCase updateCastMemberUseCase() {
        return new UpdateCastMemberUseCase(castMemberMySQLGateway);
    }

    @Bean
    public GetCastMemberUseCase getCastMemberUseCase() {
        return new GetCastMemberUseCase(castMemberMySQLGateway);
    }

    @Bean
    public ListCastMemberUseCase listCastMemberUseCase() {
        return new ListCastMemberUseCase(castMemberMySQLGateway);
    }

    @Bean
    public DeleteCastMemberUseCase deleteCastMemberUseCase() {
        return new DeleteCastMemberUseCase(castMemberMySQLGateway);
    }

}
