package br.com.brunno.admin.catalogo.infrastructure.configuration;

import br.com.brunno.admin.catalogo.infrastructure.configuration.properties.StorageProperties;
import br.com.brunno.admin.catalogo.infrastructure.services.StorageService;
import br.com.brunno.admin.catalogo.infrastructure.services.local.InMemoryStorageService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {

    @Bean
    public StorageService inMemoryStorageService() {
        return new InMemoryStorageService();
    }

    @Bean
    @ConfigurationProperties("storage.catalogo-videos")
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }

}
