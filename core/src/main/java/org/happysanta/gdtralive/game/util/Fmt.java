package org.happysanta.gdtralive.game.util;

import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.dto.LevelPack;
import org.happysanta.gdtralive.game.api.model.TrackRecord;

public class Fmt {
    public static String colon(String label, String name) {
        return String.format("%s: %s", label, name);
    }

    public static String colonNoSpace(String label, String name) {
        return String.format("%s:%s", label, name);
    }

    public static String slash(Object left, Object right) {
        return String.format("%s/%s", left, right);
    }

    public static String colon(String label) {
        return String.format("%s: ", label);
    }

    public static String ra(String label) {
        return String.format("%s>", label);
    }

    public static String sp(String left, String right) {
        return String.format("%s %s", left, right);
    }

    public static String us(String left, String right) {
        return String.format("%s_%s", left, right);
    }

    public static String dot(String left, String right) {
        return String.format("%s.%s", left, right);
    }

    public static String formatLevelsCount(Mod mod) {
        StringBuilder s = new StringBuilder();
        for (LevelPack level : mod.getLevels()) {
            s.append(level.getTracks().size()).append(" ");
        }
        return s.toString().trim();
    }

    public static String recordName(TrackRecord tr) {
        return recordName(tr.getTrackName(), tr.getTime(), tr.getLeague(), Utils.getTrackId(tr.getTrack()));
    }

    public static String recordName(String name, long time, int league, String trackId) {
        return Utils.fixFileName(String.format("%s[%s_%s_%s]", name, time, league, trackId));
    }

    public static String durationString(long millis) {
        return String.format("%d:%02d.%03d", millis / 60000, (millis / 1000) % 60, millis % 1000);
    }
}
