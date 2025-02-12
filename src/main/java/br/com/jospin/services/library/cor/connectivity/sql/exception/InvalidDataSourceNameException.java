package br.com.jospin.services.library.cor.connectivity.sql.exception;

import lombok.Getter;

@Getter
public class InvalidDataSourceNameException extends RuntimeException {

    private final String message;

    public InvalidDataSourceNameException(String message) {
        this.message = "Datasource " + message + " not found. Please check your datasources configuration";
    }

}
