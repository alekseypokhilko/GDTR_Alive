package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.util.Utils;

import java.math.BigInteger;
import java.util.Objects;

public class Color {
    public Boolean random;
    public Integer a;
    public Integer r;
    public Integer g;
    public Integer b;
    private transient Integer _intValue;

    public Color(int r, int g, int b) {
        this.a = 255;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(int a, int r, int g, int b) {
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(boolean random) {
        this.random = random;
    }

    public Integer a() {
        return Boolean.TRUE.equals(random) ? Utils.getRandom(255) : a;
    }

    public Integer r() {
        return Boolean.TRUE.equals(random) ? Utils.getRandom(255) : r;
    }

    public Integer g() {
        return Boolean.TRUE.equals(random) ? Utils.getRandom(255) : g;
    }

    public Integer b() {
        return Boolean.TRUE.equals(random) ? Utils.getRandom(255) : b;
    }

    public static Color of(String argb) {
        int alpha = Integer.parseInt(argb.substring(2, 4), 16);
        int r = Integer.parseInt(argb.substring(4, 6), 16);
        int g = Integer.parseInt(argb.substring(6, 8), 16);
        int b = Integer.parseInt(argb.substring(8, 10), 16);
        return new Color(alpha, r, g, b);
    }

    public int intValue() {
        if (_intValue == null) {
            _intValue = new BigInteger(String.format("%02x", a) + String.format("%02x", r) + String.format("%02x", g) + String.format("%02x", b), 16).intValue();
        }
        return _intValue;
    }

    public static Color random() {
        int mode = Utils.getRandom(80);
        switch (mode) {
            case 0:
                int random = Utils.getRandom(255);
                return new Color(
                        random,
                        random,
                        random
                );
            case 1:
                return new Color(
                        Utils.getRandom(255),
                        0,
                        0
                );
            case 2:
                return new Color(
                        0,
                        Utils.getRandom(255),
                        0
                );
            case 3:
                return new Color(
                        0,
                        0,
                        Utils.getRandom(255)
                );
            case 4:
                return new Color(
                        0,
                        Utils.getRandom(255),
                        Utils.getRandom(255)
                );
            case 5:
                return new Color(
                        Utils.getRandom(255),
                        0,
                        Utils.getRandom(255)
                );
            case 6:
                return new Color(
                        Utils.getRandom(255),
                        Utils.getRandom(255),
                        0
                );
            default:
                return new Color(
                        Utils.getRandom(255),
                        Utils.getRandom(255),
                        Utils.getRandom(255)
                );
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color)) return false;

        Color color = (Color) o;
        return r == color.r && g == color.g && b == color.b && Objects.equals(a, color.a);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(a);
        result = 31 * result + r;
        result = 31 * result + g;
        result = 31 * result + b;
        return result;
    }

    @Override
    public String toString() {
        return "Color{" +
                "a=" + a +
                ", r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }
}
