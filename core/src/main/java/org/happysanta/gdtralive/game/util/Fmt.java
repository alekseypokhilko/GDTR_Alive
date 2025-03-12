package org.happysanta.gdtralive.game.util;

import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.dto.LevelPack;
import org.happysanta.gdtralive.game.api.model.TrackData;
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

    public static String trackName(TrackData data) {
        return String.format("%s[%s_%s]", data.getName(), Utils.getTrackId(data), data.getLeague());
    }

    public static String copy(String name) {
        return String.format("%s-copy-%s", name, ("" + System.currentTimeMillis()).substring(7));
    }

    public static String trackNameTitle(String name, String[] leagueNames) {
        try {
            String league = leagueNames[Integer.parseInt(name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf("]")))];
            return name.substring(0, name.lastIndexOf("[")) + " " + league;
        } catch (Exception e) {
            return name;
        }
    }

    public static String recordTitle(String title, String[] leagueNames) {
        String trackName = title.substring(0, title.lastIndexOf("["));
        String meta = title.substring(title.lastIndexOf("[") + 1, title.lastIndexOf("]"));
        String league = leagueNames[Integer.parseInt(meta.substring(meta.indexOf("_") + 1, meta.lastIndexOf("_")))];
        String time = Fmt.durationString(Long.parseLong(meta.substring(0, meta.indexOf("_"))));
        return String.format("%s %s %s", trackName, league, time);
    }
}
