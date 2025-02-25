package org.happysanta.gdtralive.game.api;

import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackReference;
import org.happysanta.gdtralive.game.api.model.TrackRecord;

public enum GDFile {
    UNDEFINED("", "", Object.class),
    THEME("gdtheme", "themes", Theme.class),
    MOD("gdmod", "mods", Mod.class),
    MRG("mrg", "mrg", Object.class),
    TRACK("gdtrack", "tracks", TrackReference.class),
    RECORD("gdrecord", "recordings", TrackRecord.class);

    public final String extension;
    public final String folder;
    public final Class cls;

    GDFile(String extension, String folder, Class cls) {
        this.extension = extension;
        this.folder = folder;
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
}
