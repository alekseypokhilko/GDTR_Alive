package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.AbstractFileStorage;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DesktopFileStorage extends AbstractFileStorage {

    public DesktopFileStorage(File appFolder, Map<GDFile, File> folders) {
        super(appFolder, folders);
    }

    @Override
    public Mod readMod(String name) { //todo
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream("mod.gdmod")) {
            String content = Utils.readContent(inputStream);
            return Utils.fromJson(content, Mod.class);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public <T> void save(T obj, GDFile fileType, String fileName) {
        //todo
    }

    @Override
    protected String[] listAssets(GDFile fileType) throws IOException {
        return new String[0];
    }

    @Override
    protected InputStream fromAssets(String folder, String name) throws IOException {
        return null;
    }
}
