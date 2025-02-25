package org.happysanta.gdtralive.game.api.model;

import java.io.Serializable;

public class ViewState implements Serializable {
    // === render
    public transient int offsetX;
    public transient int offsetY;
    public transient int height;
    public transient int width;
    public transient double zoom;
    public int drawBike; //drawBike
    public int drawBiker; //drawBiker
    public int drawBiker2; //drawBiker todo rename
}
