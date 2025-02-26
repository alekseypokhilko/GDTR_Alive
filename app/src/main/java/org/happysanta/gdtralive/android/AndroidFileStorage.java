package org.happysanta.gdtralive.android;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;

import com.google.gson.Gson;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.external.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.dto.PackTrackReference;
import org.happysanta.gdtralive.game.api.model.TrackParams;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.dto.PackLevel;
import org.happysanta.gdtralive.game.api.dto.TrackReference;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.util.Utils;
import org.happysanta.gdtralive.game.util.Fmt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AndroidFileStorage implements GdFileStorage {
    public static final String ASSETS_TRACKS_FOLDER = "tracks/";
    public static final String ASSETS_MODS_FOLDER = "mods/";

    public List<TrackRecord> records = new ArrayList<>();

    private final GdApplication application;
    private final File baseDirectory;
    private final String modsFolder;
    private final String tracksFolder;

    public AndroidFileStorage(GdApplication application) {
        this.application = application;
        //todo buildExternalStorageAppDataDirs()
        this.baseDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        this.modsFolder = this.baseDirectory + "/" + Constants.APP_DIRECTORY + "/" + GDFile.MOD.folder;
        this.tracksFolder = this.baseDirectory + "/" + Constants.APP_DIRECTORY + "/" + GDFile.TRACK.folder;
    }

    @Override
    public String getModsFolder() {
        return modsFolder;
    }

    @Override
    public String getTracksFolder() {
        return tracksFolder;
    }

    @Override
    public TrackParams loadLevel(String guid) throws InvalidTrackException {
        Utils.validateGuid(guid);
        try {
            TrackParams track = Utils.read(fromAssets(ASSETS_TRACKS_FOLDER, guid), TrackParams.class);
            Utils.validateLevel(track);
            return track;
        } catch (IOException e) {
            try {
                TrackParams track = Utils.read(fromExternalStorage(tracksFolder, guid + Constants.JSON), TrackParams.class);
                Utils.validateLevel(track);
                return track;
            } catch (IOException io) {
                throw new InvalidTrackException(e);
            }
        }
    }

    @Override
    public Mod loadMod(String filename) throws InvalidTrackException {
        try {
            Mod mod = Utils.read(fromAssets(ASSETS_MODS_FOLDER, filename), Mod.class);
            Utils.validatePack(mod);
            return mod;
        } catch (IOException e) {
            try (InputStream inputStream = new FileInputStream(new File(modsFolder + filename))) {
                String content = Utils.readContent(inputStream);
                String ims = content.substring(content.indexOf(":") + 1);
                Mod mod = new Gson().fromJson(ims, Mod.class);
                Utils.validatePack(mod);
                return mod;
            } catch (IOException io) {
                throw new InvalidTrackException(e);
            }
        }
    }

    public static InputStream fromAssets(String folder, String guid) throws IOException {
        new File(Environment.getExternalStorageDirectory(), folder).mkdirs();
        return GDActivity.shared.getAssets().open(folder + guid + Constants.JSON);
    }

    public static InputStream fromExternalStorage(String folder, String filename) throws IOException {
        new File(Environment.getExternalStorageDirectory(), folder).mkdirs();
        return new FileInputStream(new File(folder + "/" + filename));
    }

    public static OutputStream toExternalStorage(String folder, String guid) throws IOException {
        new File(Environment.getExternalStorageDirectory(), folder).mkdirs();
        return new FileOutputStream(new File(
                Environment.getExternalStorageDirectory(), folder + "/" + guid + Constants.JSON));
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N) //todo lower version
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
        String sanitizedName = Utils.fixFileName(Fmt.dot(fileName, fileType.extension));
        File file = Utils.createFileInDirectory(baseDirectory, fileType.folder, sanitizedName);
        if (file != null) {
            try (PrintStream out = new PrintStream(file)) {
                String content = Fmt.colonNoSpace(fileType.extension, Utils.toJson(obj));
                out.print(content);
                application.notify("Saved"); //todo string
            } catch (Exception e) {
                //todo request permission
                application.notify(e.getMessage()); //todo string
            }
        }
    }

    @Override
    public void addRecord(TrackRecord rec) {
        records.add(rec);
    }

    @Override
    public List<TrackRecord> getAllRecords() {
        return records;
    }

    @TargetApi(Build.VERSION_CODES.N) //todo lower version
    public List<PackTrackReference> getDailyTracksReferences(String packName) throws InvalidTrackException {
        Mod mod = loadMod(packName);
        return mod.getLevels().stream()
                .map(PackLevel::getTracks)
                .flatMap(Collection::stream)
                .map(ref -> new PackTrackReference(ref.getGuid(), ref.getName(), packName))
                .collect(Collectors.toList());
    }
}
