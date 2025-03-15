package org.happysanta.gdtralive.game.api.model;

public class LeagueSwitcher {
    private final int pi;
    private int l;

    public LeagueSwitcher(int pointIndex, int league) {
        this.pi = pointIndex;
        this.l = league;
    }

    public int getPointIndex() {
        return pi;
    }

    public int getLeague() {
        return l;
    }

    public void setLeague(int l) {
        this.l = l;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeagueSwitcher)) return false;

        LeagueSwitcher that = (LeagueSwitcher) o;
        return pi == that.pi && l == that.l;
    }

    @Override
    public int hashCode() {
        int result = pi;
        result = 31 * result + l;
        return result;
    }
}
