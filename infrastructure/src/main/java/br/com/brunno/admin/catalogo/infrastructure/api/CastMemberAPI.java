package br.com.brunno.admin.catalogo.infrastructure.api;

import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/cast_members")
@Tag(name = "Cast Members")
public interface CastMemberAPI {

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Creates a new Cast member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cast Member created successfully"),
            @ApiResponse(responseCode = "422", description = "An validation error occurred"),
            @ApiResponse(responseCode = "500", description = "An internal server erro was thrown")
    })
    ResponseEntity<?> create(@RequestBody CreateCastMemberRequest createCastMemberRequest);


    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Find a Cast Member by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cast Member was found"),
            @ApiResponse(responseCode = "404", description = "Cast Member not found")
    })
    CastMemberResponse getById(@PathVariable String id);


    @PutMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Updates an existent Cast Member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cast Member updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cast Member was not found"),
            @ApiResponse(responseCode = "422", description = "A Validation Error was occurred"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateCastMemberRequest updateCastMemberRequest);


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Cast Member by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cast Member deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    void deleteById(@PathVariable String id);


    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "List all Cast Members paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listed Successfully"),
            @ApiResponse(responseCode = "422", description = "A invalid parameter was received"), //TODO: Validar se isso ocorre de fato
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    Pagination<CastMemberListResponse> findAll(
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") final String direction
    );
}
