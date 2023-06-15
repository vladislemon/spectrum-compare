package ru.ntzw.spectrum.service.properties;

import java.awt.*;

public interface PropertiesService {

    Object get(String name);

    String getString(String name);

    Integer getInteger(String name);

    Double getDouble(String name);

    Boolean getBoolean(String name);

    Color getColor(String name);

    Object get(String name, Object defaultValue);

    String getString(String name, String defaultValue);

    Integer getInteger(String name, Integer defaultValue);

    Double getDouble(String name, Double defaultValue);

    Boolean getBoolean(String name, Boolean defaultValue);

    Color getColor(String name, Color defaultValue);

    Object set(String name, Object value);

    boolean add(String name, Object value);

    Object remove(String name);
}
