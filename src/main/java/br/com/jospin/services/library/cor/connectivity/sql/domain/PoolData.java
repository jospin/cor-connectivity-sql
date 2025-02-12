package br.com.jospin.services.library.cor.connectivity.sql.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PoolData implements Serializable {

    private String connectionTimeout;
    private String minimumIdle;
    private String maximumPoolSize;
    private String idleTimeout;
    private String maxLifeTime;
    private String autoCommit;

    private String cachePrepStmts;
    private String prepStmtCacheSize;
    private String prepStmtCacheSqlLimit;
    private String useServerPrepStmts;

}