package org.happysanta.gdtralive.game;

import com.google.gson.Gson;

import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.mod.Theme;
import org.happysanta.gdtralive.game.mod.TrackReference;

import java.util.Arrays;
import java.util.UUID;

public class Utils {

    public static TrackReference initTrackTemplate(String playerName) {
        TrackParams track = Utils.trackTemplate(playerName);
        TrackReference trackReference = new TrackReference();
        trackReference.setData(track);
        trackReference.setGuid(track.getGuid());
        trackReference.setName(track.getName());

        Theme theme = Theme.defaultTheme();
        trackReference.setGameProperties(theme.getGameTheme().getProps());
        trackReference.setLeagueProperties(theme.getLeagueThemes().get(track.getLeague()).getProps());
        return trackReference;
    }

    public static TrackParams trackTemplate(String playerName) {
        TrackParams track = new Gson().fromJson("{\"author\":\"unnamed\",\"checkBackwardCollision\":true,\"checkFinishCoordinates\":true,\"finishPointIndex\":2,\"finishX\":7323648,\"finishY\":0,\"guid\":\"123\",\"invisible\":[],\"league\":0,\"name\":\"Unnamed\",\"points\":[[-4710400,-286720],[-4087808,-278528],[-3645440,-278528],[-3104768,-270336]],\"pointsCount\":4,\"startPointIndex\":1,\"startX\":-4374528,\"startY\":81920}", TrackParams.class);
        track.setAuthor(playerName);
        track.setGuid(UUID.randomUUID().toString());
        track.setName(track.getName());
        return track;
    }

    public static long calculateTimerTime(long startedTime, long finishedTime,
                                          long pausedTime, long currentTimeMillis) {
        if (startedTime > 0) {
            long finished;
            if (finishedTime > 0)
                finished = finishedTime;
            else
                finished = currentTimeMillis;
            return (finished - startedTime - pausedTime);
        }
        return 0;
    }

    public static void waitRestart(long delayedRestartAtTime, long currentTimeMillis) {
        try {
            long l2 = 1000L;
            if (delayedRestartAtTime > 0L)
                l2 = Math.min(delayedRestartAtTime - currentTimeMillis, 1000L);
            if (l2 > 0L)
                Thread.sleep(l2);
        } catch (InterruptedException ignored) {
        }
    }

    public static int[][] removeElement(int[][] arr, int removedIdx) {
        System.arraycopy(arr, removedIdx + 1, arr, removedIdx, arr.length - 1 - removedIdx);
        return Arrays.copyOf(arr, arr.length - 1);
    }

    public static int[][] addPos(int[][] a, int pos, int[] point) {
        int[][] result = new int[a.length + 1][2];
        for (int i = 0; i < pos; i++)
            result[i] = a[i];
        result[pos] = point;
        for (int i = pos + 1; i < a.length + 1; i++)
            result[i] = a[i - 1];
        return result;
    }

    public static int unpackInt(int i) {
        return (i << 16) >> 3;
    }

    public static int packInt(int i) {
        return (i << 3) >> 16;
    }

    public static String getDurationString(long millis) {
        return String.format("%d:%02d.%03d", millis / 60000, (millis / 1000) % 60, millis % 1000);
    }
}
