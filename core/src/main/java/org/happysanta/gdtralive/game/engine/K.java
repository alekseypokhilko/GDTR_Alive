package org.happysanta.gdtralive.game.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class K implements Serializable {

    public boolean m_doZ;
    public int m_aI;
    public int m_intI;
    public int m_forI;
    public int m_newI;
    public Element[] m_ifan;

    public K() {
        m_ifan = new Element[6];
        for (int i = 0; i < 6; i++)
            m_ifan[i] = new Element();

        _avV();
    }

    public void _avV() {
        m_aI = m_forI = m_newI = 0;
        m_doZ = true;
        for (int i = 0; i < 6; i++)
            m_ifan[i].init();

    }

    public K copy() {
        K element = new K();
        element.m_doZ = m_doZ;
        element.m_aI = m_aI;
        element.m_intI = m_intI;
        element.m_forI = m_forI;
        element.m_newI = m_newI;
        element.m_ifan = new Element[6];
        List<Element> list = new ArrayList<>();
        for (Element element1 : m_ifan) {
            Element copy = element1.copy();
            list.add(copy);
        }
        list.toArray(element.m_ifan);
        return element;
    }
}
