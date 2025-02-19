package org.happysanta.gdtralive.game.trainer;

import org.happysanta.gdtralive.game.engine.Element;
import org.happysanta.gdtralive.game.engine.K;

import java.io.Serializable;

public class FullEngineState implements Serializable {
    public Element[] elements;
    public Element[] NOT___spokes;
    public K[] m_Hak;

    public int flagTwitch;
    public int m_vcI_drawingFlag;
    public int m_YI;
    public int m_voidI;
    public int league;
    public boolean m_bZ;
    public int m_zI;
    public boolean drawBiker;
    public boolean drawBike;
    public boolean frontWheelTouchedGround;
    public int m_vaI;
    public int m_waI;
    public int m_xaI;
    public int acceleration;
    public int m_EI;
    public int m_CI;
    public boolean crashedInAir_MAYBE;
    public boolean m_mZ;
    public int m_TI_biker;
    public int m_kI;
    public boolean keysLocked;
    public boolean gas;
    public boolean stop;
    public boolean leansBack;
    public boolean leansForward;
    public boolean gasPressed;
    public boolean stopPressed;
    public boolean leansBackPressed;
    public boolean leansForwardPressed;
    public boolean m_RZ;
    public boolean lookAhead;
    public int deltaX;
    public int deltaY;
    public int delta;
}
