package fml.plus.auth.common.mybatis.mapper.utils;

import java.util.Collection;

/**
 * 基本断言类，抛出断言异常信息
 */
public class Assert {

    /**
     * 断言是否为真
     *
     * @param expression 布尔值
     * @param message    异常消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new AssertException(message);
        }
    }

    /**
     * 断言是否为假
     *
     * @param expression 布尔值
     * @param message    异常消息
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new AssertException(message);
        }
    }

    /**
     * 断言对象是否为{@code null}
     *
     * @param object  被检查的对象
     * @param message 异常消息
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new AssertException(message);
        }
    }

    /**
     * 断言对象是否不为{@code null}
     *
     * @param object  被检查的对象
     * @param message 异常消息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定字符串是否为空
     *
     * @param text    字符串类型
     * @param message 异常消息
     */
    public static <T extends CharSequence> void isEmpty(T text, String message) {
        if (Utils.isNotEmpty(text)) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定字符串是否为空
     *
     * @param text    字符串类型
     * @param message 异常消息
     */
    public static <T extends CharSequence> void notEmpty(T text, String message) {
        if (Utils.isEmpty(text)) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符）
     *
     * @param text    字符串类型
     * @param message 异常消息
     */
    public static <T extends CharSequence> void isBlank(T text, String message) {
        if (Utils.isNotBlank(text)) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符）
     *
     * @param text    字符串类型
     * @param message 异常消息
     */
    public static <T extends CharSequence> void notBlank(T text, String message) {
        if (Utils.isBlank(text)) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定数组是否为空
     *
     * @param array   数组
     * @param message 异常消息
     */
    public static <T> void isEmpty(T[] array, String message) {
        if (Utils.isNotEmpty(array)) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定数组是否非空
     *
     * @param array   数组
     * @param message 异常消息
     */
    public static <T> void notEmpty(T[] array, String message) {
        if (Utils.isEmpty(array)) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定集合是否为空
     *
     * @param collection 集合
     * @param message    异常消息
     */
    public static <T> void isEmpty(Collection<T> collection, String message) {
        if (Utils.isNotEmpty(collection)) {
            throw new AssertException(message);
        }
    }

    /**
     * 检查给定集合是否非空
     *
     * @param collection 集合
     * @param message    异常消息
     */
    public static <T> void notEmpty(Collection<T> collection, String message) {
        if (Utils.isEmpty(collection)) {
            throw new AssertException(message);
        }
    }

}