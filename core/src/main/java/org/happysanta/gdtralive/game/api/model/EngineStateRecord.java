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
    public int L; //league
    //    public int q; //m_IZ
    public int I; //m_TI

    // === for replay
    public int X; //deltaX
    public int Y; //deltaY
    public long t; //timer time
    public int p; //progress
    public String c; //controls w/a/s/d

    // === other
    public transient TrackData track;
    public transient boolean edit;
    public transient int selectedPointIndex;
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
}
