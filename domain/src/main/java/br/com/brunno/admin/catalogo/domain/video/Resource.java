package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.ValueObject;

import java.util.Objects;

public class Resource extends ValueObject {

    private final byte[] content;
    private final String contentType;
    private final String name;
    private final Type type;

    private Resource(byte[] content, String contentType, String name, Type type) {
        this.content = Objects.requireNonNull(content);
        this.contentType = Objects.requireNonNull(contentType);
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public static Resource with(byte[] content, String contentType, String name, Type type) {
        return new Resource(content, contentType, name, type);
    }

    public byte[] content() {
        return content;
    }

    public String contentType() {
        return contentType;
    }

    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }

    public enum Type {
        VIDEO,
        TRAILER,
        BANNER,
        THUMBNAIL,
        THUMBNAIL_HALF
    }
}
