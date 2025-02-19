package org.happysanta.gdtralive.game.mod;

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
    private List<LeagueProperties> leagueProperties;
    private GameTheme gameTheme;
    private InterfaceTheme interfaceTheme;

    public ThemeHeader getHeader() {
        return header;
    }

    public List<LeagueProperties> getLeagueThemes() {
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

    public void setLeagueThemes(List<LeagueProperties> leagueProperties) {
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
        header.setName( "Gravity Defied Original");
        header.setDescription( "Default game parameters. No changes.");
        header.setAuthor( "GD Alive");
        header.setDate( "2024-04-09");

        Theme theme = new Theme();
        theme.setHeader(header);
        theme.setInterfaceTheme(new InterfaceTheme());
        theme.setLevelNames(initDefaultLevelNames());
        theme.setLeagueThemes(LeagueProperties.getDefaultLeagueProperties());
        theme.setGameTheme(new GameTheme());
        return theme;
    }

    public static Theme amoledMod() {
        Theme theme = defaultTheme();

        ThemeHeader header = new ThemeHeader();
        header.setGuid( "b5221ae2-c4ea-4225-9d51-818fdfad34a9");
        header.setName( "Gravity Defied Original Dark");
        header.setDescription( "Based on Gravity Defied Original. Dark theme adapted for AMOLED displays");
        header.setAuthor( "GD Alive");
        header.setDate( "2024-04-09");
        theme.setHeader(header);

        GameTheme game = theme.getGameTheme();
        game.setProp(GameTheme.density, 5f);
        game.setProp(GameTheme.spriteDensity, 3.8f); //todo check null
        game.setProp(GameTheme.gameBackgroundColor, new Color(0, 0, 0));
        game.setProp(GameTheme.trackLineColor, new Color(255, 255, 255));
        game.setProp(GameTheme.perspectiveColor, new Color(150, 150, 150));
        game.setProp(GameTheme.startFlagColor, new Color(0, 255, 0));
        game.setProp(GameTheme.finishFlagColor, new Color(100, 100, 255));

        InterfaceTheme itheme = theme.getInterfaceTheme();
        itheme.setProp(InterfaceTheme.infoMessageColor, new Color(255, 255, 255));
        itheme.setProp(InterfaceTheme.progressBackgroundColor, new Color(0, 0, 0));
        itheme.setProp(InterfaceTheme.progressColor, new Color(41, 170, 39));
        itheme.setProp(InterfaceTheme.menuBackgroundColor, 0x00ffffff);
        itheme.setProp(InterfaceTheme.matteMenu, false);
        itheme.setProp(InterfaceTheme.keyboardBackgroundColor, 0x00ffffff);
        itheme.setProp(InterfaceTheme.keyboardTextColor, 0xffffffff);
        itheme.setProp(InterfaceTheme.menuTitleTextColor, 0xffffffff);
        itheme.setProp(InterfaceTheme.menuTitleBackgroundColor, 0x00ffffff);
        itheme.setProp(InterfaceTheme.frameBackgroundColor, 0x00ffffff);
        itheme.setProp(InterfaceTheme.mainMenuBackgroundColor, 0x00ffffff);
        itheme.setProp(InterfaceTheme.textColor, 0xffffffff);
        itheme.setProp(InterfaceTheme.splashColor, new Color(255, 255, 255));
        itheme.setProp(InterfaceTheme.lockSkinIndex, 2);

        LeagueProperties league100 = theme.getLeagueThemes().get(0);
        league100.setProp(LeagueProperties.backWheelsColor, new Color(255, 255, 255));
        league100.setProp(LeagueProperties.backWheelsSpokeColor, new Color(255, 255, 255));
        league100.setProp(LeagueProperties.frontWheelsColor, new Color(255, 255, 255));
        league100.setProp(LeagueProperties.forkColor, new Color(228, 228, 228));
        league100.setProp(LeagueProperties.drawWheelLines, true);
        league100.setProp(LeagueProperties.bikeLinesColor, new Color(255, 255, 255));
        league100.setProp(LeagueProperties.bikerHeadColor, new Color(156, 0, 0));
        league100.setProp(LeagueProperties.bikerLegColor, new Color(0, 0, 0));
        league100.setProp(LeagueProperties.bikerBodyColor, new Color(0, 0, 128));
        league100.setProp(LeagueProperties.steeringColor, new Color(0, 0, 0));
        league100.setProp(LeagueProperties.bikeColor, new Color(255, 255, 255));
        league100.setProp(LeagueProperties.frontWheelsSpokeColor, new Color(255, 255, 255));

        LeagueProperties league175 = theme.getLeagueThemes().get(1);
        league175.setProp(LeagueProperties.backWheelsColor, new Color(255, 255, 255));
        league175.setProp(LeagueProperties.backWheelsSpokeColor, new Color(255, 255, 255));
        league175.setProp(LeagueProperties.frontWheelsColor, new Color(255, 255, 255));
        league175.setProp(LeagueProperties.frontWheelsSpokeColor, new Color(255, 255, 255));
        league175.setProp(LeagueProperties.forkColor, new Color(228, 228, 228));
        league175.setProp(LeagueProperties.drawWheelLines, true);
        league175.setProp(LeagueProperties.bikeLinesColor, new Color(255, 255, 255));
        league175.setProp(LeagueProperties.bikerHeadColor, new Color(156, 0, 0));
        league175.setProp(LeagueProperties.bikerLegColor, new Color(0, 0, 0));
        league175.setProp(LeagueProperties.bikerBodyColor, new Color(0, 0, 128));
        league175.setProp(LeagueProperties.steeringColor, new Color(0, 0, 0));
        league175.setProp(LeagueProperties.bikeColor, new Color(255, 255, 255));

        LeagueProperties league220 = theme.getLeagueThemes().get(2);
        league220.setProp(LeagueProperties.backWheelsColor, new Color(255, 255, 255));
        league220.setProp(LeagueProperties.backWheelsSpokeColor, new Color(255, 255, 255));
        league220.setProp(LeagueProperties.frontWheelsColor, new Color(255, 255, 255));
        league220.setProp(LeagueProperties.frontWheelsSpokeColor, new Color(255, 255, 255));
        league220.setProp(LeagueProperties.forkColor, new Color(228, 228, 228));
        league220.setProp(LeagueProperties.drawWheelLines, true);
        league220.setProp(LeagueProperties.bikeLinesColor, new Color(255, 255, 255));
        league220.setProp(LeagueProperties.bikerHeadColor, new Color(156, 0, 0));
        league220.setProp(LeagueProperties.bikerLegColor, new Color(0, 0, 0));
        league220.setProp(LeagueProperties.bikerBodyColor, new Color(0, 0, 128));
        league220.setProp(LeagueProperties.steeringColor, new Color(0, 0, 0));
        league220.setProp(LeagueProperties.bikeColor, new Color(255, 255, 255));

        LeagueProperties league325 = theme.getLeagueThemes().get(3);
        league325.setProp(LeagueProperties.backWheelsColor, new Color(255, 255, 255));
        league325.setProp(LeagueProperties.backWheelsSpokeColor, new Color(255, 255, 255));
        league325.setProp(LeagueProperties.frontWheelsColor, new Color(255, 255, 255));
        league325.setProp(LeagueProperties.frontWheelsSpokeColor, new Color(255, 255, 255));
        league325.setProp(LeagueProperties.forkColor, new Color(228, 228, 228));
        league325.setProp(LeagueProperties.drawWheelLines, true);
        league325.setProp(LeagueProperties.bikeLinesColor, new Color(255, 255, 255));
        league325.setProp(LeagueProperties.bikerHeadColor, new Color(156, 0, 0));
        league325.setProp(LeagueProperties.bikerLegColor, new Color(0, 0, 0));
        league325.setProp(LeagueProperties.bikerBodyColor, new Color(0, 0, 128));
        league325.setProp(LeagueProperties.steeringColor, new Color(0, 0, 0));
        league325.setProp(LeagueProperties.bikeColor, new Color(255, 255, 255));

        return theme;
    }
}