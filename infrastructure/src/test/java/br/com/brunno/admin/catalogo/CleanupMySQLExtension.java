package br.com.brunno.admin.catalogo;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

public class CleanupMySQLExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        final var applicationContext = SpringExtension.getApplicationContext(extensionContext);

        final var crudRepositoryMap = applicationContext.getBeansOfType(CrudRepository.class);

        cleanUp(crudRepositoryMap.values());
    }

    private void cleanUp(Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}
