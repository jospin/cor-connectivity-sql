package br.com.jospin.services.library.cor.connectivity.sql.exception;

import lombok.Getter;

@Getter
public class DatabaseUnavailableException extends RuntimeException{
    private final String message;

    public DatabaseUnavailableException(String name) {
        this.message = "Database " + name + " is unavailable. Try again later.";
    }

}