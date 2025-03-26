package br.com.brunno.admin.catalogo.e2e;

import br.com.brunno.admin.catalogo.ApiTest;
import br.com.brunno.admin.catalogo.domain.Identifier;
import br.com.brunno.admin.catalogo.domain.castmember.CastMember;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberID;
import br.com.brunno.admin.catalogo.domain.castmember.CastMemberType;
import br.com.brunno.admin.catalogo.domain.category.CategoryId;
import br.com.brunno.admin.catalogo.domain.genre.GenreID;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import br.com.brunno.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import br.com.brunno.admin.catalogo.infrastructure.category.models.CreateCategoryAPIRequest;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.GenreResponse;
import br.com.brunno.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDSL {

    MockMvc mvc();

    ObjectMapper mapper();

    /*
     * CAST MEMBER
     */
    default CastMemberID givenACastMember(String name, CastMemberType type) {
        final var input = new CreateCastMemberRequest(name, type.name());
        final var castMemberId = this.given("/cast_members", input);
        return CastMemberID.from(castMemberId);
    }

    default CastMemberResponse retrieveCastMember(CastMemberID anId) {
        return this.retrieve("/cast_members/" + anId.getValue(), CastMemberResponse.class);
    }

    default void updateCastMember(CastMemberID castMemberId, String name, CastMemberType type) {
        final var updateCastMemberRequest = new UpdateCastMemberRequest(name, type.name());
        this.update("/cast_members/" + castMemberId.getValue(), updateCastMemberRequest);
    }

    default void deleteCastMember(CastMemberID id) {
        this.delete("/cast_members/" + id.getValue());
    }

    default ResultActions listCastMembers(String page) {
        return this.listCastMembers(page, "10");
    }

    default ResultActions listCastMembers(String page, String perPage) {
        return this.listCastMembers(page, perPage, "name", "asc", "");
    }

    default ResultActions listCastMembers(String page, String perPage, String sort, String direction, String terms) {
        return this.list("/cast_members", page, perPage, sort, direction, terms);
    }

    /*
     * GENRE
     */
    default GenreID givenAGenre(String name, List<String> categories, boolean isActive) {
        final var input = new CreateGenreRequest(name, categories, isActive);
        final var genreId = this.given("/genres", input);
        return GenreID.from(genreId);
    }

    default GenreResponse retrieveGenre(GenreID genreID) {
        return this.retrieve("/genres/" + genreID.getValue(), GenreResponse.class);
    }

    default void updateGenre(GenreID genreId, String name, List<String> categories, boolean isActive) {
        final var updateGenreRequest = new UpdateGenreRequest(name, categories, isActive);
        this.update("/genres/" + genreId.getValue(), updateGenreRequest);
    }

    default void deleteGenre(GenreID genreId) {
        this.delete("/genres/" + genreId.getValue());
    }

    default ResultActions listGenres(String page) {
        return this.listGenres(page, "10");
    }

    default ResultActions listGenres(String page, String perPage) {
        return this.listGenres(page, perPage, "name", "asc", "");
    }

    default ResultActions listGenres(String page, String perPage, String sort, String direction, String terms) {
        return this.list("/genres", page, perPage, sort, direction, terms);
    }

    /*
     * CATEGORY
     */
    default CategoryId givenAnCategory(String name, String description, boolean isActive) {
        final var requestBody = CreateCategoryAPIRequest.from(name, description, isActive);
        final var categoryId = this.given("/categories", requestBody);
        return CategoryId.from(categoryId);
    }

    /*
     * COMMONS
     */
    private String given(String url, Object body) {
        try {
            return this.mvc().perform(post(url)
                            .with(ApiTest.ADMIN_JWT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.mapper().writeValueAsString(body)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andReturn()
                    .getResponse()
                    .getHeader("Location")
                    .replace("%s/".formatted(url), "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T retrieve(String url, Class<T> clazz) {
        try {
            var content = this.mvc().perform(get(url)
                            .with(ApiTest.ADMIN_JWT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            return this.mapper().readValue(content, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void update(String url, Object requestBody) {
        try {
            this.mvc().perform(MockMvcRequestBuilders.put(url)
                            .with(ApiTest.ADMIN_JWT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.mapper().writeValueAsString(requestBody)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void delete(String url) {
        try {
            this.mvc().perform(MockMvcRequestBuilders.delete(url)
                            .with(ApiTest.ADMIN_JWT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }

    private ResultActions list(String url, String page, String perPage, String sort, String direction, String terms) {
        try {
            return this.mvc().perform(get(url)
                    .with(ApiTest.ADMIN_JWT)
                    .queryParam("page", page)
                    .queryParam("perPage", perPage)
                    .queryParam("sort", sort)
                    .queryParam("direction", direction)
                    .queryParam("search", terms));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> identifierToString(List<? extends Identifier> ids) {
        return ids.stream()
                .map(Identifier::getValue)
                .map(Object::toString)
                .toList();
    }
}
