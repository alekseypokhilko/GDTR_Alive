package org.happysanta.gdtralive.game.levels;

import org.happysanta.gdtralive.game.Constants;
import org.happysanta.gdtralive.game.engine.Element;
import org.happysanta.gdtralive.game.engine.Engine;

public class TrackPhysic {
    public TrackParams track;
    private int m_longI;
    public int m_eI;
    public int m_dI;

    private boolean perspectiveEnabled = true;
    private boolean shadowsEnabled = true;
    private int xPointIndexBack = 0;
    private int xPointIndexFront = 0;
    private int xPointBack = 0;
    private int xPointFront = 0;
    private int[][] m_saaI;
    private final int[] m_haI;
    private final int[] m_vaI;
    private int m_daI;

    public TrackPhysic() {
        m_saaI = null;
        track = null;
        m_haI = new int[3];
        m_vaI = new int[3];
        m_daI = 0;
        for (int j = 0; j < 3; j++) {
            m_haI[j] = (int) ((long) (Constants.m_foraI[j] + 19660 >> 1) * (long) (Constants.m_foraI[j] + 19660 >> 1) >> 16);
            m_vaI[j] = (int) ((long) (Constants.m_foraI[j] - 19660 >> 1) * (long) (Constants.m_foraI[j] - 19660 >> 1) >> 16);
        }
    }

    public int getFinishXPosition() {
        return track.points[track.finishPointIndex][0] << 1;
    }

    public int getFinishYPosition() {
        return track.points[track.finishPointIndex][1] << 1;
    }

    public int getStartPosition_MAYBE() {
        return track.points[track.startPointIndex][0] << 1;
    }

    public int getStartX() {
        return track.startX << 1;
    }

    public int getStartY() {
        return track.startY << 1;
    }

    public int _aII(int j) {
        return track._doII(j >> 1);
    }

    public void load(TrackParams l1) throws InvalidTrackException {
        try {
            m_longI = 0x80000000;
            track = l1;
            int j = track.pointsCount;
            if (m_saaI == null || m_daI < j) {
                m_saaI = (int[][]) null;
                // System.gc();
                m_daI = Math.max(j, 100); //todo pointscount
                m_saaI = new int[m_daI][2];
            }
            xPointIndexBack = 0;
            xPointIndexFront = 0;
            xPointBack = l1.points[xPointIndexBack][0];
            xPointFront = l1.points[xPointIndexFront][0];
            for (int k = 0; k < j; k++) {
                int i1 = l1.points[(k + 1) % j][0] - l1.points[k][0];
                int j1 = l1.points[(k + 1) % j][1] - l1.points[k][1];
                if (k != 0 && k != j - 1)
                    m_longI = Math.max(m_longI, l1.points[k][0]);
                int k1 = -j1;
                int i2 = i1;
                int j2 = Engine._doIII(k1, i2);
                m_saaI[k][0] = (int) (((long) k1 << 32) / (long) j2 >> 16);
                m_saaI[k][1] = (int) (((long) i2 << 32) / (long) j2 >> 16);
                if (track.startPointIndex == 0 && l1.points[k][0] > track.startX)
                    track.startPointIndex = k + 1;
                if (track.finishPointIndex == 0 && l1.points[k][0] > track.finishX)
                    track.finishPointIndex = k;
            }

            xPointIndexBack = 0;
            xPointIndexFront = 0;
            xPointBack = 0;
            xPointFront = 0;
        } catch (ArithmeticException e) {
            throw new InvalidTrackException(e);
        }
    }

    public void moveTrack(int j, int k) {
        track.moveTrack(j, k);
    }

    public void _aIIV(int j, int k, int i1) {
        track._aIIV(j + 0x18000 >> 1, k - 0x18000 >> 1, i1 >> 1);
        k >>= 1;
        j >>= 1;
        xPointIndexFront = Math.min(xPointIndexFront, track.pointsCount - 1);
        xPointIndexBack = Math.max(xPointIndexBack, 0);
        if (k > xPointFront)
            while (xPointIndexFront < track.pointsCount - 1 && k > track.points[++xPointIndexFront][0]) ;
        else if (j < xPointBack) {
            while (xPointIndexBack > 0 && j < track.points[--xPointIndexBack][0]) ;
        } else {
            while (xPointIndexBack < track.pointsCount && j > track.points[++xPointIndexBack][0]) ;
            if (xPointIndexBack > 0)
                xPointIndexBack--;
            while (xPointIndexFront > 0 && k < track.points[--xPointIndexFront][0]) ;
            xPointIndexFront = Math.min(xPointIndexFront + 1, track.pointsCount - 1);
        }

//        Integer integer = level.leagueSwitchers.get(xPointIndexBack);
//        if (integer != null) {// && !integer.equals(Helpers.getGDActivity().game.engine.league)) {
//            Helpers.getGDActivity().game.engine.league = integer;
//        }

        //for collision checking
        xPointIndexBack = Math.max(xPointIndexBack - 20, 0);
        xPointIndexFront = Math.min(xPointIndexFront + 20, track.pointsCount - 1);
        /////

        xPointBack = track.points[xPointIndexBack][0];
        xPointFront = track.points[xPointIndexFront][0];
    }

    public int checkCollisionWithTrackLine(Element n1, int j) {
        int k3 = 0;
        byte byte1 = 2;
        int l3 = n1.x >> 1;
        int i4 = n1.y >> 1;
        if (perspectiveEnabled)
            i4 -= 0x10000;
        int j4 = 0;
        int k4 = 0;
        for (int l4 = xPointIndexBack; l4 < xPointIndexFront; l4++) {
            if (track.invisible.contains(l4)) {
                continue;
            }
            int k = track.points[l4][0];
            int i1 = track.points[l4][1];
            int j1 = track.points[l4 + 1][0];
            int k1;
            int _tmp = (k1 = track.points[l4 + 1][1]) < i1 ? i1 : k1;
            if (!track.checkBackwardCollision && (l3 - m_haI[j] > j1 || l3 + m_haI[j] < k))
                continue;
            int l1 = k - j1;
            int i2 = i1 - k1;
            int j2 = (int) ((long) l1 * (long) l1 >> 16) + (int) ((long) i2 * (long) i2 >> 16);
            int k2 = (int) ((long) (l3 - k) * (long) (-l1) >> 16) + (int) ((long) (i4 - i1) * (long) (-i2) >> 16);
            int l2;
            if ((j2 >= 0 ? j2 : -j2) >= 3)
                l2 = (int) (((long) k2 << 32) / (long) j2 >> 16);
            else
                l2 = (k2 <= 0 ? -1 : 1) * (j2 <= 0 ? -1 : 1) * 0x7fffffff;
            if (l2 < 0)
                l2 = 0;
            if (l2 > 0x10000)
                l2 = 0x10000;
            int i3 = k + (int) ((long) l2 * (long) (-l1) >> 16);
            int j3 = i1 + (int) ((long) l2 * (long) (-i2) >> 16);
            l1 = l3 - i3;
            i2 = i4 - j3;
            byte byte0;
            long l5;
            if ((l5 = ((long) l1 * (long) l1 >> 16) + ((long) i2 * (long) i2 >> 16)) < (long) m_haI[j]) {
                if (l5 >= (long) m_vaI[j])
                    byte0 = 1;
                else
                    byte0 = 0;
            } else {
                byte0 = 2;
            }
            if (byte0 == 0 && (int) ((long) m_saaI[l4][0] * (long) n1.p >> 16) + (int) ((long) m_saaI[l4][1] * (long) n1.d >> 16) < 0) {
                m_eI = m_saaI[l4][0];
                m_dI = m_saaI[l4][1];
                return 0;
            }
            if (byte0 != 1 || (int) ((long) m_saaI[l4][0] * (long) n1.p >> 16) + (int) ((long) m_saaI[l4][1] * (long) n1.d >> 16) >= 0)
                continue;
            k3++;
            byte1 = 1;
            if (k3 == 1) {
                j4 = m_saaI[l4][0];
                k4 = m_saaI[l4][1];
            } else {
                j4 += m_saaI[l4][0];
                k4 += m_saaI[l4][1];
            }
        }

        if (byte1 == 1) {
            if ((int) ((long) j4 * (long) n1.p >> 16) + (int) ((long) k4 * (long) n1.d >> 16) >= 0)
                return 2;
            m_eI = j4;
            m_dI = k4;
        }
        return byte1;
    }

    public boolean isShadowsEnabled() {
        return shadowsEnabled;
    }

    public boolean isPerspectiveEnabled() {
        return perspectiveEnabled;
    }

    public void setPerspectiveEnabled(boolean enabled) {
        perspectiveEnabled = enabled;
    }

    public void setShadowsEnabled(boolean enabled) {
        shadowsEnabled = enabled;
    }

    public TrackParams getTrack() {
        return track;
    }
}
