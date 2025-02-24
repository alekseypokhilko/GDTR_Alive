package org.happysanta.gdtralive.android.menu.screens;

import static org.happysanta.gdtralive.android.Helpers.s;

import android.text.Html;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.InGameScreenProvider;
import org.happysanta.gdtralive.android.menu.Menu;
import org.happysanta.gdtralive.android.menu.MenuFactory;
import org.happysanta.gdtralive.android.menu.MenuScreen;
import org.happysanta.gdtralive.android.menu.MenuUtils;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.element.MenuItem;
import org.happysanta.gdtralive.android.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.game.Constants;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.Utils;
import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.levels.InvalidTrackException;
import org.happysanta.gdtralive.game.levels.PackTrackReference;
import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.modes.GameMode;
import org.happysanta.gdtralive.game.modes.MenuData;
import org.happysanta.gdtralive.game.modes.GameParams;
import org.happysanta.gdtralive.game.visual.Fmt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyMenu implements InGameScreenProvider {
    private final GdApplication application;
    private final Menu menu;
    private final Game game;
    private final MenuScreen screen;
    private final MenuScreen finishedScreen;
    private final MenuScreen inGameScreen;
    private final OptionsMenuElement trackSelector;
    private MenuScreen trackSelectorCurrentMenu;

    private final TrackOfTheDayMenu trackOfTheDay;

    public List<PackTrackReference> tracks = new ArrayList<>();
    public int currentTrack = 0;

    MenuFactory menuFactory;

    public DailyMenu(Menu menu, MenuScreen parent, GdApplication application, MenuFactory menuFactory) {
        this.menu = menu;
        this.game = application.getGame();
        this.application = application;
        this.menuFactory = menuFactory;
        this.screen = new MenuScreen(s(R.string.daily_challenge), parent);
        this.trackOfTheDay = new TrackOfTheDayMenu(menu, screen, application);
        this.inGameScreen = new MenuScreen(Fmt.sp(s(R.string.ingame), s(R.string.daily_challenge)), screen);
        inGameScreen.addItem(new MenuAction(s(R.string._continue), MenuAction.CONTINUE, menu,
                item -> Helpers.getGDActivity().menuToGame()));
        inGameScreen.addItem(new MenuAction(s(R.string.restart), MenuAction.RESTART, menu,
                item -> {
                    game.restart(true);
                    game.resetState();
                    menu.menuToGame();
                }
        ));
        inGameScreen.addItem(new MenuAction(s(R.string.back), menu, it -> {
            game.resetState();
            menu.menuBack();
        }));
        this.trackSelector = new OptionsMenuElement("", 0, menu, new String[1], false, screen,
                value -> {
                    OptionsMenuElement it = (OptionsMenuElement) value;
                    if (it._charvZ()) {
                        it.update();
                        trackSelectorCurrentMenu = it.getCurrentMenu();
                        menu.setCurrentMenu(trackSelectorCurrentMenu);
                    }
                }
        );
        this.finishedScreen = new MenuScreen(s(R.string.finished), screen);
    }

    public void buildScreen() {
        screen.clear();
        screen.setTitle(s(R.string.daily_challenge) + " " + Constants.DATE_FORMAT.format(new Date()));
        screen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.daily_objective))));
        try {
            //todo determine current daily pack
            String packName = "62feb1e5-b00d-45a1-8bad-6316a4e28e7e_Track 450 (Yaskevich Igor) [150.150.150]";
            tracks = application.getFileStorage().getDailyTracksReferences(packName);
        } catch (Exception e) {
            Helpers.showToast("File loading error: " + e.getMessage());
        }
        int dailyTracksCount = tracks.size();
        int completedDailyTracksCount = trackSelector.getUnlockedCount();
        String[] dailyTrackNames = new String[dailyTracksCount];
        for (int i = 0; i < dailyTracksCount; i++) {
            dailyTrackNames[i] = tracks.get(i).getName();
        }
        screen.addItem(menu.backAction());
        screen.addItem(MenuUtils.emptyLine(false));
        screen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.track_of_day_description))));
        screen.addItem(new MenuItem(s(R.string.track_of_day), trackOfTheDay.getScreen(), menu,
                item -> trackOfTheDay.buildScreen()));
        screen.addItem(MenuUtils.emptyLine(false));
        screen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.daily_tracks_description))));
        trackSelector.setOptions(dailyTrackNames);
        trackSelector.setText("Tracks(" + completedDailyTracksCount + "/" + dailyTracksCount + ")");

        screen.addItem(trackSelector);
        screen.addItem(new MenuAction(s(R.string.start), menu, it -> {
            currentTrack = trackSelector.getSelectedOption();
            startDailyTrack(currentTrack);
        }));
//        screen.addItem(MenuUtils.createEmptyLine(true));
//        screen.addItem(MenuUtils.createEmptyLine(true));
        //todo time calculation
//        screen.addItem(new HighScoreTextMenuElement(Html.fromHtml(getString(R.string.my_time) + ": " + "15:43.17"), true));
//        screen.addItem(new HighScoreTextMenuElement(Html.fromHtml(getString(R.string.highscores) + ": " + Constants.DATE_FORMAT.format(new Date())), true));
//        //todo calculate high scores
//        List<String> score = game.getFormattedScores("6e0d58e4-0b84-4c80-a283-b67060f1a372", 0);
//        for (int place = 0; place < score.size(); place++) {
//            //todo replay on click
//            screen.addItem(new HighScoreTextMenuElement(score.get(place), place, true));
//        }
    }

    public MenuScreen buildFinishedScreen() {
        trackSelector.setSelectedOption(currentTrack + 1);
        trackSelector.setUnlockedCount(Math.max(currentTrack + 1, trackSelector.getUnlockedCount()));
        trackSelector.setText("Tracks(" + trackSelector.getUnlockedCount() + "/" + tracks.size() + ")");

        finishedScreen.clear();
        //TODO
        finishedScreen.addItem(new TextMenuElement(Html.fromHtml("<b>" + s(R.string.time) + "</b>: " + Utils.getDurationString(100L))));
        //TODO
        for (String s : application.getHighScoreManager().getFormattedScores("data.getTrackGuid()", 0)) {
            finishedScreen.addItem(new TextMenuElement(s));
        }
        finishedScreen.addItem(new MenuAction(s(R.string.next), MenuAction.NEXT, menu,
                item -> {
                    currentTrack++;
                    startDailyTrack(currentTrack);
                }
        ));
        finishedScreen.addItem(new MenuAction(s(R.string.restart), MenuAction.RESTART, menu,
                item -> {
                    game.restart(true);
                    menu.menuToGame();
                }));
        finishedScreen.addItem(menu.backAction());
        return finishedScreen;
    }

    private void startDailyTrack(int trackNumber) {
        PackTrackReference trackRef = tracks.get(trackNumber);
        try {
            TrackParams track = application.getFileStorage().getLevelFromPack(trackRef.getPackGuid(), trackRef.getGuid());
            game.startTrack(GameParams.of(GameMode.DAILY, track));
        } catch (InvalidTrackException e) {
            application.notify("File loading error: " + e.getMessage());
        }
    }

    @Override
    public MenuScreen getInGameScreen(MenuData data) {
        inGameScreen.resetHighlighted();
        return inGameScreen;
    }

    @Override
    public GameMode getInGameScreenMode() {
        return GameMode.DAILY;
    }

    public MenuScreen getScreen() {
        return screen;
    }
}
