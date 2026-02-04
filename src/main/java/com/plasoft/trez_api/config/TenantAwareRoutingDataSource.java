package com.plasoft.trez_api.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TenantAwareRoutingDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> dataSources = new HashMap<>();
    private final DataSourceProperties dataSourceProperties;

    public TenantAwareRoutingDataSource(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public void initialize() {
        // Inicializa com o DataSource padrão
        DataSource defaultDataSource = createDataSource(TenantContext.DEFAULT_TENANT);
        dataSources.put(TenantContext.DEFAULT_TENANT, defaultDataSource);
        
        super.setTargetDataSources(dataSources);
        super.setDefaultTargetDataSource(defaultDataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return Optional.ofNullable(TenantContext.getInstance().getCurrentTenant())
                .orElse(TenantContext.DEFAULT_TENANT);
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = (String) determineCurrentLookupKey();
        
        // Se o DataSource ainda não existe para esse tenant, cria dinamicamente
        if (!dataSources.containsKey(tenantId)) {
            synchronized (dataSources) {
                // Double-check locking
                if (!dataSources.containsKey(tenantId)) {
                    DataSource dataSource = createDataSource(tenantId);
                    dataSources.put(tenantId, dataSource);
                    super.setTargetDataSources(dataSources);
                    super.afterPropertiesSet();
                }
            }
        }
        
        return (DataSource) dataSources.get(tenantId);
    }

    private DataSource createDataSource(String databaseName) {
        String url = dataSourceProperties.getBaseUrl() + databaseName;
        
        return DataSourceBuilder.create()
                .url(url)
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .driverClassName(dataSourceProperties.getDriverClassName())
                .build();
    }
}