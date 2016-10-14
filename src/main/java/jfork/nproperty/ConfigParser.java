/*
 * Decompiled with CFR 0_115.
 */
package jfork.nproperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jfork.nproperty.Cfg;
import jfork.nproperty.ConfigStoreFormatterIni;
import jfork.nproperty.ConfigStoreFormatterXml;
import jfork.nproperty.IConfigStoreFormatter;
import jfork.nproperty.IPropertyListener;
import jfork.typecaster.TypeCaster;
import jfork.typecaster.exception.IllegalTypeException;

public class ConfigParser {
    private static final Pattern parametersPattern = Pattern.compile("\\$\\{([^}]*)\\}");

    public static Properties parse(Object object, String path) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        return ConfigParser.parse(object, new File(path));
    }

    public static Properties parseXml(Object object, String path) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        return ConfigParser.parseXml(object, new File(path));
    }

    public static Properties parse(Object object, File file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Properties props;
        FileInputStream stream = new FileInputStream(file);
        Throwable throwable = null;
        try {
            props = ConfigParser.parse(object, stream, file.getPath());
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (stream != null) {
                if (throwable != null) {
                    try {
                        stream.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    stream.close();
                }
            }
        }
        return props;
    }

    public static Properties parseXml(Object object, File file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Properties props;
        FileInputStream stream = new FileInputStream(file);
        Throwable throwable = null;
        try {
            props = ConfigParser.parseXml(object, stream, file.getPath());
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (stream != null) {
                if (throwable != null) {
                    try {
                        stream.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    stream.close();
                }
            }
        }
        return props;
    }

    public static Properties parse(Object object, InputStream stream, String streamName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        Properties props = new Properties();
        props.load(stream);
        return ConfigParser.parse0(object, props, streamName);
    }

    public static Properties parseXml(Object object, InputStream stream, String streamName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        Properties props = new Properties();
        props.loadFromXML(stream);
        return ConfigParser.parse0(object, props, streamName);
    }

    public static Properties parse(Object object, Reader reader, String streamName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        Properties props = new Properties();
        props.load(reader);
        return ConfigParser.parse0(object, props, streamName);
    }

    private static Properties parse0(Object object, Properties props, String path) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        boolean classAnnotationPresent;
        Method[] methods;
        Field[] fields;
        boolean callEvents = object instanceof IPropertyListener;
        if (callEvents) {
            ((IPropertyListener)object).onStart(path);
        }
        boolean isClass = object instanceof Class;
        String prefix = null;
        boolean classAllowParameters = false;
        if (isClass) {
            classAnnotationPresent = ((Class)object).isAnnotationPresent(Cfg.class);
            if (classAnnotationPresent) {
                prefix = ((Cfg)((Class)object).getAnnotation(Cfg.class)).prefix();
                classAllowParameters = ((Cfg)((Class)object).getAnnotation(Cfg.class)).parametrize();
            }
        } else {
            classAnnotationPresent = object.getClass().isAnnotationPresent(Cfg.class);
            if (classAnnotationPresent) {
                prefix = ((Cfg)object.getClass().getAnnotation(Cfg.class)).prefix();
                classAllowParameters = ((Cfg)object.getClass().getAnnotation(Cfg.class)).parametrize();
            }
        }
        for (Field field : fields = object instanceof Class ? ((Class)object).getDeclaredFields() : object.getClass().getDeclaredFields()) {
            String name;
            boolean allowParameters = classAllowParameters;
            if (isClass && !Modifier.isStatic(field.getModifiers())) continue;
            if (field.isAnnotationPresent(Cfg.class)) {
                if (((Cfg)field.getAnnotation(Cfg.class)).ignore()) continue;
                name = ((Cfg)field.getAnnotation(Cfg.class)).value();
                if (!allowParameters) {
                    allowParameters = ((Cfg)field.getAnnotation(Cfg.class)).parametrize();
                }
                if (name.isEmpty()) {
                    name = field.getName();
                }
            } else {
                if (!classAnnotationPresent) continue;
                name = field.getName();
            }
            if (prefix != null && !prefix.isEmpty()) {
                name = prefix.concat(name);
            }
            boolean oldAccess = field.isAccessible();
            field.setAccessible(true);
            if (props.containsKey(name)) {
                String[] values;
                String propValue = props.getProperty(name);
                if (allowParameters) {
                    boolean exit;
                    boolean replacePlaceholders = false;
                    do {
                        Matcher parametersMatcher = parametersPattern.matcher(propValue);
                        exit = true;
                        while (parametersMatcher.find()) {
                            String parameterPropertyName = parametersMatcher.group(1);
                            if (!parameterPropertyName.isEmpty()) {
                                exit = false;
                                String parameterPropertyValue = props.containsKey(parameterPropertyName) ? props.getProperty(parameterPropertyName) : "";
                                propValue = propValue.replace(parametersMatcher.group(), parameterPropertyValue);
                                continue;
                            }
                            if (replacePlaceholders) continue;
                            replacePlaceholders = true;
                        }
                    } while (!exit);
                    if (replacePlaceholders) {
                        propValue = propValue.replace("${}", "$");
                    }
                }
                if (field.getType().isArray()) {
                    Class baseType = field.getType().getComponentType();
                    if (propValue != null) {
                        values = propValue.split(field.isAnnotationPresent(Cfg.class) ? ((Cfg)field.getAnnotation(Cfg.class)).splitter() : ";");
                        Object array = Array.newInstance(baseType, values.length);
                        field.set(object, array);
                        int index = 0;
                        for (String value : values) {
                            block47 : {
                                try {
                                    Array.set(array, index, TypeCaster.cast(baseType, value));
                                }
                                catch (NumberFormatException | IllegalTypeException e) {
                                    if (!callEvents) break block47;
                                    ((IPropertyListener)object).onInvalidPropertyCast(name, value);
                                }
                            }
                            ++index;
                        }
                        field.set(object, array);
                    }
                } else if (field.getType().isAssignableFrom(List.class)) {
                    if (field.get(object) == null) {
                        throw new NullPointerException("Cannot use null-object for parsing List splitter.");
                    }
                    Class genericType = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                    if (propValue != null) {
                        for (String value : values = propValue.split(field.isAnnotationPresent(Cfg.class) ? ((Cfg)field.getAnnotation(Cfg.class)).splitter() : ";")) {
                            try {
                                ((List)field.get(object)).add(TypeCaster.cast(genericType, value));
                                continue;
                            }
                            catch (NumberFormatException | IllegalTypeException e) {
                                if (!callEvents) continue;
                                ((IPropertyListener)object).onInvalidPropertyCast(name, value);
                            }
                        }
                    }
                } else if (propValue != null) {
                    if (TypeCaster.isCastable(field)) {
                        try {
                            TypeCaster.cast(object, field, propValue);
                        }
                        catch (NumberFormatException | IllegalTypeException e) {
                            if (callEvents) {
                                ((IPropertyListener)object).onInvalidPropertyCast(name, propValue);
                            }
                        }
                    } else {
                        Constructor construct = field.getType().getDeclaredConstructor(String.class);
                        boolean oldConstructAccess = construct.isAccessible();
                        construct.setAccessible(true);
                        field.set(object, construct.newInstance(propValue));
                        construct.setAccessible(oldConstructAccess);
                    }
                }
            } else if (object instanceof IPropertyListener) {
                ((IPropertyListener)object).onPropertyMiss(name);
            }
            field.setAccessible(oldAccess);
        }
        for (Method method : methods = object instanceof Class ? ((Class)object).getDeclaredMethods() : object.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Cfg.class)) continue;
            String propName = ((Cfg)method.getAnnotation(Cfg.class)).value();
            if (propName.isEmpty()) {
                propName = method.getName();
            }
            if (!props.containsKey(propName)) {
                if (!(object instanceof IPropertyListener)) continue;
                ((IPropertyListener)object).onPropertyMiss(propName);
                continue;
            }
            if (method.getParameterTypes().length != 1) continue;
            String propValue = props.getProperty(propName);
            boolean oldAccess = method.isAccessible();
            method.setAccessible(true);
            if (propValue != null) {
                try {
                    method.invoke(object, TypeCaster.cast(method.getParameterTypes()[0], propValue));
                }
                catch (NumberFormatException | InvocationTargetException | IllegalTypeException e) {
                    if (callEvents) {
                        ((IPropertyListener)object).onInvalidPropertyCast(propName, propValue);
                    }
                }
            } else {
                Object[] arrobject = new Object[1];
                arrobject[0] = method.getParameterTypes()[0] == String.class ? propValue : TypeCaster.cast(method.getParameterTypes()[0], propValue);
                method.invoke(object, arrobject);
            }
            method.setAccessible(oldAccess);
        }
        if (callEvents) {
            ((IPropertyListener)object).onDone(path);
        }
        return props;
    }

    public static void store(Object object, String path) throws IOException, IllegalAccessException {
        ConfigParser.store(object, new File(path));
    }

    public static void storeXml(Object object, String path) throws IOException, IllegalAccessException {
        ConfigParser.storeXml(object, new File(path));
    }

    public static void store(Object object, File file) throws IOException, IllegalAccessException {
        FileOutputStream stream = new FileOutputStream(file);
        Throwable throwable = null;
        try {
            ConfigParser.store(object, stream);
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (stream != null) {
                if (throwable != null) {
                    try {
                        stream.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    stream.close();
                }
            }
        }
    }

    public static void storeXml(Object object, File file) throws IOException, IllegalAccessException {
        FileOutputStream stream = new FileOutputStream(file);
        Throwable throwable = null;
        try {
            ConfigParser.storeXml(object, stream);
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (stream != null) {
                if (throwable != null) {
                    try {
                        stream.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    stream.close();
                }
            }
        }
    }

    public static void store(Object object, OutputStream stream) throws IOException, IllegalAccessException {
        ConfigStoreFormatterIni formatter = new ConfigStoreFormatterIni();
        ConfigParser.store0(object, formatter);
        stream.write(formatter.generate().getBytes());
    }

    public static void storeXml(Object object, OutputStream stream) throws IOException, IllegalAccessException {
        ConfigStoreFormatterXml formatter = new ConfigStoreFormatterXml();
        ConfigParser.store0(object, formatter);
        stream.write(formatter.generate().getBytes());
    }

    public static void store(Object object, Writer writer) throws IOException, IllegalAccessException {
        ConfigStoreFormatterIni formatter = new ConfigStoreFormatterIni();
        ConfigParser.store0(object, formatter);
        writer.write(formatter.generate());
    }

    public static void storeXml(Object object, Writer writer) throws IOException, IllegalAccessException {
        ConfigStoreFormatterXml formatter = new ConfigStoreFormatterXml();
        ConfigParser.store0(object, formatter);
        writer.write(formatter.generate());
    }

    private static void store0(Object object, IConfigStoreFormatter formatter) throws IOException, IllegalAccessException {
        Field[] fields;
        boolean classAnnotationPresent;
        boolean isClass = object instanceof Class;
        String prefix = null;
        if (isClass) {
            classAnnotationPresent = ((Class)object).isAnnotationPresent(Cfg.class);
            if (classAnnotationPresent) {
                prefix = ((Cfg)((Class)object).getAnnotation(Cfg.class)).prefix();
            }
        } else {
            classAnnotationPresent = object.getClass().isAnnotationPresent(Cfg.class);
            if (classAnnotationPresent) {
                prefix = ((Cfg)object.getClass().getAnnotation(Cfg.class)).prefix();
            }
        }
        for (Field field : fields = object instanceof Class ? ((Class)object).getDeclaredFields() : object.getClass().getDeclaredFields()) {
            String value;
            String name;
            StringBuilder builder;
            String splitter = ";";
            if (isClass && !Modifier.isStatic(field.getModifiers())) continue;
            if (field.isAnnotationPresent(Cfg.class)) {
                if (((Cfg)field.getAnnotation(Cfg.class)).ignore()) continue;
                name = ((Cfg)field.getAnnotation(Cfg.class)).value();
                splitter = ((Cfg)field.getAnnotation(Cfg.class)).splitter();
                if (name.isEmpty()) {
                    name = field.getName();
                }
            } else {
                if (!classAnnotationPresent) continue;
                name = field.getName();
            }
            if (prefix != null && !prefix.isEmpty()) {
                name = prefix.concat(name);
            }
            boolean oldAccess = field.isAccessible();
            field.setAccessible(true);
            Object fieldValue = field.get(object);
            if (fieldValue != null && field.getType().isArray()) {
                builder = new StringBuilder();
                int j = Array.getLength(fieldValue);
                for (int i = 0; i < j; ++i) {
                    builder.append(Array.get(fieldValue, i));
                    if (i >= j - 1) continue;
                    builder.append(splitter);
                }
                value = builder.toString();
            } else if (fieldValue != null && field.getType().isAssignableFrom(List.class)) {
                builder = new StringBuilder();
                boolean isFirst = true;
                for (Object val : (List)fieldValue) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        builder.append(splitter);
                    }
                    builder.append(val);
                }
                value = builder.toString();
            } else {
                value = String.valueOf(fieldValue);
            }
            formatter.addPair(name, value);
            field.setAccessible(oldAccess);
        }
    }
}

