package br.com.brunno.admin.catalogo.domain.video;

import br.com.brunno.admin.catalogo.domain.ValueObject;

import java.util.Objects;

public class AudioVideoMedia extends ValueObject {

    private final String checksum;
    private final String name;
    private final String rawLocation;
    private final String encodedLocation;
    private final MediaStatus status;

    private AudioVideoMedia(String checksum, String name, String rawLocation, String encodedLocation, MediaStatus status) {
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.rawLocation = Objects.requireNonNull(rawLocation);
        this.encodedLocation = Objects.requireNonNull(encodedLocation);
        this.status = Objects.requireNonNull(status);
    }

    public static AudioVideoMedia with(String checksum, String name, String rawLocation, String encodedLocation, MediaStatus status) {
        return new AudioVideoMedia(checksum, name, rawLocation, encodedLocation, status);
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
    public String getValue() {
        //TODO: Reavaliar metodo 'getValue' do valueObject. Talvez nem todos precisem desse metodo.
        return this.checksum;
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
