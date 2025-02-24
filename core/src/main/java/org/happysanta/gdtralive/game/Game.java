package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.engine.KeyboardHandler;
import org.happysanta.gdtralive.game.engine.LevelState;
import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.external.GdMenu;
import org.happysanta.gdtralive.game.external.GdSettings;
import org.happysanta.gdtralive.game.external.GdUtils;
import org.happysanta.gdtralive.game.levels.InvalidTrackException;
import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.modes.GameMode;
import org.happysanta.gdtralive.game.modes.MenuData;
import org.happysanta.gdtralive.game.modes.MenuMapper;
import org.happysanta.gdtralive.game.modes.GameParams;
import org.happysanta.gdtralive.game.recorder.Player;
import org.happysanta.gdtralive.game.recorder.Recorder;
import org.happysanta.gdtralive.game.storage.LevelsManager;
import org.happysanta.gdtralive.game.storage.ModEntity;
import org.happysanta.gdtralive.game.storage.Score;
import org.happysanta.gdtralive.game.trainer.Trainer;
import org.happysanta.gdtralive.game.util.Utils;
import org.happysanta.gdtralive.game.visual.FrameRender;
import org.happysanta.gdtralive.game.visual.GdView;
import org.happysanta.gdtralive.game.visual.Strings;

public class Game {
    private final Object menuLock = new Object();
    private final Object gameLock = new Object();

    private final Engine engine;
    private final GdApplication application;
    private final GdSettings settings;
    private final GdUtils utils;
    private final GdView view;
    private GdMenu menu;

    public final LevelsManager levelsManager;

    private final Recorder recorder;
    private final Player player;
    private final Trainer trainer;
    private final KeyboardHandler keyboardHandler;
    private final MenuMapper menuMapper;

    private GameParams params;
    private long startedTime = 0;
    private long finishedTime = 0;
    private long pausedTime = 0;
    private long pausedTimeStarted = 0;
    private long lastTrackTime = -1;
    private long delayedRestartAtTime;

    public Game(GdApplication application, int width, int height) {
        GdSettings settings = application.getSettings();

        final FrameRender frameRender = new FrameRender(application.getModManager());
        this.application = application;
        this.levelsManager = new LevelsManager(settings, application.getDataSource());
        this.engine = new Engine();
        try {
            engine.init(settings, application.getModManager().loadLevel(0, 0));
        } catch (InvalidTrackException e) {
            try {
                engine.init(settings, Utils.trackTemplate(settings.getPlayerName()));
            } catch (InvalidTrackException ignore) {
            }
        }
        this.view = new GdView(frameRender, engine, width, height);
        this.recorder = new Recorder(engine, application.getFileStorage(), settings);
        this.player = new Player(engine, () -> restart(true));
        this.trainer = new Trainer(engine, view, application.getUtils());
        this.keyboardHandler = new KeyboardHandler(application, engine, settings.getInputOption());
        this.utils = application.getUtils();
        this.settings = settings;
        this.menuMapper = new MenuMapper();

//        view.adjustDimensions(true); //todo move
    }

    public void init(GdMenu menu) {
        this.menu = menu;
        keyboardHandler.setMenu(menu);
    }

    public void gameLoop() {
        restart(false);
        menu.showMenu(MenuData.mainMenu());
        if (menu.canStartTrack()) {
            restart(true);
        }
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
            MenuData inGameMenu = menuMapper.mapInGameMenuData(params);
            menu.showMenu(inGameMenu);
            if (menu.canStartTrack()) {
                restart(true);
            }
        }

        for (int i1 = settings.getGameSpeed(); i1 > 0; i1--) {
            captureOrPlay();
            engine.timerTime = Utils.calculateTimerTime(startedTime, finishedTime, pausedTime, currentTimeMillis);
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
                    Utils.waitRestart(delayedRestartAtTime, currentTimeMillis);
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
                if (GameMode.RANDOM.equals(params.getMode())) {
                    Achievement.achievements.get(Achievement.Type.GAMBLER).increment();
                }
                if (recorder.isCapturingMode()) {
                    recorder.saveCapture(lastTrackTime);
                }
                trainer.stop();
                goalLoop();

                updateSelectors(params, getLevelsManager().getCurrentLevel()); //todo fix
                MenuData finishedMenu = menuMapper.getFinishedMenuData(params, lastTrackTime, levelsManager.getCurrentLevel());
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
            if (params != null && GameMode.REPLAY == params.getMode()) {
                player.replay(params.getMode());
                try {
                    synchronized (menuLock) {
                        menuLock.wait(Constants.WAIT_TIME);
                    }
                } catch (InterruptedException ignored) {
                }
            } else if (engine.isKeyLocked()) {
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
    private void updateSelectors(GameParams params, ModEntity level) {
        if (GameMode.CLASSIC != params.getMode()) {
            return;
        }
        if (params.getTrack() + 1 < level.getTracksCount(params.getLevel())) {
            level.setUnlockedTracks(params.getLevel(),  params.getTrack() + 1);
            level.setSelectedTrack(params.getTrack() + 1);
        } else {
            level.setUnlockedTracks(params.getLevel(),  params.getTrack() + 1);
            level.setSelectedTrack(params.getTrack() + 1);
            int newUnlocked = level.getUnlockedTracksCount(params.getLevel()) + 1;
            int tracksCount = level.getTracksCount(params.getLevel());
            if (newUnlocked > tracksCount)
                newUnlocked = tracksCount;
            int unlockedTrack = newUnlocked;
            level.setUnlockedTracks(params.getLevel(),  unlockedTrack);
            level.setSelectedTrack(unlockedTrack);

            int unlockedLevels = level.getUnlockedLevels();
            int newSelectedLevel = params.getLevel() + 1;
            if (newSelectedLevel < application.getModManager().getLevelsCount() && newSelectedLevel == unlockedLevels - 1) {
                level.setUnlockedLevels(Math.min(newSelectedLevel,  application.getModManager().getLevelsCount()));
                level.setSelectedLevel(newSelectedLevel);
                level.setUnlockedTracks(newSelectedLevel,  Math.max(0, level.getUnlockedTracksCount(newSelectedLevel)));
                level.setSelectedTrack(0);
            }
            int newSelectedLeague = params.getLeague() + 1;
            if (newSelectedLeague < Engine.leagueProperties.size() && newSelectedLeague == unlockedLevels - 1) {
                level.setUnlockedLeagues(Math.min(level.getUnlockedLeagues() + 1, Engine.leagueProperties.size()));
                level.setSelectedLeague(newSelectedLeague);
            }
        }
        levelsManager.updateLevelSettings();
    }

    private void captureOrPlay() {
        if (GameMode.REPLAY != params.getMode()) {
            if (recorder.isCapturing()) {
                recorder.captureState();
            } else {
                recorder.startCapture();
                recorder.captureState();
            }
        } else {
            recorder.setCapturingMode(false);
            trainer.stop();
            player.replay(params.getMode());
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
        engine.resetControls();
        keyboardHandler.resetButtonsTouch();
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
                    synchronized (this) { //check
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

    private void saveScore(long lastTrackTime) {
        Score score = new Score();
        score.setLevelGuid(params.getTrackParams().getGuid());
        score.setLeague(params.getLeague());
        score.setTime(lastTrackTime);
        score.setName(settings.getPlayerName());
        application.getHighScoreManager().saveHighScore(score);
        Achievement.achievements.get(Achievement.Type.TRIAL_MASTER).increment();
    }

    public LevelsManager getLevelsManager() {
        return levelsManager;
    }

    public GdView getView() {
        return view;
    }

    public Engine getEngine() {
        return engine;
    }

    public void startTrack(GameParams params) {
        this.params = params;
        if (GameMode.REPLAY == params.getMode()) {
            player.reset(); //todo check
            player.setTrackRecord(params.getTrackRecord());
            Achievement.achievements.get(Achievement.Type.SERIES_LOVER).increment();
        } else {
            recorder.setCapturingMode(true);
        }
        if (GameMode.TRACK_EDITOR == params.getMode()) {
            restart(false);
            recorder.setCapturingMode(false);
            recorder.reset();
            engine.setEditMode(true);
            view.setDrawTimer(false);
            return;
        }
        TrackParams track = params.getTrackParams();
        engine.setEditMode(false);
        try {
            engine.loadTrack(track);
        } catch (InvalidTrackException e) {
            throw new RuntimeException(e); //todo skip damaged track
        }
        engine.setLeague(track.getLeague());
        engine.unlockKeys();
        view.setDrawTimer(true);
        menu.menuToGame();
    }

    public void startAutoplay(boolean resetPlayer) {
        if (resetPlayer) {
            player.reset();
        }
        engine.startAutoplay();
    }

    public void showInfoMessage(String message, int time) {
        view.showInfoMessage(message, time);
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

    public void pause() {
        pausedTimeStarted = System.currentTimeMillis();
    }

    public void handleSetSavepointAction() {
        trainer.setSavepoint();
    }

    public void resetState() {
        trainer.stop();
        recorder.reset();
        player.reset();
    }

    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
}
