package org.happysanta.gdtralive.game.api.model;

import java.math.BigInteger;
import java.util.Objects;

public class Color {
    public final int a;
    public final int r;
    public final int g;
    public final int b;
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
