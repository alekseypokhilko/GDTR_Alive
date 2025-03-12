package org.happysanta.gdtralive.game.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.LeagueTheme;
import org.happysanta.gdtralive.game.api.dto.LevelPack;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.model.ElementRecord;
import org.happysanta.gdtralive.game.api.model.IElement;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.TrackData;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Utils {
    public static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    public static final String TRACK_TEMPLATE = "{\"author\":\"unnamed\",\"checkBackwardCollision\":true,\"checkFinishCoordinates\":true,\"finishPointIndex\":2,\"finishX\":7323648,\"finishY\":0,\"guid\":\"123\",\"invisible\":[],\"league\":0,\"name\":\"Unnamed\",\"points\":[[-4710400,-286720],[-4087808,-278528],[-3645440,-278528],[-3104768,-270336]],\"pointsCount\":4,\"startPointIndex\":1,\"startX\":-4374528,\"startY\":81920}";
    private static final Gson GSON;
    private static final TypeToken<List<Integer>> INT_LIST_TYPE = new TypeToken<List<Integer>>() {
    };

    static {
        class IElementJsonAdapter implements JsonDeserializer<IElement> {
            @Override
            public IElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return context.deserialize(json, ElementRecord.class);
            }
        }

        GSON = new GsonBuilder().registerTypeAdapter(IElement.class, new IElementJsonAdapter())
                .create();
    }

    public static TrackParams initTrackTemplate(String playerName) {
        TrackData track = Utils.trackTemplate(playerName);
        TrackParams trackParams = new TrackParams();
        trackParams.setData(track);
        trackParams.getData().setGuid(track.getGuid());
        trackParams.getData().setName(track.getName());

        Theme theme = Theme.defaultTheme();
        trackParams.setGameTheme(theme.getGameTheme());
        trackParams.setLeagueTheme(theme.getLeagueThemes().get(track.getLeague()));
        return trackParams;
    }

    public static TrackData trackTemplate(String playerName) {
        TrackData track = new Gson().fromJson(TRACK_TEMPLATE, TrackData.class);
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

    public static <T> T read(InputStream inputStream, GDFile fileType) {
        String content = readContent(inputStream);
        return (T) GSON.fromJson(content, fileType.cls);
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

    public static void validateLevel(TrackData track) throws InvalidTrackException {
        validateGuid(track.getGuid());
    }

    public static void validateMod(Mod mod) throws InvalidTrackException {
        if (mod == null) {
            throw new InvalidTrackException("Mod not found");
        }
        validateGuid(mod.getGuid());
        for (LevelPack levelPack : mod.getLevels()) {
            for (TrackParams track : levelPack.getTracks()) {
                validateGuid(track.getData().getGuid());
            }
        }
    }

    public static void validateGuid(String guid) throws InvalidTrackException {
        if (!UUID_REGEX.matcher(guid).matches()) {
            throw new InvalidTrackException("Invalid guid");
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

    public static String[] getLeagueNames(List<LeagueTheme> leagueThemes) {
        String[] names = new String[leagueThemes.size()];
        for (int i = 0; i < leagueThemes.size(); i++) {
            names[i] = leagueThemes.get(i).getName();
        }
        return names;
    }

    public static String[] getLevelTrackNames(List<TrackParams> tracks) {
        List<String> names = new ArrayList<>();
        for (TrackParams track : tracks) {
            names.add(track.getData().getName());
        }
        return names.toArray(new String[0]);
    }

    public static Mod packMod(Mod fromMod) {
        Mod mod = new Mod();
        mod.setGameTheme(fromMod.getGameTheme());
        mod.setLeagueThemes(fromMod.getLeagueThemes());
        mod.setLevelNames(fromMod.getLevelNames());
        mod.setAuthor(fromMod.getAuthor());
        mod.setGuid(fromMod.getGuid());
        mod.setName(fromMod.getName());
        mod.setDate(fromMod.getDate());
        List<LevelPack> levelPacks = new ArrayList<>();
        for (LevelPack level : fromMod.getLevels()) {
            LevelPack levelPack = new LevelPack();
            List<TrackParams> tracks = new ArrayList<>();
            for (TrackParams track : level.getTracks()) {
                TrackData from = track.getData();

                TrackData data = new TrackData();
                data.setName(from.getName());
                data.setGuid(from.getGuid());
                data.setAuthor(from.getAuthor());
                data.setLeague(from.getLeague());
                data.setStartX(Utils.packInt(from.getStartX()));
                data.setStartY(Utils.packInt(from.getStartY()));
                data.setFinishX(Utils.packInt(from.getFinishX()));
                data.setFinishY(Utils.packInt(from.getFinishY()));
                data.setStartPointIndex(from.getStartPointIndex());
                data.setFinishPointIndex(from.getFinishPointIndex());
                data.setPointsCount(from.getPointsCount());
                data.setCheckBackwardCollision(from.isCheckBackwardCollision());
                data.setInvisible(from.getInvisible());
                data.setDeadlineY(from.getDeadlineY());
                data.setCheckFinishCoordinates(from.isCheckFinishCoordinates());

                int[][] pointsFrom = from.getPoints();
                int[][] points = new int[pointsFrom.length][2];
                for (int i = 0; i < pointsFrom.length; i++) {
                    int[] X_Y = new int[2];
                    X_Y[0] = Utils.packInt(pointsFrom[i][0]);
                    X_Y[1] = Utils.packInt(pointsFrom[i][1]);
                    points[i] = X_Y;
                }
                data.setPoints(points);

                TrackParams tr = new TrackParams();
                tr.setGameTheme(track.getGameTheme());
                tr.setLeagueTheme(track.getLeagueTheme());
                tr.setData(data);
                tracks.add(tr);
            }
            levelPack.setTracks(tracks);
            levelPacks.add(levelPack);
        }
        mod.setLevels(levelPacks);
        return mod;
    }

    public static Mod unpackMod(Mod fromMod) {
        for (LevelPack level : fromMod.getLevels()) {
            for (TrackParams track : level.getTracks()) {
                TrackData data = track.getData();
                data.setStartX(Utils.unpackInt(data.getStartX()));
                data.setStartY(Utils.unpackInt(data.getStartY()));
                data.setFinishX(Utils.unpackInt(data.getFinishX()));
                data.setFinishY(Utils.unpackInt(data.getFinishY()));
                int[][] pointsFrom = data.getPoints();
                int[][] points = new int[pointsFrom.length][2];
                for (int i = 0; i < pointsFrom.length; i++) {
                    try {
                        int[] X_Y = new int[2];
                        X_Y[0] = Utils.unpackInt(pointsFrom[i][0]);
                        X_Y[1] = Utils.unpackInt(pointsFrom[i][1]);
                        points[i] = X_Y;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                data.setPoints(points);
            }
        }
        return fromMod;
    }

    public static String[] getScaleOptions() {
        String[] scaleOptions = new String[401];
        for (int i = 0; i < scaleOptions.length; i++) {
            scaleOptions[i] = "" + i;
        }
        return scaleOptions;
    }

    public static String md5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : md.digest()) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTrackId(TrackData track) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] point : track.getPoints()) {
            stringBuilder.append(Arrays.toString(point));
        }
        return md5(stringBuilder.toString());
    }

    public static void writeZip(File file, byte[] content) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                zipOut.setLevel(ZipOutputStream.DEFLATED);
                ZipEntry zipEntry = new ZipEntry("content.json");
                zipOut.putNextEntry(zipEntry);
                zipOut.write(content, 0, content.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] unzip(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return unzip(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] unzip(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            zis.getNextEntry();
            byte[] buff = new byte[1024];
            int l;
            while ((l = zis.read(buff)) > 0) {
                outputStream.write(buff, 0, l);
            }
            zis.closeEntry();
        }
        return outputStream.toByteArray();
    }
}
