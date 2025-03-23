package br.com.brunno.admin.catalogo.infrastructure.api;

import br.com.brunno.admin.catalogo.domain.pagination.Pagination;
import br.com.brunno.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import br.com.brunno.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoListResponse;
import br.com.brunno.admin.catalogo.infrastructure.video.models.VideoResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RequestMapping("/videos")
@Tag(name = "Video")
public interface VideoAPI {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all videos paginated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Videos listed"),
            @ApiResponse(responseCode = "422", description = "A query param was invalid"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    Pagination<VideoListResponse> list(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "25") int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") String dir,
            @RequestParam(name = "cast_members_id", required = false, defaultValue = "") Set<String> castMembers,
            @RequestParam(name = "genres_id", required = false, defaultValue = "") Set<String> genres,
            @RequestParam(name = "categories_id", required = false, defaultValue = "") Set<String> categories
    );



    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, // é esse consume que vai diferenciar as chamadas POST com e sem media, já que elas tem o mesmo path e verbo
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Creates a new video with medias")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Video created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> createFull(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "year_launched", required = false) Integer launchedYear,
            @RequestParam(value = "duration", required = false) Double duration,
            @RequestParam(value = "opened", required = false) Boolean isOpened,
            @RequestParam(value = "published", required = false) Boolean isPublished,
            @RequestParam(value = "rating", required = false) String rating,
            @RequestParam(value = "cast_member_id", required = false) Set<String> castMembers,
            @RequestParam(value = "categories_id", required = false) Set<String> categories,
            @RequestParam(value = "genres_id", required = false) Set<String> genres,
            @RequestParam(value = "video_file", required = false) MultipartFile videoFile,
            @RequestParam(value = "trailer_file", required = false) MultipartFile trailerFile,
            @RequestParam(value = "banner_file", required = false) MultipartFile bannerFile,
            @RequestParam(value = "thumbnail_file", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "thumbnail_half_file", required = false) MultipartFile thumbnailHalfFile
    );


    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Creates a new video without medias")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Video created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> createFull(@RequestBody CreateVideoRequest createVideoRequest);


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a video by it's identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    VideoResponse getById(@PathVariable String id);


    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update a video by it's identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video updated successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateVideoRequest updateVideoRequest);


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a video by it's identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Video deleted"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(name = "id") String id);


    @GetMapping("/{id}/medias/{type}")
    @Operation(summary = "Get a video media by it's type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Media Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Media was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<byte[]> getMediaByType(
            @PathVariable(name = "id") String videoId,
            @PathVariable(name = "type") String mediaType
    );

    @PostMapping(
            value = "/{id}/medias/{type}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Upload a video media by it's type")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Media created successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown")
    })
    ResponseEntity<?> uploadMediaByType(
            @PathVariable(name = "id") String videoId,
            @PathVariable(name = "type") String mediaType,
            @RequestParam(name = "media_file") MultipartFile media
    );
}
