package org.happysanta.gdtralive.game.external;

import org.happysanta.gdtralive.game.levels.InvalidTrackException;
import org.happysanta.gdtralive.game.levels.PackTrackReference;
import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.mod.Mod;
import org.happysanta.gdtralive.game.recorder.TrackRecord;
import org.happysanta.gdtralive.game.storage.GDFile;

import java.io.InputStream;
import java.util.List;

public interface GdFileStorage {
    TrackParams loadLevel(String guid) throws InvalidTrackException;

    Mod loadMod(String filename) throws InvalidTrackException;

    TrackParams getLevelFromPack(String packName, String trackGuid) throws InvalidTrackException;

    <T> void writeToFile(T obj, GDFile fileType, String fileName);

    String readContent(InputStream inputStream);

    <T> T read(InputStream inputStream);

    byte[] readAllBytes(InputStream in);

    void addRecord(TrackRecord rec);
    List<TrackRecord> getAllRecords();

    List<PackTrackReference> getDailyTracksReferences(String packName) throws InvalidTrackException;
}
