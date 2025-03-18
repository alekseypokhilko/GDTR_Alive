package org.happysanta.gdtralive.game;

import static org.happysanta.gdtralive.game.util.Utils.packInt;

import org.happysanta.gdtralive.game.api.Sprite;
import org.happysanta.gdtralive.game.api.external.GdCanvas;
import org.happysanta.gdtralive.game.api.model.Color;
import org.happysanta.gdtralive.game.api.model.DecorLine;
import org.happysanta.gdtralive.game.api.model.EngineStateRecord;
import org.happysanta.gdtralive.game.api.model.ViewState;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.util.FPMath;
import org.happysanta.gdtralive.game.util.Fmt;

import java.util.List;

public class FrameRender {
    private final int[][] m_KaaI = {
            {0x2cccc, -52428},
            {0x40000, 0xfffd8000},
            {0x63333, 0xffff0000},
            {0x6cccc, -39321},
            {0x39999, 39321},
            {16384, 0xfffdcccd},
            {13107, 0xfffecccd},
            {0x46666, 0x14000}
    };
    private final int[][] m_ucaaI = {
            {0x2e666, 0xfffe4ccd},
            {0x4b333, 0xfffc6667},
            {0x51999, 0xfffe4000},
            {0x60000, -58982},
            {0x40000, 0x18000},
            {0x10000, 0xfffe199a},
            {13107, 0xfffecccd},
            {0x46666, 0x14000}
    };
    private final int[][] m_SaaI = {
            {0x26666, 13107},
            {0x48000, -13107},
            {0x59999, 0x16666},
            {0x63333, 0x2e666},
            {0x54ccc, 0x11999},
            {39321, 0xfffe8000},
            {13107, -52428},
            {0x48000, 0x14000}
    };
    private final int[][] m_wcaaI = {
            {0x2cccc, -39321},
            {0x40000, 0xfffe0000},
            {0x60000, 0xffff0000},
            {0x70000, -39321},
            {0x48000, 6553},
            {16384, 0xfffdcccd},
            {13107, 0xfffecccd},
            {0x46666, 0x14ccc}
    };
    private final int[][] m_DaaI = {
            {0x2e666, 0xfffe999a},
            {0x3e666, 0xfffc6667},
            {0x51999, 0xfffe4000},
            {0x60000, -42598},
            {0x49999, 6553},
            {0x10000, 0xfffecccd},
            {13107, 0xfffecccd},
            {0x46666, 0x14ccc}
    };
    private final int[][] m_MaaI = {
            {0x26666, 13107},
            {0x48000, -13107},
            {0x59999, 0x19999},
            {0x63333, 0x2b333},
            {0x54ccc, 0x11999},
            {39321, 0xfffe8000},
            {13107, -52428},
            {0x46666, 0x14ccc}
    };
    private final int drawableOffScreenOffset = 4000000; //quick fix for drawing track line
    public final int[][] m_JaaI = {{45875}, {32768}, {52428}};
    public static final Sprite[] BODY_SPRITES = {Sprite.ARM, Sprite.LEG, Sprite.BODY};
    public static final Sprite[] START_FLAG_SPRITES = {Sprite.START_FLAG_2, Sprite.START_FLAG_0, Sprite.START_FLAG_1, Sprite.START_FLAG_0};
    public static final Sprite[] FINISH_FLAG_SPRITES = {Sprite.FINISH_FLAG_1, Sprite.FINISH_FLAG_0, Sprite.FINISH_FLAG_2, Sprite.FINISH_FLAG_0};
    public static final Sprite[] LEAGUE_FLAG_SPRITES = {Sprite.LEAGUE_FLAG_1, Sprite.LEAGUE_FLAG_0, Sprite.LEAGUE_FLAG_2, Sprite.LEAGUE_FLAG_0};

    private final ModManager mm;
    private GdCanvas canvas;
    private boolean dimmed = false;

    public FrameRender(ModManager modManager) {
        this.mm = modManager;
    }

    private ModManager mm() {
        return mm;
    }

    public void setColor(Color color) {
        if (dimmed) {
            canvas.setColorDimmed(color.r(), color.g(), color.b());
        } else {
            canvas.setColor(color.r(), color.g(), color.b());
        }
    }

    public void setCanvas(GdCanvas canvas) {
        this.canvas = canvas;
    }

    public void fillBackground(ViewState view) {
        setColor(mm().getTheme().getGameTheme().getGameBackgroundColor());
        canvas.drawRect(0, 0, view.width, view.height, view);
    }

    public void drawRespawnBike(ViewState view, EngineStateRecord state) {
        //todo refactoring extract common parts to method
        view.drawBike = 0;
        view.drawBiker2 = 0;
        view.drawBiker = 0;
        dimmed = true;
        int i1 = state.engine().x() - state.fender().x();
        int j1 = state.engine().y() - state.fender().y();
        int k1;
        if ((k1 = Engine.CALC_MAGIC(i1, j1)) != 0) {
            i1 = (int) (((long) i1 << 32) / (long) k1 >> 16);
            j1 = (int) (((long) j1 << 32) / (long) k1 >> 16);
        }
        int l1 = -j1;
        int i2 = i1;


        drawWheelsLines(state, view);
        if (view.drawBike == 1) {
            setColor(mm().getLeagueTheme(state.league()).getBikeColor());
        } else {
            setColor(mm().getLeagueTheme(state.league()).getBikeLinesColor());
        }
        drawFork(state, view);
        drawDriver(state, view, i1, j1, l1, i2);
        drawBikeLines(state, view, i1, j1, l1, i2);
        dimmed = false;
    }

    public void drawFrame(ViewState view, EngineStateRecord state) {
        fillBackground(view);
        int i1 = state.engine().x() - state.fender().x();
        int j1 = state.engine().y() - state.fender().y();
        int k1;
        if ((k1 = Engine.CALC_MAGIC(i1, j1)) != 0) {
            i1 = (int) (((long) i1 << 32) / (long) k1 >> 16);
            j1 = (int) (((long) j1 << 32) / (long) k1 >> 16);
        }
        int l1 = -j1;
        int i2 = i1;
        if (view.drawBiker2 == 1) {
            int k2 = state.fender().x();
            int j2;
            if ((j2 = state.engine().x()) >= k2) {
                int l2 = j2;
                j2 = k2;
                k2 = l2;
            }
            state.track._aIIV(j2, k2);
        }

        if (state.perspectiveEnabled)
            drawPerspective(state.element0().x(), state.element0().y(), view, state);
        if (view.drawBike == 1) drawBike(i1, j1, state, view);
        if (mm().getLeagueTheme(state.league()).isDrawWheelSprite()) {
            drawBackWheelsSprite(state, view);
        }
        drawWheelsLines(state, view);
        if (view.drawBike == 1) {
            setColor(mm().getLeagueTheme(state.league()).getBikeColor());
        } else {
            setColor(mm().getLeagueTheme(state.league()).getBikeLinesColor());
        }
        //todo WHY front wheel only?
//        _ifIIIV((state.frontWheel().x() << 2) >> 16,
//                (state.frontWheel().y() << 2) >> 16,
//                (Constants.m_foraI[0] << 2) >> 16,
//                FPMath.angle(i1, j1), view
//        );
        if (view.drawBiker2 == 0) drawFork(state, view);
        drawDriver(state, view, i1, j1, l1, i2);
        if (!(view.drawBike == 1)) drawBikeLines(state, view, i1, j1, l1, i2);
        drawTrackLine(view, state);
        drawDecorLines(state.element0().x(), state.element0().y(), view, state);
    }

    public synchronized void drawTrackLine(ViewState view, EngineStateRecord state) {
        setColor(mm().getGameTheme().getTrackLineColor());
        int index;
        int[][] points = state.track.points;
        for (index = 0; index < state.track.pointsCount - 1 && points[index][0] <= state.track.cameraX; index++)
            ;
        if (index > 0)
            index--;
        do {
            try {
                if (state.edit && state.selectedLineIndex == 0) {
                    //draw point/selected point
                    setColor(new Color(255, 0, 0));
                    drawLineWheel(
                            packInt(points[index][0]),
                            packInt(points[index][1]),
                            state.selectedPointIndex == index ? 8 : 4,
                            view
                    );
                }
            } catch (Exception e) {
                e.printStackTrace(); //todo handle/ show popup
            }

            if (index >= state.track.pointsCount - 1) {
                break;
            }
            setColor(mm().getGameTheme().getTrackLineColor());
            if (!state.track.invisible.contains(index)) {
                canvas.drawLine2(
                        packInt(points[index][0]),
                        packInt(points[index][1]),
                        packInt(points[index + 1][0]),
                        packInt(points[index + 1][1]),
                        view
                );
            }
            if (state.track.startPointIndex == index) {
                drawStartFlag(packInt(points[state.track.startPointIndex][0]),
                        packInt(points[state.track.startPointIndex][1]), view, state);
                setColor(mm().getGameTheme().getTrackLineColor());
            }
            if (index != 0 && state.track.getLeagueSwitcher(index) != null) {
                drawLeagueFlag(packInt(points[index][0]),
                        packInt(points[index][1]), view, state);
                setColor(mm().getGameTheme().getTrackLineColor());
            }
            if (state.track.finishPointIndex == index) {
                drawFinishFlag(packInt(points[state.track.finishPointIndex][0]),
                        packInt(points[state.track.finishPointIndex][1]), view, state);
                setColor(mm().getGameTheme().getTrackLineColor());
            }
            if (points[index][0] > state.track.cameraY + drawableOffScreenOffset)
                break;
            index++;
        } while (true);
    }

    private void drawDecorLines(int e0X, int e0Y, ViewState view, EngineStateRecord state) {
        List<DecorLine> decorLines = state.track.getDecorLines();
        for (int dlI = 0; dlI < decorLines.size(); dlI++) {
            DecorLine decorLine = decorLines.get(dlI);
            int[][] points = decorLine.getPoints();
            if (points == null || points.length < 2) {
                continue;
            }

            e0X >>= 1;
            e0Y >>= 1;
            int pEndX = e0X - points[0][0];
            int pEndY = (e0Y + 0x320000) - points[0][1];
            int k3 = Engine.CALC_MAGIC(pEndX, pEndY);
            pEndX = (int) (((long) pEndX << 32) / (long) (k3 >> 1 >> 1) >> 16);
            pEndY = (int) (((long) pEndY << 32) / (long) (k3 >> 1 >> 1) >> 16);

            for (int i = 0; i < points.length - 1; i++) {
                Color color = decorLine.getColor();
                setColor(color == null ? mm().getGameTheme().getTrackLineColor() : color);
                canvas.drawLine2(packInt(points[i][0]), packInt(points[i][1]), packInt(points[i + 1][0]), packInt(points[i + 1][1]), view);

                try {
                    if (state.edit && state.selectedLineIndex > 0 && dlI == state.selectedLineIndex - 1) {
                        //draw point/selected point
                        setColor(new Color(255, 0, 0));
                        drawLineWheel(
                                packInt(points[i][0]),
                                packInt(points[i][1]),
                                state.selectedPointIndex == i ? 8 : 4,
                                view
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Boolean.TRUE.equals(decorLine.getPerspective())) {
                    Color pColor = decorLine.getPerspectiveColor();
                    setColor(pColor == null ? mm().getGameTheme().getPerspectiveColor() : pColor);
                    int pStartX = pEndX;
                    int pStartY = pEndY;
                    pEndX = e0X - points[i + 1][0];
                    pEndY = (e0Y + 0x320000) - points[i + 1][1];
                    int l3 = Engine.CALC_MAGIC(pEndX, pEndY);
                    pEndX = (int) (((long) pEndX << 32) / (long) (l3 >> 1 >> 1) >> 16);
                    pEndY = (int) (((long) pEndY << 32) / (long) (l3 >> 1 >> 1) >> 16);
                    canvas.drawLine2(packInt(points[i][0] + pStartX), packInt(points[i][1] + pStartY), packInt(points[i + 1][0] + pEndX), packInt(points[i + 1][1] + pEndY), view);
                    canvas.drawLine2(packInt(points[i][0]), packInt(points[i][1]), packInt(points[i][0] + pStartX), packInt(points[i][1] + pStartY), view);
                }
            }

            try {
                if (state.edit && state.selectedLineIndex > 0 && dlI == state.selectedLineIndex - 1) {
                    //draw point/selected point
                    setColor(new Color(255, 0, 0));
                    drawLineWheel(
                            packInt(points[points.length -1 ][0]),
                            packInt(points[points.length -1][1]),
                            state.selectedPointIndex == points.length -1 ? 8 : 4,
                            view
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Boolean.TRUE.equals(decorLine.getPerspective())) {
                Color pColor = decorLine.getPerspectiveColor();
                setColor(pColor == null ? mm().getGameTheme().getPerspectiveColor() : pColor);
                canvas.drawLine2(packInt(points[points.length - 1][0]), packInt(points[points.length - 1][1]), packInt(points[points.length - 1][0] + pEndX), packInt(points[points.length - 1][1] + pEndY), view);
            }
        }
    }

    public void drawPerspective(int k, int i1, ViewState view, EngineStateRecord state) {
        setColor(mm().getGameTheme().getPerspectiveColor());
        k >>= 1;
        i1 >>= 1;
        drawPerspective2(k, i1, view, state);
    }


    public synchronized void drawPerspective2(int k, int i1, ViewState view, EngineStateRecord state) {
        int k2 = 0;
        int l2 = 0;
        int index;
        int[][] points = state.track.points;
        for (index = 0; index < state.track.pointsCount - 1 && points[index][0] <= state.track.cameraX; index++)
            ;
        if (index > 0)
            index--;
        int i3 = k - points[index][0];
        int j3 = (i1 + 0x320000) - points[index][1];
        int k3 = Engine.CALC_MAGIC(i3, j3);
        i3 = (int) (((long) i3 << 32) / (long) (k3 >> 1 >> 1) >> 16);
        j3 = (int) (((long) j3 << 32) / (long) (k3 >> 1 >> 1) >> 16);
        setColor(mm().getGameTheme().getPerspectiveColor());
        do {
            if (index >= state.track.pointsCount - 1)
                break;
            int j1 = i3;
            int l1 = j3;
            i3 = k - points[index + 1][0];
            j3 = (i1 + 0x320000) - points[index + 1][1];
            int l3 = Engine.CALC_MAGIC(i3, j3);
            i3 = (int) (((long) i3 << 32) / (long) (l3 >> 1 >> 1) >> 16);
            j3 = (int) (((long) j3 << 32) / (long) (l3 >> 1 >> 1) >> 16);
            if (!state.track.invisible.contains(index)) {
                canvas.drawLine2(packInt(points[index][0] + j1), packInt(points[index][1] + l1), packInt(points[index + 1][0] + i3), packInt(points[index + 1][1] + j3), view);
            }
            if (!state.track.invisible.contains(index - 1) || !state.track.invisible.contains(index)) {
                canvas.drawLine2(packInt(points[index][0]), packInt(points[index][1]), packInt(points[index][0] + j1), packInt(points[index][1] + l1), view);
            }
            if (index > 1) {
                if (points[index][0] > state.track.shadowX && k2 == 0)
                    k2 = index - 1;
                if (points[index][0] > state.track.shadowY && l2 == 0)
                    l2 = index - 1;
            }
            if (state.track.startPointIndex == index) {
                drawStartFlag(packInt(points[state.track.startPointIndex][0] + j1), packInt(points[state.track.startPointIndex][1] + l1), view, state);
                setColor(mm().getGameTheme().getPerspectiveColor());
            }
            if (index != 0 && state.track.getLeagueSwitcher(index) != null) {
                drawLeagueFlag(packInt(points[index][0] + j1), packInt(points[index][1] + l1), view, state);
                setColor(mm().getGameTheme().getPerspectiveColor());
            }
            if (state.track.finishPointIndex == index) {
                drawFinishFlag(packInt(points[state.track.finishPointIndex][0] + j1), packInt(points[state.track.finishPointIndex][1] + l1), view, state);
                setColor(mm().getGameTheme().getPerspectiveColor());
            }
            if (points[index][0] > state.track.cameraY + drawableOffScreenOffset)
                break;
            index++;
        } while (true);
        int k1 = i3;
        int i2 = j3;
        canvas.drawLine2(
                packInt(points[state.track.pointsCount - 1][0]),
                packInt(points[state.track.pointsCount - 1][1]),
                packInt(points[state.track.pointsCount - 1][0] + k1),
                packInt(points[state.track.pointsCount - 1][1] + i2),
                view
        );
        if (state.shadowsEnabled)
            drawShadows(k2, l2, view, state);
    }


    public void drawShadows(int k, int index, ViewState view, EngineStateRecord state) {
        try {
            if (index <= state.track.pointsCount - 1) {
                int[][] points = state.track.points;
                int j1 = Math.max(state.track.shadow_m_gI - (points[k][1] + points[index + 1][1] >> 1), 0);
                if (state.track.shadow_m_gI <= points[k][1] || state.track.shadow_m_gI <= points[index + 1][1])
                    j1 = Math.min(j1, 0x50000);
                state.track.shadow_m_rI = (int) ((long) state.track.shadow_m_rI * 49152L >> 16) + (int) ((long) j1 * 16384L >> 16);
                if (state.track.shadow_m_rI <= 0x88000) {
                    int k1 = (int) (0x190000L * (long) state.track.shadow_m_rI >> 16) >> 16;
                    canvas.setColor(k1, k1, k1);
                    int l1 = points[k][0] - points[k + 1][0];
                    //todo java.lang.ArithmeticException: divide by zero
                    int i2 = (int) (((long) (points[k][1] - points[k + 1][1]) << 32) / (long) l1 >> 16);
                    int j2 = points[k][1] - (int) ((long) points[k][0] * (long) i2 >> 16);
                    int k2 = (int) ((long) state.track.shadowX * (long) i2 >> 16) + j2;
                    l1 = points[index][0] - points[index + 1][0];
                    i2 = (int) (((long) (points[index][1] - points[index + 1][1]) << 32) / (long) l1 >> 16);
                    j2 = points[index][1] - (int) ((long) points[index][0] * (long) i2 >> 16);
                    int l2 = (int) ((long) state.track.shadowY * (long) i2 >> 16) + j2;
                    if (k == index && !state.track.getInvisible().contains(k)) {
                        canvas.drawLine2(
                                packInt(state.track.shadowX),
                                packInt(k2 + 0x10000),
                                packInt(state.track.shadowY),
                                packInt(l2 + 0x10000),
                                view
                        );
                        return;
                    }
                    if (!state.track.getInvisible().contains(k)) {
                        canvas.drawLine2(
                                packInt(state.track.shadowX),
                                packInt(k2 + 0x10000),
                                packInt(points[k + 1][0]),
                                packInt(points[k + 1][1] + 0x10000),
                                view
                        );
                    }

                    if (false) {//todo draw shadows on min x point
                        for (int i3 = k + 1; i3 < index; i3++) {
                            if (!state.track.getInvisible().contains(i3)) {
                                canvas.drawLine2(
                                        packInt(points[i3][0]),
                                        packInt(points[i3][1] + 0x10000),
                                        packInt(points[i3 + 1][0]),
                                        packInt(points[i3 + 1][1] + 0x10000),
                                        view
                                );
                            }
                        }
                    }

                    if (!state.track.getInvisible().contains(index)) {
                        canvas.drawLine2(
                                packInt(points[index][0]),
                                packInt(points[index][1] + 0x10000),
                                packInt(state.track.shadowY),
                                packInt(l2 + 0x10000),
                                view
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // �������� �����
    public void _ifIIIV(int j, int k, int l, int i1, ViewState view) {
        l++;
        float j1 = canvas.offsetX(j - l, view.offsetX);
        float k1 = canvas.offsetY(k + l, view.offsetY);
        int l1 = l << 1;
        if ((i1 = -(int) (((long) (int) ((long) i1 * 0xb40000L >> 16) << 32) / 0x3243fL >> 16)) < 0)
            i1 += 360;
        canvas.drawArc(i1, j1, k1, l1);
    }

    private void drawBike(int i1, int j1, EngineStateRecord state, ViewState view) {
        int engineAngle = FPMath.angle(state.element0().x() - state.engine().x(), state.element0().y() - state.engine().y());
        int fenderAngle = FPMath.angle(state.element0().x() - state.fender().x(), state.element0().y() - state.fender().y());
        int engineX = (state.element0().x() >> 1) + (state.engine().x() >> 1);
        int engineY = (state.element0().y() >> 1) + (state.engine().y() >> 1);
        int fenderX = (state.element0().x() >> 1) + (state.fender().x() >> 1);
        int fenderY = (state.element0().y() >> 1) + (state.fender().y() >> 1);
        int i3 = -j1;
        int j3 = i1;
        engineX += (int) ((long) i3 * 0x10000L >> 16) - (int) ((long) i1 * 32768L >> 16);
        engineY += (int) ((long) j3 * 0x10000L >> 16) - (int) ((long) j1 * 32768L >> 16);
        fenderX += (int) ((long) i3 * 0x10000L >> 16) - (int) ((long) i1 * 0x1ccccL >> 16);
        fenderY += (int) ((long) j3 * 0x10000L >> 16) - (int) ((long) j1 * 0x20000L >> 16);
        drawFender((fenderX << 2) / (float) 0xFFFF, (fenderY << 2) / (float) 0xFFFF, fenderAngle, view, state);
        drawEngine((engineX << 2) / (float) 0xFFFF, (engineY << 2) / (float) 0xFFFF, engineAngle, view, state);
    }

    public void drawFender(float j, float k, int l, ViewState view, EngineStateRecord state) {
        float fAngleDeg = (float) (l / (float) 0xFFFF / Math.PI * 180) - 180 + 15;
        if (fAngleDeg >= 360) fAngleDeg -= 360;
        canvas.drawSpriteWithRotation(j, k, view, state, fAngleDeg, Sprite.FENDER);
    }

    public void drawEngine(float j, float k, int angle, ViewState view, EngineStateRecord state) {
        float fAngleDeg = (float) (angle / (float) 0xFFFF / Math.PI * 180) - 180;
        canvas.drawSpriteWithRotation(j, k, view, state, fAngleDeg, Sprite.ENGINE);
    }

    private void drawFork(EngineStateRecord state, ViewState view) {
        setColor(mm().getLeagueTheme(state.league()).getForkColor());
        canvas.drawLine(state.engine().x(), state.engine().y(), state.frontWheel().x(), state.frontWheel().y(), view);
    }

    private void drawBackWheelsSprite(EngineStateRecord state, ViewState view) {
        int i1 = 1;
        int j1 = 1;
        switch (state.league()) {
            case 2: // '\002'
            case 3: // '\003'
                i1 = j1 = 0;
                break;

            case 1: // '\001'
                i1 = 0;
                break;
        }
        drawWheel((state.backWheel().x() << 2) / (float) 0xFFFF, (state.backWheel().y() << 2) / (float) 0xFFFF, i1, view, state);
        drawWheel((state.frontWheel().x() << 2) / (float) 0xFFFF, (state.frontWheel().y() << 2) / (float) 0xFFFF, j1, view, state);
    }

    public void drawWheel(float j, float k, int wheel, ViewState view, EngineStateRecord state) {
        Sprite sprite;
        if (wheel == 1)
            sprite = Sprite.WHEEL_SMALL; // small
        else
            sprite = Sprite.WHEEL_BIG; // big
//        if (mm()) {
//
//        }

        canvas.drawSprite2(j, k, view, state, sprite);
    }

    // Draws red circle
    public void drawLineWheel(float j, float k, int l, ViewState view) {
        float i1 = l / 2;
        float j1 = canvas.offsetX(j - i1, view.offsetX);
        float k1 = canvas.offsetY(k + i1, view.offsetY);
        canvas.drawRect2(l, j1, k1);
    }

    private void drawWheelsLines(EngineStateRecord state, ViewState view) {
        int i1;
        int j1 = (int) ((long) (i1 = 114688/*state.aI*/) * 58982L >> 16);
        int k1 = (int) ((long) i1 * 45875L >> 16);

        if (mm().getTheme().getLeagueThemes().get(state.league()).isDrawWheelLines()) {
            Color frontWheelsColor = mm().getTheme().getLeagueThemes().get(state.league()).getFrontWheelsColor();
            if (frontWheelsColor != null) {
                setColor(frontWheelsColor);
                drawLineWheel((state.frontWheel().x() << 2) >> 16, (state.frontWheel().y() << 2) >> 16, (i1 + i1 << 2) >> 16, view);
                drawLineWheel((state.frontWheel().x() << 2) >> 16, (state.frontWheel().y() << 2) >> 16, (j1 + j1 << 2) >> 16, view);
            }
            Color backWheelsColor = mm().getTheme().getLeagueThemes().get(state.league()).getBackWheelsColor();
            if (backWheelsColor != null) {
                setColor(backWheelsColor);
                drawLineWheel((state.backWheel().x() << 2) >> 16, (state.backWheel().y() << 2) >> 16, (i1 + i1 << 2) >> 16, view);
                drawLineWheel((state.backWheel().x() << 2) >> 16, (state.backWheel().y() << 2) >> 16, (j1 + j1 << 2) >> 16, view);
            }
        }

        // right wheel
        int l1 = j1;
        int i2 = 0;
        int j2;
        int k2 = FPMath._doII(j2 = state.frontWheel().r());
        int l2 = FPMath.sin(j2);
        int i3 = l1;
        l1 = (int) ((long) k2 * (long) l1 >> 16) + (int) ((long) (-l2) * (long) i2 >> 16);
        i2 = (int) ((long) l2 * (long) i3 >> 16) + (int) ((long) k2 * (long) i2 >> 16);
        k2 = FPMath._doII(j2 = 0x141b2);
        l2 = FPMath.sin(j2);
        for (int k3 = 0; k3 < 5; k3++) {
            //spokes
            setColor(mm().getTheme().getLeagueThemes().get(state.league()).getFrontWheelsSpokeColor());
            canvas.drawLine(state.frontWheel().x(), state.frontWheel().y(), state.frontWheel().x() + l1, state.frontWheel().y() + i2, view);
            i3 = l1;
            l1 = (int) ((long) k2 * (long) l1 >> 16) + (int) ((long) (-l2) * (long) i2 >> 16);
            i2 = (int) ((long) l2 * (long) i3 >> 16) + (int) ((long) k2 * (long) i2 >> 16);
        }

        // left wheel
        l1 = j1;
        i2 = 0;
        // k2 = FPMath._doII(j2 = m_aaan[2].m_bI);
        k2 = FPMath._doII(j2 = Math.round(state.backWheel().r() / 1.75f));
        l2 = FPMath.sin(j2);
        i3 = l1;
        l1 = (int) ((long) k2 * (long) l1 >> 16) + (int) ((long) (-l2) * (long) i2 >> 16);
        i2 = (int) ((long) l2 * (long) i3 >> 16) + (int) ((long) k2 * (long) i2 >> 16);
        k2 = FPMath._doII(j2 = 0x141b2);
        l2 = FPMath.sin(j2);

        boolean toUpdate = true;
        int[][] l = new int[5][4];
        for (int l3 = 0; l3 < 5; l3++) {
            if (toUpdate) {
                l[l3][0] = state.backWheel().x();
                l[l3][1] = state.backWheel().y();
                l[l3][2] = state.backWheel().x() + l1;
                l[l3][3] = state.backWheel().y() + i2;
            }
            setColor(mm().getTheme().getLeagueThemes().get(state.league()).getBackWheelsSpokeColor());
            canvas.drawLine(l[l3][0], l[l3][1], l[l3][2], l[l3][3], view);
            int j3 = l1;
            l1 = (int) ((long) k2 * (long) l1 >> 16) + (int) ((long) (-l2) * (long) i2 >> 16);
            i2 = (int) ((long) l2 * (long) j3 >> 16) + (int) ((long) k2 * (long) i2 >> 16);
        }

        if (state.league() < mm().getLeagueThemes().size()) {
            Color backWheelDotColor = mm().getLeagueTheme(state.league()).getBackWheelDotColor();
            if (backWheelDotColor != null) {
                setColor(backWheelDotColor);
                drawLineWheel((state.backWheel().x() << 2) / (float) 0xFFFF, (state.backWheel().y() << 2) / (float) 0xFFFF, 4, view);
            }
            Color frontWheelDotColor = mm().getLeagueTheme(state.league()).getFrontWheelDotColor();
            if (frontWheelDotColor != null) {
                setColor(frontWheelDotColor);
                drawLineWheel((state.frontWheel().x() << 2) / (float) 0xFFFF, (state.frontWheel().y() << 2) / (float) 0xFFFF, 4, view);
            }
        }
    }

    private void drawDriver(EngineStateRecord state, ViewState view, int i1, int j1, int k1, int l1) {
        int i2 = 0;
        int j2 = 0x10000;
        int k2 = state.element0().x();
        int l2 = state.element0().y();
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        int j4 = 0;
        int k4 = 0;
        int l4 = 0;
        int i5 = 0;
        int j5 = 0;
        int k5 = 0;
        int l5 = 0;
        int i6 = 0;
        int j6 = 0;
        int k6 = 0;
        int l6 = 0;
        int[][] ai = (int[][]) null;
        int[][] ai1 = (int[][]) null;
        int[][] ai2 = (int[][]) null;
        if (view.drawBiker == 1) {
            if (state.getI() < 32768) {
                ai1 = m_ucaaI;
                ai2 = m_KaaI;
                j2 = (int) ((long) state.getI() * 0x20000L >> 16);
            } else if (state.getI() > 32768) {
                i2 = 1;
                ai1 = m_KaaI;
                ai2 = m_SaaI;
                j2 = (int) ((long) (state.getI() - 32768) * 0x20000L >> 16);
            } else {
                ai = m_KaaI;
            }
        } else if (state.getI() < 32768) {
            ai1 = m_DaaI;
            ai2 = m_wcaaI;
            j2 = (int) ((long) state.getI() * 0x20000L >> 16);
        } else if (state.getI() > 32768) {
            i2 = 1;
            ai1 = m_wcaaI;
            ai2 = m_MaaI;
            j2 = (int) ((long) (state.getI() - 32768) * 0x20000L >> 16);
        } else {
            ai = m_wcaaI;
        }
        for (int j7 = 0; j7 < m_KaaI.length; j7++) {
            int i8;
            int j8;
            if (ai1 != null) {
                j8 = (int) ((long) ai1[j7][0] * (long) (0x10000 - j2) >> 16) + (int) ((long) ai2[j7][0] * (long) j2 >> 16);
                i8 = (int) ((long) ai1[j7][1] * (long) (0x10000 - j2) >> 16) + (int) ((long) ai2[j7][1] * (long) j2 >> 16);
            } else {
                j8 = ai[j7][0];
                i8 = ai[j7][1];
            }
            int k8 = k2 + (int) ((long) k1 * (long) j8 >> 16) + (int) ((long) i1 * (long) i8 >> 16);
            int l8 = l2 + (int) ((long) l1 * (long) j8 >> 16) + (int) ((long) j1 * (long) i8 >> 16);
            switch (j7) {
                case 0: // '\0'
                    k4 = k8;
                    l4 = l8;
                    break;

                case 1: // '\001'
                    i5 = k8;
                    j5 = l8;
                    break;

                case 2: // '\002'
                    k5 = k8;
                    l5 = l8;
                    break;

                case 3: // '\003'
                    i6 = k8;
                    j6 = l8;
                    break;

                case 4: // '\004'
                    k6 = k8;
                    l6 = l8;
                    break;

                case 5: // '\005'
                    k3 = k8;
                    l3 = l8;
                    break;

                case 6: // '\006'
                    i4 = k8;
                    j4 = l8;
                    break;

                case 7: // '\007'
                    i3 = k8;
                    j3 = l8;
                    break;
            }
        }

        int i7 = (int) ((long) m_JaaI[i2][0] * (long) (0x10000 - j2) >> 16) + (int) ((long) m_JaaI[i2 + 1][0] * (long) j2 >> 16);
        if (view.drawBiker == 1) {
            _aIIIV(k3 << 2, l3 << 2, k4 << 2, l4 << 2, 1, view, state);
            _aIIIV(k4 << 2, l4 << 2, i5 << 2, j5 << 2, 1, view, state);
            drawBikerPart(i5 << 2, j5 << 2, k5 << 2, l5 << 2, 2, i7, view, state);
            _aIIIV(k5 << 2, l5 << 2, k6 << 2, l6 << 2, 0, view, state);
            int k7 = FPMath.angle(i1, j1);
            if (state.getI() > 32768)
                k7 += 20588;
            drawHelmet((i6 << 2) / (float) 0xFFFF, (j6 << 2) / (float) 0xFFFF, k7, view, state);
        } else {
            setColor(mm().getLeagueTheme(state.league()).getBikerLegColor());
            canvas.drawLine(k3, l3, k4, l4, view);
            canvas.drawLine(k4, l4, i5, j5, view);
            setColor(mm().getLeagueTheme(state.league()).getBikerBodyColor());
            canvas.drawLine(i5, j5, k5, l5, view);
            canvas.drawLine(k5, l5, k6, l6, view);
            canvas.drawLine(k6, l6, i3, j3, view);
            int l7 = 0x10000;
            setColor(mm().getLeagueTheme(state.league()).getBikerHeadColor());
            drawLineWheel((i6 << 2) >> 16, (j6 << 2) >> 16, (l7 + l7 << 2) >> 16, view);
        }

        setColor(mm().getLeagueTheme(state.league()).getSteeringColor());
        drawSteering((i3 << 2) >> 16, (j3 << 2) >> 16, view, state);
        drawSteering((i4 << 2) >> 16, (j4 << 2) >> 16, view, state);
    }

    public void drawSteering(int j, int k, ViewState view, EngineStateRecord state) {
        canvas.drawSprite2((float) j, (float) k, view, state, Sprite.STEERING);
    }

    private void drawBikeLines(EngineStateRecord state, ViewState view, int i1, int j1, int k1, int l1) {
        int i2 = state.backWheel().x();
        int j2 = state.backWheel().y();
        int k2 = i2 + (int) ((long) k1 * (long) 32768 >> 16);
        int l2 = j2 + (int) ((long) l1 * (long) 32768 >> 16);
        int i3 = i2 - (int) ((long) k1 * (long) 32768 >> 16);
        int j3 = j2 - (int) ((long) l1 * (long) 32768 >> 16);
        int k3 = state.element0().x() + (int) ((long) i1 * 32768L >> 16);
        int l3 = state.element0().y() + (int) ((long) j1 * 32768L >> 16);
        int i4 = k3 - (int) ((long) i1 * 0x20000L >> 16);
        int j4 = l3 - (int) ((long) j1 * 0x20000L >> 16);
        int k4 = i4 + (int) ((long) k1 * 0x10000L >> 16);
        int l4 = j4 + (int) ((long) l1 * 0x10000L >> 16);
        int i5 = i4 + (int) ((long) i1 * 49152L >> 16) + (int) ((long) k1 * 49152L >> 16);
        int j5 = j4 + (int) ((long) j1 * 49152L >> 16) + (int) ((long) l1 * 49152L >> 16);
        int k5 = i4 + (int) ((long) k1 * 32768L >> 16);
        int l5 = j4 + (int) ((long) l1 * 32768L >> 16);
        int i6 = state.frontWheel().x();
        int j6 = state.frontWheel().y();
        int k6 = state.fender().x() - (int) ((long) i1 * 49152L >> 16);
        int l6 = state.fender().y() - (int) ((long) j1 * 49152L >> 16);
        int i7 = k6 - (int) ((long) k1 * 32768L >> 16);
        int j7 = l6 - (int) ((long) l1 * 32768L >> 16);
        int k7 = (k6 - (int) ((long) i1 * 0x20000L >> 16)) + (int) ((long) k1 * 16384L >> 16);
        int l7 = (l6 - (int) ((long) j1 * 0x20000L >> 16)) + (int) ((long) l1 * 16384L >> 16);
        int i8 = state.engine().x();
        int j8 = state.engine().y();
        int k8 = i8 + (int) ((long) k1 * 32768L >> 16);
        int l8 = j8 + (int) ((long) l1 * 32768L >> 16);
        int i9 = (i8 + (int) ((long) k1 * 0x1c000L >> 16)) - (int) ((long) i1 * 32768L >> 16);
        int j9 = (j8 + (int) ((long) l1 * 0x1c000L >> 16)) - (int) ((long) j1 * 32768L >> 16);

        setColor(mm().getLeagueTheme(state.league()).getBikeLinesColor());
        drawLineWheel((k5 << 2) >> 16, (l5 << 2) >> 16, (32768 + 32768 << 2) >> 16, view);
        if (view.drawBiker2 == 0) {
            canvas.drawLine(k2, l2, k4, l4, view);
            canvas.drawLine(i3, j3, i4, j4, view);
        }
        canvas.drawLine(k3, l3, i4, j4, view);
        canvas.drawLine(k3, l3, i8, j8, view);
        canvas.drawLine(i5, j5, k8, l8, view);
        canvas.drawLine(k8, l8, i9, j9, view);
        if (view.drawBiker2 == 0) {
            canvas.drawLine(i8, j8, i6, j6, view);
            canvas.drawLine(i9, j9, i6, j6, view);
        }
        canvas.drawLine(k4, l4, i7, j7, view);
        canvas.drawLine(i5, j5, k6, l6, view);
        canvas.drawLine(k6, l6, k7, l7, view);
        canvas.drawLine(i7, j7, k7, l7, view);
    }

    public void drawStartFlag(int j, int k, ViewState view, EngineStateRecord state) {
        setColor(mm().getGameTheme().getStartFlagColor());
        canvas.drawLine2(j, k, j, k + 32, view);
        canvas.drawFlag(j, k, view, START_FLAG_SPRITES[state.ft >> 16]);
    }

    public void drawLeagueFlag(int j, int k, ViewState view, EngineStateRecord state) {
        setColor(mm().getGameTheme().getStartFlagColor());
        canvas.drawLine2(j, k, j, k + 32, view);
        canvas.drawFlag(j, k, view, LEAGUE_FLAG_SPRITES[state.ft >> 16]);
    }

    public void drawFinishFlag(int j, int k, ViewState view, EngineStateRecord state) {
        setColor(mm().getGameTheme().getFinishFlagColor());
        canvas.drawLine2(j, k, j, k + 32, view);
        canvas.drawFlag(j, k, view, FINISH_FLAG_SPRITES[state.ft >> 16]);
    }

    public void drawHelmet(float j, float k, int angle, ViewState view, EngineStateRecord state) {
        float fAngleDeg = (float) (angle / (float) 0xFFFF / Math.PI * 180) - 90 - 10;
        if (fAngleDeg >= 360) fAngleDeg -= 360;
        if (fAngleDeg < 0) fAngleDeg = 360 + fAngleDeg;
        canvas.drawSpriteWithRotation(j, k, view, state, fAngleDeg, Sprite.HELMET);
    }

    public void _aIIIV(int j, int k, int l, int i1, int j1, ViewState view, EngineStateRecord state) {
        drawBikerPart(j, k, l, i1, j1, 32768, view, state);
    }

    public void drawBikerPart(int j, int k, int l, int i1, int j1, int k1, ViewState view, EngineStateRecord state) {
        float l1 = canvas.offsetX((int) ((long) l * (long) k1 >> 16) + (int) ((long) j * (long) (0x10000 - k1) >> 16) >> 16, view.offsetX);
        float i2 = canvas.offsetY((int) ((long) i1 * (long) k1 >> 16) + (int) ((long) k * (long) (0x10000 - k1) >> 16) >> 16, view.offsetY);
        int j2 = FPMath.angle(l - j, i1 - k);

        float fAngleDeg = (float) (j2 / (float) 0xFFFF / Math.PI * 180) - 180;
        canvas.drawBodySprite(BODY_SPRITES[j1], l1, i2, fAngleDeg, state.league());
    }

    public void drawInfoMessage(String message, ViewState view) {
        canvas.drawInfoMessage2(message, mm().getInterfaceTheme().getInfoMessageColor(), view);
    }

    public void drawProgress(int progress, ViewState view) {
        double progr = progress / (double) 0xFFFF;

        setColor(mm().getInterfaceTheme().getProgressBackgroundColor());
        canvas.drawRect(0, 0, view.width, 5, view);

        setColor(mm().getInterfaceTheme().getProgressColor());
        canvas.drawRect(0, 0, (int) Math.round(view.width * Math.min(Math.max(progr, 0), 1)), 5, view);
    }

    public void drawLogo(Sprite sprite, ViewState view) {
        setColor(mm().getInterfaceTheme().getSplashColor());
        canvas.drawRect(0, 0, view.width, view.height, view);
        canvas.drawLogoSprite(sprite, view);
    }

    public void drawTimer(long millis, ViewState view) {
        canvas.drawTimer2(mm().getInterfaceTheme().getTextColorInt(), Fmt.durationString(millis), view);
    }

    public void drawAttemptCounter(ViewState view, String msg) {
        canvas.drawAttemptCounter(mm().getInterfaceTheme().getTextColorInt(), msg, view);
    }
}
