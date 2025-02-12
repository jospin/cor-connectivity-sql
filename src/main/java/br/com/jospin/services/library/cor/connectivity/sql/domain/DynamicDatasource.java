package br.com.jospin.services.library.cor.connectivity.sql.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix="application")
public class DynamicDatasource implements Serializable {

    private PoolData pooldata = new PoolData();
    private List<Credential> credentials = new ArrayList<>();
}
