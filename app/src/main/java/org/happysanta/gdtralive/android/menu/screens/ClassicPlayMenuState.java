package org.happysanta.gdtralive.android.menu.screens;

import static org.happysanta.gdtralive.android.Helpers.getStringArray;
import static org.happysanta.gdtralive.android.Helpers.s;
import static org.happysanta.gdtralive.android.Helpers.showAlert;
import static org.happysanta.gdtralive.android.Helpers.showConfirm;

import android.text.Html;
import android.view.View;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.menu.Menu;
import org.happysanta.gdtralive.android.menu.MenuFactory;
import org.happysanta.gdtralive.android.menu.MenuScreen;
import org.happysanta.gdtralive.android.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.element.MenuItem;
import org.happysanta.gdtralive.android.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.game.util.Utils;
import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.modes.GameMode;
import org.happysanta.gdtralive.game.modes.MenuData;
import org.happysanta.gdtralive.game.modes.MenuType;
import org.happysanta.gdtralive.game.modes.GameParams;
import org.happysanta.gdtralive.game.storage.ModEntity;
import org.happysanta.gdtralive.game.visual.Fmt;

import java.util.List;
import java.util.Map;

public class ClassicPlayMenuState {
    private final MenuFactory menuFactory;
    private final GdApplication application;
    private final Menu menu;

    public OptionsMenuElement levelSelector;
    private final OptionsMenuElement leagueSelector;
    public OptionsMenuElement trackSelector;
    private MenuScreen trackSelectorCurrentMenu;

    private String[] leagues;
    private final String[] difficultyLevels;
    private final int[] selectedTrack = new int[100];

    //todo extract common parts / split view and logic
    public ClassicPlayMenuState(Menu menu, GdApplication application, MenuFactory menuFactory) {
        this.menu = menu;
        this.menuFactory = menuFactory;
        this.application = application;

        //init
        difficultyLevels = application.getModManager().getLevelNames().toArray(new String[0]);
        getLevel().initIfClear();

        try {
            selectedTrack[getLevel().getSelectedLevel()] = getLevel().getSelectedTrack();
        } catch (ArrayIndexOutOfBoundsException _ex) {
            getLevel().setSelectedLevel(0);
            getLevel().setSelectedTrack(0);
            selectedTrack[getLevel().getSelectedLevel()] = getLevel().getSelectedTrack();
        }

        leagues = application.getModManager().getLeagueNames();

        levelSelector = new OptionsMenuElement(s(R.string.level), getLevel().getSelectedLevel(), menu, difficultyLevels, false, menuFactory.get(MenuType.PLAY_CLASSIC),
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    if (it._charvZ()) {
                        MenuScreen levelSelectorCurrentMenu = it.getCurrentMenu();
                        menu.setCurrentMenu(levelSelectorCurrentMenu);
                    }
                    trackSelector.setOptions(application.getModManager().getLeagueTrackNames(it.getSelectedOption()), false);
                    trackSelector.setUnlockedCount(getLevel().getUnlockedTracksCount(it.getSelectedOption()));
                    trackSelector.setSelectedOption(selectedTrack[it.getSelectedOption()]);
                });
        trackSelector = new OptionsMenuElement(s(R.string.track), selectedTrack[getLevel().getSelectedLevel()], menu, application.getModManager().getLeagueTrackNames(getLevel().getSelectedLevel()), false, menuFactory.get(MenuType.PLAY_CLASSIC),
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    if (it._charvZ()) {
                        it.setUnlockedCount(getLevel().getUnlockedTracksCount(levelSelector.getSelectedOption()));
                        it.update();
                        trackSelectorCurrentMenu = it.getCurrentMenu();
                        menu.setCurrentMenu(trackSelectorCurrentMenu);
                    }
                    selectedTrack[levelSelector.getSelectedOption()] = it.getSelectedOption();
                }
        );
        leagueSelector = new OptionsMenuElement(s(R.string.league), getLevel().getSelectedLeague(), menu, leagues, false, menuFactory.get(MenuType.PLAY_CLASSIC),
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    if (it._charvZ()) {
                        MenuScreen leagueSelectorCurrentMenu = it.getCurrentMenu();
                        it.setScreen(menu.currentMenu);
                        menu.setCurrentMenu(leagueSelectorCurrentMenu);
                    }
                });
        try {
            trackSelector.setUnlockedCount(getLevel().getUnlockedTracksCount(getLevel().getSelectedLevel()));
        } catch (ArrayIndexOutOfBoundsException _ex) {
            trackSelector.setUnlockedCount(0);
        }

        levelSelector.setUnlockedCount(getLevel().getUnlockedLevels());
        leagueSelector.setUnlockedCount(getLevel().getUnlockedLeagues());
        //playMenu.addItem(new ActionMenuElement(getString(R.string.random_track) + ">", this, item -> startRandomTrack()));
    }

    public MenuScreen createHighScore(Map<MenuType, MenuScreen> r) {
        MenuScreen hs = new MenuScreen(s(R.string.highscores), r.get(MenuType.PLAY_CLASSIC));
        hs.setBeforeShowAction(this::showHighScoreMenu);
        hs.addItem(menu.backAction());
        hs.setBuilder((s, data) -> {
            s.clear();
            s.setTitle(Fmt.colon(s(R.string.highscores), application.getModManager().getTrackName(data.getSelectedLevel(), data.getSelectedTrack())));
            s.addItem(new HighScoreTextMenuElement(Html.fromHtml(Fmt.colon(s(R.string.league), application.getModManager().getLeagueNames()[data.getSelectedLeague()])), true));

            List<String> scores = application.getHighScoreManager()
                    .getFormattedScores(data.getSelectedLeague(), data.getSelectedLevel(), data.getSelectedTrack());
            for (int place = 0; place < scores.size(); place++) {
                s.addItem(new HighScoreTextMenuElement(scores.get(place), place, true));
            }
            if (scores.isEmpty())
                s.addItem(new TextMenuElement(s(R.string.no_highscores)));

            s.addItem(menu.backAction());
            s.highlightElement();
            return s;
        });
        return hs;
    }

    public MenuScreen createInGameClassic(Map<MenuType, MenuScreen> r) {
        MenuScreen inGame = new MenuScreen(s(R.string.ingame), r.get(MenuType.PLAY_CLASSIC));
        inGame.addItem(new MenuAction(s(R.string._continue), MenuAction.CONTINUE, menu, item -> application.menuToGame()));
        inGame.addItem(new MenuAction(Fmt.colon(s(R.string.restart), ""), MenuAction.RESTART, menu, item -> menu.menuToGame()));
        inGame.addItem(new MenuAction(s(R.string.training_mode), menu, item -> {
            //todo remove Helpers.getGDActivity()
            Helpers.getGDActivity().actionButton.setVisibility(View.VISIBLE);
            application.menuToGame();
        }));
        inGame.addItem(menu.createAction(MenuAction.LIKE, item -> application.notify("Coming soon")));
        inGame.addItem(new MenuItem(s(R.string.options), r.get(MenuType.OPTIONS), menu, null));
        inGame.addItem(new MenuItem(s(R.string.help), r.get(MenuType.HELP), menu, null));
        inGame.addItem(menu.createAction(MenuAction.PLAY_MENU, item -> actionGoToPlayMenu()));
        inGame.setBuilder((s, data) -> {
            if (data != null) {
                s.getActions(MenuAction.RESTART).setText(Fmt.colon(s(R.string.restart), data.getTrackName()));
            }
            s.resetHighlighted();
            return s;
        });
        return inGame;
    }

    public MenuScreen createFinishedClassic(Map<MenuType, MenuScreen> r) {
        MenuScreen finished = new MenuScreen(s(R.string.finished), r.get(MenuType.PLAY_CLASSIC));
        finished.setBuilder(this::buildClassicFinishedScreen);
        return finished;
    }

    public void transformPlayClassic(MenuScreen s) {
        s.addItem(new MenuAction(Fmt.ra(s(R.string.start)), menu, item -> {
            if (levelSelector.getSelectedOption() > levelSelector.getUnlockedCount()
                    || trackSelector.getSelectedOption() > trackSelector.getUnlockedCount()
                    || leagueSelector.getSelectedOption() > leagueSelector.getUnlockedCount()) {
                showAlert("GD Classic", s(R.string.complete_to_unlock), null);
            } else {
                int league = leagueSelector.getSelectedOption();
                int level = levelSelector.getSelectedOption();
                int track = trackSelector.getSelectedOption();
                application.getGame().startTrack(GameParams.of(GameMode.CLASSIC, application.getModManager().loadLevel(level, track), league, level, track));
            }
        }));
        s.addItem(levelSelector);
        s.addItem(trackSelector);
        s.addItem(leagueSelector);
        s.addItem(new MenuItem(s(R.string.highscores), menuFactory.get(MenuType.HIGH_SCORE), menu, null));
        s.addItem(new MenuAction(Fmt.ra(s(R.string.unlock_all)), -1, menu,
                item -> {
                    showConfirm(s(R.string.unlock_all), s(R.string.unlock_all_text), () -> {
                        int leaguesCount = application.getModManager().getLeagueThemes().size();
                        getLevel().setUnlockedLeagues(leaguesCount);
                        getLevel().setUnlockedLevels(leaguesCount);
                        getLevel().unlockAllTracks();
                        leagues = application.getModManager().getLeagueNames();
                        leagueSelector.setOptions(leagues);
                        leagueSelector.setUnlockedCount(getLevel().getUnlockedLeagues());
                        levelSelector.setUnlockedCount(getLevel().getUnlockedLevels());
                        application.getGame().levelsManager.setTemporallyUnlocked(true);
                        application.notify("Unlocked all tracks, leagues and difficulties");
                    }, () -> {
                    });
                }));
        s.addItem(menu.backAction());
    }

    private MenuScreen showFinishMenu(MenuScreen finishedMenu, MenuData data) {
        finishedMenu.clear();
        long millis = data.getLastTrackTime();
        finishedMenu.addItem(new TextMenuElement(Html.fromHtml("<b>" + s(R.string.time) + "</b>: " + Utils.getDurationString(millis))));
        for (String s : application.getHighScoreManager().getFormattedScores(data.getTrackGuid(), data.getSelectedLeague())) {
            finishedMenu.addItem(new TextMenuElement(s));
        }

        int completedCount = getLevel().getUnlockedTracksCount(data.getSelectedLevel()); // TODO test
        finishedMenu.addItem(new TextMenuElement(Html.fromHtml(String.format(s(R.string.tracks_completed_tpl),
                completedCount,
                application.getModManager().getLevelTracksCount(data.getSelectedLevel()),  //todo just track counts
                difficultyLevels[data.getSelectedLevel()]
        ))));

        boolean leagueCompleted = data.getNewSelectedLeague() != data.getSelectedLeague();
        if (leagueCompleted) {
            boolean flag = true;
            for (int i1 = 0; i1 < 3; i1++)
                if (getLevel().getUnlockedTracksCount(i1) != application.getModManager().getLevelTracksCount(i1) - 1)
                    flag = false;

            if (!flag)
                finishedMenu.addItem(new TextMenuElement(s(R.string.level_completed_text)));
            finishedMenu.addItem(new TextMenuElement(s(R.string.congratulations) + leagues[data.getNewSelectedLeague()]));
            try {
                //todo crashed on league unlock
                showAlert(s(R.string.league_unlocked), s(R.string.league_unlocked_text) + leagues[data.getNewSelectedLeague()], null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            menuFactory.get(MenuType.IN_GAME_CLASSIC)
                    .getActions(MenuAction.RESTART)
                    .setText(Fmt.colon(s(R.string.restart), data.getTrackName()));

            finishedMenu.addItem(new MenuAction(Fmt.colon(s(R.string.next), application.getModManager().getTrackName(data.getSelectedLevel(), data.getNewSelectedTrack())), MenuAction.NEXT, menu,
                    item -> {
                        int league = leagueSelector.getSelectedOption();
                        int level = levelSelector.getSelectedOption();
                        int track = trackSelector.getSelectedOption();
                        application.getGame().startTrack(GameParams.of(GameMode.CLASSIC, application.getModManager().loadLevel(level, track), league, level, track));
                    }
            ));
        }
        finishedMenu.addItem(new MenuAction(Fmt.colon(s(R.string.restart), data.getTrackName()), MenuAction.RESTART, menu, item -> menu.menuToGame()));
        finishedMenu.addItem(menu.createAction(MenuAction.PLAY_MENU, item -> actionGoToPlayMenu()));
        finishedMenu.resetHighlighted();
        finishedMenu.highlightElement();
        return finishedMenu;
    }

    private ModEntity getLevel() {
        return application.getGame().getLevelsManager().getCurrentLevel();
    }

    public void showHighScoreMenu() {
        MenuData data = new MenuData();
        data.setSelectedLevel(levelSelector.getSelectedOption());
        data.setSelectedTrack(trackSelector.getSelectedOption());
        data.setSelectedLeague(leagueSelector.getSelectedOption());
        menuFactory.get(MenuType.HIGH_SCORE).build(data);
    }

    public void resetSelectors() {
        trackSelector.setOptions(application.getModManager().getLeagueTrackNames(levelSelector.getSelectedOption()), false);
        if (menu.currentMenu == trackSelectorCurrentMenu) {
            selectedTrack[levelSelector.getSelectedOption()] = trackSelector.getSelectedOption();
        }
        trackSelector.setUnlockedCount(getLevel().getUnlockedTracksCount(levelSelector.getSelectedOption()));
        trackSelector.setSelectedOption(selectedTrack[levelSelector.getSelectedOption()]);
    }

    private void actionGoToPlayMenu() {
        application.getGame().resetState();
        menu.menuBack();
    }

    public MenuScreen getScreen() {
        //todo remove
        return menuFactory.get(MenuType.PLAY_CLASSIC);
    }

    private MenuScreen buildClassicFinishedScreen(MenuScreen finishedMenu, MenuData data) {
        updateSelectors(data);
        int place = application.getHighScoreManager()
                .getHighScores(data.getTrackGuid(), data.getSelectedLeague())
                .getPlace(data.getSelectedLeague(), data.getLastTrackTime()); //todo npe?
        if (place >= 0 && place <= 2) {
            finishedMenu.clear();
            finishedMenu.addItem(new HighScoreTextMenuElement(getStringArray(R.array.finished_places)[place], place, false));
            long millis = data.getLastTrackTime();
            finishedMenu.addItem(new TextMenuElement(Utils.getDurationString(millis)));
            finishedMenu.addItem(menu.createAction(MenuAction.OK, item -> showFinishMenu(finishedMenu, data)));
            menu.m_blZ = false;
            return finishedMenu;
        } else {
            return showFinishMenu(finishedMenu, data);
        }
    }

    private void updateSelectors(MenuData data) {
        trackSelector.setUnlockedCount(data.getNewUnlockedTrackCount());
        trackSelector.setSelectedOption(data.getNewSelectedTrack());
        if (data.getNewSelectedLevel() != data.getSelectedLevel()) {
            trackSelector.setOptions(application.getModManager().getLeagueTrackNames(data.getNewSelectedLevel()), true);
        }
        leagueSelector.setUnlockedCount(data.getNewUnlockedLeagueCount());
        leagueSelector.setSelectedOption(data.getNewSelectedLeague());
        levelSelector.setUnlockedCount(data.getNewUnlockedLevelCount());
        levelSelector.setSelectedOption(data.getNewSelectedLevel());
    }
}
