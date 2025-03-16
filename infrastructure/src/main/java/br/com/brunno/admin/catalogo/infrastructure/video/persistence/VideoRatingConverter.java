package br.com.brunno.admin.catalogo.infrastructure.video.persistence;

import br.com.brunno.admin.catalogo.domain.video.Rating;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter
public class VideoRatingConverter implements AttributeConverter<Rating, String> {

    @Override
    public String convertToDatabaseColumn(Rating rating) {
        if (Objects.isNull(rating)) return null;
        return rating.getName();
    }

    @Override
    public Rating convertToEntityAttribute(String s) {
        if (Objects.isNull(s)) return null;
        return Rating.of(s).orElse(null);
    }
}
