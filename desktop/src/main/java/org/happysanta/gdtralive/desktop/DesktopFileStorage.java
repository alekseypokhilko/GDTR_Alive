package org.happysanta.gdtralive.desktop;

import com.google.gson.Gson;

import org.happysanta.gdtralive.game.external.GdFileStorage;
import org.happysanta.gdtralive.game.levels.InvalidTrackException;
import org.happysanta.gdtralive.game.levels.PackTrackReference;
import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.mod.Mod;
import org.happysanta.gdtralive.game.mod.PackLevel;
import org.happysanta.gdtralive.game.mod.TrackReference;
import org.happysanta.gdtralive.game.recorder.TrackRecord;
import org.happysanta.gdtralive.game.storage.GDFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DesktopFileStorage implements GdFileStorage {
    private static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final String ASSETS_TRACKS_FOLDER = "tracks/";
    public static final String ASSETS_MODS_FOLDER = "mods/";

    public static final String APP_DIRECTORY = "GDAlive";

    @Override
    public TrackParams loadLevel(String guid) throws InvalidTrackException {
        return new TrackParams();
    }

    @Override
    public Mod loadMod(String filename) throws InvalidTrackException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream("mod.json")) {
            String content = readContent(inputStream);
            String ims = content.substring(content.indexOf(":") + 1);
            Mod mod = new Gson().fromJson(ims, Mod.class);
            validatePack(mod);
            return mod;
        } catch (IOException e) {
            throw new InvalidTrackException(e);
        }
    }

    private <T> T read(InputStream ims, Class<T> cls) {
        Gson gson = new Gson();
        java.io.Reader reader = new InputStreamReader(ims);
        return gson.fromJson(reader, cls);
    }

    private static void validateLevel(TrackParams track) throws InvalidTrackException {
        validateGuid(track.getGuid());
    }

    private static void validatePack(Mod mod) throws InvalidTrackException {
        validateGuid(mod.getGuid());
        for (PackLevel packLevel : mod.getLevels()) {
            for (TrackReference track : packLevel.getTracks()) {
                validateGuid(track.getGuid());
            }
        }
    }

    private static void validateGuid(String guid) throws InvalidTrackException {
        if (!UUID_REGEX.matcher(guid).matches()) {
            throw new InvalidTrackException("Invalid track guid");
        }
    }

    @Override
    public TrackParams getLevelFromPack(String packName, String trackGuid) throws InvalidTrackException {
        Mod mod = loadMod(packName);
        return mod.getLevels().stream()
                .map(PackLevel::getTracks)
                .flatMap(Collection::stream)
                .filter(tRef -> tRef.getGuid().equals(trackGuid))
                .findAny()
                .map(TrackReference::getData)
                .orElseThrow(() -> new InvalidTrackException("Level not found"));
    }

    @Override
    public <T> void writeToFile(T obj, GDFile fileType, String fileName) {
        String sanitizedName = fixFileName(fileName + "." + fileType.extension);
        //todo
        File file = createFileInDirectory(fileType.folder, sanitizedName);
        if (file != null) {
            try (PrintStream out = new PrintStream(file)) {
                String levelContent = fileType.extension + ":" + toJson(obj);
                out.print(levelContent);
            } catch (Exception e) {
                e.printStackTrace();
                //todo request permission
            }
        }
    }

    @Override
    public String readContent(InputStream inputStream) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            return buf.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T read(InputStream inputStream) {
        String content = readContent(inputStream);
        GDFile fileType = GDFile.getType(content);
        String ims = GDFile.cutHeader(content);
        return (T) new Gson().fromJson(ims, fileType.cls);
    }

    @Override
    public byte[] readAllBytes(InputStream in) {
        try {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addRecord(TrackRecord rec) {

    }

    @Override
    public List<TrackRecord> getAllRecords() {
        return null;
    }

    @Override
    public List<PackTrackReference> getDailyTracksReferences(String packName) throws InvalidTrackException {
        return null;
    }

    private File createFileInDirectory(String subDir, String fileName) {
        File appDirectory = createAppDirectoryInDownloads();
        File subDirectory = createSubFolderInAppDirectory(subDir);
        if (appDirectory == null) {
            return null;
        }

        try {
            return new File(subDirectory, fileName);
        } catch (Exception e) {
            return null;
        }
    }

    public static File createAppDirectoryInDownloads() {
        File downloadsDirectory = new File(URI.create(""));//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File appDirectory = new File(downloadsDirectory, APP_DIRECTORY);

        if (!appDirectory.exists()) {
            boolean directoryCreated = appDirectory.mkdir();
            if (!directoryCreated) {
                // Failed to create the directory
                return null;
            }
        }

        return appDirectory;
    }

    private static File createSubFolderInAppDirectory(String subDir) {
        File appDirectory = createAppDirectoryInDownloads();
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

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            fileName = fileName.replaceAll("[\u0001-\u001f<>\u202E:\"/\\\\|?*\u007f]+", "").trim();
        }
        return fileName;
    }

    private static <T> String toJson(T obj) {
        return new Gson().toJson(obj);
    }
}
