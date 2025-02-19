package org.happysanta.gdtralive.game.levels;

public class PackTrackReference {
    private String guid;
    private String name;
    private String packGuid;

    public PackTrackReference(String guid, String name, String packGuid) {
        this.guid = guid;
        this.name = name;
        this.packGuid = packGuid;
    }

    public PackTrackReference() {
    }

    public String getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public String getPackGuid() {
        return packGuid;
    }
}
