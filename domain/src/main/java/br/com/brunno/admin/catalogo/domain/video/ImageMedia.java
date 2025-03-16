package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.ValueObject;

import java.util.Objects;

public class ImageMedia extends ValueObject {

    private final DomainId id;
    private final String checksum;
    private final String name;
    private final String location;

    private ImageMedia(DomainId id, String checksum, String name, String location) {
        this.id = Objects.requireNonNull(id);
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
    }

    public static ImageMedia with(String checksum, String name, String location) {
        return ImageMedia.with(DomainId.generate(), checksum, name, location);
    }

    public static ImageMedia with(DomainId id, String checksum, String name, String location) {
        return new ImageMedia(id, checksum, name, location);
    }

    public DomainId id() {
        return id;
    }

    public String checksum() {
        return checksum;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    // TODO: Reavaliar esse equals. Foi feito antes de incluir o campo 'id' na classe
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ImageMedia that = (ImageMedia) o;
        return Objects.equals(checksum, that.checksum) && Objects.equals(location, that.location);
    }

    // TODO: Reavaliar esse hashCode. Foi feito antes de incluir o campo 'id' na classe
    @Override
    public int hashCode() {
        return Objects.hash(checksum, location);
    }
}
