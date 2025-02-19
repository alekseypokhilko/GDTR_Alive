package org.happysanta.gdtralive.game.recorder;

import org.happysanta.gdtralive.game.engine.Element;

import java.io.Serializable;

public class ElementRecord implements IElement, Serializable {

    public int x;
    public int y;
    public int r; // rotation?

    public ElementRecord() {
        init();
    }

    public void init() {
        x = y = r = 0;
    }

    public static ElementRecord from(Element e) {
        ElementRecord rec = new ElementRecord();
        rec.x = e.x;
        rec.y = e.y;
        rec.r = e.r;
        return rec;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int r() {
        return r;
    }
}
