package com.barterAuctions.portal.config.customExceptions;

public class UnauthorizedAccessException extends Exception {
    static String defaultMessage = "Nie masz uprawnień do wykonania tej akcji.";

    public UnauthorizedAccessException(String message){
        super(message);
    }

    public UnauthorizedAccessException() {
        super(defaultMessage);
    }
}
