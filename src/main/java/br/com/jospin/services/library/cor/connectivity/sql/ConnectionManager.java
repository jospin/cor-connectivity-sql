package br.com.jospin.services.library.cor.connectivity.sql;

import br.com.jospin.services.library.cor.connectivity.sql.domain.Credential;
import br.com.jospin.services.library.cor.connectivity.sql.domain.CredentialType;
import br.com.jospin.services.library.cor.connectivity.sql.domain.DynamicDatasource;
import br.com.jospin.services.library.cor.connectivity.sql.domain.PoolData;
import br.com.jospin.services.library.cor.connectivity.sql.exception.DatabaseUnavailableException;
import br.com.jospin.services.library.cor.connectivity.sql.exception.InvalidDataSourceNameException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Slf4j
@Component
@ConfigurationProperties
@EnableConfigurationProperties(ConnectionManager.class)
public class ConnectionManager {

    private Map<String, Boolean> connectionHealth = new HashMap<>();
    private Map<String, NamedParameterJdbcTemplate> namedTemplates = new HashMap<>();
    private Map<String, HikariConfig> datasourceConfig = new HashMap<>();

    /**
     * Initializes datasource and store jdbc templates for those who are healthy
     *
     * @param datasourceProperties - Configuration of datasource
     */

    public void loadTemplates(DynamicDatasource datasourceProperties) {
        Optional.ofNullable(datasourceProperties).ifPresent(dataSource -> {
            for (Credential credential : dataSource.getCredentials()) {
                boolean isUp = false;
                try {
                    HikariConfig hikariConfig = configureConnection(dataSource.getPooldata(), credential);
                    datasourceConfig.put(credential.getName(), hikariConfig);

                    HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

                    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(hikariDataSource);

                    namedTemplates.put(credential.getName(), namedTemplate);
                    isUp = true;
                } catch (Exception e) {
                    log.error("Error to establish connection with the datasource " + credential.getName(), e);
                    throw new DatabaseUnavailableException(credential.getName());
                }
                connectionHealth.put(credential.getName(), isUp);
            }
        });
    }

    public void refreshDataSource(final String datasourceName) {
        connectionHealth.put(datasourceName, false);
        if (!datasourceConfig.containsKey(datasourceName)) {
            throw new InvalidDataSourceNameException(datasourceName);
        }
        try {
            HikariDataSource hikariDataSource = new HikariDataSource(datasourceConfig.get(datasourceName));
            NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(hikariDataSource);

            namedTemplates.put(datasourceName, namedTemplate);

            connectionHealth.put(datasourceName, true);
        } catch (Exception e) {
            throw new DatabaseUnavailableException(datasourceName);
        }
    }

    /**
     * Configuring connections pool
     *
     * @param poolData   - configurations of pool
     * @param credential - configurations of database connection
     * @return a new HikariConfig object
     */
    private HikariConfig configureConnection(PoolData poolData, Credential credential) {
        HikariConfig hikariConfig = new HikariConfig();

        log.info("Configuration connection {}", credential.getName());
        Optional.ofNullable(credential.getDrive()).ifPresent(c -> {
            hikariConfig.setDriverClassName(credential.getDrive());

            hikariConfig.setConnectionTestQuery(Optional.ofNullable(CredentialType.getConnetionTypeByDriveName(credential.getDrive())).get().getDefaultQuery());

        });

        Optional.ofNullable(credential.getUrl()).ifPresent(c -> hikariConfig.setJdbcUrl(credential.getUrl()));
        Optional.ofNullable(credential.getUsername()).ifPresent(c -> hikariConfig.setUsername(credential.getUsername()));
        Optional.ofNullable(credential.getPassword()).ifPresent(c -> hikariConfig.setPassword(credential.getPassword()));
        Optional.ofNullable(credential.getName()).ifPresent(c -> hikariConfig.setPoolName(credential.getName()));
        Optional.ofNullable(poolData.getConnectionTimeout()).ifPresent(c -> hikariConfig.setConnectionTimeout(Long.parseLong(poolData.getConnectionTimeout().trim())));
        Optional.ofNullable(poolData.getMinimumIdle()).ifPresent(c -> hikariConfig.setMinimumIdle(Integer.parseInt(poolData.getMinimumIdle())));
        Optional.ofNullable(poolData.getMaximumPoolSize()).ifPresent(c -> hikariConfig.setMaximumPoolSize(Integer.parseInt(poolData.getMaximumPoolSize())));
        Optional.ofNullable(poolData.getIdleTimeout()).ifPresent(c -> hikariConfig.setIdleTimeout(Long.parseLong(poolData.getIdleTimeout().trim())));
        Optional.ofNullable(poolData.getMaxLifeTime()).ifPresent(c -> hikariConfig.setMaxLifetime(Long.parseLong(poolData.getMaxLifeTime().trim())));
        Optional.ofNullable(poolData.getAutoCommit()).ifPresent(c -> hikariConfig.setAutoCommit(Boolean.parseBoolean(poolData.getAutoCommit().trim())));
        Optional.ofNullable(poolData.getCachePrepStmts()).ifPresent(c -> hikariConfig.addDataSourceProperty("cachePrepStmts", Boolean.parseBoolean(poolData.getCachePrepStmts().trim())));
        Optional.ofNullable(poolData.getPrepStmtCacheSize()).ifPresent(c -> hikariConfig.addDataSourceProperty("prepStmtCacheSize", Integer.parseInt(poolData.getPrepStmtCacheSize().trim())));
        Optional.ofNullable(poolData.getPrepStmtCacheSqlLimit()).ifPresent(c -> hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", Integer.parseInt(poolData.getPrepStmtCacheSqlLimit().trim())));
        Optional.ofNullable(poolData.getUseServerPrepStmts()).ifPresent(c -> hikariConfig.addDataSourceProperty("useServerPrepStmts", Boolean.parseBoolean(poolData.getUseServerPrepStmts().trim())));
        return hikariConfig;
    }

}
