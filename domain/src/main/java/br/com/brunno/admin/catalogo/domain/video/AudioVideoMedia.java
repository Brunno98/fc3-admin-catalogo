package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.DomainId;
import br.com.brunno.admin.catalogo.domain.ValueObject;

import java.util.Objects;

public class AudioVideoMedia extends ValueObject {

    private final DomainId id;
    private final String checksum;
    private final String name;
    private final String rawLocation;
    private final String encodedLocation;
    private final MediaStatus status;

    private AudioVideoMedia(DomainId id, String checksum, String name, String rawLocation, String encodedLocation, MediaStatus status) {
        this.id = Objects.requireNonNull(id);
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.rawLocation = Objects.requireNonNull(rawLocation);
        this.encodedLocation = Objects.requireNonNull(encodedLocation);
        this.status = Objects.requireNonNull(status);
    }

    public static AudioVideoMedia with(String checksum, String name, String rawLocation) {
        return AudioVideoMedia.with(DomainId.generate(), checksum, name, rawLocation, "", MediaStatus.PENDING);
    }

    public static AudioVideoMedia with(
            DomainId id,
            String checksum,
            String name,
            String rawLocation,
            String encodedLocation,
            MediaStatus status
    ) {
        return new AudioVideoMedia(id, checksum, name, rawLocation, encodedLocation, status);
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

    public String rawLocation() {
        return rawLocation;
    }

    public String encodedLocation() {
        return encodedLocation;
    }

    public MediaStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AudioVideoMedia that = (AudioVideoMedia) o;
        return Objects.equals(checksum, that.checksum) && Objects.equals(rawLocation, that.rawLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, rawLocation);
    }
}
