package fml.plus.auth.common.mybatis.mapper.provider;

import org.apache.ibatis.builder.annotation.ProviderContext;

import static fml.plus.auth.common.mybatis.mapper.provider.SqlScript.*;

/**
 * 基础的增删改查操作
 */
public class ExampleProvider {
    // @formatter:off

    private static final String EXAMPLE_SET_CLAUSE_INNER_WHEN =
            """
                <foreach collection="example.setValues" item="setValue">
                    <choose>
                      <when test="setValue.noValue">
                        ${setValue.condition},
                      </when>
                      <when test="setValue.singleValue">
                        ${setValue.condition} = #{setValue.value},
                      </when>
                    </choose>
                </foreach>
            """;

    private static final String EXAMPLE_WHERE_CLAUSE_INNER_WHEN =
            """
                <when test="criterion.noValue">
                  AND ${criterion.condition}
                </when>
                <when test="criterion.singleValue">
                  AND ${criterion.condition} #{criterion.value}
                </when>
                <when test="criterion.betweenValue">
                  AND ${criterion.condition} #{criterion.value} AND
                  #{criterion.secondValue}
                </when>
                <when test="criterion.listValue">
                  AND ${criterion.condition}
                  <foreach close=")" collection="criterion.value" item="listItem"
                    open="(" separator=",">
                    #{listItem}
                  </foreach>
                </when>
            """;

    /**
     * example 结构的动态 SQL 查询条件，用于接口参数只有一个 Example 对象时
     */
    public static final String EXAMPLE_WHERE_CLAUSE =
            "  <foreach collection=\"oredCriteria\" item=\"criteria\" separator=\" OR \">\n" +
            "    <if test=\"criteria.valid\">\n" +
            "      <trim prefix=\"(\" prefixOverrides=\"AND\" suffix=\")\">\n" +
            "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
            "          <choose>\n" +
            EXAMPLE_WHERE_CLAUSE_INNER_WHEN +
            "            <when test=\"criterion.orValue\">\n" +
            "              <foreach collection=\"criterion.value\" item=\"orCriteria\" separator=\" OR \" open = \" AND (\" close = \")\">\n" +
            "                <if test=\"orCriteria.valid\">\n" +
            "                  <trim prefix=\"(\" prefixOverrides=\"AND\" suffix=\")\">\n" +
            "                    <foreach collection=\"orCriteria.criteria\" item=\"criterion\">\n" +
            "                      <choose>\n" +
            EXAMPLE_WHERE_CLAUSE_INNER_WHEN +
            "                      </choose>\n" +
            "                    </foreach>\n" +
            "                  </trim>\n" +
            "                </if>\n" +
            "              </foreach>\n" +
            "            </when>\n" +
            "          </choose>\n" +
            "        </foreach>\n" +
            "      </trim>\n" +
            "    </if>\n" +
            "  </foreach>\n";

    /**
     * example 结构的动态 SQL 查询条件，用于多个参数时，Example 对应 @Param("example")
     */
    public static final String UPDATE_BY_EXAMPLE_WHERE_CLAUSE =
            "  <foreach collection=\"example.oredCriteria\" item=\"criteria\"\n separator=\" OR \">\n" +
            "    <if test=\"criteria.valid\">\n" +
            "      <trim prefix=\"(\" prefixOverrides=\"AND\" suffix=\")\">\n" +
            "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
            "          <choose>\n" +
            EXAMPLE_WHERE_CLAUSE_INNER_WHEN +
            "            <when test=\"criterion.orValue\">\n" +
            "              <foreach collection=\"criterion.value\" item=\"orCriteria\" separator=\" OR \" open = \" AND (\" close = \")\">\n" +
            "                <if test=\"orCriteria.valid\">\n" +
            "                  <trim prefix=\"(\" prefixOverrides=\"AND\" suffix=\")\">\n" +
            "                    <foreach collection=\"orCriteria.criteria\" item=\"criterion\">\n" +
            "                      <choose>\n" +
            EXAMPLE_WHERE_CLAUSE_INNER_WHEN +
            "                      </choose>\n" +
            "                    </foreach>\n" +
            "                  </trim>\n" +
            "                </if>\n" +
            "              </foreach>\n" +
            "            </when>\n" +
            "          </choose>\n" +
            "        </foreach>\n" +
            "      </trim>\n" +
            "    </if>\n" +
            "  </foreach>\n";

    // @formatter:on


    /**
     * 根据 Example 条件批量查询，根据 Example 条件查询总数，查询结果的数量由方法定义
     */
    public static String findByExample(ProviderContext context) {
        return SqlScript.caching(context, entity ->  {
            LRSupplier selectColumns = () -> ifTest("distinct", () -> "distinct ")
                    + ifTest("selectColumns != null and selectColumns != ''", () -> "${selectColumns}")
                    + ifTest("selectColumns == null or selectColumns == ''", entity::columnAsPropertyList);

            LRSupplier wheres = () -> ifParameterNotNull(() -> EXAMPLE_WHERE_CLAUSE);

            LRSupplier orderBy = () -> ifTest("orderByClause != null", () -> " ORDER BY ${orderByClause}");

            return script(() -> SELECT_PAGE(entity.table(), selectColumns, wheres, orderBy));
        });
    }

    /**
     * 根据 Example 条件查询总数
     */
    public static String findCountByExample(ProviderContext context) {
        return SqlScript.caching(context, entity -> {

            LRSupplier selectColumns = () -> COUNT(() -> ifTest("distinct", () -> "distinct ") + "*");

            LRSupplier wheres = () -> ifParameterNotNull(() -> EXAMPLE_WHERE_CLAUSE);

            return script(() -> SELECT(entity.table(), selectColumns, wheres));
        });
    }


    /**
     * 根据 Example 条件批量更新实体信息
     */
    public static String updateByExampleSetValues(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            LRSupplier sets = () -> EXAMPLE_SET_CLAUSE_INNER_WHEN + variableNotNull("example", "Example cannot be null");

            LRSupplier wheres = () -> UPDATE_BY_EXAMPLE_WHERE_CLAUSE;

            return script(() -> UPDATE(entity.table(), sets, wheres));
        });
    }

    /**
     * 根据 Example 删除
     */
    public static String deleteByExample(ProviderContext context) {
        return SqlScript.caching(context, entity -> {
            LRSupplier wheres = () -> EXAMPLE_WHERE_CLAUSE;

            return script(() -> DELETE(entity.table(), wheres));
        });
    }
}