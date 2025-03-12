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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractFileStorage implements GdFileStorage {
    private static final Set<GDFile> ZIPPED;

    static {
        ZIPPED = new HashSet<>();
        ZIPPED.add(GDFile.RECORD);
        ZIPPED.add(GDFile.MOD);
    }

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
    public File getFile(GDFile gdFile, String name) {
        return new File(Fmt.slash(appFolder.getAbsolutePath(), gdFile.folder), gdFile.addExtension(name));
    }

    @Override
    public Mod readMod(String name) {
        GDFile gdFile = GDFile.MOD;
        try {
            InputStream inputStream = fromAssets(gdFile.folder, gdFile.addExtension(name));
            byte[] res = Utils.unzip(inputStream);
            if (res != null && res.length != 0) {
                return Utils.fromJson(new String(res, StandardCharsets.UTF_8), gdFile);
            }
        } catch (IOException e) {
        }
        return unzip(name, gdFile);
    }

    @Override
    public Theme readTheme(String name) {
        return read(name, GDFile.THEME);
    }

    @Override
    public TrackRecord readRecord(String name) {
        return unzip(name, GDFile.RECORD);
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
            String content = Utils.toJson(obj);
            if (ZIPPED.contains(fileType)) {
                Utils.writeZip(file, content.getBytes(StandardCharsets.UTF_8));
            } else {
                try (PrintStream out = new PrintStream(file)) {
                    out.print(content);
                    out.flush();
                    out.close();
                    notify("Saved");
                } catch (Exception e) {
                    notify("Error: " + e.getMessage());
                }
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

    private <T> T read(String name, GDFile gdFile) {
        try {
            InputStream inputStream = fromAssets(gdFile.folder, gdFile.addExtension(name));
            if (inputStream != null) {
                return (T) Utils.read(inputStream, gdFile);
            }
        } catch (IOException e) {
        }
        String modsFolderPath = folders.get(gdFile).getAbsolutePath();
        try (InputStream inputStream = new FileInputStream(new File(Fmt.slash(modsFolderPath, gdFile.addExtension(name))))) {
            return Utils.fromJson(Utils.readContent(inputStream), gdFile);
        } catch (IOException e) {
            e.printStackTrace();
            notify("Error: " + e.getMessage());
        }
        return null;
    }

    private void notify(String message) {
        if (application != null) {
            application.notify(message);
        }
    }

    private <T> T unzip(String name, GDFile gdFile) {
        String folderPath = folders.get(gdFile).getAbsolutePath();
        try {
            byte[] res = Utils.unzip(new File(Fmt.slash(folderPath, gdFile.addExtension(name))));
            if (res == null || res.length == 0) {
                return read(name, gdFile);
            } else {
                return Utils.fromJson(new String(res, StandardCharsets.UTF_8), gdFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return read(name, gdFile);
        }
    }

    protected abstract String[] listAssets(GDFile fileType) throws IOException;

    protected abstract InputStream fromAssets(String folder, String name) throws IOException;
}
