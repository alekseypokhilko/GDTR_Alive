package org.happysanta.gdtralive.game.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.PackLevel;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackReference;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.TrackParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utils {
    public static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    public static final String TRACK_TEMPLATE = "{\"author\":\"unnamed\",\"checkBackwardCollision\":true,\"checkFinishCoordinates\":true,\"finishPointIndex\":2,\"finishX\":7323648,\"finishY\":0,\"guid\":\"123\",\"invisible\":[],\"league\":0,\"name\":\"Unnamed\",\"points\":[[-4710400,-286720],[-4087808,-278528],[-3645440,-278528],[-3104768,-270336]],\"pointsCount\":4,\"startPointIndex\":1,\"startX\":-4374528,\"startY\":81920}";
    private static final Gson GSON = new Gson();
    private static final TypeToken<List<Integer>> INT_LIST_TYPE = new TypeToken<List<Integer>>() {
    };

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
        TrackParams track = new Gson().fromJson(TRACK_TEMPLATE, TrackParams.class);
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

    public static int getRandom(int bound) {
        return new Random().nextInt(bound);
    }

    public static int[][] copyArray(int[][] original) {
        return Arrays.copyOf(original, original.length);
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

    public static byte[] readAllBytes(InputStream in) {
        try {
            byte[] bytes = new byte[5242880]; //todo read length from file
            in.read(bytes);
            in.close();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e); //todo handle
        }
    }

    public static String readContent(InputStream inputStream) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            return buf.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null; //todo handle NPE
        }
    }

    public static <T> T read(InputStream inputStream) {
        String content = readContent(inputStream);
        GDFile fileType = GDFile.getType(content);
        String ims = GDFile.cutHeader(content);
        return (T) GSON.fromJson(ims, fileType.cls);
    }

    public static List<Integer> parseIntList(String counts) {
        return GSON.fromJson(counts, INT_LIST_TYPE);
    }

    public static <T> String toJson(T obj) {
        return GSON.toJson(obj);
    }
    public static <T> T fromJson(String ims, Class<T> classOfT) {
        return GSON.fromJson(ims, classOfT);
    }

    public static <T> T fromJson(String ims, GDFile gdFile) {
        return (T) GSON.fromJson(ims, gdFile.cls);
    }

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            fileName = fileName.replaceAll("[\u0001-\u001f<>\u202E:\"/\\\\|?*\u007f]+", "").trim();
        }
        return fileName;
    }

    public static void validateLevel(TrackParams track) throws InvalidTrackException {
        validateGuid(track.getGuid());
    }

    public static void validatePack(Mod mod) throws InvalidTrackException {
        if (mod == null) {
            throw new InvalidTrackException("Mod not found");
        }
        validateGuid(mod.getGuid());
        for (PackLevel packLevel : mod.getLevels()) {
            for (TrackReference track : packLevel.getTracks()) {
                validateGuid(track.getGuid());
            }
        }
    }

    public static void validateGuid(String guid) throws InvalidTrackException {
        if (!UUID_REGEX.matcher(guid).matches()) {
            throw new InvalidTrackException("Invalid track guid");
        }
    }

    public static File createFileInDirectory(File baseDirectory, String subDir, String fileName) {
        File appDirectory = createAppDirectory(baseDirectory);
        File subDirectory = createSubFolderInAppDirectory(baseDirectory, subDir);
        if (appDirectory == null) {
            return null;
        }

        try {
            return new File(subDirectory, fileName);
        } catch (Exception e) {
            return null;
        }
    }

    private static File createAppDirectory(File baseDirectory) {
        File appDirectory = new File(baseDirectory, Constants.APP_DIRECTORY);

        if (!appDirectory.exists()) {
            boolean directoryCreated = appDirectory.mkdir();
            if (!directoryCreated) {
                // Failed to create the directory
                return null;
            }
        }

        return appDirectory;
    }

    private static File createSubFolderInAppDirectory(File baseDirectory, String subDir) {
        File appDirectory = createAppDirectory(baseDirectory);
        if (appDirectory == null) {
            return null;
        }

        File subDirectory = new File(appDirectory, subDir);
        if (!subDirectory.exists()) {
            boolean directoryCreated = subDirectory.mkdir();
            if (!directoryCreated) {
                // Failed to create the directory
                return null;
            }
        }
        return subDirectory;
    }

    public static boolean isEmpty(String text) {
        return text.isEmpty();
    }
}
