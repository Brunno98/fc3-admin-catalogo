package br.com.brunno.admin.catalogo.infrastructure.services.local;

import br.com.brunno.admin.catalogo.domain.resource.Resource;
import br.com.brunno.admin.catalogo.infrastructure.services.StorageService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryStorageService implements StorageService {

    private final Map<String, Resource> storage;

    public InMemoryStorageService() {
        this.storage = new HashMap<>();
    }

    public void reset() {
        this.storage.clear();
    }

    public Map<String, Resource> getStorage() {
        return storage;
    }

    @Override
    public void deleteAll(Collection<String> names) {
        if (Objects.isNull(names)) return;

        names.forEach(this.storage::remove);
    }

    @Override
    public Optional<Resource> get(String name) {
        return Optional.ofNullable(this.storage.get(name));
    }

    @Override
    public List<String> list(String prefix) {
        if (Objects.isNull(prefix)) return Collections.emptyList();
        return this.storage.keySet()
                .stream()
                .filter(name -> name.startsWith(prefix))
                .toList();
    }

    @Override
    public void store(String name, Resource resource) {
        this.storage.put(name, resource);
    }
}
