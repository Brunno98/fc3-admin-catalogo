package br.com.brunno.admin.catalogo.application.video.media.get;

public record MediaOutput(
        String name,
        String contentType,
        byte[] content
) {}
