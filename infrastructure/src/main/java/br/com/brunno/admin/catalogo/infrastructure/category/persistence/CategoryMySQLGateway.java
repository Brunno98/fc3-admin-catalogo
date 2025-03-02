package br.com.brunno.admin.catalogo.infrastructure.category.persistence;

import br.com.brunno.admin.catalogo.domain.category.Category;
import br.com.brunno.admin.catalogo.domain.category.CategoryGateway;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.infrastructure.util.SpecificationUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;

    public CategoryMySQLGateway(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(Category aCategory) {
        return save(aCategory);
    }

    @Override
    public void deleteById(CategoryId anId) {
        final var idValue = anId.getValue();
        if (this.categoryRepository.existsById(idValue)) {
            this.categoryRepository.deleteById(idValue);
        }
    }

    @Override
    public Optional<Category> findById(CategoryId anId) {
        return this.categoryRepository.findById(anId.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(Category aCategory) {
        return save(aCategory);
    }

    @Override
    public Pagination<Category> findAll(SearchQuery aQuery) {
        final var pageRequest = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final Specification<CategoryJpaEntity> specification = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(str -> {
                    final var likeName = SpecificationUtil.<CategoryJpaEntity>like("name", str);
                    final var likeDescription = SpecificationUtil.<CategoryJpaEntity>like("description", str);
                    return likeName.or(likeDescription);
                })
                .orElse(null);

        final var pageResult = this.categoryRepository.findAll(specification, pageRequest);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public List<CategoryId> existisByIds(List<CategoryId> ids) {
        final var idsAsString = ids.stream().map(CategoryId::getValue).toList();
        return categoryRepository.existsByIds(idsAsString).stream()
                .map(CategoryId::from)
                .toList();
    }

    private Category save(Category aCategory) {
        return this.categoryRepository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }
}
