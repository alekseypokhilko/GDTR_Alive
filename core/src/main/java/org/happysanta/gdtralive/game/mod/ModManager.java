package org.happysanta.gdtralive.game.mod;

import org.happysanta.gdtralive.game.Constants;
import org.happysanta.gdtralive.game.external.GdFileStorage;
import org.happysanta.gdtralive.game.external.GdUtils;
import org.happysanta.gdtralive.game.levels.TrackParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Hot reload mods, themes, sprites, leagues
 */
public class ModManager {

    private final List<Runnable> themeReloadHandlers = new ArrayList<>();

    private Theme theme;
    private Mod currentMod;
    private TrackReference currentTrack;
    private TrackProperties currentTrackProperties;

    private final GdUtils utils;

    public ModManager(GdFileStorage fileStorage, GdUtils utils) {
        this.utils = utils;
        theme = loadTheme();
        try {
            currentMod = fileStorage.loadMod(Constants.PACK_NAME); //todo handle pack not found
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        //todo delete/move
//        String s = FileStorage.toJson(theme);
//        Theme read = FileStorage.read(s, Theme.class);
//        theme.gameTheme = new GameTheme(read.getGameTheme().getProps());
//        theme.interfaceTheme = new InterfaceTheme(read.getInterfaceTheme().getProps());
//        List<LeagueTheme> leagueThemes = new ArrayList<>();
//        for (LeagueTheme leagueTheme :read.getLeagueThemes()) {
//            leagueThemes.add(new LeagueTheme(leagueTheme.getProps()));
//        }
//        theme.leagueThemes = leagueThemes;

    }

    public Mod getMod() {
        return currentMod;
    }

    public void setTrackProperties(TrackReference track) {
        this.currentTrack = track;
        if (track == null) {
            this.currentTrackProperties = null;
            return;
        }

        TrackProperties trackProperties = new TrackProperties();
        if (track.getGameProperties() != null) {
            trackProperties.setGameProperties(new GameTheme(track.getGameProperties()));
        }
        if (track.getLeagueProperties() != null) {
            trackProperties.setLeagueProperties(new LeagueProperties(track.getLeagueProperties()));
        }
        if (trackProperties.getGameProperties() != null || trackProperties.getLeagueProperties() != null) {
            this.currentTrackProperties = trackProperties;
        }
    }

    public void setMod(Mod mod) {
        currentMod = mod;
    }

    public List<LeagueProperties> getLeagueThemes() {
        return theme.getLeagueThemes();
    }

    public LeagueProperties getLeagueTheme(int league) {
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

    public List<LeagueProperties> getLeaguesProperties() {
        return getLeagueThemes();
    }

    public LeagueProperties getLeagueProperties(int league) {
        return getLeagueThemes().get(league);
    }

    public String[] getLeagueNames() {
        List<LeagueProperties> leagueProperties = getLeaguesProperties();
        String[] names = new String[leagueProperties.size()];
        for (int i = 0; i < leagueProperties.size(); i++) {
            names[i] = leagueProperties.get(i).getName();
        }
        return names;
    }

    public String[] getLeagueTrackNames(int league) {
        List<String> names = new ArrayList<>();
        for (TrackReference track : currentMod.getLevels().get(league).getTracks()) {
            names.add(track.getName());
        }
        return names.toArray(new String[0]);
    }

    public void installTheme(Theme theme) {
        this.theme = theme;
        //Settings.setSelectedThemeGuid(theme.getHeader().getGuid());
        reloadTheme();
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
                } catch (Exception e) {
                    e.printStackTrace(); //todo
                }
            }
        }
    }

    public Theme loadTheme() {
//        if (true) {
//            return Theme.amoledMod(); //todo
//        }
        String themeGuid = "e46a37c0-69e1-4646-8f9c-b47247586635"; //Helpers.getGDActivity().settings.getSelectedThemeGuid();
        if ("e46a37c0-69e1-4646-8f9c-b47247586635".equals(themeGuid)) {
            return Theme.defaultTheme();
        } else if ("b5221ae2-c4ea-4225-9d51-818fdfad34a9".equals(themeGuid)) {
            return Theme.amoledMod(); //todo
        } else {
            try {
//            theme = Theme.amoledMod();
//            Helpers.showToast("Active mod: " + (mod.name == null ? "Unnamed" : mod.name));

//            TrackRecord rec = FileLoader.read(FileLoader.fromAssets(ASSETS_MODS_FOLDER, "one minute"), TrackRecord.class);
//            FileLoader.toJson(new Object());
//            FileOutputStream fout = new FileOutputStream(new File(
//                    Environment.getExternalStorageDirectory(), "GDAlive/record.json"));
//            ObjectOutputStream oos = new ObjectOutputStream(fout);
//            oos.writeObject(rec);
                return Theme.defaultTheme();
            } catch (Exception e) {
//                Helpers.showToast("Mod activation failed");
                return Theme.defaultTheme();
            }
        }
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
        int level = utils.getRandom(0, 3);
        int track = utils.getRandom(0, currentMod.getLevels().get(level).getTracks().size());
        TrackReference trackReference = currentMod.getLevels().get(level).getTracks().get(track);
        return trackReference.getData();
    }

    public int getLevelsCount() {
        return currentMod.getLevels().size();
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
}
