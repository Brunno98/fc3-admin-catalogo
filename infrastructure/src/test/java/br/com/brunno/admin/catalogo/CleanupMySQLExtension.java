package br.com.brunno.admin.catalogo;

import br.com.brunno.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import br.com.brunno.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import br.com.brunno.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class CleanupMySQLExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        final var applicationContext = SpringExtension.getApplicationContext(extensionContext);

        cleanUp(List.of(
                applicationContext.getBean(GenreRepository.class),
                applicationContext.getBean(CategoryRepository.class),
                applicationContext.getBean(CastMemberRepository.class)
        ));
    }

    private void cleanUp(Collection<CrudRepository<?, ?>> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}
