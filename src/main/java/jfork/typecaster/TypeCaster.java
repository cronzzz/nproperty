/*
 * Decompiled with CFR 0_115.
 */
package jfork.typecaster;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import jfork.typecaster.exception.IllegalTypeException;

public class TypeCaster {
    private static final Class[] _allowedTypes = new Class[]{Integer.class, Integer.TYPE, Short.class, Short.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE, Long.class, Long.TYPE, Boolean.class, Boolean.TYPE, String.class, Character.class, Character.TYPE, Byte.class, Byte.TYPE, AtomicInteger.class, AtomicBoolean.class, AtomicLong.class, BigInteger.class, BigDecimal.class};

    public static void cast(Object object, Field field, String value) throws IllegalAccessException, IllegalTypeException {
        if (!TypeCaster.isCastable(field)) {
            throw new IllegalTypeException("Unsupported type [" + field.getType().getName() + "] for field [" + field.getName() + "]");
        }
        Class type = field.getType();
        boolean oldAccess = field.isAccessible();
        field.setAccessible(true);
        if (type.isEnum()) {
            field.set(object, Enum.valueOf(type, value));
        } else if (type == Integer.class || type == Integer.TYPE) {
            field.set(object, Integer.decode(value));
        } else if (type == Short.class || type == Short.TYPE) {
            field.set(object, Short.decode(value));
        } else if (type == Float.class || type == Float.TYPE) {
            field.set(object, Float.valueOf(Float.parseFloat(value)));
        } else if (type == Double.class || type == Double.TYPE) {
            field.set(object, Double.parseDouble(value));
        } else if (type == Long.class || type == Long.TYPE) {
            field.set(object, Long.decode(value));
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            field.set(object, Boolean.parseBoolean(value));
        } else if (type == String.class) {
            field.set(object, value);
        } else if (type == Character.class || type == Character.TYPE) {
            field.set(object, Character.valueOf(value.charAt(0)));
        } else if (type == Byte.class || type == Byte.TYPE) {
            field.set(object, Byte.valueOf(Byte.parseByte(value)));
        } else if (type == AtomicInteger.class) {
            field.set(object, new AtomicInteger(Integer.decode(value)));
        } else if (type == AtomicBoolean.class) {
            field.set(object, new AtomicBoolean(Boolean.parseBoolean(value)));
        } else if (type == AtomicLong.class) {
            field.set(object, new AtomicLong(Long.decode(value)));
        } else if (type == BigInteger.class) {
            field.set(object, new BigInteger(value));
        } else if (type == BigDecimal.class) {
            field.set(object, new BigDecimal(value));
        } else {
            field.setAccessible(oldAccess);
            throw new IllegalTypeException("Unsupported type [" + type.getName() + "] for field [" + field.getName() + "]");
        }
        field.setAccessible(oldAccess);
    }

    public static <T> T cast(Class<T> type, String value) throws IllegalAccessException, IllegalTypeException {
        if (!TypeCaster.isCastable(type)) {
            throw new IllegalTypeException("Unsupported type [" + type.getName() + "]");
        }
        if (type.isEnum()) {
            return Enum.valueOf(type, value);
        }
        if (type == Integer.class || type == Integer.TYPE) {
            return Integer.class.cast(Integer.decode(value));
        }
        if (type == Short.class || type == Short.TYPE) {
            return Short.class.cast(Short.decode(value));
        }
        if (type == Float.class || type == Float.TYPE) {
            return Float.class.cast(Float.valueOf(Float.parseFloat(value)));
        }
        if (type == Double.class || type == Double.TYPE) {
            return Double.class.cast(Double.parseDouble(value));
        }
        if (type == Long.class || type == Long.TYPE) {
            return Long.class.cast(Long.decode(value));
        }
        if (type == Boolean.class || type == Boolean.TYPE) {
            return Boolean.class.cast(Boolean.parseBoolean(value));
        }
        if (type == String.class) {
            return (T)value;
        }
        if (type == Character.class || type == Character.TYPE) {
            return (T)Character.valueOf(value.charAt(0));
        }
        if (type == Byte.class || type == Byte.TYPE) {
            return Byte.class.cast(Byte.decode(value));
        }
        if (type == AtomicInteger.class) {
            return (T)new AtomicInteger(Integer.decode(value));
        }
        if (type == AtomicBoolean.class) {
            return (T)new AtomicBoolean(Boolean.parseBoolean(value));
        }
        if (type == AtomicLong.class) {
            return (T)new AtomicLong(Long.decode(value));
        }
        if (type == BigInteger.class) {
            return (T)new BigInteger(value);
        }
        if (type == BigDecimal.class) {
            return (T)new BigDecimal(value);
        }
        throw new IllegalTypeException("Unsupported type [" + type.getName() + "]");
    }

    public static boolean isCastable(Class type) {
        if (type.isEnum()) {
            return true;
        }
        for (Class t : _allowedTypes) {
            if (t != type) continue;
            return true;
        }
        return false;
    }

    public static boolean isCastable(Object object) {
        return TypeCaster.isCastable(object.getClass());
    }

    public static boolean isCastable(Field field) {
        return TypeCaster.isCastable(field.getType());
    }
}

