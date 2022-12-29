package fml.plus.auth.common.mybatis.mapper.provider;

import fml.plus.auth.common.mybatis.mapper.fn.Fn;
import fml.plus.auth.common.mybatis.mapper.mapper.ExampleMapper;
import fml.plus.auth.common.mybatis.mapper.utils.Assert;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.RowBounds;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 封装 Example 的查询条件，方便链式调用
 *
 * @param <T> 实体类类型
 */
public class ExampleWrapper<T> {
    private final ExampleMapper<T> exampleMapper;
    private final Example<T> example;
    private Example.Criteria<T> current;

    public ExampleWrapper(ExampleMapper<T> exampleMapper) {
        this.exampleMapper = exampleMapper;
        this.example = exampleMapper.example();
        this.current = example.createCriteria();
    }

    /**
     * or 一组条件
     *
     * @return 条件
     */
    public ExampleWrapper<T> or() {
        this.current = this.example.or();
        return this;
    }

    /**
     * 获取查询条件
     */
    public Example<T> example() {
        return example;
    }

    /**
     * 指定查询列
     *
     * @param fns 方法引用
     */
    @SafeVarargs
    public final ExampleWrapper<T> select(Fn<T, ?>... fns) {
        this.example.selectColumns(fns);
        return this;
    }


    /**
     * 通过方法引用方式设置排序字段
     *
     * @param fn    排序列的方法引用
     * @param order 排序方式
     * @return Example
     */
    public ExampleWrapper<T> orderBy(Fn<T, ?> fn, Example.Order order) {
        this.example.orderBy(fn, order);
        return this;
    }

    /**
     * 通过方法引用方式设置排序字段，升序排序
     *
     * @param fns 排序列的方法引用
     * @return Example
     */
    @SafeVarargs
    public final ExampleWrapper<T> orderByAsc(Fn<T, Object>... fns) {
        this.example.orderByAsc(fns);
        return this;
    }

    /**
     * 通过方法引用方式设置排序字段，降序排序
     *
     * @param fns 排序列的方法引用
     * @return Example
     */
    @SafeVarargs
    public final ExampleWrapper<T> orderByDesc(Fn<T, Object>... fns) {
        this.example.orderByDesc(fns);
        return this;
    }

    /**
     * 设置 distince
     */
    public ExampleWrapper<T> distinct() {
        this.example.setDistinct(true);
        return this;
    }

    /**
     * 设置 分页
     */
    public ExampleWrapper<T> page(int offset, int limit) {
        this.example.page(offset, limit);
        return this;
    }

    /**
     * 设置 分页
     */
    public ExampleWrapper<T> page(RowBounds rowBounds) {
        this.example.page(rowBounds.getOffset(), rowBounds.getLimit());
        return this;
    }

    /**
     * 设置更新字段和值
     *
     * @param useSet 表达式条件, true 使用，false 不使用
     * @param setSql "column = value"
     */
    public ExampleWrapper<T> set(boolean useSet, String setSql) {
        return useSet ? set(setSql) : this;
    }

    /**
     * 设置更新字段和值
     *
     * @param setSql "column = value"
     */
    public ExampleWrapper<T> set(String setSql) {
        this.example.set(setSql);
        return this;
    }

    /**
     * 设置更新字段和值
     *
     * @param useSet 表达式条件, true 使用，false 不使用
     * @param fn     字段
     * @param value  值
     */
    public ExampleWrapper<T> set(boolean useSet, Fn<T, ?> fn, Object value) {
        return useSet ? set(fn, value) : this;
    }

    /**
     * 设置更新字段和值
     *
     * @param fn    字段
     * @param value 值
     */
    public ExampleWrapper<T> set(Fn<T, ?> fn, Object value) {
        this.example.set(fn, value);
        return this;
    }

    /**
     * 设置增加的字段和值
     * @param fn    字段
     * @param value 值
     */
    public ExampleWrapper<T> increment(Fn<T, ?> fn, int value) {
        this.example.increment(fn, value);
        return this;
    }

    /**
     * 设置减小的字段和值
     * @param fn    字段
     * @param value 值
     */
    public ExampleWrapper<T> decrement(Fn<T, ?> fn, int value) {
        this.example.decrement(fn, value);
        return this;
    }

    /**
     * 指定字段为 null
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     */
    public ExampleWrapper<T> isNull(boolean useCondition, Fn<T, ?> fn) {
        return useCondition ? isNull(fn) : this;
    }

    /**
     * 指定字段为 null
     *
     * @param fn 字段对应的 get 方法引用
     */
    public ExampleWrapper<T> isNull(Fn<T, ?> fn) {
        this.current.addCriterion(fn.toColumn() + " IS NULL");
        return this;
    }

    /**
     * 指定字段不为 null
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     */
    public ExampleWrapper<T> isNotNull(boolean useCondition, Fn<T, ?> fn) {
        return useCondition ? isNotNull(fn) : this;
    }

    /**
     * 指定字段不为 null
     *
     * @param fn 字段对应的 get 方法引用
     */
    public ExampleWrapper<T> isNotNull(Fn<T, ?> fn) {
        this.current.addCriterion(fn.toColumn() + " IS NOT NULL");
        return this;
    }

    /**
     * 字段 = 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值
     */
    public ExampleWrapper<T> eq(boolean useCondition, Fn<T, ?> fn, Object value) {
        return useCondition ? eq(fn, value) : this;
    }

    /**
     * 字段 = 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值
     */
    public ExampleWrapper<T> eq(Fn<T, ?> fn, Object value) {
        this.current.addCriterion(fn.toColumn() + " =", value);
        return this;
    }

    /**
     * 字段 != 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值
     */
    public ExampleWrapper<T> ne(boolean useCondition, Fn<T, ?> fn, Object value) {
        return useCondition ? ne(fn, value) : this;
    }

    /**
     * 字段 != 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值
     */
    public ExampleWrapper<T> ne(Fn<T, ?> fn, Object value) {
        this.current.addCriterion(fn.toColumn() + " <>", value);
        return this;
    }

    /**
     * 字段 > 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值
     */
    public ExampleWrapper<T> gt(boolean useCondition, Fn<T, ?> fn, Object value) {
        return useCondition ? gt(fn, value) : this;
    }

    /**
     * 字段 > 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值
     */
    public ExampleWrapper<T> gt(Fn<T, ?> fn, Object value) {
        this.current.addCriterion(fn.toColumn() + " >", value);
        return this;
    }

    /**
     * 字段 >= 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值
     */
    public ExampleWrapper<T> ge(boolean useCondition, Fn<T, ?> fn, Object value) {
        return useCondition ? ge(fn, value) : this;
    }

    /**
     * 字段 >= 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值
     */
    public ExampleWrapper<T> ge(Fn<T, ?> fn, Object value) {
        this.current.addCriterion(fn.toColumn() + " >=", value);
        return this;
    }

    /**
     * 字段 < 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值
     */
    public ExampleWrapper<T> lt(boolean useCondition, Fn<T, ?> fn, Object value) {
        return useCondition ? lt(fn, value) : this;
    }

    /**
     * 字段 < 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值
     */
    public ExampleWrapper<T> lt(Fn<T, ?> fn, Object value) {
        this.current.addCriterion(fn.toColumn() + " <", value);
        return this;
    }

    /**
     * 字段 <= 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值
     */
    public ExampleWrapper<T> le(boolean useCondition, Fn<T, ?> fn, Object value) {
        return useCondition ? le(fn, value) : this;
    }

    /**
     * 字段 <= 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值
     */
    public ExampleWrapper<T> le(Fn<T, ?> fn, Object value) {
        this.current.addCriterion(fn.toColumn() + " <=", value);
        return this;
    }

    /**
     * 字段 in (值集合)
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param values       值集合
     */
    public ExampleWrapper<T> in(boolean useCondition, Fn<T, ?> fn, Iterable<?> values) {
        return useCondition ? in(fn, values) : this;
    }

    /**
     * 字段 in (值集合)
     *
     * @param fn     字段对应的 get 方法引用
     * @param values 值集合
     */
    public ExampleWrapper<T> in(Fn<T, ?> fn, Iterable<?> values) {
        this.current.addCriterion(fn.toColumn() + " IN", values);
        return this;
    }

    /**
     * 字段 not in (值集合)
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param values       值集合
     */
    public ExampleWrapper<T> notIn(boolean useCondition, Fn<T, ?> fn, Iterable<?> values) {
        return useCondition ? notIn(fn, values) : this;
    }

    /**
     * 字段 not in (值集合)
     *
     * @param fn     字段对应的 get 方法引用
     * @param values 值集合
     */
    public ExampleWrapper<T> notIn(Fn<T, ?> fn, Iterable<?> values) {
        this.current.addCriterion(fn.toColumn() + " NOT IN", values);
        return this;
    }

    /**
     * 字段 between value1 and value 2
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value1       值1
     * @param value2       值2
     */
    public ExampleWrapper<T> between(boolean useCondition, Fn<T, ?> fn, Object value1, Object value2) {
        return useCondition ? between(fn, value1, value2) : this;
    }

    /**
     * 字段 between value1 and value 2
     *
     * @param fn     字段对应的 get 方法引用
     * @param value1 值1
     * @param value2 值2
     */
    public ExampleWrapper<T> between(Fn<T, ?> fn, Object value1, Object value2) {
        this.current.addCriterion(fn.toColumn() + " BETWEEN", value1, value2);
        return this;
    }

    /**
     * 字段 not between value1 and value 2
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value1       值1
     * @param value2       值2
     */
    public ExampleWrapper<T> notBetween(boolean useCondition, Fn<T, ?> fn, Object value1, Object value2) {
        return useCondition ? notBetween(fn, value1, value2) : this;
    }

    /**
     * 字段 not between value1 and value 2
     *
     * @param fn     字段对应的 get 方法引用
     * @param value1 值1
     * @param value2 值2
     */
    public ExampleWrapper<T> notBetween(Fn<T, ?> fn, Object value1, Object value2) {
        this.current.addCriterion(fn.toColumn() + " NOT BETWEEN", value1, value2);
        return this;
    }

    /**
     * 字段 like %值%
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值，两侧自动添加 %
     */
    public ExampleWrapper<T> contains(boolean useCondition, Fn<T, ?> fn, String value) {
        return useCondition ? contains(fn, value) : this;
    }

    /**
     * 字段 like %值%
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值，两侧自动添加 %
     */
    public ExampleWrapper<T> contains(Fn<T, ?> fn, String value) {
        this.current.addCriterion(fn.toColumn() + "  LIKE", "%" + value + "%");
        return this;
    }

    /**
     * 字段 like 值%，匹配 value 为前缀的值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值，右侧自动添加 %
     */
    public ExampleWrapper<T> startsWith(boolean useCondition, Fn<T, ?> fn, String value) {
        return useCondition ? startsWith(fn, value) : this;
    }

    /**
     * 字段 like 值%，匹配 value 为前缀的值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值，右侧自动添加 %
     */
    public ExampleWrapper<T> startsWith(Fn<T, ?> fn, String value) {
        this.current.addCriterion(fn.toColumn() + "  LIKE", value + "%");
        return this;
    }

    /**
     * 字段 like %值，匹配 value 为后缀的值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值，左侧自动添加 %
     */
    public ExampleWrapper<T> endsWith(boolean useCondition, Fn<T, ?> fn, String value) {
        return useCondition ? endsWith(fn, value) : this;
    }

    /**
     * 字段 like %值，匹配 value 为后缀的值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值，左侧自动添加 %
     */
    public ExampleWrapper<T> endsWith(Fn<T, ?> fn, String value) {
        this.current.addCriterion(fn.toColumn() + "  LIKE", "%" + value);
        return this;
    }

    /**
     * 字段 like 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值，需要指定 '%'(多个), '_'(单个) 模糊匹配
     */
    public ExampleWrapper<T> like(boolean useCondition, Fn<T, ?> fn, String value) {
        return useCondition ? like(fn, value) : this;
    }

    /**
     * 字段 like 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值，需要指定 '%'(多个), '_'(单个) 模糊匹配
     */
    public ExampleWrapper<T> like(Fn<T, ?> fn, String value) {
        this.current.addCriterion(fn.toColumn() + "  LIKE", value);
        return this;
    }

    /**
     * 字段 not like 值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param fn           字段对应的 get 方法引用
     * @param value        值，需要指定 % 模糊匹配
     */
    public ExampleWrapper<T> notLike(boolean useCondition, Fn<T, ?> fn, String value) {
        return useCondition ? notLike(fn, value) : this;
    }

    /**
     * 字段 not like 值
     *
     * @param fn    字段对应的 get 方法引用
     * @param value 值，需要指定 % 模糊匹配
     */
    public ExampleWrapper<T> notLike(Fn<T, ?> fn, String value) {
        this.current.addCriterion(fn.toColumn() + "  NOT LIKE", value);
        return this;
    }

    /**
     * 添加任意条件，条件一定是后端使用的，需要自己防止 SQL 注入
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param condition    任意条件，例如 "length(countryname)<5"
     */
    public ExampleWrapper<T> anyCondition(boolean useCondition, String condition) {
        return useCondition ? anyCondition(condition) : this;
    }

    /**
     * 添加任意条件，条件一定是后端使用的，需要自己防止 SQL 注入
     *
     * @param condition 任意条件，例如 "length(countryName)<5"
     */
    public ExampleWrapper<T> anyCondition(String condition) {
        this.current.andCondition(condition);
        return this;
    }

    /**
     * 手写左边条件，右边用value值
     *
     * @param useCondition 表达式条件, true 使用，false 不使用
     * @param condition    例如 "length(countryName)="
     * @param value        例如 5
     */
    public ExampleWrapper<T> anyCondition(boolean useCondition, String condition, Object value) {
        return useCondition ? anyCondition(condition, value) : this;
    }

    /**
     * 手写左边条件，右边用value值
     *
     * @param condition 例如 "length(countryName)="
     * @param value     例如 5
     */
    public ExampleWrapper<T> anyCondition(String condition, Object value) {
        this.current.andCondition(condition, value);
        return this;
    }

    /**
     * 嵌套 or 查询，数组多个条件直接使用 or，单个条件中为 and
     *
     * @param orParts 条件块
     */
    @SafeVarargs
    public final ExampleWrapper<T> or(Function<Example.OrCriteria<T>, Example.OrCriteria<T>>... orParts) {
        if (orParts != null && orParts.length > 0) {
            this.current.andOr(Arrays.stream(orParts).map(orPart -> orPart.apply(example.orPart())).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * 根据当前条件删除
     */
    public int delete() {
        return exampleMapper.delete(example);
    }

    /**
     * 将符合当前查询条件的数据更新为 {@link #set(String)} 和 {@link #set(Fn, Object)} 设置的值
     */
    public int update() {
        Assert.notEmpty(example.getSetValues(), "必须通过 set 方法设置更新的列和值");
        return exampleMapper.update(example);
    }

    /**
     * 根据当前查询条件查询
     */
    public List<T> list() {
        return exampleMapper.findList(example);
    }

    /**
     * 根据当前查询条件查询出一个结果，当存在多个符合条件的结果时会抛出异常 {@link TooManyResultsException}
     */
    public T one() {
        return exampleMapper.findOne(example);
    }

    /**
     * 根据当前查询条件查询出第一个结果
     */
    public T first() {
        example.page(0, 1);
        return exampleMapper.findList(example).stream().findFirst().orElse(null);
    }

    /**
     * 根据当前查询条件查询出前 n 个结果
     * @param n 结果数
     */
    public List<T> top(int n) {
        example.page(0, n);
        return exampleMapper.findList(example);
    }

    /**
     * 查询符合当前条件的结果数
     */
    public long count() {
        return exampleMapper.findCount(example);
    }
}