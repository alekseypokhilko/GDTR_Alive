package org.happysanta.gdtralive.game.engine;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.LevelState;
import org.happysanta.gdtralive.game.api.model.LeagueProperties;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.model.Element;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.api.model.ElementRecord;
import org.happysanta.gdtralive.game.api.model.EngineStateRecord;
import org.happysanta.gdtralive.game.api.model.FullEngineState;
import org.happysanta.gdtralive.game.util.FPMath;
import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    private static final int BACK_WHEEL_CIRCLE = 2;
    private static final int FRONT_WHEEL_CIRCLE = 1;
    private static final int FRONT_WHEEL = 1;
    private static final int BACK_WHEEL = 2;

    private boolean replay;
    public boolean edit;
    public int selectedPointIndex = 0;
    private EngineStateRecord replayState;
    private EngineStateRecord respawn = null;

    private final TrackPhysic trackPhysic;
    public Element[] elements;
    public Element[] NOT___spokes;
    public K[] m_Hak;

    public long timerTime = 0;
    public static int flagTwitch = 0;
    public static int m_vcI_drawingFlag = 0;
    public static int m_YI;
    public static int m_voidI;
    public int league = 0;
    public boolean m_bZ;
    public int m_zI;
    public boolean drawBiker;
    public boolean drawBike;
    public boolean frontWheelTouchedGround;
    private int m_vaI;
    private int m_waI;
    private int m_xaI;
    private int acceleration;
    private int m_EI;
    private int m_CI;
    public boolean crashedInAir_MAYBE;
    private boolean m_mZ;
    public int m_TI_biker;
    private int m_kI;
    private boolean keysLocked;
    private boolean gas;
    private boolean stop;
    private boolean leansBack;
    private boolean leansForward;
    private boolean gasPressed;
    private boolean stopPressed;
    private boolean leansBackPressed;
    private boolean leansForwardPressed;
    private boolean m_RZ;
    private boolean lookAhead;
    public int deltaX;
    public int deltaY;
    private int delta;
    public int[][] leftWheelParams;

    public Engine() {
        m_vaI = 0;
        m_waI = 1;
        m_xaI = -1;
        acceleration = 0;
        m_EI = 0;
        m_CI = 0;
        crashedInAir_MAYBE = false;
        m_mZ = false;
        m_TI_biker = 32768;
        m_kI = 0;
        keysLocked = false;
        m_bZ = false;
        elements = new Element[6];
        for (int j = 0; j < 6; j++)
            elements[j] = new Element();

        m_zI = 0;
        m_RZ = false;
        frontWheelTouchedGround = false;
        lookAhead = true;
        deltaX = 0;
        deltaY = 0;
        delta = 0xa0000;

        this.trackPhysic = new TrackPhysic();
    }

    public void init(GdSettings settings, TrackData track) throws InvalidTrackException {
        this.drawBiker = settings.isDriverSpriteEnabled();
        this.drawBike = settings.isBikeSpriteEnabled();
        setLookAhead(settings.isLookAheadEnabled());
        this.trackPhysic.setPerspectiveEnabled(settings.isPerspectiveEnabled());
        this.trackPhysic.setShadowsEnabled(settings.isShadowsEnabled());
        this.trackPhysic.load(track);
        resetToStart_MAYBE();
        keysLocked = false;
        updateState();
        crashedInAir_MAYBE = false;
        leftWheelParams = new int[5][4];
        setMode(1);
    }

    public static int _doIII(int j, int i1) {
        int j1 = j >= 0 ? j : -j;
        int k1;
        int l1;
        int i2;
        if ((k1 = i1 >= 0 ? i1 : -i1) >= j1) {
            l1 = k1;
            i2 = j1;
        } else {
            l1 = j1;
            i2 = k1;
        }
        return (int) (64448L * (long) l1 >> 16) + (int) (28224L * (long) i2 >> 16);
    }

    public int getDrawSpriteState() {
        if (drawBiker && drawBike)
            return 3; //no sprites
        if (drawBike)
            return 1;
        return !drawBiker ? 0 : 2;
    }

    public void setDrawSpriteState(int j) {
        drawBiker = false;
        drawBike = false;
        if ((j & 2) != 0)
            drawBiker = true;
        if ((j & 1) != 0)
            drawBike = true;
    }

    public void setMode(int j) {
        m_zI = j;
        switch (j) {
            case 1: // '\001'
            default:
                m_YI = 1310;
                break;
        }
        m_voidI = 0x190000;
        setLeague(1);
        resetToStart_MAYBE();
    }

    public void setLeague(int j) {
        league = j;
        resetToStart_MAYBE();
    }

    public void resetToStart_MAYBE() {
        setStartCoordinates_MAYBE(trackPhysic.getStartX(), trackPhysic.getStartY());
        acceleration = 0;
        m_kI = 0;
        crashedInAir_MAYBE = false;
        m_mZ = false;
        m_RZ = false;
        frontWheelTouchedGround = false;
        keysLocked = false;
        m_bZ = false;
        trackPhysic.track._aIIV(
                (m_Hak[2].m_ifan[5].x + 0x18000) - Constants.m_foraI[0],
                (m_Hak[FRONT_WHEEL].m_ifan[5].x - 0x18000) + Constants.m_foraI[0]
        );
    }

    public void setPerspectiveEnabled(boolean flag) {
        trackPhysic.setPerspectiveEnabled(flag);
        int j = (flag ? 0x10000 : 0xffff0000) << 1;
        for (int i1 = 0; i1 < 6; i1++) {
            for (int j1 = 0; j1 < 6; j1++)
                m_Hak[i1].m_ifan[j1].y += j;
        }
    }

    private void setStartCoordinates_MAYBE(int j, int i1) {
        if (m_Hak == null)
            m_Hak = new K[6];
        if (NOT___spokes == null)
            NOT___spokes = new Element[10];
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        for (int j1 = 0; j1 < 6; j1++) {
            int l2 = 0;
            switch (j1) {
                case 0: // '\0'
                    i2 = 1;
                    l1 = 0x58000;
                    j2 = 0;
                    k2 = 0;
                    break;

                case 4: // '\004'
                    i2 = 1;
                    l1 = 0x38000;
                    j2 = 0xfffe0000;
                    k2 = 0x30000;
                    break;

                case 3: // '\003'
                    i2 = 1;
                    l1 = 0x38000;
                    j2 = 0x20000;
                    k2 = 0x30000;
                    break;

                case 1: // '\001'
                    i2 = 0;
                    l1 = 0x18000;
                    j2 = 0x38000;
                    k2 = 0;
                    break;

                case 2: // '\002'
                    i2 = 0;
                    l1 = 0x58000;
                    j2 = 0xfffc8000;
                    k2 = 0;
                    l2 = 21626;
                    break;

                case 5: // '\005'
                    i2 = 2;
                    l1 = 0x48000;
                    j2 = 0;
                    k2 = 0x50000;
                    break;
            }
            if (m_Hak[j1] == null)
                m_Hak[j1] = new K();
            m_Hak[j1]._avV();
            m_Hak[j1].m_aI = Constants.m_foraI[i2];
            m_Hak[j1].m_intI = i2;
            m_Hak[j1].m_forI = (int) ((long) (int) (0x1000000000000L / (long) l1 >> 16) * (long) league().getM_yI() >> 16);
            m_Hak[j1].m_ifan[m_vaI].x = j + j2;
            m_Hak[j1].m_ifan[m_vaI].y = i1 + k2;
            m_Hak[j1].m_ifan[5].x = j + j2;
            m_Hak[j1].m_ifan[5].y = i1 + k2;
            m_Hak[j1].m_newI = l2;
        }

        for (int k1 = 0; k1 < 10; k1++) {
            if (NOT___spokes[k1] == null)
                NOT___spokes[k1] = new Element();
            NOT___spokes[k1].init();
            NOT___spokes[k1].x = league().getM_qI();
            NOT___spokes[k1].r = league().getM_xI();
        }

        NOT___spokes[0].y = 0x38000;
        NOT___spokes[1].y = 0x38000;
        NOT___spokes[2].y = 0x39b05;
        NOT___spokes[3].y = 0x39b05;
        NOT___spokes[4].y = 0x40000;
        NOT___spokes[5].y = 0x35aa6;
        NOT___spokes[6].y = 0x35aa6;
        NOT___spokes[7].y = 0x2d413;
        NOT___spokes[8].y = 0x2d413;
        NOT___spokes[9].y = 0x50000;
        NOT___spokes[5].r = (int) ((long) league().getM_xI() * 45875L >> 16);
        NOT___spokes[6].x = (int) (6553L * (long) league().getM_qI() >> 16);
        NOT___spokes[5].x = (int) (6553L * (long) league().getM_qI() >> 16);
        NOT___spokes[9].x = (int) (0x11999L * (long) league().getM_qI() >> 16);
        NOT___spokes[8].x = (int) (0x11999L * (long) league().getM_qI() >> 16);
        NOT___spokes[7].x = (int) (0x11999L * (long) league().getM_qI() >> 16);
    }

    public void moveTrack(int j, int i1) {
        trackPhysic.moveTrack(j, i1);
    }

    public void resetControls() {
        gasPressed = stopPressed = leansForwardPressed = leansBackPressed = false;
    }

    public void processKeyPressed(int gaz, int tilt) {
        if (!keysLocked) {
            gasPressed = stopPressed = leansForwardPressed = leansBackPressed = false;
            if (gaz > 0) {
                gasPressed = true;
            } else if (gaz < 0) {
                stopPressed = true;
            }
            if (tilt > 0) {
                leansForwardPressed = true;
            } else if (tilt < 0) {
                leansBackPressed = true;
            }
        }
    }

    public synchronized void startAutoplay() {
        resetToStart_MAYBE();
        keysLocked = true;
    }

    public synchronized void unlockKeys() {
        keysLocked = false;
    }

    public boolean isKeyLocked() {
        return keysLocked;
    }

    private void autoplay() {
        int j = m_Hak[FRONT_WHEEL].m_ifan[m_vaI].x - m_Hak[2].m_ifan[m_vaI].x;
        int i1 = m_Hak[FRONT_WHEEL].m_ifan[m_vaI].y - m_Hak[2].m_ifan[m_vaI].y;
        int j1 = _doIII(j, i1);
        i1 = (int) (((long) i1 << 32) / (long) j1 >> 16);
        stop = false;
        if (i1 < 0) {
            leansBack = true;
            leansForward = false;
        } else if (i1 > 0) {
            leansForward = true;
            leansBack = false;
        }
        boolean flag;
        if ((flag = (m_Hak[2].m_ifan[m_vaI].y - m_Hak[0].m_ifan[m_vaI].y <= 0 ? -1 : 1)
                * (m_Hak[2].m_ifan[m_vaI].p - m_Hak[0].m_ifan[m_vaI].p <= 0 ? -1 : 1) > 0)
                && leansForward || !flag && leansBack) {
            gas = true;
        } else {
            gas = false;
        }
    }

    private void _qvV() {
        if (!crashedInAir_MAYBE) { // не реагирует на клаву
            int j = m_Hak[FRONT_WHEEL].m_ifan[m_vaI].x - m_Hak[2].m_ifan[m_vaI].x;
            int i1 = m_Hak[FRONT_WHEEL].m_ifan[m_vaI].y - m_Hak[2].m_ifan[m_vaI].y;
            int j1 = _doIII(j, i1);
            j = (int) (((long) j << 32) / (long) j1 >> 16);
            i1 = (int) (((long) i1 << 32) / (long) j1 >> 16);
            if (gas && acceleration >= -league().getM_qI())
                acceleration -= league().getAcceleration_m_charI();
            if (stop) {
                acceleration = 0;
                m_Hak[FRONT_WHEEL].m_ifan[m_vaI].o = (int) ((long) m_Hak[FRONT_WHEEL].m_ifan[m_vaI].o * (long) (0x10000 - league().getM_abI()) >> 16);
                m_Hak[2].m_ifan[m_vaI].o = (int) ((long) m_Hak[2].m_ifan[m_vaI].o * (long) (0x10000 - league().getM_abI()) >> 16);
                if (m_Hak[FRONT_WHEEL].m_ifan[m_vaI].o < 6553)
                    m_Hak[FRONT_WHEEL].m_ifan[m_vaI].o = 0;
                if (m_Hak[2].m_ifan[m_vaI].o < 6553)
                    m_Hak[2].m_ifan[m_vaI].o = 0;
            }
            m_Hak[0].m_forI = (int) (11915L * (long) league().getM_yI() >> 16);
            m_Hak[0].m_forI = (int) (11915L * (long) league().getM_yI() >> 16);
            m_Hak[4].m_forI = (int) (18724L * (long) league().getM_yI() >> 16);
            m_Hak[3].m_forI = (int) (18724L * (long) league().getM_yI() >> 16);
            m_Hak[FRONT_WHEEL].m_forI = (int) (43690L * (long) league().getM_yI() >> 16);
            m_Hak[2].m_forI = (int) (11915L * (long) league().getM_yI() >> 16);
            m_Hak[5].m_forI = (int) (14563L * (long) league().getM_yI() >> 16);
            if (leansBack) {
                m_Hak[0].m_forI = (int) (18724L * (long) league().getM_yI() >> 16);
                m_Hak[4].m_forI = (int) (14563L * (long) league().getM_yI() >> 16);
                m_Hak[3].m_forI = (int) (18724L * (long) league().getM_yI() >> 16);
                m_Hak[FRONT_WHEEL].m_forI = (int) (43690L * (long) league().getM_yI() >> 16);
                m_Hak[2].m_forI = (int) (10082L * (long) league().getM_yI() >> 16);
            } else if (leansForward) {
                m_Hak[0].m_forI = (int) (18724L * (long) league().getM_yI() >> 16);
                m_Hak[4].m_forI = (int) (18724L * (long) league().getM_yI() >> 16);
                m_Hak[3].m_forI = (int) (14563L * (long) league().getM_yI() >> 16);
                m_Hak[FRONT_WHEEL].m_forI = (int) (26214L * (long) league().getM_yI() >> 16);
                m_Hak[2].m_forI = (int) (11915L * (long) league().getM_yI() >> 16);
            }
            if (leansBack || leansForward) {
                int k1 = -i1;
                int l1 = j;
                if (leansBack && m_kI > -league().getRotation1()) {
                    int i2 = 0x10000;
                    if (m_kI < 0)
                        i2 = (int) (((long) (league().getRotation1() - (m_kI >= 0 ? m_kI : -m_kI)) << 32) / (long) league().getRotation1() >> 16);
                    int k2 = (int) ((long) league().getRotationForce() * (long) i2 >> 16);
                    int i3 = (int) ((long) k1 * (long) k2 >> 16);
                    int k3 = (int) ((long) l1 * (long) k2 >> 16);
                    int i4 = (int) ((long) j * (long) k2 >> 16);
                    int k4 = (int) ((long) i1 * (long) k2 >> 16);
                    if (m_TI_biker > 32768)
                        m_TI_biker = Math.max(m_TI_biker - 1638, 0);
                    else
                        m_TI_biker = Math.max(m_TI_biker - 3276, 0);
                    m_Hak[4].m_ifan[m_vaI].p -= i3;
                    m_Hak[4].m_ifan[m_vaI].d -= k3;
                    m_Hak[3].m_ifan[m_vaI].p += i3;
                    m_Hak[3].m_ifan[m_vaI].d += k3;
                    m_Hak[5].m_ifan[m_vaI].p -= i4;
                    m_Hak[5].m_ifan[m_vaI].d -= k4;
                }
                if (leansForward && m_kI < league().getRotation1()) {
                    int j2 = 0x10000;
                    if (m_kI > 0)
                        j2 = (int) (((long) (league().getRotation1() - m_kI) << 32) / (long) league().getRotation1() >> 16);
                    int l2 = (int) ((long) league().getRotationForce() * (long) j2 >> 16);
                    int j3 = (int) ((long) k1 * (long) l2 >> 16);
                    int l3 = (int) ((long) l1 * (long) l2 >> 16);
                    int j4 = (int) ((long) j * (long) l2 >> 16);
                    int l4 = (int) ((long) i1 * (long) l2 >> 16);
                    if (m_TI_biker > 32768)
                        m_TI_biker = Math.min(m_TI_biker + 1638, 0x10000);
                    else
                        m_TI_biker = Math.min(m_TI_biker + 3276, 0x10000);
                    m_Hak[4].m_ifan[m_vaI].p += j3;
                    m_Hak[4].m_ifan[m_vaI].d += l3;
                    m_Hak[3].m_ifan[m_vaI].p -= j3;
                    m_Hak[3].m_ifan[m_vaI].d -= l3;
                    m_Hak[5].m_ifan[m_vaI].p += j4;
                    m_Hak[5].m_ifan[m_vaI].d += l4;
                }
                return;
            }
            if (m_TI_biker < 26214) {
                m_TI_biker += 3276;
                return;
            }
            if (m_TI_biker > 39321) {
                m_TI_biker -= 3276;
                return;
            }
            m_TI_biker = 32768;
        }
    }

    public void setReplayMode(boolean replay) {
        this.replay = replay;
    }

    public void setEditMode(boolean edit) {
        this.edit = edit;
    }

    public void setReplayState(EngineStateRecord replayState) {
        this.replayState = replayState;
    }

    public synchronized LevelState getLevelState() {
        if (replay) {
            return LevelState.REPLAY;
        }
        if (edit) {
            setStartCoordinates_MAYBE(trackPhysic.getStartX(), trackPhysic.getStartY());
            return LevelState.EDIT;
        }
        gas = gasPressed;
        stop = stopPressed;
        leansBack = leansBackPressed;
        leansForward = leansForwardPressed;
        if (keysLocked) {
            autoplay();
        }
        recalculateFlagPosition_MAYBE();
        _qvV();
        LevelState levelState;
        try {
            levelState = determineLevelState(m_YI);
        } catch (Exception e) {
            //if out of bounds
            levelState = LevelState.CRASHED;
        }
        if (levelState == LevelState.CRASHED || m_mZ) {
            return LevelState.CRASHED;
        } else if (crashedInAir_MAYBE) {
            return LevelState.CRASHED_IN_AIR;
        } else if (isStartNotCrossed()) {
            frontWheelTouchedGround = false;
            return LevelState.START_NOT_CROSSED;
        } else {
            return levelState;
        }
    }

    public int currentX_MAYBE() {
        if (replay && replayState != null) {
            //todo BUG when replay on other device?
            return (replayState.elements()[0].x() + replayState.X << 2) >> 16;
        }
        if (edit) {
            return (element0().x + deltaX << 2) >> 16;
        }
        if (lookAhead)
            deltaX = (int) (((long) element0().p << 32) / 0x180000L >> 16) + (int) ((long) deltaX * 57344L >> 16);
        else
            deltaX = 0;
        deltaX = Math.min(deltaX, delta);
        deltaX = Math.max(deltaX, -delta);
        return (element0().x + deltaX << 2) >> 16;
    }

    public int currentY_MAYBE() {
        if (replay && replayState != null) {
            return (replayState.elements()[0].y() + replayState.Y << 2) >> 16;
        }
        if (edit) {
            return (element0().y + deltaY << 2) >> 16;
        }
        if (lookAhead)
            deltaY = (int) (((long) element0().d << 32) / 0x180000L >> 16) + (int) ((long) deltaY * 57344L >> 16);
        else
            deltaY = 0;
        deltaY = Math.min(deltaY, delta);
        deltaY = Math.max(deltaY, -delta);
        return (element0().y + deltaY << 2) >> 16;
    }

    public static void recalculateFlagPosition_MAYBE() {
        if (flagTwitch > 0x38000)
            flagTwitch = 0;
        m_vcI_drawingFlag += 655;
        int j = 32768 + ((FPMath.sin(m_vcI_drawingFlag) >= 0 ? FPMath.sin(m_vcI_drawingFlag) : -FPMath.sin(m_vcI_drawingFlag)) >> 1);
        flagTwitch += (int) (6553L * (long) j >> 16);
    }

    public boolean isStartNotCrossed() {
        return m_Hak[FRONT_WHEEL].m_ifan[m_vaI].x < trackPhysic.getStartPosition_MAYBE();
    }

    public boolean isFinishCrossed() {
        if (trackPhysic.track.checkFinishCoordinates) {
            return (m_Hak[FRONT_WHEEL].m_ifan[m_waI].y > trackPhysic.getFinishYPosition() || m_Hak[BACK_WHEEL].m_ifan[m_waI].y > trackPhysic.getFinishYPosition())
                    && (m_Hak[FRONT_WHEEL].m_ifan[m_waI].x > trackPhysic.getFinishXPosition() || m_Hak[BACK_WHEEL].m_ifan[m_waI].x > trackPhysic.getFinishXPosition());
        }
        return m_Hak[FRONT_WHEEL].m_ifan[m_waI].x > trackPhysic.getFinishXPosition()
                || m_Hak[BACK_WHEEL].m_ifan[m_waI].x > trackPhysic.getFinishXPosition();
    }

    private LevelState determineLevelState(int j) {
        boolean flag = m_RZ;
        int i1 = 0;
        int j1 = j;
        int j2;
        if (trackPhysic.track.deadlineY != null && trackPhysic.track.deadlineY > currentY_MAYBE()) { //todo front wheel
            return LevelState.CRASHED;
        }
        do {
            if (i1 >= j)
                break;
            _aaIV(j1 - i1);
            int k1;
            if (!flag && isFinishCrossed())
                k1 = 3;
            else
                k1 = checkCollisionWithTrackLine(m_waI);
            if (!flag && m_RZ)
                return k1 == 3 ? LevelState.S1 : LevelState.FINISHED;
            if (k1 == 0) {
                if (((j1 = i1 + j1 >> 1) - i1 >= 0 ? j1 - i1 : -(j1 - i1)) < 65)
                    return LevelState.CRASHED;
            } else if (k1 == 3) {
                m_RZ = true;
                j1 = i1 + j1 >> 1;
            } else {
                int i2;
                if (k1 == 1) {
                    //Helpers.logDebug("TOUCHED GROUND");
                    do {
                        _caIV(m_waI);
                        j2 = checkCollisionWithTrackLine(m_waI);
                        i2 = j2;
                        if (j2 == 0)
                            return LevelState.CRASHED;
                    } while (i2 != 2);
                }
                i1 = j1;
                j1 = j;
                m_vaI = m_vaI != 1 ? 1 : 0;
                m_waI = m_waI != 1 ? 1 : 0;
            }
        } while (true);
        int l1;
        if ((l1 = (int) ((long) (m_Hak[FRONT_WHEEL].m_ifan[m_vaI].x - m_Hak[BACK_WHEEL].m_ifan[m_vaI].x) * (long) (m_Hak[FRONT_WHEEL].m_ifan[m_vaI].x - m_Hak[BACK_WHEEL].m_ifan[m_vaI].x) >> 16)
                + (int) ((long) (m_Hak[FRONT_WHEEL].m_ifan[m_vaI].y - m_Hak[BACK_WHEEL].m_ifan[m_vaI].y) * (long) (m_Hak[FRONT_WHEEL].m_ifan[m_vaI].y - m_Hak[BACK_WHEEL].m_ifan[m_vaI].y) >> 16))
                < 0xf0000)
            crashedInAir_MAYBE = true;
        if (l1 > 0x460000)
            crashedInAir_MAYBE = true;
        return LevelState.IN_PROCESS;
    }

    private void _aIV(int j) {
        for (int i1 = 0; i1 < 6; i1++) {
            K k1;
            Element n1;
            (n1 = (k1 = m_Hak[i1]).m_ifan[j]).l = 0;
            n1.g = 0;
            n1.a = 0;
            n1.g -= (int) (((long) m_voidI << 32) / (long) k1.m_forI >> 16);
        }

        if (!crashedInAir_MAYBE) {
            _akkV(m_Hak[0], NOT___spokes[1], m_Hak[BACK_WHEEL], j, 0x10000);
            _akkV(m_Hak[0], NOT___spokes[0], m_Hak[FRONT_WHEEL], j, 0x10000);
            _akkV(m_Hak[BACK_WHEEL], NOT___spokes[6], m_Hak[4], j, 0x20000);
            _akkV(m_Hak[FRONT_WHEEL], NOT___spokes[5], m_Hak[3], j, 0x20000);
        }
        _akkV(m_Hak[0], NOT___spokes[2], m_Hak[3], j, 0x10000);
        _akkV(m_Hak[0], NOT___spokes[3], m_Hak[4], j, 0x10000);
        _akkV(m_Hak[3], NOT___spokes[4], m_Hak[4], j, 0x10000);
        _akkV(m_Hak[5], NOT___spokes[8], m_Hak[3], j, 0x10000);
        _akkV(m_Hak[5], NOT___spokes[7], m_Hak[4], j, 0x10000);
        _akkV(m_Hak[5], NOT___spokes[9], m_Hak[0], j, 0x10000);
        Element n2 = m_Hak[BACK_WHEEL].m_ifan[j];
        acceleration = (int) ((long) acceleration * (long) (0x10000 - league().getM_jI()) >> 16);
        n2.a = acceleration;
        if (n2.o > league().getM_PI())
            n2.o = league().getM_PI();
        if (n2.o < -league().getM_PI())
            n2.o = -league().getM_PI();
        int j1 = 0;
        int l1 = 0;
        for (int i2 = 0; i2 < 6; i2++) {
            j1 += m_Hak[i2].m_ifan[j].p;
            l1 += m_Hak[i2].m_ifan[j].d;
        }

        j1 = (int) (((long) j1 << 32) / 0x60000L >> 16);
        l1 = (int) (((long) l1 << 32) / 0x60000L >> 16);
        int j3 = 0;
        for (int k3 = 0; k3 < 6; k3++) {
            int j2 = m_Hak[k3].m_ifan[j].p - j1;
            int k2 = m_Hak[k3].m_ifan[j].d - l1;
            if ((j3 = _doIII(j2, k2)) > 0x1e0000) {
                int l2 = (int) (((long) j2 << 32) / (long) j3 >> 16);
                int i3 = (int) (((long) k2 << 32) / (long) j3 >> 16);
                m_Hak[k3].m_ifan[j].p -= l2;
                m_Hak[k3].m_ifan[j].d -= i3;
            }
        }

        byte byte0 = ((byte) (m_Hak[BACK_WHEEL].m_ifan[j].y - m_Hak[0].m_ifan[j].y < 0 ? -1 : 1));
        byte byte1 = ((byte) (m_Hak[BACK_WHEEL].m_ifan[j].p - m_Hak[0].m_ifan[j].p < 0 ? -1 : 1));
        if (byte0 * byte1 > 0) {
            m_kI = j3;
        } else {
            m_kI = -j3;
        }
    }

    private void _akkV(K k1, Element n1, K k2, int j, int i1) {
        Element n2 = k1.m_ifan[j];
        Element n3 = k2.m_ifan[j];
        int j1 = n2.x - n3.x;
        int l1 = n2.y - n3.y;
        int i2;
        if (((i2 = _doIII(j1, l1)) >= 0 ? i2 : -i2) >= 3) {
            j1 = (int) (((long) j1 << 32) / (long) i2 >> 16);
            l1 = (int) (((long) l1 << 32) / (long) i2 >> 16);
            int j2 = i2 - n1.y;
            int l2 = (int) ((long) j1 * (long) (int) ((long) j2 * (long) n1.x >> 16) >> 16);
            int i3 = (int) ((long) l1 * (long) (int) ((long) j2 * (long) n1.x >> 16) >> 16);
            int j3 = n2.p - n3.p;
            int k3 = n2.d - n3.d;
            int l3 = (int) ((long) ((int) ((long) j1 * (long) j3 >> 16) + (int) ((long) l1 * (long) k3 >> 16)) * (long) n1.r >> 16);
            l2 += (int) ((long) j1 * (long) l3 >> 16);
            i3 += (int) ((long) l1 * (long) l3 >> 16);
            l2 = (int) ((long) l2 * (long) i1 >> 16);
            i3 = (int) ((long) i3 * (long) i1 >> 16);
            n2.l -= l2;
            n2.g -= i3;
            n3.l += l2;
            n3.g += i3;
        }
    }

    private void _aIIV(int j, int i1, int j1) {
        for (int l1 = 0; l1 < 6; l1++) {
            Element n1 = m_Hak[l1].m_ifan[j];
            Element n2;
            (n2 = m_Hak[l1].m_ifan[i1]).x = (int) ((long) n1.p * (long) j1 >> 16);
            n2.y = (int) ((long) n1.d * (long) j1 >> 16);
            int k1 = (int) ((long) j1 * (long) m_Hak[l1].m_forI >> 16);
            n2.p = (int) ((long) n1.l * (long) k1 >> 16);
            n2.d = (int) ((long) n1.g * (long) k1 >> 16);
        }

    }

    private void _zIIV(int j, int i1, int j1) {
        for (int k1 = 0; k1 < 6; k1++) {
            Element n1 = m_Hak[k1].m_ifan[j];
            Element n2 = m_Hak[k1].m_ifan[i1];
            Element n3 = m_Hak[k1].m_ifan[j1];
            n1.x = n2.x + (n3.x >> 1);
            n1.y = n2.y + (n3.y >> 1);
            n1.p = n2.p + (n3.p >> 1);
            n1.d = n2.d + (n3.d >> 1);
        }

    }

    private void _aaIV(int j) {
        _aIV(m_vaI);
        _aIIV(m_vaI, 2, j);
        _zIIV(4, m_vaI, 2);
        _aIV(4);
        _aIIV(4, 3, j >> 1);
        _zIIV(4, m_vaI, 3);
        _zIIV(m_waI, m_vaI, 2);
        _zIIV(m_waI, m_waI, 3);

        // wheels?!?!?!?! oh my god i found it!!!!!
        for (int i1 = 1; i1 <= 2; i1++) {
            Element n1 = m_Hak[i1].m_ifan[m_vaI];
            Element n2;
            (n2 = m_Hak[i1].m_ifan[m_waI]).r = n1.r + (int) ((long) j * (long) n1.o >> 16);
            n2.o = n1.o + (int) ((long) j * (long) (int) ((long) m_Hak[i1].m_newI * (long) n1.a >> 16) >> 16);
        }

    }

    private int checkCollisionWithTrackLine(int j) {
        byte byte0 = 2;
        int i1;
        i1 = (i1 = Math.max(m_Hak[FRONT_WHEEL].m_ifan[j].x, m_Hak[BACK_WHEEL].m_ifan[j].x)) >= m_Hak[5].m_ifan[j].x ? i1 : m_Hak[5].m_ifan[j].x;
        int j1;
        j1 = (j1 = Math.min(m_Hak[FRONT_WHEEL].m_ifan[j].x, m_Hak[BACK_WHEEL].m_ifan[j].x)) >= m_Hak[5].m_ifan[j].x ? m_Hak[5].m_ifan[j].x : j1;
        trackPhysic._aIIV(j1 - Constants.m_foraI[0], i1 + Constants.m_foraI[0], m_Hak[5].m_ifan[j].y);
        int k1 = m_Hak[FRONT_WHEEL].m_ifan[j].x - m_Hak[BACK_WHEEL].m_ifan[j].x;
        int l1 = m_Hak[FRONT_WHEEL].m_ifan[j].y - m_Hak[BACK_WHEEL].m_ifan[j].y;
        int i2 = _doIII(k1, l1);
        k1 = (int) (((long) k1 << 32) / (long) i2 >> 16);
        int j2 = -(int) (((long) l1 << 32) / (long) i2 >> 16);
        int k2 = k1;
        for (int l2 = 0; l2 < 6; l2++) {
            if (l2 == 4 || l2 == 3)
                continue;
            Element n1 = m_Hak[l2].m_ifan[j];
            if (l2 == 0) {
                n1.x += (int) ((long) j2 * 0x10000L >> 16);
                n1.y += (int) ((long) k2 * 0x10000L >> 16);
            }
            int i3 = trackPhysic.checkCollisionWithTrackLine(n1, m_Hak[l2].m_intI);
            if (l2 == 0) {
                n1.x -= (int) ((long) j2 * 0x10000L >> 16);
                n1.y -= (int) ((long) k2 * 0x10000L >> 16);
            }
            m_EI = trackPhysic.m_eI;
            m_CI = trackPhysic.m_dI;
            if (l2 == 5 && i3 != 2)
                m_mZ = true;
            if (l2 == 1 && i3 != 2)
                frontWheelTouchedGround = true;
            if (i3 == 1) {
                m_xaI = l2;
                byte0 = 1;
                continue;
            }
            if (i3 != 0)
                continue;
            m_xaI = l2;
            byte0 = 0;
            break;
        }

        return byte0;
    }

    private void _caIV(int j) {
        K k1;
        Element n1;
        (n1 = (k1 = m_Hak[m_xaI]).m_ifan[j]).x += (int) ((long) m_EI * 3276L >> 16);
        n1.y += (int) ((long) m_CI * 3276L >> 16);
        int i1;
        int j1;
        int l1;
        int i2;
        int j2;
        if (stop && (m_xaI == 2 || m_xaI == 1) && n1.o < 6553) {
            i1 = league().getM_gI() - league().getM_WI();
            j1 = 13107;
            l1 = 39321;
            i2 = 26214 - league().getM_WI();
            j2 = 26214 - league().getM_WI();
        } else {
            i1 = league().getM_gI();
            j1 = league().getM_fI();
            l1 = league().getM_eI();
            i2 = league().getM_aeI();
            j2 = league().getM_adI();
        }
        int k2 = _doIII(m_EI, m_CI);
        m_EI = (int) (((long) m_EI << 32) / (long) k2 >> 16);
        m_CI = (int) (((long) m_CI << 32) / (long) k2 >> 16);
        int l2 = n1.p;
        int i3 = n1.d;
        int j3 = -((int) ((long) l2 * (long) m_EI >> 16) + (int) ((long) i3 * (long) m_CI >> 16));
        int k3 = -((int) ((long) l2 * (long) (-m_CI) >> 16) + (int) ((long) i3 * (long) m_EI >> 16));
        int l3 = (int) ((long) i1 * (long) n1.o >> 16) - (int) ((long) j1 * (long) (int) (((long) k3 << 32) / (long) k1.m_aI >> 16) >> 16);
        int i4 = (int) ((long) i2 * (long) k3 >> 16) - (int) ((long) l1 * (long) (int) ((long) n1.o * (long) k1.m_aI >> 16) >> 16);
        int j4 = -(int) ((long) j2 * (long) j3 >> 16);
        int k4 = (int) ((long) (-i4) * (long) (-m_CI) >> 16);
        int l4 = (int) ((long) (-i4) * (long) m_EI >> 16);
        int i5 = (int) ((long) (-j4) * (long) m_EI >> 16);
        int j5 = (int) ((long) (-j4) * (long) m_CI >> 16);
        n1.o = l3;
        n1.p = k4 + i5;
        n1.d = l4 + j5;
    }

    public void calculateDelta(int j) {
        delta = (int) (((long) (int) (0xa0000L * (long) (j << 16) >> 16) << 32) / 0x800000L >> 16);
    }

    public int getProgress() {
        if (replay && replayState != null) {
            return replayState.p;
        }
        int j = Math.max(frontWheel().x, backWheel().x);
        if (crashedInAir_MAYBE)
            return trackPhysic._aII(element0().x);
        else
            return trackPhysic._aII(j);
    }

    public void updateState() {
        if (replay) {
            return;
        }
        synchronized (m_Hak) {
            for (int j = 0; j < 6; j++) {
                m_Hak[j].m_ifan[5].x = m_Hak[j].m_ifan[m_vaI].x;
                m_Hak[j].m_ifan[5].y = m_Hak[j].m_ifan[m_vaI].y;
                m_Hak[j].m_ifan[5].r = m_Hak[j].m_ifan[m_vaI].r;
            }

            m_Hak[0].m_ifan[5].p = m_Hak[0].m_ifan[m_vaI].p;
            m_Hak[0].m_ifan[5].d = m_Hak[0].m_ifan[m_vaI].d;
            m_Hak[BACK_WHEEL].m_ifan[5].o = m_Hak[BACK_WHEEL].m_ifan[m_vaI].o;
        }
    }

    public void updateElementPosition() {
        if (replay) {
            return;
        }
        synchronized (m_Hak) {
            for (int j = 0; j < 6; j++) {
                elements[j].x = m_Hak[j].m_ifan[5].x;
                elements[j].y = m_Hak[j].m_ifan[5].y;
                elements[j].r = m_Hak[j].m_ifan[5].r; //wheels rotating
            }

            element0().p = m_Hak[0].m_ifan[5].p;
            element0().d = m_Hak[0].m_ifan[5].d;
            backWheel().o = m_Hak[BACK_WHEEL].m_ifan[5].o;
        }
    }

    public Element element0() {
        return elements[0];
    }

    public Element frontWheel() {
        return elements[FRONT_WHEEL_CIRCLE];
    }

    public Element backWheel() {
        return elements[BACK_WHEEL_CIRCLE];
    }

    public void setState(FullEngineState s) {
        List<Element> e = new ArrayList<>();
        for (Element element : s.elements) {
            e.add(element.copy());
        }

        List<Element> ns = new ArrayList<>();
        for (Element element : s.NOT___spokes) {
            ns.add(element.copy());
        }

        List<K> k = new ArrayList<>();
        for (K element : s.m_Hak) {
            k.add(element.copy());
        }

        elements = e.toArray(new Element[0]);
        NOT___spokes = ns.toArray(new Element[0]);
        m_Hak = k.toArray(new K[0]);
        flagTwitch = s.flagTwitch;
        m_vcI_drawingFlag = s.m_vcI_drawingFlag;
        m_YI = s.m_YI;
        m_voidI = s.m_voidI;
        league = s.league;
        m_bZ = s.m_bZ;
        m_zI = s.m_zI;
        drawBiker = s.drawBiker;
        drawBike = s.drawBike;
        frontWheelTouchedGround = s.frontWheelTouchedGround;
        m_vaI = s.m_vaI;
        m_waI = s.m_waI;
        m_xaI = s.m_xaI;
        acceleration = s.acceleration;
        m_EI = s.m_EI;
        m_CI = s.m_CI;
        crashedInAir_MAYBE = s.crashedInAir_MAYBE;
        m_mZ = s.m_mZ;
        m_TI_biker = s.m_TI_biker;
        m_kI = s.m_kI;
        keysLocked = s.keysLocked;
        gas = s.gas;
        stop = s.stop;
        leansBack = s.leansBack;
        leansForward = s.leansForward;
        gasPressed = s.gasPressed;
        stopPressed = s.stopPressed;
        leansBackPressed = s.leansBackPressed;
        leansForwardPressed = s.leansForwardPressed;
        m_RZ = s.m_RZ;
        lookAhead = s.lookAhead;
        deltaX = s.deltaX;
        deltaY = s.deltaY;
        delta = s.delta;
    }

    public FullEngineState getFullState() {
        FullEngineState s = new FullEngineState();
        List<Element> e = new ArrayList<>();
        for (Element element : elements) {
            e.add(element.copy());
        }

        List<Element> ns = new ArrayList<>();
        for (Element element : NOT___spokes) {
            ns.add(element.copy());
        }

        List<K> k = new ArrayList<>();
        for (K element : m_Hak) {
            k.add(element.copy());
        }

        s.elements = e.toArray(new Element[0]);
        s.NOT___spokes = ns.toArray(new Element[0]);
        s.m_Hak = k.toArray(new K[0]);
        s.flagTwitch = flagTwitch;
        s.m_vcI_drawingFlag = m_vcI_drawingFlag;
        s.m_YI = m_YI;
        s.m_voidI = m_voidI;
        s.league = league;
        s.m_bZ = m_bZ;
        s.m_zI = m_zI;
        s.drawBiker = drawBiker;
        s.drawBike = drawBike;
        s.frontWheelTouchedGround = frontWheelTouchedGround;
        s.m_vaI = m_vaI;
        s.m_waI = m_waI;
        s.m_xaI = m_xaI;
        s.acceleration = acceleration;
        s.m_EI = m_EI;
        s.m_CI = m_CI;
        s.crashedInAir_MAYBE = crashedInAir_MAYBE;
        s.m_mZ = m_mZ;
        s.m_TI_biker = m_TI_biker;
        s.m_kI = m_kI;
        s.keysLocked = keysLocked;
        s.gas = gas;
        s.stop = stop;
        s.leansBack = leansBack;
        s.leansForward = leansForward;
        s.gasPressed = gasPressed;
        s.stopPressed = stopPressed;
        s.leansBackPressed = leansBackPressed;
        s.leansForwardPressed = leansForwardPressed;
        s.m_RZ = m_RZ;
        s.lookAhead = lookAhead;
        s.deltaX = deltaX;
        s.deltaY = deltaY;
        s.delta = delta;
        return s;
    }


    public EngineStateRecord getState() {
        if (replay && replayState != null) {
            replayState.replay = true;
            return replayState;
        }
        EngineStateRecord state = new EngineStateRecord();
        List<Element> list = new ArrayList<>();
        List<ElementRecord> e = new ArrayList<>();
        for (Element element : elements) {
            list.add(element.copy());
            e.add(ElementRecord.from(element));
        }
        state.track = trackPhysic.track;
        state.edit = edit;
        state.selectedPointIndex = selectedPointIndex;
        state.perspectiveEnabled = trackPhysic.isPerspectiveEnabled();
        state.shadowsEnabled = trackPhysic.isShadowsEnabled();
        state.elements = list.toArray(new Element[6]);
        state.e = e.toArray(e.toArray(new ElementRecord[0]));
        state.I = m_TI_biker;
        state.L = league;
        state.t = timerTime;
        state.l = Utils.copyArray(leftWheelParams);
        state.X = deltaX;
        state.Y = deltaY;
        state.ft = flagTwitch;

        state.p = getProgress();
        state.c = String.format(
                "%s%s%s%s",
                gasPressed ? "w" : "_",
                leansBackPressed ? "a" : "_",
                stopPressed ? "s" : "_",
                leansForwardPressed ? "d" : "_"
        );
        return state;
    }

    public EngineStateRecord getStateReference() {
        if (replay && replayState != null) {
            replayState.replay = true;
            return replayState;
        }
        EngineStateRecord state = new EngineStateRecord();
        state.track = trackPhysic.track;
        state.edit = edit;
        state.selectedPointIndex = selectedPointIndex;
        state.perspectiveEnabled = trackPhysic.isPerspectiveEnabled();
        state.shadowsEnabled = trackPhysic.isShadowsEnabled();
        state.elements = elements;
        state.e = elements;
        state.I = m_TI_biker;
        state.L = league;
        state.t = timerTime;
        state.l = leftWheelParams;
        state.X = deltaX;
        state.Y = deltaY;
        state.ft = flagTwitch;

        state.p = getProgress();
        return state;
    }

    public TrackPhysic getTrackPhysic() {
        return trackPhysic;
    }

    public void loadTrack(TrackData track) throws InvalidTrackException {
        trackPhysic.load(track);
    }

    public void setDrawBiker(boolean drawBiker) {
        this.drawBiker = drawBiker;
    }

    public void setDrawBike(boolean drawBike) {
        this.drawBike = drawBike;
    }

    public void setLookAhead(boolean flag) {
        lookAhead = flag;
    }

    public EngineStateRecord getRespawn() {
        return respawn;
    }

    public void setRespawn(EngineStateRecord respawn) {
        this.respawn = respawn;
    }

    public static final List<LeagueProperties> leagueProperties = LeagueProperties.getDefaultLeagueProperties();

    private LeagueProperties league() {
        return leagueProperties.get(league);
    }
}
