package org.happysanta.gdtralive.game;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractFileStorage implements GdFileStorage {
    protected List<TrackRecord> records = new ArrayList<>();

    // /storage/emulated/0/Android/data/org.happysanta.gdtralive/files/GDTR_Alive
    protected Application application;
    protected final File appFolder;
    Map<GDFile, File> folders;

    public AbstractFileStorage(File appFolder, Map<GDFile, File> folders) {
        this.appFolder = appFolder;
        this.folders = folders;
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
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
            files.addAll(Arrays.asList(listAssets(fileType)));
        } catch (Exception e) {
            notify("Error: " + e.getMessage());
        }
        try {
            for (File file : Objects.requireNonNull(folders.get(fileType).listFiles())) {
                files.add(file.getName());
            }
        } catch (Exception e) {
            notify("Error: " + e.getMessage());
        }
        return files;
    }

    @Override
    public <T> void save(T obj, GDFile fileType, String fileName) {
        String sanitizedName = Utils.fixFileName(Fmt.dot(fileName, fileType.extension));
        File file = new File(Fmt.slash(appFolder.getAbsolutePath(), fileType.folder), sanitizedName);
        if (file != null) {
            try (PrintStream out = new PrintStream(file)) {
                String content = Utils.toJson(obj);
                out.print(content);
                out.flush();
                out.close();
                notify("Saved");
            } catch (Exception e) {
                notify("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void delete(GDFile gdFile, String name) {
        try {
            boolean deleted = new File(Fmt.slash(appFolder.getAbsolutePath(), gdFile.folder), gdFile.addExtension(name)).delete();
            if (deleted) {
                notify("Deleted");
            }
        } catch (Exception e) {
            notify("Error: " + e.getMessage());
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
            return (T) Utils.read(fromAssets(gdFile.folder, gdFile.addExtension(name)), gdFile);
        } catch (IOException e) {
        }
        String modsFolderPath = folders.get(gdFile).getAbsolutePath();
        try (InputStream inputStream = new FileInputStream(new File(Fmt.slash(modsFolderPath, gdFile.addExtension(name))))) {
            return Utils.fromJson(Utils.readContent(inputStream), gdFile);
        } catch (IOException e) {
            e.printStackTrace();
            notify("Error: " + e.getMessage());
        }
        notify("Not found");
        return null;
    }

    private void notify(String message) {
        if (application != null) {
            application.notify(message);
        }
    }

    protected abstract String[] listAssets(GDFile fileType) throws IOException;

    protected abstract InputStream fromAssets(String folder, String name) throws IOException;
}
