package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.dto.PackTrackReference;
import org.happysanta.gdtralive.game.api.model.TrackParams;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.api.GDFile;

import java.util.List;

public interface GdFileStorage {
    String getModsFolder();

    String getTracksFolder();

    TrackParams loadLevel(String guid) throws InvalidTrackException;

    Mod loadMod(String filename) throws InvalidTrackException;

    TrackParams getLevelFromPack(String packName, String trackGuid) throws InvalidTrackException;

    <T> void writeToFile(T obj, GDFile fileType, String fileName);

    void addRecord(TrackRecord rec);
    List<TrackRecord> getAllRecords();

    List<PackTrackReference> getDailyTracksReferences(String packName) throws InvalidTrackException;
}
