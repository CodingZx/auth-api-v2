package fml.plus.auth.common.mybatis.mapper.fn;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参考通用 Mapper weekend 实现，用于获取方法引用对应的字段信息
 */
class Reflections {
    private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
    private static final Pattern IS_PATTERN                 = Pattern.compile("^is[A-Z].*");
    private static final Pattern INSTANTIATED_CLASS_PATTERN = Pattern.compile("\\(L(?<cls>.+);\\).+");

    private Reflections() {
    }

    public static ClassField fnToFieldName(Fn<?,?> fn) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(fn);
            String getter = serializedLambda.getImplMethodName();
            if (GET_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(3);
            } else if (IS_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(2);
            }
            String field = Introspector.decapitalize(getter);
            //主要是这里  serializedLambda.getInstantiatedMethodType()
            Matcher matcher = INSTANTIATED_CLASS_PATTERN.matcher(serializedLambda.getInstantiatedMethodType());
            String implClass;
            if (matcher.find()) {
                implClass = matcher.group("cls").replaceAll("/", "\\.");
            } else {
                implClass = serializedLambda.getImplClass().replaceAll("/", "\\.");
            }
            Class<?> clazz = Class.forName(implClass);
            return new ClassField(clazz, field);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ClassField {
        private final Class<?> clazz;
        private final String   field;

        public ClassField(Class<?> clazz, String field) {
            this.clazz = clazz;
            this.field = field;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getField() {
            return field;
        }
    }
}