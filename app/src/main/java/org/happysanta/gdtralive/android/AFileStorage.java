package org.happysanta.gdtralive.android;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AFileStorage implements GdFileStorage {
    public List<TrackRecord> records = new ArrayList<>();

    // /storage/emulated/0/Android/data/org.happysanta.gdtralive/files/GDTR_Alive
    private final File appFolder;
    Map<GDFile, File> folders = new HashMap<>();

    public AFileStorage(GDActivity application) {
        folders.put(GDFile.MOD, application.getExternalFilesDir(GDFile.MOD.appFolder));
        folders.put(GDFile.THEME, application.getExternalFilesDir(GDFile.THEME.appFolder));
        folders.put(GDFile.TRACK, application.getExternalFilesDir(GDFile.TRACK.appFolder));
        folders.put(GDFile.RECORD, application.getExternalFilesDir(GDFile.RECORD.appFolder));
        this.appFolder = application.getExternalFilesDir(Constants.APP_DIRECTORY);
    }

    @Override
    public Mod readMod(String name) {
        return read(name, GDFile.MOD);
    }

    @Override
    public Theme readTheme(String name) {
        return read(name, GDFile.THEME);
    }

    @Override
    public TrackRecord readRecord(String name) {
        return read(name, GDFile.RECORD);
    }

    @Override
    public TrackRecord readTrack(String name) {
        return read(name, GDFile.TRACK);
    }

    @Override
    public List<String> listFiles(GDFile fileType) {
        List<String> files = new ArrayList<>();
        try {
            files.addAll(Arrays.asList(Objects.requireNonNull(GDActivity.shared.getAssets().list(fileType.folder))));
        } catch (Exception ignore) {
        }
        try {
            for (File file : Objects.requireNonNull(folders.get(fileType).listFiles())) {
                files.add(file.getName());
            }
        } catch (Exception ignore) {
        }
        return files;
    }

    @Override
    public <T> void save(T obj, GDFile fileType, String fileName) {
        String sanitizedName = Utils.fixFileName(Fmt.dot(fileName, fileType.extension));
        File file = new File(Fmt.slash(appFolder.getAbsolutePath(), fileType.folder), sanitizedName);
        if (file != null) {
            try (PrintStream out = new PrintStream(file)) {
                String content = GDFile.addHeader(obj, fileType);
                out.print(content);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void delete(GDFile gdFile, String name) {
        try {
            new File(Fmt.slash(appFolder.getAbsolutePath(), gdFile.folder), gdFile.addExtension(name)).delete();
        } catch (Exception ignore) {
        }
    }

    @Override
    public void addRecord(TrackRecord rec) { //todo
        records.add(rec);
    }

    @Override
    public List<TrackRecord> getAllRecords() { //todo
        return records;
    }

    private <T> T read(String name, GDFile gdFile) {
        try {
            return (T) Utils.read(fromAssets(gdFile.folder, name));
        } catch (IOException e) {
        }
        String modsFolderPath = folders.get(gdFile).getAbsolutePath();
        try (InputStream inputStream = new FileInputStream(new File(Fmt.slash(modsFolderPath, name)))) {
            return (T) Utils.fromJson(GDFile.cutHeader(Utils.readContent(inputStream)), gdFile);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    private InputStream fromAssets(String folder, String name) throws IOException {
        return GDActivity.shared.getAssets().open(Fmt.slash(folder, name));
    }
}
