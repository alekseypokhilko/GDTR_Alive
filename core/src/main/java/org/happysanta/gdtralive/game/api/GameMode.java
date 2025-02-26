package org.happysanta.gdtralive.game.api;

public enum GameMode {
    CLASSIC(MenuType.IN_GAME_CLASSIC, MenuType.FINISHED_CLASSIC),
    REPLAY(MenuType.IN_GAME_REPLAY, MenuType.FINISHED_SINGLE),
    TRACK_OF_THE_DAY(MenuType.IN_GAME_TOD, MenuType.FINISHED_TOD),
    DAILY(MenuType.IN_GAME_DAILY, MenuType.FINISHED_DAILY),
    TRACK_EDITOR(MenuType.IN_GAME_TRACK_EDITOR, MenuType.FINISHED_EDIT),
    TRACK_EDITOR_PLAY(MenuType.IN_GAME_TRACK_EDITOR, MenuType.FINISHED_EDIT),
    SINGLE_TRACK(MenuType.IN_GAME_SINGLE, MenuType.FINISHED_SINGLE),
    RANDOM(MenuType.IN_GAME_PLAY, MenuType.FINISHED_PLAY);

    public final MenuType inGame;
    public final MenuType finished;

    GameMode(MenuType inGame, MenuType finished) {
        this.inGame = inGame;
        this.finished = finished;
    }
}
