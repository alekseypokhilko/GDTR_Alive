package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.LevelState;
import org.happysanta.gdtralive.game.api.S;
import org.happysanta.gdtralive.game.api.dto.OpponentDisconnectRequest;
import org.happysanta.gdtralive.game.api.dto.OpponentState;
import org.happysanta.gdtralive.game.api.dto.ScoreDto;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.external.GdMenu;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.Score;
import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.http.APIClient;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Mapper;
import org.happysanta.gdtralive.game.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Core game logic
 */
public class Game {
    private final Object menuLock = new Object();
    private final Object gameLock = new Object();

    private final Engine engine;
    private final Application application;
    private final GdSettings settings;
    private final GdStr str;
    private final GdView view;
    private GdMenu menu;
    private final Recorder recorder;
    private final Player player;
    private final Trainer trainer;
    private final KeyboardHandler keyboardHandler;
    private GameParams params;
    public int attemptCount = 0;
    private long startedTime = 0;
    private long finishedTime = 0;
    private long pausedTime = 0;
    private long pausedTimeStarted = 0;
    private long lastTrackTime = -1;
    private long delayedRestartAtTime;
    private DatagramSocket udpSocket;
    private Thread receiverThread;

    public Game(Application application, int width, int height) {
        GdSettings settings = application.getSettings();

        final FrameRender frameRender = new FrameRender(application.getModManager());
        this.application = application;
        this.engine = new Engine();
        try {
            engine.init(settings, application.getModManager().loadLevel(0, 0));
        } catch (InvalidTrackException e) {
            try {
                engine.init(settings, Utils.trackTemplate(settings.getPlayerName()));
            } catch (InvalidTrackException ignore) {
            }
        }
        this.view = new GdView(frameRender, engine, width, height, application.getPlatform(), this, application.getStr());
        this.recorder = new Recorder(engine, application.getFileStorage(), settings);
        this.player = new Player(engine, () -> restart(true));
        this.trainer = new Trainer(engine, this);
        this.keyboardHandler = new KeyboardHandler(application, engine, settings.getInputOption());
        this.str = application.getStr();
        this.settings = settings;

        if (settings.isTestFeaturesEnabled()) {
            try {
                udpSocket = new DatagramSocket();
                udpSocket.setSoTimeout(1000);
                receiverThread = new Thread(this::receiveOnlinePacket, "online-thread");
                receiverThread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
                application.getPlatform().notify(ex.getMessage());
            }
        }
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
            MenuData inGameMenu = Mapper.mapInGameMenuData(params);
            menu.showMenu(inGameMenu);
            if (menu.canStartTrack()) {
                restart(true);
            }
        }

        for (int i1 = settings.getGameSpeed(); i1 > 0; i1--) {
            engine.timerTime = Utils.calculateTimerTime(startedTime, finishedTime, pausedTime, currentTimeMillis);
            captureOrPlay();
            LevelState levelState = engine.getLevelState();
            if (LevelState.CRASHED_IN_AIR == levelState && delayedRestartAtTime == 0L) {
                trainer.onCrash(() -> {
                    recorder.stopCapture();
                    delayedRestartAtTime = currentTimeMillis + 3000L;
                    view.showInfoMessage(str.s(S.CRASHED), 3000);
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
                    view.showInfoMessage(str.s(S.CRASHED), 3000);
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
                if (settings.isRecordingEnabled() && recorder.isCapturingMode()) {
                    recorder.saveCapture(lastTrackTime);
                }
                trainer.stop();
                goalLoop();

                ModEntity modEntity = application.getModManager().getModState();
                if (GameMode.CAMPAIGN == params.getMode()) {
                    updateProgress(modEntity, params);
                    application.getModManager().saveModState();
                }
                MenuData finishedMenu = Mapper.getFinishedMenuData(params, lastTrackTime, modEntity, attemptCount);
                menu.showMenu(finishedMenu);

                this.attemptCount = 0;
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

//        boolean menuShown = application.isMenuShown();
//        boolean alive = application.isAlive();
//        boolean menuNotEmpty = !menu.isCurrentMenuEmpty();
        while (application.isMenuShown() && application.isAlive() && !menu.isCurrentMenuEmpty()) {
            if (application.isOnPause()) {
                while (application.isOnPause()) {
                    if (!application.isAlive() || menu.isCurrentMenuEmpty()) {
                        break;
                    }
                    try {
                        Thread.sleep(25L);
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

    private void updateProgress(ModEntity entity, GameParams params) {
        int currentTrack = params.getTrack();
        int currentTrackNumber = currentTrack + 1;
        int currentLevel = params.getLevel();

        int unlockedLevels = entity.getUnlockedLevels();
        int unlockedTracks = entity.getUnlockedTracksCount(currentLevel);
        int currentLevelTacksCount = entity.getTracksCount(currentLevel);
        int levelsCount = entity.getLevelsCount();
        int leaguesCount = Engine.leagueProperties.size(); //todo refactor

        //finished all tracks in selected level
        if (currentTrackNumber == currentLevelTacksCount) {
            int unlockedLevel = Math.min(currentLevel + 2, levelsCount);
            entity.setUnlockedLevels(unlockedLevel);

            int unlockedLeague = Math.min(currentLevel + 1, leaguesCount);
            //unlock when finished at not completed level
            if (currentLevel >= unlockedLevels - 1) {
                entity.setUnlockedLeagues(unlockedLeague);
                entity.setSelectedLeague(unlockedLeague);
            }

            //skip the last level that was already unlocked
            if (unlockedLevel < levelsCount) {
                entity.setUnlockedTracks(unlockedLevel, 0);
            }

            entity.setSelectedLevel(unlockedLevel - 1);
            entity.setSelectedTrack(0);
        }

        //finished non last track in selected level
        if (currentTrackNumber < currentLevelTacksCount && currentLevel < levelsCount) {
            int unlockedTrack = Math.min(currentTrack + 1, currentLevelTacksCount - 1);
            if (unlockedTrack > unlockedTracks) {
                entity.setUnlockedTracks(currentLevel, unlockedTrack);
            }
            entity.setSelectedTrack(unlockedTrack);
            entity.setSelectedLevel(currentLevel);
            entity.setSelectedLeague(params.getLeague());
        }
    }

    private void captureOrPlay() {
        if (GameMode.ONLINE == params.getMode()) {
            sendState();
        }
        if (GameMode.REPLAY == params.getMode()) {
            recorder.setCapturingMode(false);
            trainer.stop();
            player.replay(params.getMode());
            return;
        }
        if (!trainer.isTrainingMode()) {
            if (engine.timerTime > 0) {
                player.nextGhostState();
            } else {
                player.reset();
            }
        }
        if (recorder.isCapturing()) {
            if (engine.timerTime > 0) {
                recorder.captureState();
            } else {
                recorder.startCapture();
            }
        } else {
            if (settings.isRecordingEnabled() && engine.timerTime > 0) {
                recorder.startCapture();
                recorder.captureState();
            }
        }
    }

    public void restart(boolean showLevelName) {
        this.attemptCount++;
        view.setDrawTimer(params != null && params.getMode() != GameMode.TRACK_EDITOR && !application.isMenuShown());
        engine.resetToStart_MAYBE();
        startedTime = 0;
        finishedTime = 0;
        pausedTime = 0;
        delayedRestartAtTime = 0;
        if (showLevelName && !application.isMenuShown())
            view.showInfoMessage(engine.getTrackPhysic().getTrack().name, 3000);
        engine.resetControls();
        keyboardHandler.resetButtonsTouch();
        trainer.stop();
        trainer.prepare();
        if (params != null && params.getMode() != GameMode.REPLAY) {
            setGhost(engine.getTrackPhysic().getTrack());
        }
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
            view.showInfoMessage(str.s(S.WHEELIE), 1000);
        else
            view.showInfoMessage(str.s(S.FINISHED), 1000);
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
        view.setDrawTimer(params != null && params.getMode() != GameMode.TRACK_EDITOR);
        if (pausedTimeStarted > 0 && startedTime > 0) {
            pausedTime += (System.currentTimeMillis() - pausedTimeStarted);
            pausedTimeStarted = 0;
        }
    }

    private void saveScore(long lastTrackTime) {
        if (trainer.isTrainingMode() || engine.isGodMode()) {
            return;
        }
        Score score = new Score();
        score.setTrackId(Utils.getTrackId(engine.getTrackPhysic().getTrack()));
        score.setLeague(params.getLeague());
        score.setTime(lastTrackTime);
        score.setName(settings.getPlayerName());
        application.getHighScoreManager().saveHighScore(score);

        if (settings.isTestFeaturesEnabled()) {
            try {
                ScoreDto scoreDto = Mapper.toDto(score, params.getRoomId());
                String url = application.getServerConfig().url();
                application.runOnIOThread(() -> APIClient.serverCall(url, serverApi -> serverApi.sendScore(scoreDto)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Achievement.achievements.get(Achievement.Type.TRIAL_MASTER).increment();
        }
    }

    public GdView getView() {
        return view;
    }

    public Engine getEngine() {
        return engine;
    }

    public void startTrack(GameParams params) {
        this.params = params;
        this.attemptCount = 0;
        if (GameMode.REPLAY == params.getMode()) {
            player.reset();
            player.setTrackRecord(params.getTrackRecord());
            Achievement.achievements.get(Achievement.Type.SERIES_LOVER).increment();
            loadTrack(params.getTrackData(), params.getTrackData().league);
            engine.setReplayMode(true);
            engine.setGodMode(true);
            engine.startAutoplay();
            menu.menuToGame();
            return;
        } else {
            engine.setReplayMode(false);
            engine.setGodMode(settings.isGodModeEnabled());
            recorder.setCapturingMode(settings.isRecordingEnabled());
        }
        if (GameMode.TRACK_EDITOR == params.getMode()) {
            restart(false);
            recorder.setCapturingMode(false);
            recorder.reset();
            engine.setEditMode(true);
            view.setDrawTimer(false);
            loadTrack(params.getTrackData(), params.getTrackData().league);
            return;
        }
        if (GameMode.ONLINE == params.getMode()) {
            joinRoom();
        }
        TrackData track = params.getTrackData();
        engine.setEditMode(false);
        int league = GameMode.CAMPAIGN == params.getMode()
                ? params.getLeague()
                : track.getLeague();
        loadTrack(track, league);
        if (GameMode.CAMPAIGN == params.getMode()) {
            setGhost(track);
        }
        engine.unlockKeys();
        view.setDrawTimer(true);
        menu.menuToGame();
    }

    private void loadTrack(TrackData track, int league) {
        try {
            engine.setLeague(league);
            //Utils.flip(track);
            engine.loadTrack(track);
        } catch (InvalidTrackException e) {
            e.printStackTrace();
        }
    }

    private void setGhost(TrackData track) {
        player.setTrackRecord(null);
        player.reset();
        try {
            if (!settings.isGhostEnabled()) {
                return;
            }
            String trackId = Utils.getTrackId(track);
            Score score = application.getHighScoreManager()
                    .getLocalHighScores(trackId, engine.league)
                    .get(engine.league)
                    .get(0);
            String fileName = Fmt.recordName(track.getName(), score.getTime(), engine.league, trackId);
            TrackRecord trackRecord = application.getFileStorage().readRecord(fileName);
            player.setTrackRecord(trackRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setRecordingEnabled(boolean flag) {
        settings.setRecordingEnabled(flag);
        if (!flag) {
            recorder.setCapturingMode(false);
            recorder.reset();
        } else {
            recorder.setCapturingMode(true);
        }
    }

    public void setShadowsEnabled(boolean enabled) {
        settings.setShadowsEnabled(enabled);
        engine.getTrackPhysic().setShadowsEnabled(enabled);
    }

    public void setGodModeEnabled(boolean enabled) {
        settings.setGodModeEnabled(enabled);
        engine.setGodMode(enabled);
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
        player.reset();
        trainer.setSavepoint();
    }

    public void resetState() {
        disconnectFromRoom();
        trainer.stop();
        recorder.reset();
        player.reset();
        engine.resetOpponents();
    }

    public GameParams getParams() {
        return params;
    }

    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    private void joinRoom() {
        if (settings.isTestFeaturesEnabled()) {
            application.runOnIOThread(() -> {
                String json = String.format("%s%s", Constants.HANDSHAKE_PREFIX, settings.getPlayerId());
                for (int i = 0; i < 10; i++) { //todo
                    send(json);
                }
            });
        }
    }

    private void sendState() {
        if (settings.isTestFeaturesEnabled()) {
            OpponentState opponentState = new OpponentState(settings.getPlayerName(), engine.getState());
            application.runOnIOThread(() -> {
                String msg = String.format("%s%s", settings.getPlayerId(), Utils.toJson(opponentState));
                send(msg);
            });
        }
    }

    private void disconnectFromRoom() {
        if (params != null && params.getMode() == GameMode.ONLINE) {
            if (settings.isTestFeaturesEnabled()) {
                try {
                    String url = application.getServerConfig().url();
                    OpponentDisconnectRequest req = new OpponentDisconnectRequest();
                    req.setRoomId(params.getRoomId());
                    req.setId(settings.getPlayerId());
                    for (int i = 0; i < 10; i++) { //todo
                        application.runOnIOThread(() -> APIClient.serverCall(url, serverApi -> serverApi.opponentDisconnect(req)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receiveOnlinePacket() {
        while (!Thread.interrupted()) {
            try {
                if (udpSocket.isClosed()) {
                    return;
                }
                byte[] buf2 = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf2, buf2.length);
                udpSocket.receive(packet);
                application.runOnIOThread(() -> handleOnlineMessage(packet));
            } catch (SocketTimeoutException ignore) {
            } catch (Exception e) {
                e.printStackTrace();//todo
            }
        }
    }

    private void handleOnlineMessage(DatagramPacket packet) {
        try {
            String opponentId = Utils.mapOpponentId(packet);
            String message = new String(packet.getData(), StandardCharsets.UTF_8).trim();
            OpponentState opponentState = Utils.fromJson(message.substring(message.indexOf("{")), OpponentState.class);
            if (Constants.DISCONNECTED.equals(opponentState.getStatus())) {
                engine.removeOpponent(opponentId);
            } else {
                engine.addOpponent(opponentId, opponentState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(String msg) {
        try {
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(
                    bytes, bytes.length,
                    InetAddress.getByName(application.getServerConfig().getHost()), 27654
            );
            udpSocket.send(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
