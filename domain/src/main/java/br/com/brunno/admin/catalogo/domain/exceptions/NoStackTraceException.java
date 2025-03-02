package br.com.brunno.admin.catalogo.domain.exceptions;

public class NoStackTraceException extends RuntimeException {

    public NoStackTraceException(String message) {
        super(message, null, true, false);
    }

    public NoStackTraceException(String message, Throwable cause) {
      super(message, cause, true, false);
    }
}
