package org.happysanta.gdtralive.game.api.external;

import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.TrackRecord;

import java.io.File;
import java.util.List;

public interface GdFileStorage {
    Mod readMod(String name);

    Theme readTheme(String name);

    TrackRecord readRecord(String name);

    TrackParams readTrack(String name);

    List<String> listFiles(GDFile fileType);

    <T> void save(T obj, GDFile fileType, String fileName);

    void delete(GDFile gdFile, String name);

    void setApplication(Application application);

    File getFile(GDFile gdFile, String name);
}
