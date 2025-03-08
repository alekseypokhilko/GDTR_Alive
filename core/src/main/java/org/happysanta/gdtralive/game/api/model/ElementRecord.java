package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.util.Utils;

import java.io.Serializable;

/**
 * Represent position of player/bike part
 * Used for replay file
 */
public class ElementRecord implements IElement, Serializable {

    public Integer x;
    public Integer y;
    public Integer r; // rotation?

    public ElementRecord() {
        init();
    }

    public void init() {
        x = y = r = 0;
    }

    public static ElementRecord from(Element e) {
        ElementRecord rec = new ElementRecord();
        rec.x = e.x == 0 ? null : Utils.packInt(e.x);
        rec.y = e.y == 0 ? null : Utils.packInt(e.y);
        rec.r = e.r == 0 ? null : Utils.packInt(e.r);
        return rec;
    }

    @Override
    public int x() {
        return x == null ? 0 : Utils.unpackInt(x);
    }

    @Override
    public int y() {
        return y == null ? 0 : Utils.unpackInt(y);
    }

    @Override
    public int r() {
        return r == null ? 0 : Utils.unpackInt(r);
    }
}
