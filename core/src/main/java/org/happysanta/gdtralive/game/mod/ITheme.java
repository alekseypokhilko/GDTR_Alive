package org.happysanta.gdtralive.game.mod;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ITheme {

    Map<String, String> getProps();

    void setProp(String key, Object prop);

    static void mergeProperties(Map<String, String> source, Map<String, Object> target) {
        for (Map.Entry<String, String> entry : source.entrySet()) {
            try {
                String key = entry.getKey();
                Object targetObj = target.get(key);
                String value = entry.getValue();
                if (targetObj instanceof Color) {
                    if (value != null && !"".equals(value)) {
                        target.put(key, mapToColor(value));
                    }
                    //todo
//                } else if (targetObj instanceof GdBitmap) {
//                    if (value != null && !"".equals(value)) {
//                        target.put(key, fromBase64(value));
//                    }
                } else if (targetObj instanceof Integer) {
                    if (value != null && !"".equals(value)) {
                        target.put(key, Integer.parseInt(value));
                    }
                } else if (targetObj instanceof Float) {
                    if (value != null && !"".equals(value)) {
                        target.put(key, Float.parseFloat(value));
                    }
                } else if (targetObj instanceof Boolean) {
                    if (value != null && !"".equals(value)) {
                        target.put(key, Boolean.valueOf(value));
                    }
                } else if (targetObj == null) {
                    target.put(key, mapToColor(value)); //todo
                } else {
                    target.put(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace(); //todo log
            }
        }
        System.out.println();
    }

    static LinkedHashMap<String, String> convertProperties(Map<String, Object> source) {
        LinkedHashMap<String, String> target = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            try {
                Object targetObj = source.get(entry.getKey());
                if (targetObj instanceof Color) {
                    Color color = (Color) entry.getValue();
                    target.put(entry.getKey(), String.format("%s,%s,%s", color.r, color.g, color.b));
                    //todo
//                } else if (targetObj instanceof GdBitmap) {
//                    target.put(entry.getKey(), toBase64(((GdBitmap) entry.getValue()).bitmap));
                } else if (targetObj instanceof Boolean) {
                    target.put(entry.getKey(), entry.getValue().toString());
                } else if (targetObj instanceof Integer) {
                    target.put(entry.getKey(), "" + entry.getValue().toString());
                } else if (targetObj instanceof Float) {
                    target.put(entry.getKey(), "" + entry.getValue().toString());
                } else {
                    //todo
                    target.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
                }
            } catch (Exception e) {
                e.printStackTrace(); //todo log
            }
        }
        return target;
    }

    static Color mapToColor(String value) {
        String r = value.substring(0, value.indexOf(","));
        String g = value.substring(value.indexOf(",") + 1, value.lastIndexOf(","));
        String b = value.substring(value.lastIndexOf(",") + 1);
        return new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
    }
}