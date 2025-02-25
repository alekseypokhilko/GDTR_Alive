package org.happysanta.gdtralive.desktop;

import com.google.gson.Gson;

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

import java.io.IOException;
import java.io.InputStream;
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
            String content = Utils.readContent(inputStream);
            String ims = content.substring(content.indexOf(":") + 1);
            Mod mod = new Gson().fromJson(ims, Mod.class);
            validatePack(mod);
            return mod;
        } catch (IOException e) {
            throw new InvalidTrackException(e);
        }
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
        ///todo
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

    @Override
    public String getModsFolder() {
        return null;
    }

    @Override
    public String getTracksFolder() {
        return null;
    }
}
