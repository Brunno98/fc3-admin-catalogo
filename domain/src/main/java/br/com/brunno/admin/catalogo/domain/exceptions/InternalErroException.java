package br.com.brunno.admin.catalogo.domain.exceptions;

public class InternalErroException extends NoStackTraceException {
    public InternalErroException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InternalErroException with(String message, Throwable cause) {
        return new InternalErroException(message, cause);
    }
}
