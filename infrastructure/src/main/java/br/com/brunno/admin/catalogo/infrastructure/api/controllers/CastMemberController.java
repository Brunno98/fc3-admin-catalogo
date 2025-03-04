package br.com.brunno.admin.catalogo.infrastructure.api.controllers;

import br.com.brunno.admin.catalogo.application.castmember.create.CreateCastMemberCommand;
import br.com.brunno.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.get.GetCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.retrieve.list.ListCastMemberUseCase;
import br.com.brunno.admin.catalogo.application.castmember.update.UpdateCastMemberCommand;
import br.com.brunno.admin.catalogo.application.castmember.update.UpdateCastMemberOutput;
import br.com.brunno.admin.catalogo.application.castmember.update.UpdateCastMemberUseCase;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.exceptions.DomainException;
import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.domain.pagination.SearchQuery;
import br.com.brunno.admin.catalogo.domain.validation.Error;
import br.com.brunno.admin.catalogo.infrastructure.api.CastMemberAPI;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final GetCastMemberUseCase getCastMemberUseCase;
    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;
    private final ListCastMemberUseCase listCastMemberUseCase;

    public CastMemberController(
            final GetCastMemberUseCase getCastMemberUseCase,
            final CreateCastMemberUseCase createCastMemberUseCase,
            final UpdateCastMemberUseCase updateCastMemberUseCase,
            final DeleteCastMemberUseCase deleteCastMemberUseCase,
            final ListCastMemberUseCase listCastMemberUseCase
    ) {
        this.getCastMemberUseCase = Objects.requireNonNull(getCastMemberUseCase);
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase);
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase);
        this.listCastMemberUseCase = Objects.requireNonNull(listCastMemberUseCase);
    }

    @Override
    public ResponseEntity<?> create(CreateCastMemberRequest createCastMemberRequest) {
        if (!CastMemberType.isValue(createCastMemberRequest.type())) {
            final var invalidType = createCastMemberRequest.type();
            throw DomainException.with(new Error("Value '%s' is not a valid CastMemberType %s"
                    .formatted(invalidType, CastMemberType.getValuesNames())));
        }

        final var aCommand = new CreateCastMemberCommand(
                createCastMemberRequest.name(),
                CastMemberType.valueOf(createCastMemberRequest.type())
        );

        final var output = this.createCastMemberUseCase.execute(aCommand);

        return ResponseEntity
                .created(URI.create("/cast_members/"+output.id().getValue()))
                .body(Map.of("id", output.id().getValue()));
    }

    @Override
    public CastMemberResponse getById(String id) {
        return CastMemberResponse.from(this.getCastMemberUseCase.execute(CastMemberID.from(id)));
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateCastMemberRequest updateCastMemberRequest) {
        if (!CastMemberType.isValue(updateCastMemberRequest.type())) {
            final var invalidType = updateCastMemberRequest.type();
            throw DomainException.with(new Error("Value '%s' is not a valid CastMemberType %s"
                    .formatted(invalidType, CastMemberType.getValuesNames())));
        }

        final var aCommand = new UpdateCastMemberCommand(
                CastMemberID.from(id),
                updateCastMemberRequest.name(),
                CastMemberType.valueOf(updateCastMemberRequest.type())
        );

        final var output = this.updateCastMemberUseCase.execute(aCommand);

        return ResponseEntity.ok(Map.of("id", output.id().getValue()));
    }

    @Override
    public void deleteById(String id) {
        this.deleteCastMemberUseCase.execute(CastMemberID.from(id));
    }

    @Override
    public Pagination<CastMemberListResponse> findAll(String search, int page, int perPage, String sort, String direction) {
        final var aQuery = new SearchQuery(
                page,
                perPage,
                search,
                sort,
                direction
        );

        return this.listCastMemberUseCase.execute(aQuery)
                .map(CastMemberListResponse::from);
    }
}
