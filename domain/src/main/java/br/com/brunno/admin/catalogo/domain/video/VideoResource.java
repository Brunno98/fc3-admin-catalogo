package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.ValueObject;
import br.com.brunno.admin.catalogo.domain.resource.Resource;

import java.util.Objects;

public class VideoResource extends ValueObject {
    private final Resource resource;
    private final VideoMediaType type;

    public VideoResource(Resource resource, VideoMediaType type) {
        this.resource = resource;
        this.type = type;
    }

    public static VideoResource with(Resource resource, VideoMediaType type) {
        return new VideoResource(resource, type);
    }

    public Resource resource() {
        return resource;
    }

    public VideoMediaType type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VideoResource that = (VideoResource) o;
        return type() == that.type();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type());
    }
}
