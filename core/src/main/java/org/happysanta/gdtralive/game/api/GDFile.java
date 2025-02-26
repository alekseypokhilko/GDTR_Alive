package org.happysanta.gdtralive.game.api;

import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackReference;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

public enum GDFile {
    UNDEFINED("", "", null, Object.class),
    THEME("gdtheme", "themes", Fmt.slash(Constants.APP_DIRECTORY, "themes"), Theme.class),
    MOD("gdmod", "mods", Fmt.slash(Constants.APP_DIRECTORY, "mods"), Mod.class),
    MRG("mrg", "mrg", Fmt.slash(Constants.APP_DIRECTORY, "mrg"), Object.class),
    TRACK("gdtrack", "tracks", Fmt.slash(Constants.APP_DIRECTORY, "tracks"), TrackReference.class),
    RECORD("gdrecord", "recordings", Fmt.slash(Constants.APP_DIRECTORY, "recordings"), TrackRecord.class);

    public final String extension;
    public final String folder;
    public final String appFolder;
    public final Class cls;

    GDFile(String extension, String folder, String appFolder, Class cls) {
        this.extension = extension;
        this.folder = folder;
        this.appFolder = appFolder;
        this.cls = cls;
    }

    public static GDFile getType(String content) {
        String header = content.substring(0, content.indexOf(":"));
        for (GDFile value : values()) {
            if (value.extension.equals(header)) {
                return value;
            }
        }
        return UNDEFINED;
    }

    public static GDFile getTypeFromExtension(String extension) {
        for (GDFile value : values()) {
            if (value.extension.equals(extension)) {
                return value;
            }
        }
        return UNDEFINED;
    }

    public static String cutHeader(String content) {
        return content.substring(content.indexOf(":") + 1);
    }

    public static <T> String addHeader(T obj, GDFile fileType) {
        return Fmt.colonNoSpace(fileType.extension, Utils.toJson(obj));
    }

    public String addExtension(String name) {
        return Fmt.dot(name, this.extension);
    }
}
