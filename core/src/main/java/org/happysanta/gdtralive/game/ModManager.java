package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.GameTheme;
import org.happysanta.gdtralive.game.api.dto.InterfaceTheme;
import org.happysanta.gdtralive.game.api.dto.LeaguePropertiesTheme;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackReference;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.TrackParams;
import org.happysanta.gdtralive.game.api.model.TrackProperties;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Mapper;
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
    private final float defaultDensity; //todo move to settings (not editable)
    private final GdDataSource dataSource;
    private final GdFileStorage fileStorage;
    private final GdSettings settings;

    public ModManager(GdFileStorage fileStorage, GdSettings settings, GdDataSource dataSource, float defaultDensity) {
        this.defaultDensity = defaultDensity;
        this.fileStorage = fileStorage;
        this.dataSource = dataSource;
        this.settings = settings;
        this.theme = loadTheme(settings.getSelectedTheme());

        initMod();
    }

    private void initMod() {
        try {
            dataSource.open();
            if (!dataSource.isDefaultModCreated()) {
                initDefaultModsState();
            } else {
                modState = dataSource.getMod(settings.getLevelId());
                currentMod = loadMod(modState.getName());
                if (currentMod == null) {
                    currentMod = loadMod(Constants.DEFAULT_MOD);
                    modState = dataSource.getMod(1); //default
                }
            }
        } catch (Exception ignore) {
        }
        try {
            if (currentMod == null) {
                currentMod = loadMod(Constants.DEFAULT_MOD);
            }
        } catch (Exception e) {
            throw new RuntimeException(e); //this is possible will not happen due to default mod in assets folder
        }
    }

    private void initDefaultModsState() throws InvalidTrackException {
        Mod defaultMod = loadMod(Constants.DEFAULT_MOD);
        ModEntity entity = Mapper.mapModToEntity(defaultMod, true);
        ModEntity state = dataSource.createMod(entity);
        setCurrentMod(defaultMod, state);

        dataSource.createMod(Mapper.mapModToEntity(loadMod(Constants.ORIGINAL_MOD), false));
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
        ModEntity state = dataSource.getMod(mod.getGuid());
        if (state != null) {
            setCurrentMod(mod, state);
        } else {
            fileStorage.save(mod, GDFile.MOD, mod.getName());
            ModEntity modEntity = Mapper.mapModToEntity(currentMod, false);
            setCurrentMod(mod, dataSource.createMod(modEntity));
        }
    }

    private void setCurrentMod(Mod mod, ModEntity state) {
        modState = state;
        currentMod = mod;
        settings.setLevelId(modState.getId());
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
        dataSource.updateMod(modState);
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
        settings.setSelectedTheme(theme.getHeader().getName());
        reloadTheme();
    }

    public void adjustScale(Theme theme) {
        if (theme == null) {
            theme = this.theme;
        }
        int scale = settings.getScale();
        theme.getInterfaceTheme().setProp(InterfaceTheme.density, defaultDensity);
        theme.getGameTheme().setProp(GameTheme.scaledDensity, defaultDensity * scale / 100);
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
