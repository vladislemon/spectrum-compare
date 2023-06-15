package ru.ntzw.spectrum.service.properties;

import ru.ntzw.spectrum.service.StorableService;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimplePropertiesService extends StorableService implements PropertiesService {

    private LinkedHashMap<String, Object> properties;

    public SimplePropertiesService(Path storagePath) {
        super(storagePath);
    }

    @Override
    public Object get(String name) {
        return get(name, null);
    }

    @Override
    public String getString(String name) {
        return getString(name, null);
    }

    @Override
    public Integer getInteger(String name) {
        return getInteger(name, null);
    }

    @Override
    public Double getDouble(String name) {
        return getDouble(name, null);
    }

    @Override
    public Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    @Override
    public Color getColor(String name) {
        return getColor(name, null);
    }

    @Override
    public Object get(String name, Object defaultValue) {
        if(properties.containsKey(name))
            return properties.get(name);
        add(name, defaultValue);
        return defaultValue;
    }

    @Override
    public String getString(String name, String defaultValue) {
        return (String) get(name, defaultValue);
    }

    @Override
    public Integer getInteger(String name, Integer defaultValue) {
        return (Integer) get(name, defaultValue);
    }

    @Override
    public Double getDouble(String name, Double defaultValue) {
        return (Double) get(name, defaultValue);
    }

    @Override
    public Boolean getBoolean(String name, Boolean defaultValue) {
        return (Boolean) get(name, defaultValue);
    }

    @Override
    public Color getColor(String name, Color defaultValue) {
        return (Color) get(name, defaultValue);
    }

    @Override
    public Object set(String name, Object value) {
        return properties.put(name, value);
    }

    @Override
    public boolean add(String name, Object value) {
        if(properties.containsKey(name))
            return false;
        set(name, value);
        return true;
    }

    @Override
    public Object remove(String name) {
        return properties.remove(name);
    }

    @Override
    public void init() throws Exception {
        properties = new LinkedHashMap<>();
        super.init();
    }

    @Override
    public void dispose() throws Exception {
        super.dispose();
    }

    @Override
    protected void load(BufferedReader reader) throws IOException {
        properties.clear();
        String line;
        while((line = reader.readLine()) != null) {
            String[] keyValuePair = line.split(KEY_SEPARATOR, 2);
            String[] typeValuePair = keyValuePair[1].split(TYPE_SEPARATOR, 2);
            properties.put(keyValuePair[0], stringToObject(typeValuePair[0], typeValuePair[1]));
        }
    }

    @Override
    protected void store(BufferedWriter writer) throws IOException {
        for(Map.Entry<String, Object> entry : properties.entrySet()) {
            writer.write(entry.getKey() + KEY_SEPARATOR + objectToString(entry.getValue()));
            writer.newLine();
        }
    }

    private Object stringToObject(String type, String value) {
        char prefix = type.charAt(0);
        switch(prefix) {
            case PREFIX_NULL:
                return null;
            case PREFIX_STRING:
                return value;
            case PREFIX_INTEGER:
                return Integer.parseInt(value);
            case PREFIX_DOUBLE:
                return Double.parseDouble(value);
            case PREFIX_BOOLEAN:
                return Boolean.parseBoolean(value);
            case PREFIX_COLOR:
                return stringToColor(value);

            default:
                throw new UnsupportedOperationException();
        }
    }

    private String objectToString(Object object) {
        char prefix = getPrefix(object);
        switch(prefix) {
            case PREFIX_NULL:
                return PREFIX_NULL + TYPE_SEPARATOR;
            case PREFIX_STRING:
                return PREFIX_STRING + TYPE_SEPARATOR + object.toString();
            case PREFIX_INTEGER:
                return PREFIX_INTEGER + TYPE_SEPARATOR + object.toString();
            case PREFIX_DOUBLE:
                return PREFIX_DOUBLE + TYPE_SEPARATOR + object.toString();
            case PREFIX_BOOLEAN:
                return PREFIX_BOOLEAN + TYPE_SEPARATOR + object.toString();
            case PREFIX_COLOR:
                return PREFIX_COLOR + TYPE_SEPARATOR + colorToString((Color) object);

            default:
                throw new UnsupportedOperationException();
        }
    }

    private Color stringToColor(String s) {
        try {
            return (Color) Color.class.getField(s).get(null);
        } catch(IllegalAccessException | NoSuchFieldException e) {
            String[] parts = s.split(RGB_SEPARATOR);
            try {
                return new Color(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim())
                );
            } catch(Exception e1) {
                throw new UnsupportedOperationException(e1);
            }
        }
    }

    private String colorToString(Color c) {
        for(Field field : Color.class.getFields()) {
            try {
                if(Modifier.isStatic(field.getModifiers()) && field.get(null).equals(c))
                    return field.getName();
            } catch(IllegalAccessException e) {
                //
            }
        }
        return c.getRed() + RGB_SEPARATOR + c.getGreen() + RGB_SEPARATOR + c.getBlue();
    }

    private char getPrefix(Object object) {
        if(object == null)
            return PREFIX_NULL;
        Class objClass = object.getClass();
        if(objClass == String.class)
            return PREFIX_STRING;
        if(objClass == Integer.class)
            return PREFIX_INTEGER;
        if(objClass == Double.class)
            return PREFIX_DOUBLE;
        if(objClass == Boolean.class)
            return PREFIX_BOOLEAN;
        if(objClass == Color.class)
            return PREFIX_COLOR;
        return PREFIX_OBJECT;
    }

    private static final String KEY_SEPARATOR = "=";
    private static final String TYPE_SEPARATOR = ":";
    private static final String RGB_SEPARATOR = ",";

    private static final char PREFIX_NULL = 'N';
    private static final char PREFIX_OBJECT = 'O';
    private static final char PREFIX_STRING = 'S';
    private static final char PREFIX_INTEGER = 'I';
    private static final char PREFIX_DOUBLE = 'D';
    private static final char PREFIX_BOOLEAN = 'B';
    private static final char PREFIX_COLOR = 'C';
}
