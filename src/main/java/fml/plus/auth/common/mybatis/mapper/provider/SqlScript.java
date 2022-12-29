package fml.plus.auth.common.mybatis.mapper.provider;

import fml.plus.auth.common.mybatis.mapper.Caching;
import fml.plus.auth.common.mybatis.mapper.EntityFactory;
import fml.plus.auth.common.mybatis.mapper.EntityTable;
import fml.plus.auth.common.mybatis.mapper.utils.Assert;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.function.Supplier;

/**
 * 对 xml 形式 sql 简单封装，便于使用
 */
public interface SqlScript {

    /**
     * 换行符
     */
    String LF = "\n";

    /**
     * 生成对应的 SQL，支持动态标签
     *
     * @param entity 实体类信息
     * @return xml sql 脚本
     */
    String getSql(EntityTable entity);


    /**
     * 创建SQL并缓存
     *
     * @param providerContext 执行方法上下文
     * @param sqlScript       xml sql 脚本实现
     * @return 缓存key
     */
    static String caching(ProviderContext providerContext, SqlScript sqlScript) {
        EntityTable entity = EntityFactory.getEntityTable(providerContext.getMapperType(), providerContext.getMapperMethod());
        return Caching.cache(providerContext, entity, () -> sqlScript.getSql(entity));
    }

    /**
     * 生成 INSERT 语句
     */
    static String INSERT(String tableName, LRSupplier column, LRSupplier values) {
        return String.format("INSERT INTO %s ( %s ) \n VALUES( %s )", tableName, column.get(), values.get());
    }

    /**
     * 生成 UPDATE 语句
     */
    static String UPDATE(String tableName, LRSupplier sets, LRSupplier where) {
        return String.format("UPDATE %s %s %s", tableName, set(sets::getWithLR), where(where::getWithLR));
    }

    /**
     * 生成 DELETE 语句
     */
    static String DELETE(String tableName, LRSupplier where) {
        return String.format("DELETE FROM %s %s", tableName, where(where::getWithLR));
    }

    /**
     * 生成 SELECT 语句
     */
    static String SELECT(String tableName, LRSupplier columns, LRSupplier where) {
        return String.format("SELECT %s \n FROM %s %s", columns.getWithLR(), tableName, where(where::getWithLR));
    }
    /**
     * 生成 SELECT PAGE 语句
     */
    static String SELECT_PAGE(String tableName, LRSupplier columns, LRSupplier where, LRSupplier orderBy) {
        return String.format("SELECT %s \n FROM %s %s %s %s", columns.getWithLR(), tableName, where(where::getWithLR), orderBy.getWithLR(), PAGE());
    }

    /**
     * 生成 COUNT 语句
     */
    static String COUNT(LRSupplier count) {
        return String.format("COUNT( %s )", count.get());
    }

    /**
     * 生成 分页 语句
     */
    static String PAGE() {
        return ifTest("rows != null", () -> " LIMIT #{rows.limit} OFFSET #{rows.offset}");
    }



    /**
     * 生成 script 标签包装的 xml 结构
     *
     * @param content 标签中的内容
     */
    static String script(LRSupplier content) {
        return String.format("<script>\n%s\n</script>", content.get());
    }

    /**
     * 生成 set 标签包装的 xml 结构
     *
     * @param content 标签中的内容
     * @return set 标签包装的 xml 结构
     */
    static String set(LRSupplier content) {
        return String.format("\n<set>%s\n</set> ", content.getWithLR());
    }


    /**
     * 生成 where 标签包装的 xml 结构
     *
     * @param content 标签中的内容
     * @return where 标签包装的 xml 结构
     */
    static String where(LRSupplier content) {
        return String.format("\n<where>%s\n</where> ", content.getWithLR());
    }

    /**
     * 生成 if 标签包装的 xml 结构
     *
     * @param test    if 的判断条件
     * @param content 标签中的内容
     * @return if 标签包装的 xml 结构
     */
    static String ifTest(String test, LRSupplier content) {
        return String.format("<if test=\"%s\">%s\n</if> ", test, content.getWithLR());
    }

    /**
     * 生成 &lt;if test="_parameter != null"&gt; 标签包装的 xml 结构，允许参数为空时使用，
     *
     * @param content 标签中的内容
     * @return &lt;if test="_parameter != null"&gt; 标签包装的 xml 结构
     */
    static String ifParameterNotNull(LRSupplier content) {
        return String.format("<if test=\"_parameter != null\">%s\n</if> ", content.getWithLR());
    }

    /**
     * 增加对参数的校验，参数不能为 null
     *
     * @param variable 参数
     * @param message  提示信息
     * @return 在代码基础上增加一段校验
     */
    static String variableNotNull(String variable, String message) {
        return String.format("\n${@%s.Assert@notNull(" + variable + ", '" + message + "')}\n", Assert.class.getPackage().getName());
    }

    /**
     * 生成 trim 标签包装的 xml 结构
     *
     * @param prefix          前缀
     * @param suffix          后缀
     * @param prefixOverrides 前缀替换内容
     * @param suffixOverrides 后缀替换内容
     * @param content         标签中的内容
     * @return trim 标签包装的 xml 结构
     */
    static String trim(String prefix, String suffix, String prefixOverrides, String suffixOverrides, LRSupplier content) {
        return String.format("\n<trim prefix=\"%s\" prefixOverrides=\"%s\" suffixOverrides=\"%s\" suffix=\"%s\">%s\n</trim> "
                , prefix, prefixOverrides, suffixOverrides, suffix, content.getWithLR());
    }

    /**
     * 生成 trim 标签包装的 xml 结构
     *
     * @param prefix          前缀
     * @param suffix          后缀
     * @param prefixOverrides 前缀替换内容
     * @param content         标签中的内容
     * @return trim 标签包装的 xml 结构
     */
    static String trimPrefixOverrides(String prefix, String suffix, String prefixOverrides, LRSupplier content) {
        return String.format("\n<trim prefix=\"%s\" prefixOverrides=\"%s\" suffix=\"%s\">%s\n</trim> ", prefix, prefixOverrides, suffix, content.getWithLR());
    }

    /**
     * 生成 trim 标签包装的 xml 结构
     *
     * @param prefix          前缀
     * @param suffix          后缀
     * @param suffixOverrides 后缀替换内容
     * @param content         标签中的内容
     * @return trim 标签包装的 xml 结构
     */
    static String trimSuffixOverrides(String prefix, String suffix, String suffixOverrides, LRSupplier content) {
        return String.format("\n<trim prefix=\"%s\" suffixOverrides=\"%s\" suffix=\"%s\">%s\n</trim> ", prefix, suffixOverrides, suffix, content.getWithLR());
    }

    /**
     * 保证所有字符串前面都有换行符
     */
    interface LRSupplier extends Supplier<String> {

        default String getWithLR() {
            String str = get();
            if (!str.isEmpty() && str.charAt(0) == LF.charAt(0)) {
                return str;
            }
            return LF + str;
        }

    }
}
