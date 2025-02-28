package org.happysanta.gdtralive.android.menu.screens;

import static org.happysanta.gdtralive.android.Helpers.s;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.AMenu;
import org.happysanta.gdtralive.android.menu.MenuFactory;
import org.happysanta.gdtralive.android.menu.MenuScreen;
import org.happysanta.gdtralive.android.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.game.api.GdApplication;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.model.ModEntity;

public class CampaignSelectors {
    private final GdApplication application;
    private final AMenu menu;

    private final OptionsMenuElement levelSelector;
    private final OptionsMenuElement leagueSelector;
    private final OptionsMenuElement trackSelector;
    private MenuScreen trackSelectorCurrentMenu;

    private String[] leagueNames;
    private String[] difficultyLevels;
    private final int[] selectedTrack = new int[100];

    //todo extract common parts / split view and logic
    public CampaignSelectors(AMenu menu, GdApplication application, MenuFactory menuFactory) {
        this.menu = menu;
        this.application = application;

        this.difficultyLevels = application.getModManager().getLevelNames().toArray(new String[0]);
        this.leagueNames =  application.getModManager().getLeagueNames();

        try {
            selectedTrack[getLevel().getSelectedLevel()] = getLevel().getSelectedTrack();
        } catch (ArrayIndexOutOfBoundsException _ex) {
            getLevel().setSelectedLevel(0);
            getLevel().setSelectedTrack(0);
            selectedTrack[getLevel().getSelectedLevel()] = getLevel().getSelectedTrack();
        }


        levelSelector = new OptionsMenuElement(s(R.string.level), getLevel().getSelectedLevel(), menu, this.difficultyLevels, false, menuFactory.get(MenuType.PLAY_CAMPAIGN),
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    if (it._charvZ()) {
                        MenuScreen levelSelectorCurrentMenu = it.getCurrentMenu();
                        menu.setCurrentMenu(levelSelectorCurrentMenu);
                    }
                    getTrackSelector().setOptions(application.getModManager().getLeagueTrackNames(it.getSelectedOption()), false);
                    getTrackSelector().setUnlockedCount(getLevel().getUnlockedTracksCount(it.getSelectedOption()));
                    getTrackSelector().setSelectedOption(selectedTrack[it.getSelectedOption()]);
                });
        trackSelector = new OptionsMenuElement(s(R.string.track), selectedTrack[getLevel().getSelectedLevel()], menu, application.getModManager().getLeagueTrackNames(getLevel().getSelectedLevel()), false, menuFactory.get(MenuType.PLAY_CAMPAIGN),
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    if (it._charvZ()) {
                        it.setUnlockedCount(getLevel().getUnlockedTracksCount(getLevelSelector().getSelectedOption()));
                        it.update();
                        trackSelectorCurrentMenu = it.getCurrentMenu();
                        menu.setCurrentMenu(trackSelectorCurrentMenu);
                    }
                    selectedTrack[getLevelSelector().getSelectedOption()] = it.getSelectedOption();
                }
        );
        leagueSelector = new OptionsMenuElement(s(R.string.league), getLevel().getSelectedLeague(), menu, this.leagueNames, false, menuFactory.get(MenuType.PLAY_CAMPAIGN),
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
    }

    public OptionsMenuElement getLevelSelector() {
        return levelSelector;
    }

    public OptionsMenuElement getLeagueSelector() {
        return leagueSelector;
    }

    public OptionsMenuElement getTrackSelector() {
        return trackSelector;
    }

    public String[] getLeagueNames() {
        return leagueNames;
    }

    public String[] getDifficultyLevels() {
        return difficultyLevels;
    }

    public void setDifficultyLevels(String[] difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
        this.levelSelector.setOptions(difficultyLevels);
    }

    public void setLeagueNames(String[] leagueNames) {
        this.leagueNames = leagueNames;
        this.leagueSelector.setOptions(leagueNames);
    }

    private ModEntity getLevel() {
        return application.getModManager().getModState();
    }

    public void resetSelectors() {
        trackSelector.setOptions(application.getModManager().getLeagueTrackNames(levelSelector.getSelectedOption()), false);
        if (menu.currentMenu == trackSelectorCurrentMenu) {
            selectedTrack[levelSelector.getSelectedOption()] = trackSelector.getSelectedOption();
        }
        trackSelector.setUnlockedCount(getLevel().getUnlockedTracksCount(levelSelector.getSelectedOption()));
        trackSelector.setSelectedOption(selectedTrack[levelSelector.getSelectedOption()]);
    }

    public void updateSelectors(MenuData data) {
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
