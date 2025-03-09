package org.happysanta.gdtralive.game.api.dto;

import org.happysanta.gdtralive.game.api.model.Color;

import java.util.ArrayList;
import java.util.List;

public class Theme {
    public static final int MENU_TITLE_LAYOUT_TOP_PADDING = 25;
    public static final int MENU_TITLE_LAYOUT_BOTTOM_PADDING = 13;
    public static final int MENU_TITLE_LAYOUT_X_PADDING = 30;
    public static final int MENU_TITLE_FONT_SIZE = 30;
    public static final int GAME_MENU_BUTTON_LAYOUT_WIDTH = 40;
    public static final int GAME_MENU_BUTTON_LAYOUT_HEIGHT = 56;

    private ThemeHeader header;
    private List<String> levelNames;
    private List<LeagueTheme> leagueProperties;
    private GameTheme gameTheme;
    private InterfaceTheme interfaceTheme;

    public ThemeHeader getHeader() {
        return header;
    }

    public List<LeagueTheme> getLeagueThemes() {
        return leagueProperties;
    }

    public GameTheme getGameTheme() {
        return gameTheme;
    }

    public InterfaceTheme getInterfaceTheme() {
        return interfaceTheme;
    }

    public List<String> getLevelNames() {
        return levelNames;
    }

    public void setHeader(ThemeHeader header) {
        this.header = header;
    }

    public void setLevelNames(List<String> levelNames) {
        this.levelNames = levelNames;
    }

    public void setLeagueThemes(List<LeagueTheme> leagueProperties) {
        this.leagueProperties = leagueProperties;
    }

    public void setGameTheme(GameTheme gameTheme) {
        this.gameTheme = gameTheme;
    }

    public void setInterfaceTheme(InterfaceTheme interfaceTheme) {
        this.interfaceTheme = interfaceTheme;
    }

    private static List<String> initDefaultLevelNames() {
        List<String> levelNames = new ArrayList<>();
        levelNames.add("Easy");
        levelNames.add("Medium");
        levelNames.add("Hard");
        return levelNames;
    }

    public static Theme defaultTheme() {

        ThemeHeader header = new ThemeHeader();
        header.setGuid("e46a37c0-69e1-4646-8f9c-b47247586635");
        header.setName("GDTR Original");
        header.setDescription("Default game parameters. No changes.");
        header.setAuthor("GD Alive");
        header.setDate("2024-04-09");

        Theme theme = new Theme();
        theme.setHeader(header);
        theme.setInterfaceTheme(new InterfaceTheme());
        theme.setLevelNames(initDefaultLevelNames());
        theme.setLeagueThemes(LeagueTheme.getDefaultLeagueProperties());
        theme.setGameTheme(new GameTheme());
        return theme;
    }

    public static Theme amoledMod() {
        Theme theme = defaultTheme();

        ThemeHeader header = new ThemeHeader();
        header.setGuid("b5221ae2-c4ea-4225-9d51-818fdfad34a9");
        header.setName("GDTR Black");
        header.setDescription("Based on Gravity Defied Original. Dark theme adapted for AMOLED displays");
        header.setAuthor("GD Alive");
        header.setDate("2024-04-09");
        theme.setHeader(header);

        GameTheme game = theme.getGameTheme();
        game.setGameBackgroundColor(new Color(0, 0, 0));
        game.setTrackLineColor(new Color(255, 255, 255));
        game.setPerspectiveColor(new Color(150, 150, 150));
        game.setStartFlagColor(new Color(0, 255, 0));
        game.setFinishFlagColor(new Color(100, 100, 255));

        InterfaceTheme itheme = theme.getInterfaceTheme();
        itheme.setInfoMessageColor(new Color(255, 255, 255));
        itheme.setProgressBackgroundColor(new Color(0, 0, 0));
        itheme.setProgressColor(new Color(41, 170, 39));
        itheme.setMenuBackgroundColor(Color.of("0x00ffffff"));
        itheme.setKeyboardBackgroundColor(Color.of("0x00ffffff"));
        itheme.setKeyboardTextColor(Color.of("0xffffffff"));
        itheme.setMenuTitleTextColor(Color.of("0xffffffff"));
        itheme.setMenuTitleBackgroundColor(Color.of("0x00ffffff"));
        itheme.setFrameBackgroundColor(Color.of("0x00ffffff"));
        itheme.setMainMenuBackgroundColor(Color.of("0x00ffffff"));
        itheme.setTextColor(Color.of("0xffffffff"));
        itheme.setSplashColor(new Color(255, 255, 255));
        itheme.setLockSkinIndex(2);

        LeagueTheme league100 = theme.getLeagueThemes().get(0);
        league100.setBackWheelsColor(new Color(255, 255, 255));
        league100.setBackWheelsSpokeColor(new Color(255, 255, 255));
        league100.setFrontWheelsColor(new Color(255, 255, 255));
        league100.setFrontWheelsSpokeColor(new Color(255, 255, 255));
        league100.setForkColor(new Color(255, 255, 255));
        league100.setDrawWheelLines(true);
        league100.setBikeLinesColor(new Color(255, 255, 255));
        league100.setBikerHeadColor(new Color(255, 255, 255));
        league100.setBikerLegColor(new Color(255, 255, 255));
        league100.setBikerBodyColor(new Color(255, 255, 255));
        league100.setSteeringColor(new Color(255, 255, 255));
        league100.setBikeColor(new Color(255, 255, 255));

        LeagueTheme league175 = theme.getLeagueThemes().get(1);
        league175.setBackWheelsColor(new Color(255, 255, 255));
        league175.setBackWheelsSpokeColor(new Color(255, 255, 255));
        league175.setFrontWheelsColor(new Color(255, 255, 255));
        league175.setFrontWheelsSpokeColor(new Color(255, 255, 255));
        league175.setForkColor(new Color(255, 255, 255));
        league175.setDrawWheelLines(true);
        league175.setBikeLinesColor(new Color(255, 255, 255));
        league175.setBikerHeadColor(new Color(255, 255, 255));
        league175.setBikerLegColor(new Color(255, 255, 255));
        league175.setBikerBodyColor(new Color(255, 255, 255));
        league175.setSteeringColor(new Color(255, 255, 255));
        league175.setBikeColor(new Color(255, 255, 255));

        LeagueTheme league220 = theme.getLeagueThemes().get(2);
        league220.setBackWheelsColor(new Color(255, 255, 255));
        league220.setBackWheelsSpokeColor(new Color(255, 255, 255));
        league220.setFrontWheelsColor(new Color(255, 255, 255));
        league220.setFrontWheelsSpokeColor(new Color(255, 255, 255));
        league220.setForkColor(new Color(255, 255, 255));
        league220.setDrawWheelLines(true);
        league220.setBikeLinesColor(new Color(255, 255, 255));
        league220.setBikerHeadColor(new Color(255, 255, 255));
        league220.setBikerLegColor(new Color(255, 255, 255));
        league220.setBikerBodyColor(new Color(255, 255, 255));
        league220.setSteeringColor(new Color(255, 255, 255));
        league220.setBikeColor(new Color(255, 255, 255));

        LeagueTheme league325 = theme.getLeagueThemes().get(3);
        league325.setBackWheelsColor(new Color(255, 255, 255));
        league325.setBackWheelsSpokeColor(new Color(255, 255, 255));
        league325.setFrontWheelsColor(new Color(255, 255, 255));
        league325.setFrontWheelsSpokeColor(new Color(255, 255, 255));
        league325.setForkColor(new Color(255, 255, 255));
        league325.setDrawWheelLines(true);
        league325.setBikeLinesColor(new Color(255, 255, 255));
        league325.setBikerHeadColor(new Color(255, 255, 255));
        league325.setBikerLegColor(new Color(255, 255, 255));
        league325.setBikerBodyColor(new Color(255, 255, 255));
        league325.setSteeringColor(new Color(255, 255, 255));
        league325.setBikeColor(new Color(255, 255, 255));

        return theme;
    }
}