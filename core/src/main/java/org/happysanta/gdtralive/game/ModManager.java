package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.dto.GameTheme;
import org.happysanta.gdtralive.game.api.dto.InterfaceTheme;
import org.happysanta.gdtralive.game.api.dto.LeagueTheme;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.external.GdFileStorage;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.api.model.TrackTheme;
import org.happysanta.gdtralive.game.util.Mapper;
import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Hot reload mods, themes, sprites, leagues
 */
public class ModManager {

    private final List<Runnable> themeReloadHandlers = new ArrayList<>();

    private Application application;
    private Theme theme;
    private Mod currentMod;
    private ModEntity modState;
    boolean temporallyUnlockedAll = false;
    private TrackTheme currentTrackTheme;
    private final float defaultDensity;
    private float gameDensity;
    private final GdDataSource dataSource;
    private final GdFileStorage fileStorage;
    private final GdSettings settings;

    public ModManager(GdFileStorage fileStorage, GdSettings settings, GdDataSource dataSource, float defaultDensity, Application application) {
        this.application = application;
        this.defaultDensity = defaultDensity;
        this.fileStorage = fileStorage;
        this.dataSource = dataSource;
        this.settings = settings;
        this.theme = loadTheme(settings.getSelectedTheme());

        initMod();
        adjustScale(null);
    }

    private void initMod() {
        try {
            dataSource.open();
            if (!dataSource.isDefaultModCreated()) {
                initDefaultModsState();
            } else {
                ModEntity modState = dataSource.getMod(settings.getLevelId());
                Mod mod = loadMod(modState.getName());
                setCurrentMod(mod, modState);
                if (currentMod == null) {
                    Mod defaultMod = loadMod(Constants.DEFAULT_MOD);
                    ModEntity defaultModState = dataSource.getMod(1); //default
                    setCurrentMod(defaultMod, defaultModState);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public List<ModEntity> getAllInstalledMods() {
        return dataSource.getAllLevels();
    }

    public String[] getAllInstalledModNames() {
        List<String> names = new ArrayList<>();
        for (ModEntity mod : getAllInstalledMods()) {
            names.add(mod.getName());
        }
        return names.toArray(new String[]{});
    }

    public void activateMod(String modName) {
        Mod mod = fileStorage.readMod(modName);
        activateMod(mod);
    }

    public void deleteMod(String modName) {
        if (Constants.IGNORE_SAVE.contains(modName)) {
            return;
        }
        Mod mod = fileStorage.readMod(modName);
        fileStorage.delete(GDFile.MOD, modName);
        ModEntity saved = dataSource.getMod(mod.getGuid());
        dataSource.deleteLevel(saved);
        long modId = settings.getLevelId();
        if (saved.getId() == modId) {
            ModEntity newActive = dataSource.getMod(1);
            activateMod(newActive.getName());
        }
    }

    public void activateMod(Mod mod) {
        ModEntity state = dataSource.getMod(mod.getGuid());
        if (state != null) {
            setCurrentMod(mod, state);
        } else {
            Mod packed = mod.pack();
            if (!Constants.IGNORE_SAVE.contains(mod.getName())) {
                fileStorage.save(packed, GDFile.MOD, mod.getName());
            }
            ModEntity modEntity = Mapper.mapModToEntity(mod, false);
            ModEntity saved = dataSource.createMod(modEntity);
            setCurrentMod(packed, saved);
        }
    }

    private void setCurrentMod(Mod mod, ModEntity state) {
        modState = state;
        currentMod = mod.unpack();
        currentTrackTheme = null;
        settings.setLevelId(modState.getId());
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

    public void setTrackTheme(TrackParams track) {
        if (track == null) {
            return;
        }
        this.currentTrackTheme = null;

        TrackTheme trackTheme = new TrackTheme();
        if (track.getGameTheme() != null) {
            trackTheme.setGameTheme(track.getGameTheme());
        }
        if (track.getLeagueTheme() != null) {
            trackTheme.setLeagueTheme(track.getLeagueTheme());
        }
        if (trackTheme.getGameTheme() != null || trackTheme.getLeagueTheme() != null) {
            this.currentTrackTheme = trackTheme;
        }
    }

    public LeagueTheme getLeagueTheme(int league) {
        if (currentTrackTheme != null && currentTrackTheme.getLeagueTheme() != null) {
            return currentTrackTheme.getLeagueTheme();
        }
        if (currentMod.getLeagueThemes() != null) {
            return currentMod.getLeagueThemes(league);
        }
        return getLeagueThemes().get(league);
    }

    public Theme getTheme() {
        return theme;
    }

    public GameTheme getGameTheme() {
        if (currentTrackTheme != null && currentTrackTheme.getGameTheme() != null) {
            return currentTrackTheme.getGameTheme();
        }
        if (currentMod.getGameTheme() != null) {
            return currentMod.getGameTheme();
        }
        return theme.getGameTheme();
    }

    public List<LeagueTheme> getLeagueThemes() {
        if (currentMod.getLeagueThemes() != null) {
            return currentMod.getLeagueThemes();
        }
        return theme.getLeagueThemes();
    }

    public InterfaceTheme getInterfaceTheme() {
        return theme.getInterfaceTheme();
    }

    public List<String> getLevelNames() {
        if (currentMod.getLevelNames() != null) {
            return currentMod.getLevelNames();
        }
        return theme.getLevelNames();
    }

    public String[] getLeagueNames() {
        if (currentMod.getLeagueThemes() != null) {
            Utils.getLeagueNames(currentMod.getLeagueThemes());
        }
        return Utils.getLeagueNames(theme.getLeagueThemes());
    }

    public String[] getLeagueTrackNames(int league) {
        return Utils.getLevelTrackNames(currentMod.getLevels().get(league).getTracks());
    }

    public void installTheme(Theme theme) {
        String name = theme.getHeader().getName();
        if (!Constants.IGNORE_SAVE.contains(name)) {
            fileStorage.save(theme, GDFile.THEME, name);
        }
        this.theme = loadTheme(name);
        settings.setSelectedTheme(name);
        reloadTheme();
        //Achievement.achievements.get(Achievement.Type.ESTHETE).increment();
    }

    public float getInterfaceDensity() {
        return defaultDensity;
    }

    public float getGameDensity() {
        return gameDensity;
    }

    public float getSpriteDensity() {
        return defaultDensity;
    }

    public void adjustScale(Integer value) {
        if (value != null) {
            settings.setScale(value);
        }
        this.gameDensity = defaultDensity * settings.getScale() / 100;
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
            try {
                Theme fromStorage = fileStorage.readTheme(name);
                theme = fromStorage == null ? Theme.defaultTheme() : fromStorage;
            } catch (Exception e) {
                e.printStackTrace();
                theme = Theme.defaultTheme();
            }
        }
        return theme;
    }

    public TrackData loadLevel(int levelIndex, int trackIndex) {
        int index = trackIndex;
        try {
            if (trackIndex >= currentMod.getLevels().get(levelIndex).getTracks().size())
                index = 0;
        } catch (Exception e) {
            index = 0;
        }

        TrackParams trackParams = currentMod.getLevels().get(levelIndex).getTracks().get(index);
        setTrackTheme(trackParams);
        return trackParams.getData();
    }

    public TrackData getRandomTrack() {
        int level = Utils.getRandom(currentMod.getLevels().size());
        int track = Utils.getRandom(currentMod.getLevels().get(level).getTracks().size());
        TrackParams trackParams = currentMod.getLevels().get(level).getTracks().get(track);
        setTrackTheme(trackParams);
        return trackParams.getData();
    }

    public String getTrackId(int level, int track) {
        return Utils.getTrackId(currentMod.getLevels().get(level).getTracks().get(track).getData());
    }

    public String getTrackName(int level, int track) {
        return currentMod.getLevels().get(level).getTracks().get(track).getData().getName();
    }

    public int getLevelTracksCount(int level) {
        return currentMod.getLevels().get(level).getTracks().size();
    }

    public Mod loadMod(String filename) throws InvalidTrackException {
        Mod mod = fileStorage.readMod(filename);
        Utils.validateMod(mod);
        return mod;
    }

    public void skipTrack(int level) {
        int unlockedTracksCount = modState.getUnlockedTracksCount(level);
        modState.setUnlockedTracks(level, unlockedTracksCount + 1);
        modState.setSelectedTrack(Math.min(unlockedTracksCount + 1, modState.getTracksCount(level) - 1));
        dataSource.updateMod(modState);
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
