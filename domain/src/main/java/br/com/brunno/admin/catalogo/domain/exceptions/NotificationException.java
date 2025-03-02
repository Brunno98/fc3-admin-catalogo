package br.com.brunno.admin.catalogo.domain.exceptions;

import br.com.brunno.admin.catalogo.domain.validation.handler.Notification;

public class NotificationException extends DomainException {

    public NotificationException(String aMessage, Notification aNotification) {
        super(aMessage, aNotification.getErrors());
    }

}
