package br.com.jospin.services.library.cor.connectivity.sql.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Credential implements Serializable {
    private String name;
    private String drive;
    private String url;
    private String username;
    private String password;
}
