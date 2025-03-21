package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.model.Color;
import org.happysanta.gdtralive.game.api.model.EngineStateRecord;
import org.happysanta.gdtralive.game.api.Sprite;
import org.happysanta.gdtralive.game.api.model.ViewState;

public interface GdCanvas {

    void setColor(Color color);

    void setColor(int r, int g, int b);

    void setColorDimmed(int r, int g, int b);

    void drawRect(int left, int top, int right, int bottom, ViewState view);

    void drawLine(int j, int k, int l, int i1, ViewState view);

    void drawLine2(int j, int k, int l, int i1, ViewState view);

    float offsetX(float j, int offsetX);

    float offsetY(float j, int offsetY);

    void drawInfoMessage2(String message, Color color, ViewState view);
    void drawOpponentName(String message, Color color, ViewState view, int x, int y);

    void drawTimer2(int color, String time, ViewState view);
    void drawAttemptCounter(int color, String count, ViewState view);

    void drawSpriteWithRotation(Float j, Float k, ViewState view, EngineStateRecord state, Float fAngleDeg, Sprite sprite);


    void drawArc(int i1, Float j1, Float k1, int l1);

    void drawRect2(int l, Float j1, Float k1);

    void drawFlag(int j, int k, ViewState view, Sprite sprite);
    void drawSprite2(Float j, Float k, ViewState view, EngineStateRecord state, Sprite sprite);
    void drawBodySprite(Sprite sprite, Float l1, Float i2, Float fAngleDeg, int league);

    void drawLogoSprite(Sprite sprite, ViewState view);
}
