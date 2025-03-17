package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.util.Utils;

import java.math.BigInteger;
import java.util.Objects;

public class Color {
    public static final Integer RANDOM = 1;
    public static final Integer RANDOM_ONCE = 2;

    public int mode = 0;
    public Integer a;
    public Integer r;
    public Integer g;
    public Integer b;

    public transient Integer _a;
    public transient Integer _r;
    public transient Integer _g;
    public transient Integer _b;
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

    private Color(Integer mode) {
        this.mode = mode;
    }

    public Integer a() {
        if (RANDOM_ONCE == mode) {
            if (_a == null) {
                _a = Utils.getRandom(255);
            }
            return _a;
        }
        return RANDOM == mode ? Utils.getRandom(255) : a;
    }

    public Integer r() {
        if (RANDOM_ONCE == mode) {
            if (_r == null) {
                _r = Utils.getRandom(255);
            }
            return _r;
        }
        return RANDOM.equals(mode) ? Utils.getRandom(255) : r;
    }

    public Integer g() {
        if (RANDOM_ONCE == mode) {
            if (_g == null) {
                _g = Utils.getRandom(255);
            }
            return _g;
        }
        return RANDOM.equals(mode) ? Utils.getRandom(255) : g;
    }

    public Integer b() {
        if (RANDOM_ONCE == mode) {
            if (_b == null) {
                _b = Utils.getRandom(255);
            }
            return _b;
        }
        return RANDOM.equals(mode) ? Utils.getRandom(255) : b;
    }

    public static Color of(String argb) {
        int alpha = Integer.parseInt(argb.substring(2, 4), 16);
        int r = Integer.parseInt(argb.substring(4, 6), 16);
        int g = Integer.parseInt(argb.substring(6, 8), 16);
        int b = Integer.parseInt(argb.substring(8, 10), 16);
        return new Color(alpha, r, g, b);
    }

    public static Color random() {
        return new Color(RANDOM);
    }

    public static Color randomOnce() {
        return new Color(RANDOM_ONCE);
    }

    public int intValue() {
        if (RANDOM.equals(mode)) {
            return calculateIntValue();
        }
        if (_intValue == null) {
            _intValue = calculateIntValue();
        }
        return _intValue;
    }

    private int calculateIntValue() {
        return new BigInteger(String.format("%02x", a()) + String.format("%02x", r()) + String.format("%02x", g()) + String.format("%02x", b()), 16).intValue();
    }

    public static Color random2() {
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
    public String toString() {
        return "Color{" +
                "mode=" + mode +
                ", a=" + a +
                ", r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color)) return false;

        Color color = (Color) o;
        return Objects.equals(mode, color.mode)
                && Objects.equals(a, color.a)
                && Objects.equals(r, color.r)
                && Objects.equals(g, color.g)
                && Objects.equals(b, color.b);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(mode);
        result = 31 * result + Objects.hashCode(a);
        result = 31 * result + Objects.hashCode(r);
        result = 31 * result + Objects.hashCode(g);
        result = 31 * result + Objects.hashCode(b);
        return result;
    }
}
