package org.happysanta.gdtralive.android.menu.screens;

import static org.happysanta.gdtralive.android.Helpers.s;

import android.text.Html;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.Menu;
import org.happysanta.gdtralive.android.menu.MenuScreen;
import org.happysanta.gdtralive.android.menu.MenuUtils;
import org.happysanta.gdtralive.android.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.api.external.GdApplication;
import org.happysanta.gdtralive.game.api.exception.InvalidTrackException;
import org.happysanta.gdtralive.game.api.model.TrackParams;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.util.Fmt;

import java.util.List;

public class TrackOfTheDayMenu {
    private final GdApplication application;
    private final Menu menu;
    private final Game game;
    private final MenuScreen screen;
    private final MenuScreen inGameScreen;

    public TrackOfTheDayMenu(Menu menu, MenuScreen parent, GdApplication application) {
        this.menu = menu;
        this.game = application.getGame();
        this.application = application;
        this.screen = new MenuScreen(s(R.string.track_of_day), parent);
        this.inGameScreen = new MenuScreen(Fmt.sp(s(R.string.ingame), s(R.string.track_of_day)), screen);
        inGameScreen.addItem(new MenuAction(s(R.string._continue), MenuAction.CONTINUE, menu, __ -> application.menuToGame()));
        inGameScreen.addItem(new MenuAction(s(R.string.restart), MenuAction.RESTART, menu,
                __ -> {
                    game.restart(true);
                    game.resetState();
                    menu.menuToGame();
                }
        ));
        inGameScreen.addItem(new MenuAction(s(R.string.back), menu, it -> {
            game.resetState();
            menu.menuBack();
        }));
    }

    public void buildScreen() {
        //todo prepared level files with dates
        String packName = "5b48212c-222d-4b2d-9316-fff5e6804be3_}{ott@bb)4 [10.14.6]";
        String trackGuid = "6e0d58e4-0b84-4c80-a283-b67060f1a372";

        String trackName = "???";
        String author = "???";
        TrackParams track = null;
        try {
            track = application.getFileStorage().getLevelFromPack(packName, trackGuid);
            trackName = track.getName();
            author = track.getAuthor();
        } catch (InvalidTrackException e) {
            application.notify("File loading error: " + e.getMessage());
        }
        screen.clear();
        screen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.track_of_day_objective))));
        screen.addItem(MenuUtils.emptyLine(true));
        screen.addItem(new TextMenuElement(Html.fromHtml("Track: " + trackName)));
        screen.addItem(new TextMenuElement(Html.fromHtml("Author: " + author)));
        TrackParams finalTrack = track;
        screen.addItem(new MenuAction(s(R.string.start), menu, __ -> game.startTrack(GameParams.of(GameMode.TRACK_OF_THE_DAY, finalTrack))));
        screen.addItem(menu.backAction());
        screen.addItem(MenuUtils.emptyLine(true));
        screen.addItem(MenuUtils.emptyLine(true));
        screen.addItem(new HighScoreTextMenuElement(Html.fromHtml(s(R.string.highscores) + ":"), true));
        List<String> score = application.getHighScoreManager().getFormattedScores(trackGuid, 0);
        for (int place = 0; place < score.size(); place++) {
            //todo replay on click
            screen.addItem(new HighScoreTextMenuElement(score.get(place), place, true));
        }
    }

    public MenuScreen getScreen() {
        return screen;
    }
}
