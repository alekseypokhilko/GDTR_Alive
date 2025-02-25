package org.happysanta.gdtralive.game.api.model;

import java.io.Serializable;

public class Element implements IElement, Serializable {

    public int x;
    public int y;
    public int r; // rotation?
    public int p; //perspective?
    public int d; //m_dI
    public int o; //m_gotoI
    public int l; //m_nullI
    public int g; //m_longI
    public int a; //acceleration?

    public Element() {
        init();
    }

    public void init() {
        x = y = r = 0;
        p = d = o = 0;
        l = g = a = 0;
    }

    public Element copy() {
        Element element = new Element();
        element.x = x;
        element.y = y;
        element.r = r;
        element.p = p;
        element.d = d;
        element.o = o;
        element.l = l;
        element.g = g;
        element.a = a;
        return element;
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
