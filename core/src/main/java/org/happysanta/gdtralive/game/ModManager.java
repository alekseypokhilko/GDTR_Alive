package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.GameTheme;
import org.happysanta.gdtralive.game.api.dto.InterfaceTheme;
import org.happysanta.gdtralive.game.api.dto.LeaguePropertiesTheme;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackReference;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.external.GdApplication;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.TrackParams;
import org.happysanta.gdtralive.game.api.model.TrackProperties;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Hot reload mods, themes, sprites, leagues
 */
public class ModManager {

    private final List<Runnable> themeReloadHandlers = new ArrayList<>();

    private Theme theme;
    private Mod currentMod;
    private ModEntity modState;
    boolean temporallyUnlockedAll = false;
    private TrackProperties currentTrackProperties;
    private final float defaultDensity;
    private final GdApplication application;
    private final GdDataSource dataSource;
    private final GdFileStorage fileStorage;

    public ModManager(GdApplication application, GdFileStorage fileStorage, float defaultDensity) {
        this.application = application;
        this.defaultDensity = defaultDensity;
        this.fileStorage = fileStorage;
        this.dataSource = application.getDataSource();
        this.theme = loadTheme(application.getSettings().getSelectedTheme());
        try {
            dataSource.open();
            if (!dataSource.isDefaultLevelCreated()) {
                this.currentMod = loadMod(Constants.DEFAULT_MOD);
                List<Integer> counts = this.currentMod.getTrackCounts();
                long now = System.currentTimeMillis();
                this.modState = dataSource.createLevel(
                        currentMod.getGuid(), currentMod.getName(), currentMod.getAuthor(),
                        counts, now, now, true, 1
                );
                application.getSettings().setLevelId(modState.getId());
            }
            this.modState = dataSource.getLevel(application.getSettings().getLevelId());
        } catch (Exception ignore) {
        }
        try {
            if (currentMod == null) {
                currentMod = loadMod(Constants.DEFAULT_MOD);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Mod getMod() {
        return currentMod;
    }

    public void setTrackProperties(TrackReference track) {
        if (track == null) {
            this.currentTrackProperties = null;
            return;
        }

        TrackProperties trackProperties = new TrackProperties();
        if (track.getGameProperties() != null) {
            trackProperties.setGameProperties(new GameTheme(track.getGameProperties()));
        }
        if (track.getLeagueProperties() != null) {
            trackProperties.setLeagueProperties(new LeaguePropertiesTheme(track.getLeagueProperties()));
        }
        if (trackProperties.getGameProperties() != null || trackProperties.getLeagueProperties() != null) {
            this.currentTrackProperties = trackProperties;
        }
    }

    public void activateMod(Mod mod) {
        temporallyUnlockedAll = true; //todo switch between mods
        currentMod = mod;
    }

    public List<LeaguePropertiesTheme> getLeagueThemes() {
        return theme.getLeagueThemes();
    }

    public ModEntity getModState() {
        return modState;
    }

    public void saveModState() {
        if (temporallyUnlockedAll) {
            return;
        }
        dataSource.updateLevel(modState);
    }

    public void setTemporallyUnlockedAll(boolean temporallyUnlockedAll) {
        this.temporallyUnlockedAll = temporallyUnlockedAll;
    }

    public LeaguePropertiesTheme getLeagueTheme(int league) {
        if (currentTrackProperties != null && currentTrackProperties.getLeagueProperties() != null) {
            return currentTrackProperties.getLeagueProperties();
        }
        return getLeagueThemes().get(league);
    }

    public List<String> getLevelNames() {
        return theme.getLevelNames();
    }

    public GameTheme getGameTheme() {
        if (currentTrackProperties != null && currentTrackProperties.getGameProperties() != null) {
            return currentTrackProperties.getGameProperties();
        }
        return theme.getGameTheme();
    }

    public InterfaceTheme getInterfaceTheme() {
        return theme.getInterfaceTheme();
    }

    public String[] getLeagueNames() {
        return theme.getLeagueNames();
    }

    public String[] getLeagueTrackNames(int league) {
        return currentMod.getLevelTrackNames(league);
    }

    public void installTheme(String name) {
        this.theme = loadTheme(name);
        adjustScale(this.theme);
        application.getSettings().setSelectedTheme(theme.getHeader().getName());
        reloadTheme();
    }

    public void adjustScale(Theme theme) {
        if (theme == null) {
            theme = this.theme;
        }
        int scale = application.getSettings().getScale();
        theme.getGameTheme().setProp(GameTheme.density, defaultDensity * scale / 100);
        theme.getGameTheme().setProp(GameTheme.spriteDensity, defaultDensity);
    }

    public void registerThemeReloadHandler(Runnable handler) {
        //todo fix memory leak
        //elements does not removed after view deletion
        themeReloadHandlers.add(handler);
    }

    public void reloadTheme() {
        for (Runnable handler : themeReloadHandlers) {
            if (handler != null) {
                try {
                    handler.run();
                } catch (Exception ignore) {
                }
            }
        }
    }

    public Theme loadTheme(String name) {
        Theme theme;
        if ("GDTR Original".equals(name)) {
            theme = Theme.defaultTheme();
        } else if ("GDTR Black".equals(name)) {
            theme = Theme.amoledMod();
        } else {
            Theme fromStorage = fileStorage.readTheme(name);
            theme = fromStorage == null ? Theme.defaultTheme() : fromStorage;
        }
        adjustScale(theme);
        return theme;
    }

    public TrackParams loadLevel(int levelIndex, int trackIndex) {
        int index = trackIndex;
        try {
            if (trackIndex >= currentMod.getLevels().get(levelIndex).getTracks().size())
                index = 0;
        } catch (Exception e) {
            index = 0;
        }

        TrackReference trackReference = currentMod.getLevels().get(levelIndex).getTracks().get(index);
        return trackReference.getData();
    }

    public TrackParams getRandomTrack() {
        int level = Utils.getRandom(currentMod.getLevels().size());
        int track = Utils.getRandom(currentMod.getLevels().get(level).getTracks().size());
        TrackReference trackReference = currentMod.getLevels().get(level).getTracks().get(track);
        return trackReference.getData();
    }

    public String getTrackGuid(int level, int track) {
        return currentMod.getLevels().get(level).getTracks().get(track).getGuid();
    }

    public String getTrackName(int level, int track) {
        return currentMod.getLevels().get(level).getTracks().get(track).getName();
    }

    public int getLevelTracksCount(int level) {
        return currentMod.getLevels().get(level).getTracks().size();
    }

    public Mod loadMod(String filename) throws InvalidTrackException {
        String name = Fmt.dot(filename, GDFile.MOD.extension);
        Mod mod = fileStorage.readMod(name);
        Utils.validatePack(mod);
        return mod;
    }
}
