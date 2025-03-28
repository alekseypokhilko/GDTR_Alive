package org.happysanta.gdtralive.game.api;

public enum GameMode {
    CAMPAIGN(MenuType.IN_GAME_CAMPAIGN, MenuType.FINISHED_CAMPAIGN),
    REPLAY(MenuType.IN_GAME_REPLAY, MenuType.FINISHED_SINGLE),
    ONLINE(MenuType.IN_GAME_RANDOM, MenuType.FINISHED_RANDOM),
    TRACK_OF_THE_DAY(MenuType.IN_GAME_TOD, MenuType.FINISHED_TOD),
    DAILY(MenuType.IN_GAME_DAILY, MenuType.FINISHED_DAILY),
    TRACK_EDITOR(MenuType.IN_GAME_TRACK_EDITOR, MenuType.FINISHED_EDIT),
    TRACK_EDITOR_PLAY(MenuType.IN_GAME_TRACK_EDITOR, MenuType.FINISHED_EDIT),
    SINGLE_TRACK(MenuType.IN_GAME_SINGLE, MenuType.FINISHED_SINGLE),
    RANDOM(MenuType.IN_GAME_RANDOM, MenuType.FINISHED_RANDOM);

    public final MenuType inGame;
    public final MenuType finished;

    GameMode(MenuType inGame, MenuType finished) {
        this.inGame = inGame;
        this.finished = finished;
    }
}
