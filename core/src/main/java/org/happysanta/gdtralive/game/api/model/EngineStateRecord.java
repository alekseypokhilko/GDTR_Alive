package org.happysanta.gdtralive.game.api.model;

import java.io.Serializable;

public class EngineStateRecord implements Serializable {
    private static final int BACK_WHEEL_CIRCLE = 2;
    private static final int FRONT_WHEEL_CIRCLE = 1;
    private static final int ENGINE = 3;
    private static final int FENDER = 4;

    // === render
    public IElement[] e;
    //    public int aI; //m_Hak1m_aI
    public Integer L; //league
    //    public int q; //m_IZ
    public Integer I; //m_TI //Ускорение

    // === for replay
    public Integer X; //deltaX
    public Integer Y; //deltaY
    public Long t; //timer time
    public Integer p; //progress
    public String c; //controls w/a/s/d

    // === other
    public transient TrackData track;
    public transient boolean edit;
    public transient int selectedPointIndex;
    public transient int selectedLineIndex;
    public transient boolean perspectiveEnabled;
    public transient boolean shadowsEnabled;
    public transient boolean replay = false;
    public transient Element[] elements;
    public transient int ft; //flagTwitch
    public transient int[][] l; //leftWheelParams

    public IElement[] elements() {
        if (replay) {
            return e;
        } else {
            return elements;
        }
    }

    public IElement element0() {
        return elements()[0];
    }

    public IElement fender() {
        return elements()[FENDER];
    }

    public IElement engine() {
        return elements()[ENGINE];
    }

    public IElement frontWheel() {
        return elements()[FRONT_WHEEL_CIRCLE];
    }

    public IElement backWheel() {
        return elements()[BACK_WHEEL_CIRCLE];
    }

    public int getX() {
        return X == null ? 0 : X;
    }

    public int getY() {
        return Y == null ? 0 : Y;
    }

    public int getP() {
        return p == null ? 0 : p;
    }

    public Integer getI() {
        return I == null ? 0 : I;
    }

    public Integer league() {
        return L;
    }

    public void setLeague(Integer l) {
        L = l;
    }
}
