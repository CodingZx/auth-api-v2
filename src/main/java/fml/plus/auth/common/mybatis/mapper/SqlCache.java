package fml.plus.auth.common.mybatis.mapper;

import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * sql缓存
 */
public class SqlCache {
    /**
     * 执行方法上下文
     */
    private final ProviderContext providerContext;
    /**
     * 实体类信息
     */
    private final EntityTable      entity;
    /**
     * sql 提供者
     */
    private final Supplier<String> sqlScriptSupplier;

    SqlCache(ProviderContext providerContext, EntityTable entity, Supplier<String> sqlScriptSupplier) {
        Objects.requireNonNull(providerContext);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(sqlScriptSupplier);
        this.providerContext = providerContext;
        this.entity = entity;
        this.sqlScriptSupplier = sqlScriptSupplier;
    }

    /**
     * 该方法延迟到最终生成 SqlSource 时才执行
     */
    public String getSqlScript() {
        return sqlScriptSupplier.get();
    }

    /**
     * @return 执行方法上下文
     */
    public ProviderContext getProviderContext() {
        return providerContext;
    }

    /**
     * @return 实体类信息
     */
    public EntityTable getEntity() {
        return entity;
    }

}
