package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.engine.KeyboardHandler;
import org.happysanta.gdtralive.game.engine.LevelState;
import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.external.GdFileStorage;
import org.happysanta.gdtralive.game.external.GdMenu;
import org.happysanta.gdtralive.game.external.GdSettings;
import org.happysanta.gdtralive.game.external.GdUtils;
import org.happysanta.gdtralive.game.levels.InvalidTrackException;
import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.mod.Mod;
import org.happysanta.gdtralive.game.mod.TrackReference;
import org.happysanta.gdtralive.game.modes.GameMode;
import org.happysanta.gdtralive.game.modes.MenuData;
import org.happysanta.gdtralive.game.modes.MenuMapper;
import org.happysanta.gdtralive.game.recorder.Player;
import org.happysanta.gdtralive.game.recorder.Recorder;
import org.happysanta.gdtralive.game.recorder.TrackRecord;
import org.happysanta.gdtralive.game.storage.LevelsManager;
import org.happysanta.gdtralive.game.storage.ModEntity;
import org.happysanta.gdtralive.game.storage.Score;
import org.happysanta.gdtralive.game.trainer.Trainer;
import org.happysanta.gdtralive.game.visual.GdView;
import org.happysanta.gdtralive.game.visual.Strings;

import java.util.List;

public class Game {
    private final Object menuLock = new Object();
    private final Object gameLock = new Object();

    private final Engine engine;
    private final GdApplication application;
    private final GdSettings settings;
    private final GdFileStorage fileStorage;
    private final GdUtils utils;
    private final GdView view;
    private GdMenu menu;

    public final LevelsManager levelsManager;

    private final Recorder recorder;
    private final Player player;
    private final Trainer trainer;
    private final KeyboardHandler keyboardHandler;

    private final MenuMapper menuMapper;

    private GameMode mode;
    private int selectedLevel;
    private int selectedTrack;
    private int selectedLeague;
    private boolean singleTrackPlayed;
    private long startedTime = 0;
    private long finishedTime = 0;
    private long pausedTime = 0;
    private long pausedTimeStarted = 0;
    private long lastTrackTime = -1;
    private long delayedRestartAtTime;

    public Game(GdApplication application, GdView view, LevelsManager levelsManager,
                Engine engine, Player player,
                GdUtils utils, GdSettings settings, GdFileStorage fileStorage) {
        this.application = application;
        this.levelsManager = levelsManager;
        this.engine = engine;
        this.view = view;
        this.recorder = new Recorder(engine, fileStorage, settings);
        this.player = player;
        this.trainer = new Trainer(engine, view, utils);
        this.keyboardHandler = new KeyboardHandler(application, engine, settings.getInputOption());
        this.utils = utils;
        this.settings = settings;
        this.fileStorage = fileStorage;
        this.menuMapper = new MenuMapper(this);

        //post construct
        this.player.setRestartMethod(() -> restart(true));

//        view.adjustDimensions(true); //todo move
    }

    public void setMenu(GdMenu menu) {
        this.menu = menu;
        keyboardHandler.setMenu(menu);
    }

    public void gameLoop() {
        while (application.isAlive()) {
            tick();
            if (!application.isAlive()) {
                break;
            }
            try {
                synchronized (gameLock) {
                    gameLock.wait(Constants.WAIT_TIME);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void tick() {
        long currentTimeMillis = System.currentTimeMillis();

        if (application.isMenuShown()) {
            MenuData inGameMenu = menuMapper.mapInGameMenuData();
            menu.showMenu(inGameMenu);
            if (menu.canStartTrack()) {
                restart(true);
            }
        }

        for (int i1 = settings.getGameSpeed(); i1 > 0; i1--) {
            captureOrPlay();
            engine.timerTime = calculateTimerTime(startedTime, finishedTime, pausedTime, currentTimeMillis);
            LevelState levelState = engine.getLevelState();
            if (LevelState.CRASHED_IN_AIR == levelState && delayedRestartAtTime == 0L) {
                trainer.onCrash(() -> {
                    recorder.stopCapture();
                    delayedRestartAtTime = currentTimeMillis + 3000L;
                    view.showInfoMessage(utils.s(Strings.CRASHED), 3000);
                });
            }
            if (delayedRestartAtTime != 0L && delayedRestartAtTime < currentTimeMillis) {
                trainer.onCrash(() -> {
                    recorder.stopCapture();
                    restart(true);
                });
            }
            if (LevelState.CRASHED == levelState) {
                trainer.onCrash(() -> {
                    recorder.stopCapture();
                    finishedTime = currentTimeMillis;
                    view.showInfoMessage(utils.s(Strings.CRASHED), 3000);
                    waitRestart(delayedRestartAtTime, currentTimeMillis);
                    restart(true);
                });
            } else if (LevelState.START_NOT_CROSSED == levelState) {
                startedTime = 0;
                finishedTime = 0;
                pausedTime = 0;
            } else if (levelState == LevelState.S1 || levelState == LevelState.FINISHED) {
                finishedTime = currentTimeMillis;
                lastTrackTime = (finishedTime - startedTime);
                saveScore(lastTrackTime);
                if (GameMode.RANDOM.equals(mode)) {
                    Achievement.achievements.get(Achievement.Type.GAMBLER).increment();
                }
                if (recorder.isCapturingMode()) {
                    recorder.saveCapture(lastTrackTime);
                }
                trainer.stop();
                goalLoop();

                updateSelectors(); //todo fix
                MenuData finishedMenu = menuMapper.getFinishedMenuData(
                        lastTrackTime, selectedLevel, selectedTrack, selectedLeague
                );
                menu.showMenu(finishedMenu);

                if (menu.canStartTrack()) {
                    restart(true);
                }
            }
            boolean startIsCrossed = levelState != LevelState.START_NOT_CROSSED;
            if (startIsCrossed && startedTime == 0) {
                startedTime = currentTimeMillis;
            }
        }
        engine.updateState();
    }

    public void menuBackgroundLoop() {
        view.setDrawTimer(false);
        engine.updateState();
        application.gameToMenu();

        while (application.isMenuShown() && application.isAlive() && !menu.isCurrentMenuEmpty()) {
            if (application.isOnPause()) {
                while (application.isOnPause()) {
                    if (!application.isAlive() || menu.isCurrentMenuEmpty()) {
                        break;
                    }
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            if (player.isReplayMode()) {
                player.replay();
                try {
                    synchronized (menuLock) {
                        menuLock.wait(Constants.WAIT_TIME);
                    }
                } catch (InterruptedException ignored) {
                }
            } else if (engine != null && engine.isKeyLocked()) {
                LevelState j1 = engine.getLevelState();
                if (j1 != LevelState.IN_PROCESS && j1 != LevelState.START_NOT_CROSSED) {
                    try {
                        engine.resetToStart_MAYBE();
                        engine.startAutoplay();
                    } catch (NullPointerException ignored) {
                    }
                }
                engine.updateState();
                // _hvV();
                try {
                    synchronized (menuLock) {
                        menuLock.wait(20L);
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    //todo test and fix
    private void updateSelectors() {
        if (GameMode.CLASSIC != mode) {
            return;
        }
        ModEntity level = getLevelsManager().getCurrentLevel();
        if (selectedTrack + 1 < level.getTracksCount(selectedLevel)) {
            level.setUnlockedTracks(selectedLevel, selectedTrack + 1);
            level.setSelectedTrack(selectedTrack + 1);
        } else {
            level.setUnlockedTracks(selectedLevel, selectedTrack + 1);
            level.setSelectedTrack(selectedTrack + 1);
            int newUnlocked = level.getUnlockedTracksCount(selectedLevel) + 1;
            int tracksCount = level.getTracksCount(selectedLevel);
            if (newUnlocked > tracksCount)
                newUnlocked = tracksCount;
            int unlockedTrack = newUnlocked;
            level.setUnlockedTracks(selectedLevel, unlockedTrack);
            level.setSelectedTrack(unlockedTrack);

            int unlockedLevels = level.getUnlockedLevels();
            int newSelectedLevel = selectedLevel + 1;
            if (newSelectedLevel < application.getModManager().getLevelsCount() && newSelectedLevel == unlockedLevels - 1) {
                level.setUnlockedLevels(Math.min(newSelectedLevel, application.getModManager().getLevelsCount()));
                level.setSelectedLevel(newSelectedLevel);
                level.setUnlockedTracks(newSelectedLevel, Math.max(0, level.getUnlockedTracksCount(newSelectedLevel)));
                level.setSelectedTrack(0);
            }
            int newSelectedLeague = selectedLeague + 1;
            if (newSelectedLeague < Engine.leagueProperties.size() && newSelectedLeague == unlockedLevels - 1) {
                level.setUnlockedLeagues(Math.min(level.getUnlockedLeagues() + 1, Engine.leagueProperties.size()));
                level.setSelectedLeague(newSelectedLeague);
            }
        }
        levelsManager.updateLevelSettings();
    }

    private void captureOrPlay() {
        if (!player.isReplayMode()) {
            if (recorder.isCapturing()) {
                recorder.captureState();
            } else {
                recorder.startCapture();
                recorder.captureState();
            }
        } else {
            recorder.setCapturingMode(false);
            trainer.stop();
            player.replay();
        }
    }

    public void restart(boolean showLevelName) {
        view.setDrawTimer(true);
        engine.resetToStart_MAYBE();
        startedTime = 0;
        finishedTime = 0;
        pausedTime = 0;
        delayedRestartAtTime = 0;
        if (showLevelName)
            view.showInfoMessage(engine.getTrackPhysic().getTrack().name, 3000);
        resetControls();
        trainer.prepare();
    }

    //fromMenu
    public void restart() {
        resetState();
        restart(true);
        menu.menuToGame();
    }

    public void goalLoop() {
        long l1 = 0L;
        if (!engine.frontWheelTouchedGround)
            view.showInfoMessage(utils.s(Strings.WHEELIE), 1000);
        else
            view.showInfoMessage(utils.s(Strings.FINISHED), 1000);
        for (long l2 = System.currentTimeMillis() + 1000L; l2 > System.currentTimeMillis(); /*view.postInvalidate()*/) {
            if (application.isMenuShown()) {
                //m_di.postInvalidate();
                return;
            }
            for (int j = settings.getGameSpeed(); j > 0; j--) {
                if (engine.getLevelState() == LevelState.CRASHED)
                    try {
                        long l3;
                        if ((l3 = l2 - System.currentTimeMillis()) > 0L)
                            Thread.sleep(l3);
                        return;
                    } catch (InterruptedException _ex) {
                        return;
                    }
            }

            engine.updateState();
            long l;
            if ((l = System.currentTimeMillis()) - l1 < 30L) {
                try {
                    synchronized (this) {
                        wait(Math.max(30L - (l - l1), 1L));
                    }
                } catch (InterruptedException ignored) {
                }
                l1 = System.currentTimeMillis();
            } else {
                l1 = l;
            }
        }
    }

    public void resume() {
        view.setDrawTimer(true);
        if (pausedTimeStarted > 0 && startedTime > 0) {
            pausedTime += (System.currentTimeMillis() - pausedTimeStarted);
            pausedTimeStarted = 0;
        }
    }

    public static long calculateTimerTime(long startedTime, long finishedTime,
                                          long pausedTime, long currentTimeMillis) {
        if (startedTime > 0) {
            long finished;
            if (finishedTime > 0)
                finished = finishedTime;
            else
                finished = currentTimeMillis;
            return (finished - startedTime - pausedTime);
        }
        return 0;
    }

    private void saveScore(long lastTrackTime) {
        Score score = new Score();
        score.setLevelGuid(getCurrentTrackGuid());
        score.setLeague(getSelectedLeague());
        score.setTime(lastTrackTime);
        score.setName(settings.getPlayerName());
        application.getHighScoreManager().saveHighScore(score);
        Achievement.achievements.get(Achievement.Type.TRIAL_MASTER).increment();
    }

    private void resetControls() {
        engine.resetControls();
        keyboardHandler.resetButtonsTouch();
    }

    private void waitRestart(long delayedRestartAtTime, long currentTimeMillis) {
        try {
            long l2 = 1000L;
            if (delayedRestartAtTime > 0L)
                l2 = Math.min(delayedRestartAtTime - currentTimeMillis, 1000L);
            if (l2 > 0L)
                Thread.sleep(l2);
        } catch (InterruptedException ignored) {
        }
    }

    public LevelsManager getLevelsManager() {
        return levelsManager;
    }

    public String getCurrentTrackGuid() {
        return engine.getTrackPhysic().track.getGuid();
    }

    public String getCurrentTrackName() {
        return engine.getTrackPhysic().getTrack().name;
    }

    public void setTrack(TrackParams track) {
        try {
            engine.loadTrack(track);
        } catch (Exception e) {
            throw new RuntimeException(e); //todo
        }
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public int getSelectedTrack() {
        return selectedTrack;
    }

    public long getLastTrackTime() { //todo remove
        return lastTrackTime;
    }

    public int getSelectedLeague() {
        return selectedLeague;
    }

    public boolean isSingleTrackPlayed() {
        return singleTrackPlayed;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public void startRandomTrack(Mod mod) {
        int level = utils.getRandom(0, 3);
        int track = utils.getRandom(0, mod.getLevels().get(level).getTracks().size());
        TrackReference trackReference = mod.getLevels().get(level).getTracks().get(track);
        TrackParams data = trackReference.getData();
        if (data == null) {
            //todo load track by guid or next
        }
        this.selectedLevel = level;
        this.selectedLeague = level;
        this.selectedTrack = track;
        this.singleTrackPlayed = true;
        this.mode = GameMode.RANDOM;
        engine.loadTrack(data);
        engine.setLeague(selectedLeague);
        engine.unlockKeys();
        menu.menuToGame();
    }

    public void startTrack(int league, int level, int track, boolean single) {
        this.selectedLevel = level;
        this.selectedLeague = league;
        this.selectedTrack = track;
        this.singleTrackPlayed = single;
        this.setMode(GameMode.CLASSIC);
        try {
            TrackParams trackParams = application.getModManager().loadLevel(selectedLevel, selectedTrack);
            engine.loadTrack(trackParams);
        } catch (InvalidTrackException e) {
            //todo menu.skipDamagedTrack();
        }
        engine.setLeague(selectedLeague);
        engine.unlockKeys();
        menu.menuToGame();
    }

    public void startTrack(TrackParams track, boolean single) {
        engine.loadTrack(track);
        engine.setLeague(track.getLeague());
        engine.unlockKeys();
        this.selectedLeague = track.getLeague();
        this.singleTrackPlayed = single;
        menu.menuToGame();
    }

    public void startTrack(TrackParams track, int league, boolean single) {
        engine.loadTrack(track);
        engine.setLeague(league);
        engine.unlockKeys();
        this.selectedLeague = league;
        this.singleTrackPlayed = single;
        menu.menuToGame();
    }

    public void startTrack(String packName, String trackGuid, boolean single) {
        try {
            startTrack(fileStorage.getLevelFromPack(packName, trackGuid), single);
        } catch (InvalidTrackException e) {
            application.notify("File loading error: " + e.getMessage());
        }
    }

    public void startRandomTrack(List<String> modsNames) {
        String packName = modsNames.get(utils.getRandom(0, modsNames.size()));
        try {
            Mod mod = fileStorage.loadMod(packName);
            startRandomTrack(mod);
            setMode(GameMode.RANDOM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAutoplay() {
        engine.startAutoplay();
    }

    // ======= settings =======
    public void setPerspectiveEnabled(boolean flag) {
        settings.setPerspectiveEnabled(flag);
        engine.setPerspectiveEnabled(flag);
    }

    public void setShadowsEnabled(boolean enabled) {
        settings.setShadowsEnabled(enabled);
        engine.getTrackPhysic().setShadowsEnabled(enabled);
    }

    public void setDrawBiker(boolean drawBiker) {
        settings.setDriverSpriteEnabled(drawBiker);
        engine.setDrawBiker(drawBiker);
    }

    public void setDrawBike(boolean drawBike) {
        settings.setBikeSpriteEnabled(drawBike);
        engine.setDrawBike(drawBike);
    }

    public void setLookAhead(boolean flag) {
        settings.setLookAheadEnabled(flag);
        engine.setLookAhead(flag);
    }

    public void setInputOption(int option) {
        keyboardHandler.setInputOption(option);
        settings.setInputOption(option);
    }
    // ======= settings =======

    public void setShowTimer(boolean draw) {
        view.setDrawTimer(draw);
    }

    public void pause() {
        pausedTimeStarted = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public void startReplay(TrackRecord rec) {
        player.setTrackRecord(rec);
        player.setReplayMode(true);
        startTrack(rec.getTrack(), true);
        Achievement.achievements.get(Achievement.Type.SERIES_LOVER).increment();
    }

    public void handleSetSavepointAction() {
        trainer.setSavepoint();
    }

    public void resetState() {
        trainer.stop();
        recorder.reset();
        player.reset();
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
}
