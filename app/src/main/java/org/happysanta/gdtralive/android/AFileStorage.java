package org.happysanta.gdtralive.android;

import org.happysanta.gdtralive.game.AbstractFileStorage;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.util.Fmt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public class AFileStorage extends AbstractFileStorage {

    public AFileStorage(File appFolder, Map<GDFile, File> folders) {
        super(appFolder, folders);
    }

    @Override
    protected String[] listAssets(GDFile fileType) throws IOException {
        return Objects.requireNonNull(GDActivity.shared.getAssets().list(fileType.folder));
    }

    @Override
    protected InputStream fromAssets(String folder, String name) throws IOException {
        return GDActivity.shared.getAssets().open(Fmt.slash(folder, name));
    }
}
