package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.Platform;
import org.happysanta.gdtralive.game.api.S;
import org.happysanta.gdtralive.game.api.dto.GameTheme;
import org.happysanta.gdtralive.game.api.dto.InterfaceTheme;
import org.happysanta.gdtralive.game.api.dto.LeagueTheme;
import org.happysanta.gdtralive.game.api.dto.OpponentJoinRequest;
import org.happysanta.gdtralive.game.api.dto.RoomDto;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.ThemeHeader;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdStr;
import org.happysanta.gdtralive.game.api.external.GdTrackEditor;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.menu.PlatformMenuElementFactory;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.IMenuItemElement;
import org.happysanta.gdtralive.game.api.menu.element.IOptionsMenuElement;
import org.happysanta.gdtralive.game.api.model.Color;
import org.happysanta.gdtralive.game.api.model.DecorLine;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.LeagueSwitcher;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.api.util.Consumer;
import org.happysanta.gdtralive.game.api.util.Function;
import org.happysanta.gdtralive.game.http.APIClient;
import org.happysanta.gdtralive.game.util.ColorUtil;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MenuFactory<T> {
    public static final int OK = 0;
    public static final int BACK = 1;
    public static final int EXIT = 2;
    public static final int YES = 3;
    public static final int NO = 4;
    public static final int PLAY_MENU = 5;
    public static final int GO_TO_MAIN = 6;
    public static final int RESTART = 7;
    public static final int NEXT = 8;
    public static final int CONTINUE = 9;
    public static final int INSTALL = 10;
    public static final int LOAD = 11;
    public static final int SELECT_FILE = 12;
    public static final int DELETE = 13;
    public static final int RESTART_WITH_NEW_LEVEL = 14;
    public static final int SEND_LOGS = 15;
    public static final int LIKE = 16;

    private final GdStr str;
    private final PlatformMenuElementFactory<T> e;
    private final Map<MenuType, MenuScreen<T>> menus = new HashMap<>();
    private final Application application;
    private final GdPlatform platform;

    private Menu<T> menu;
    private Game game;

    private IOptionsMenuElement<T> levelSelector;
    private IOptionsMenuElement<T> leagueSelector;
    private IOptionsMenuElement<T> trackSelector;
    private MenuScreen<T> trackSelectorCurrentMenu;
    private IOptionsMenuElement<T> themeEditorLeagueSelector;
    private GdTrackEditor trackEditor;

    private String[] leagueNames = new String[10];
    private String[] difficultyLevels = new String[10];
    private final int[] selectedTrack = new int[100];
    private final List<String> modNames = new ArrayList<>(); //todo remove
    private final List<String> themeNames = new ArrayList<>(); //todo remove
    private final List<String> trackNames = new ArrayList<>(); //todo remove

    public MenuFactory(Application application, GdPlatform platform, PlatformMenuElementFactory<T> platformMenuElementFactory) {
        this.application = application;
        this.platform = platform;
        this.e = platformMenuElementFactory;
        this.str = application.getStr();
    }

    public MenuScreen<T> add(MenuType type, Function<Map<MenuType, MenuScreen<T>>, MenuScreen<T>> builder) {
        MenuScreen<T> screen = builder.apply(menus);
        menus.put(type, screen);
        return screen;
    }

    public MenuScreen<T> transform(MenuType type, Consumer<MenuScreen<T>> transformer) {
        MenuScreen<T> screen = menus.get(type);
        transformer.accept(screen);
        return screen;
    }

    public MenuScreen<T> get(MenuType type) {
        MenuScreen<T> screen = menus.get(type);
        if (screen == null) {
//            return new FakeMenuScreen<T>(menu);
            throw new IllegalStateException("FIX ME: no " + type); //todo remove
        }
        return screen;
    }

    //Все переплетено
    //Потяни за нить
    //За ней потянется клубок
    public void init(Menu<T> menu, GdTrackEditor trackEditor, Game game) {
        this.menu = menu;
        this.trackEditor = trackEditor;
        this.game = game;
        add(MenuType.MAIN, r -> e.screen(str.s(S.main), null));
        add(MenuType.PLAY, r -> e.screen(str.s(S.play), r.get(MenuType.MAIN)));
        add(MenuType.RENAME, this::createRenameScreen);
        add(MenuType.PROFILE, this::createProfileScreen);
        add(MenuType.ABOUT, this::createAboutScreen);
        add(MenuType.HELP, this::createHelpScreen);
        add(MenuType.OPTIONS, this::createOptionsScreen);
        add(MenuType.CAMPAIGN, this::createPlayCampaign);

        add(MenuType.FINISHED_RANDOM, this::createFinishedRandom);
        add(MenuType.IN_GAME_RANDOM, this::createInGameRandom);
        add(MenuType.WORKSHOP, r -> e.screen(str.s(S.workshop), r.get(MenuType.MAIN)));
        add(MenuType.MODS, this::createMods);
        add(MenuType.MOD_OPTIONS, this::createModOptions);
        add(MenuType.TRACKS, this::createTracks);
        add(MenuType.TRACK_OPTIONS, this::createTrackOptions);
//        add(MenuType.FINISHED_SINGLE, this::createFinishedSingle);
        add(MenuType.THEMES, this::createThemes);
        add(MenuType.THEME_OPTIONS, this::createThemeOptions);
        add(MenuType.THEME_EDITOR, this::createThemeEditor);

        add(MenuType.TRACK_EDITOR_OPTIONS, this::createTrackEditorOptions);
        add(MenuType.IN_GAME_TRACK_EDITOR, this::createInGameEditor);
        add(MenuType.RECORDINGS, this::createRecordings);
        transform(MenuType.WORKSHOP, this::transformWorkshop);
        add(MenuType.FINISHED_EDIT, r -> this.get(MenuType.IN_GAME_TRACK_EDITOR));

        //        add(MenuType.DAILY, r -> new Menuel.screen(str.s(Strings.daily_challenge), r.get(MenuType.PLAY)));//todo
//        DailyMenu dailyMenu = new DailyMenu(menu, playMenuScreen, application, this); //todo

        add(MenuType.RECORDING_OPTIONS, this::createRecordingOptions);
        add(MenuType.IN_GAME_REPLAY, this::createInGameReplay);
        add(MenuType.ACHIEVEMENTS, this::createAchievements);
        transform(MenuType.PLAY, this::fillPlay);

        createCampaignSelectors();
        add(MenuType.HIGH_SCORE, this::createHighScore);
        add(MenuType.IN_GAME_CAMPAIGN, this::createInGameCampaign);
        add(MenuType.FINISHED_CAMPAIGN, this::createFinishedCampaign);
        transform(MenuType.CAMPAIGN, this::transformPlayCampaign);

        transform(MenuType.MAIN, this::fillMainScreen);
    }

    private MenuScreen<T> createRenameScreen(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.rename), r.get(MenuType.MAIN))
                .builder((screen, data) -> {
                    screen.clear();
                    IInputTextElement<T> nameInput = e.editText(Fmt.colon(str.s(S.value), ""), data.getValue(), null);
                    screen.add(nameInput);
                    screen.add(e.action(str.s(S.save), __ -> {
                        data.getHandler().accept(nameInput.getText());
                    }));
                    screen.add(e.backAction());
                    return screen;
                });
    }

    private MenuScreen<T> createInGameReplay(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.replay), r.get(MenuType.RECORDING_OPTIONS)).builder((s, data) -> {
            s.clear();
            s.add(e.actionContinue(__ -> application.menuToGame()));
            s.add(e.menu(str.s(S.options), this.get(MenuType.OPTIONS), null));
            s.add(e.action(str.s(S.back), it -> {
                game.resetState();
                menu.menuBack();
            }));
            s.resetHighlighted();
            return s;
        });
    }

    private MenuScreen<T> createInGameEditor(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.track_editor), r.get(MenuType.TRACKS)).builder((s, data) -> {
            s.clear();
            TrackParams currentTrack = trackEditor.getCurrentTrack();
            trackEditor.setCurrentTrack(currentTrack == null ? game.getParams().getTrackParams() : currentTrack);
            s.add(e.action(str.s(S.back), __ -> trackEditor.startEditing()));
            s.add(e.action(str.s(S.play), __ -> trackEditor.playTrack()));
            s.add(e.menu(str.s(S.track_options), this.get(MenuType.TRACK_EDITOR_OPTIONS),
                    __ -> this.get(MenuType.TRACK_EDITOR_OPTIONS).build(new MenuData(trackEditor.getCurrentTrack()))));
            s.add(e.menu(str.s(S.options), this.get(MenuType.OPTIONS), null));
            s.add(e.action(str.s(S.save), __ -> trackEditor.saveTrack()));
            s.add(e.action(str.s(S.exit_editor), __ -> {
                this.get(MenuType.TRACKS).build();
                trackEditor.exitEditor();
            }));
            s.resetHighlighted();
            trackEditor.hideLayout();
            return s;
        });
    }

    private MenuScreen<T> createTrackEditorOptions(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = e.screen(str.s(S.track_editor), r.get(MenuType.TRACKS));
        return screen.builder((s, data) -> {
            TrackParams track = data.getTrackRef();
            s.clear();
            s.add(e.backAction());
            s.add(e.textHtmlBold(str.s(S.guid), track.getData().getGuid()));
            s.add(e.textHtmlBold(str.s(S.author), track.getData().getAuthor()));
            s.add(e.editText(Fmt.colon(str.s(S.name)), track.getData().getName(), item -> trackEditor.getCurrentTrack().getData().setName(item.getText().trim())));
            s.add(e.emptyLine(true));
            s.add(e.selector(str.s(S.league), track.getData().league, application.getModManager().getLeagueNames(), screen, it -> {
                if (it._charvZ()) {
                    MenuScreen<T> leagueSelectorCurrentMenu = it.getCurrentMenu();
                    it.setScreen(menu.getCurrentMenu());
                    menu.setCurrentMenu(leagueSelectorCurrentMenu);
                } else {
                    trackEditor.saveLeagueInput(it.getSelectedOption());
                    this.get(MenuType.TRACK_EDITOR_OPTIONS).build(new MenuData(trackEditor.getCurrentTrack()));
                }
            }));

            s.add(e.textHtmlBold(str.s(S.track_properties), null));
            Map<String, Color> colors = ColorUtil.colors;
            String[] colorNames = ColorUtil.colorNames;

            GameTheme gt = trackEditor.getCurrentTrack().getGameTheme();
            gt = gt == null ? new GameTheme() : gt;
            GameTheme finalGt = gt;
            String deadlineY = track.getData().getDeadlineY() == null
                    ? ""
                    : "" + track.getData().getDeadlineY();
            s.add(e.editText(Fmt.colon(str.s(S.deadlineY)), deadlineY, item -> {
                try {
                    trackEditor.getCurrentTrack().getData().setDeadlineY(Integer.parseInt(item.getText().trim()));
                } catch (Exception ignore) {
                    trackEditor.getCurrentTrack().getData().setDeadlineY(null);
                }
            }));
            s.add(e.color(str.s(S.trackLineColor), ColorUtil.indexOf(finalGt.getTrackLineColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                finalGt.setTrackLineColor(colors.get(colorNames[item.getSelectedOption()]));
                application.getModManager().setTrackTheme(trackEditor.getCurrentTrack());
            }));
            s.add(e.color(str.s(S.perspectiveColor), ColorUtil.indexOf(finalGt.getPerspectiveColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                finalGt.setPerspectiveColor(colors.get(colorNames[item.getSelectedOption()]));
                application.getModManager().setTrackTheme(trackEditor.getCurrentTrack());
            }));

            for (int i = 0; i < trackEditor.getCurrentTrack().getData().getDecorLines().size(); i++) {
                s.add(e.textHtmlBold(Fmt.sp(str.s(S.decor_line), (i + 1)), null));
                DecorLine dl = trackEditor.getCurrentTrack().getData().getDecorLines().get(i);
                s.add(e.color(str.s(S.lineColor), ColorUtil.indexOf(dl.getColor()), s, item -> {
                    if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                    dl.setColor(colors.get(colorNames[item.getSelectedOption()]));
                }));
                s.add(e.toggle(str.s(S.perspective), dl.getPerspective() ? 0 : 1, item -> {
                    dl.setPerspective(item.getSelectedOption() == 0);
                }));
                s.add(e.color(str.s(S.perspectiveColor), ColorUtil.indexOf(dl.getColor()), s, item -> {
                    if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                    dl.setPerspectiveColor(colors.get(colorNames[item.getSelectedOption()]));
                }));
            }

            for (int i = 0; i < trackEditor.getCurrentTrack().getData().getLeagueSwitchers().size(); i++) {
                if (i == 0) {
                    continue;
                }
                LeagueSwitcher ls = trackEditor.getCurrentTrack().getData().getLeagueSwitchers().get(i);
                s.add(e.textHtmlBold(Fmt.sp(str.s(S.league_switcher), ("i=" + ls.getPointIndex())), null));
                s.add(e.selector(str.s(S.league), ls.getLeague(), application.getModManager().getLeagueNames(), screen, it -> {
                    if (it._charvZ()) {
                        MenuScreen<T> leagueSelectorCurrentMenu = it.getCurrentMenu();
                        it.setScreen(menu.getCurrentMenu());
                        menu.setCurrentMenu(leagueSelectorCurrentMenu);
                    } else {
                        ls.setLeague(it.getSelectedOption());
                        this.get(MenuType.TRACK_EDITOR_OPTIONS).build(new MenuData(trackEditor.getCurrentTrack()));
                    }
                }));
            }

            System.gc(); //hopefully
            return s;
        });
    }

    private MenuScreen<T> createThemes(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = e.screen(str.s(S.themes), r.get(MenuType.WORKSHOP))
                .setBeforeShowAction(() -> this.get(MenuType.THEMES).build());
        return screen.builder((s, data) -> {
            s.clear();
            themeNames.clear();
            themeNames.add(Theme.defaultTheme().getHeader().getName());
            themeNames.add(Theme.amoledMod().getHeader().getName());
            List<String> filenames = application.getFileStorage().listFiles(GDFile.THEME);
            for (String filename : filenames) {
                themeNames.add(GDFile.THEME.cutExtension(filename));
            }
            s.add(e.backAction());
            String title = str.s(S.import_theme);
            s.add(e.action(title, SELECT_FILE, it -> platform.pickFile(Constants.PICKFILE_THEME_RESULT_CODE)));
            s.add(e.emptyLine(true));
            s.add(e.emptyLine(true));
            for (int i = 0; i < themeNames.size(); i++) { //todo
                try {
                    IMenuItemElement<T> options = e.menu(themeNames.get(i), this.get(MenuType.THEME_OPTIONS),
                            item -> this.get(MenuType.THEME_OPTIONS).build(new MenuData(themeNames.get(item.getValue()))));
                    options.setValue(i);
                    screen.add(options);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return s;
        });
    }

    private MenuScreen<T> createThemeOptions(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.theme), r.get(MenuType.WORKSHOP)).builder((s, data) -> {
            String name = GDFile.THEME.cutExtension(data.getValue());
            Theme theme = data.getTheme() == null
                    ? application.getModManager().loadTheme(name)
                    : data.getTheme();
            s.clear();
            ThemeHeader header = theme.getHeader();
            s.add(e.textHtmlBold(str.s(S.name), header.getName()));
            s.add(e.textHtmlBold(str.s(S.description), header.getDescription()));
            s.add(e.textHtmlBold(str.s(S.guid), header.getGuid()));
            s.add(e.textHtmlBold(str.s(S.author), header.getAuthor()));
            s.add(e.textHtmlBold(str.s(S.date), header.getDate()));
            s.add(e.emptyLine(true));
            s.add(e.action(str.s(S.install), __ -> application.getModManager().installTheme(theme)));
            if (!Constants.IGNORE_SAVE.contains(name)) {
                s.add(e.menu(str.s(S.edit), this.get(MenuType.THEME_EDITOR), __ -> {
                    application.getModManager().activateMod(Constants.ORIGINAL_MOD);
                    this.get(MenuType.THEME_EDITOR).setParent(s).build(new MenuData(theme, theme.getHeader().getName()));
                }));
                s.add(e.action(str.s(S.rename), __ -> {
                    MenuData menuData = new MenuData(header.getName());
                    menuData.setHandler(o -> {
                        String newName = Utils.fixFileName((String) o);
                        header.setName(newName);
                        application.notify("Renamed");
                        s.build(new MenuData(theme, newName));
                        menu.back();
                    });
                    MenuScreen<T> build = this.get(MenuType.RENAME).setParent(s).build(menuData);
                    menu.setCurrentMenu(build);
                }));
            }
            s.add(e.action(str.s(S.copy), __ -> {
                header.setName(Fmt.copy(header.getName()));
                header.setGuid(UUID.randomUUID().toString());
                header.setAuthor(application.getSettings().getPlayerName());
                header.setDate(new Date().toString());
                application.getFileStorage().save(theme, GDFile.THEME, header.getName());
            }));
            if (!Constants.IGNORE_SAVE.contains(name)) {
                s.add(e.action(str.s(S.share), __ -> application.getPlatform().share(GDFile.THEME, header.getName())));
                s.add(e.action(str.s(S.delete), __ -> this.application.getFileStorage().delete(GDFile.THEME, header.getName())));
            }
            s.add(e.backAction(() -> this.get(MenuType.THEMES).build()));
            return s;
        });
    }

    private MenuScreen<T> createThemeEditor(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.theme), r.get(MenuType.THEME_OPTIONS)).builder((s, data) -> {
            Theme theme = data.getTheme();
            reloadTheme(theme);
            ThemeHeader header = theme.getHeader();
            s.clear();
            s.add(e.backAction(() -> {
                application.getFileStorage().save(theme, GDFile.THEME, theme.getHeader().getName());
                r.get(MenuType.THEME_OPTIONS).build(new MenuData(theme, header.getName()));
            }));
            s.add(e.textHtmlBold(str.s(S.name), header.getName()));
            s.add(e.textHtmlBold(str.s(S.description), header.getDescription()));
            s.add(e.textHtmlBold(str.s(S.guid), header.getGuid()));
            s.add(e.textHtmlBold(str.s(S.author), header.getAuthor()));

            Map<String, Color> colors = ColorUtil.colors;
            String[] colorNames = ColorUtil.colorNames;

            s.add(e.emptyLine(true));
            s.add(e.textHtmlBold(str.s(S.track_properties), null));
            GameTheme gt = theme.getGameTheme();
            s.add(e.color(str.s(S.trackLineColor), ColorUtil.indexOf(gt.getTrackLineColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                gt.setTrackLineColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.perspectiveColor), ColorUtil.indexOf(gt.getPerspectiveColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                gt.setPerspectiveColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.gameBackgroundColor), ColorUtil.indexOf(gt.getGameBackgroundColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                gt.setGameBackgroundColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.startFlagColor), ColorUtil.indexOf(gt.getStartFlagColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                gt.setStartFlagColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.finishFlagColor), ColorUtil.indexOf(gt.getFinishFlagColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                gt.setFinishFlagColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));

            s.add(e.emptyLine(true));
            s.add(e.textHtmlBold(str.s(S.interface_properties), null));
            InterfaceTheme it = theme.getInterfaceTheme();
            s.add(e.color(str.s(S.infoMessageColor), ColorUtil.indexOf(it.getInfoMessageColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setInfoMessageColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.progressColor), ColorUtil.indexOf(it.getProgressColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setProgressColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.progressBackgroundColor), ColorUtil.indexOf(it.getProgressBackgroundColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setProgressBackgroundColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.textColor), ColorUtil.indexOf(it.getTextColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setTextColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.menuTitleTextColor), ColorUtil.indexOf(it.getMenuTitleTextColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setMenuTitleTextColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.splashColor), ColorUtil.indexOf(it.getSplashColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setSplashColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.keyboardTextColor), ColorUtil.indexOf(it.getKeyboardTextColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setKeyboardTextColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.selector(str.s(S.keyboardPosition), it.getKeyboardPosition(), Utils.getScaleOptions(), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                it.setKeyboardPosition(Integer.parseInt(Utils.getScaleOptions()[item.getSelectedOption()]));
                reloadTheme(theme);
            }));

            s.add(e.emptyLine(true));
            s.add(e.textHtmlBold(str.s(S.league_properties), null));
            if (themeEditorLeagueSelector == null) {
                themeEditorLeagueSelector = e.selector(str.s(S.league), 0, application.getModManager().getLeagueNames(), r.get(MenuType.THEME_EDITOR), item -> {
                    if (item._charvZ()) {
                        MenuScreen<T> leagueSelectorCurrentMenu = item.getCurrentMenu();
                        item.setScreen(menu.getCurrentMenu());
                        menu.setCurrentMenu(leagueSelectorCurrentMenu);
                    } else {
                        this.get(MenuType.THEME_EDITOR).build(new MenuData(theme, theme.getHeader().getName()));
                    }
                });
            }
            s.add(themeEditorLeagueSelector);

            int selectedLeague = themeEditorLeagueSelector.getSelectedOption();
            application.getGame().getEngine().league = selectedLeague;
            LeagueTheme lt = theme.getLeagueThemes().get(selectedLeague);
            s.add(e.color(str.s(S.backWheelsColor), ColorUtil.indexOf(lt.getBackWheelsColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBackWheelsColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.backWheelDotColor), ColorUtil.indexOf(lt.getBackWheelDotColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBackWheelDotColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.backWheelsSpokeColor), ColorUtil.indexOf(lt.getBackWheelsSpokeColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBackWheelsSpokeColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.frontWheelsColor), ColorUtil.indexOf(lt.getFrontWheelsColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setFrontWheelsColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.frontWheelDotColor), ColorUtil.indexOf(lt.getFrontWheelDotColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setFrontWheelDotColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.frontWheelsSpokeColor), ColorUtil.indexOf(lt.getFrontWheelsSpokeColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setFrontWheelsSpokeColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.toggle(str.s(S.drawWheelLines), lt.isDrawWheelLines() ? 0 : 1, item -> {
                lt.setDrawWheelLines(item.getSelectedOption() == 0);
                reloadTheme(theme);
            }));
            s.add(e.toggle(str.s(S.drawWheelSprite), lt.isDrawWheelSprite() ? 0 : 1, item -> {
                lt.setDrawWheelSprite(item.getSelectedOption() == 0);
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.bikeColor), ColorUtil.indexOf(lt.getBikeColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBikeColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.bikeLinesColor), ColorUtil.indexOf(lt.getBikeLinesColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBikeLinesColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.forkColor), ColorUtil.indexOf(lt.getForkColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setForkColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.steeringColor), ColorUtil.indexOf(lt.getSteeringColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setSteeringColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.bikerHeadColor), ColorUtil.indexOf(lt.getBikerHeadColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBikerHeadColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.bikerBodyColor), ColorUtil.indexOf(lt.getBikerBodyColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBikerBodyColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            s.add(e.color(str.s(S.bikerLegColor), ColorUtil.indexOf(lt.getBikerLegColor()), s, item -> {
                if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                lt.setBikerLegColor(colors.get(colorNames[item.getSelectedOption()]));
                reloadTheme(theme);
            }));
            return s;
        });
    }

    private void reloadTheme(Theme theme) {
        application.getModManager().setTheme(theme);
        application.getModManager().reloadTheme();
    }

    private MenuScreen<T> createModOptions(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.mod), r.get(MenuType.MODS)).builder((s, data) -> {
            Mod mod = data.getMod();
            s.clear();
            s.add(e.textHtmlBold(str.s(S.name), mod.getName()));
            s.add(e.textHtmlBold(str.s(S.guid), mod.getGuid()));
            s.add(e.textHtmlBold(str.s(S.tracks), Fmt.formatLevelsCount(mod)));
            s.add(e.textHtmlBold(str.s(S.author), mod.getAuthor()));
            s.add(e.textHtmlBold(str.s(S.date), mod.getDate()));
            s.add(e.emptyLine(false));

            s.add(e.action(str.s(S.install), __ -> {
                String modName = mod.getName();
                application.getModManager().activateMod(mod);
                application.notify(String.format("Active: %s", mod.getName()));
                this.get(MenuType.CAMPAIGN).setTitle(Fmt.sp(str.s(S.play), modName));
                resetSelectors();
                s.highlightElement();
            }));
            s.add(e.action(str.s(S.copy), __ -> {
                mod.setName(Fmt.copy(mod.getName()));
                mod.setGuid(UUID.randomUUID().toString());
                mod.setAuthor(application.getSettings().getPlayerName());
                mod.setDate(new Date().toString());
                application.getFileStorage().save(mod, GDFile.MOD, mod.getName());
            }));
            if (!Constants.IGNORE_SAVE.contains(mod.getName())) {
                s.add(e.action(str.s(S.rename), __ -> {
                    MenuData menuData = new MenuData(mod.getName());
                    menuData.setHandler(o -> {
                        String newName = Utils.fixFileName((String) o);
                        mod.setName(newName);
                        application.notify("Renamed");
                        s.build(new MenuData(mod, null));
                        menu.back();
                    });
                    MenuScreen<T> build = this.get(MenuType.RENAME).setParent(s).build(menuData);
                    menu.setCurrentMenu(build);
                }));
                s.add(e.action(str.s(S.share), __ -> application.getPlatform().share(GDFile.MOD, mod.getName())));
            }
            s.add(e.action(str.s(S.delete), -1, __ -> this.application.getModManager().deleteMod(mod.getName())));
            s.add(e.backAction(() -> this.get(MenuType.MODS).build()));
            return s;
        });
    }

    private MenuScreen<T> createTrackOptions(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.track), r.get(MenuType.TRACKS)).builder((s, data) -> {
            TrackParams track = data.getTrackRef();
            if (!track.getData().unpacked) {
                Utils.unpackTrack(track.getData());
                track.getData().unpacked = true;
            }
            trackEditor.setCurrentTrack(track);
            s.clear();
            s.add(e.textHtmlBold(str.s(S.name), track.getData().getName()));
            s.add(e.textHtmlBold(str.s(S.guid), Utils.getTrackId(track.getData())));
            s.add(e.textHtmlBold(str.s(S.author), track.getData().getAuthor()));
            s.add(e.emptyLine(false));
            s.add(e.action(str.s(S.play), __ -> game.startTrack(GameParams.of(GameMode.TRACK_EDITOR_PLAY, track))));
            s.add(e.action(str.s(S.edit), __ -> trackEditor.startEditing()));
            s.add(e.action(str.s(S.copy), __ -> {
                track.getData().setName(Fmt.copy(track.getData().getName()));
                track.getData().setGuid(UUID.randomUUID().toString());
                track.getData().setAuthor(application.getSettings().getPlayerName());
                application.getFileStorage().save(track.pack(), GDFile.TRACK, Fmt.trackName(track.getData()));
            }));
            s.add(e.action(str.s(S.rename), __ -> {
                MenuData menuData = new MenuData(track.getData().getName());
                menuData.setHandler(o -> {
                    String newName = Utils.fixFileName((String) o);
                    track.getData().setName(newName);
                    application.getFileStorage().save(track.pack(), GDFile.TRACK, Fmt.trackName(track.getData()));
                    s.build(new MenuData(track));
                    menu.back();
                });
                MenuScreen<T> build = this.get(MenuType.RENAME).setParent(s).build(menuData);
                menu.setCurrentMenu(build);
            }));
            if (data.getValue() != null) {
                s.add(e.action(str.s(S.share), __ -> application.getPlatform().share(GDFile.TRACK, data.getValue())));
                s.add(e.action(str.s(S.delete), -1, __ -> this.application.getFileStorage().delete(GDFile.TRACK, data.getValue())));
            }
            s.add(e.backAction(() -> this.get(MenuType.TRACKS).build()));
            return s;
        });
    }

    private MenuScreen<T> createMods(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.mod_packs), r.get(MenuType.WORKSHOP)).builder((s, data) -> {
            s.clear();
            modNames.clear();
            s.add(e.backAction());
            s.add(e.action(str.s(S.import_mod), SELECT_FILE, it1 -> platform.pickFile(Constants.PICKFILE_MOD_RESULT_CODE)));
            s.add(e.action(str.s(S.import_mrg), SELECT_FILE, it -> platform.pickFile(Constants.PICKFILE_MRG_RESULT_CODE)));
            s.add(e.emptyLine(true));
            s.add(e.emptyLine(true));
            int i = 0;
            List<String> list = application.getFileStorage().listFiles(GDFile.MOD);
            for (String filename : list) {
                String name = GDFile.MOD.cutExtension(filename);
                modNames.add(name);
                IMenuItemElement<T> options = e.menu(name, this.get(MenuType.MOD_OPTIONS),
                        item -> {
                            try {
                                Mod mod = this.application.getModManager().loadMod(modNames.get(item.getValue()));
                                if (mod != null) {
                                    this.get(MenuType.MOD_OPTIONS).build(new MenuData(mod, name));
                                } else {
                                    menu.back();
                                }
                            } catch (Exception ex) {
                                application.notify("Failed to load mod");
                                menu.back();
                            }
                        });
                options.setValue(i);
                s.add(options);
                i++;
            }
            s.resetHighlighted();
            return s;
        });
    }

    private MenuScreen<T> createTracks(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.tracks), r.get(MenuType.WORKSHOP)).builder((s, data) -> {
            s.clear();
            trackNames.clear();
            s.add(e.backAction());
            s.add(e.action(Fmt.ra(str.s(S.create_new_track)), item -> {
                application.getModManager().installTheme(Theme.defaultTheme());
                trackEditor.createNew(application.getSettings().getPlayerName());
            }));
            s.add(e.action(str.s(S.import_track), SELECT_FILE, it1 -> platform.pickFile(Constants.PICKFILE_TRACK_RESULT_CODE)));
            s.add(e.emptyLine(true));
            s.add(e.emptyLine(true));
            int i = 0;
            List<String> list = application.getFileStorage().listFiles(GDFile.TRACK);
            String[] leagueNames = application.getModManager().getLeagueNames();
            for (String filename : list) {
                String name = GDFile.TRACK.cutExtension(filename);
                trackNames.add(name);
                IMenuItemElement<T> options = e.menu(Fmt.trackNameTitle(name, leagueNames), this.get(MenuType.TRACK_OPTIONS),
                        item -> {
                            try {
                                TrackParams trackParams = this.application.getFileStorage().readTrack(trackNames.get(item.getValue()));
                                if (trackParams != null) {
                                    this.get(MenuType.TRACK_OPTIONS).build(new MenuData(trackParams, name));
                                } else {
                                    menu.back();
                                }
                            } catch (Exception ex) {
                                application.notify("Failed to load track");
                                menu.back();
                            }
                        });
                options.setValue(i);
                s.add(options);
                i++;
            }
            s.resetHighlighted();
            return s;
        });
    }

    private void fillPlay(MenuScreen<T> screen) {
        screen.setBeforeShowAction(screen::build).builder((s, data) -> {
            ModManager modManager = application.getModManager();
            s.clear();
            s.setBeforeShowAction(() -> game.startAutoplay(false));
//        s.addItem(new MenuItem(str.s(Strings.daily_challenge), this.get(MenuType.DAILY), menu, __ -> this.get(MenuType.DAILY).build())); //todo
            String[] modNames = modManager.getAllInstalledModNames();
            String currentModName = modManager.getMod().getName();
            int currentModIndex = 0;
            for (int i = 0; i < modNames.length; i++) {
                if (currentModName.equals(modNames[i])) {
                    currentModIndex = i;
                }
            }
            MenuScreen<T> parent = this.get(MenuType.PLAY);
            IOptionsMenuElement<T> campaignSelector = e.selector(str.s(S.campaign_select), currentModIndex, modNames, parent, it -> {
                if (it._charvZ()) {
                    MenuScreen<T> leagueSelectorCurrentMenu = it.getCurrentMenu();
                    it.setScreen(menu.getCurrentMenu());
                    menu.setCurrentMenu(leagueSelectorCurrentMenu);
                }
            });
            if (application.getSettings().isTestFeaturesEnabled()) {
                s.add(e.action("Online β", __ -> {
                    CountDownLatch latch = new CountDownLatch(1);
                    try {
                        String url = application.getServerConfig().url();
                        OpponentJoinRequest req = new OpponentJoinRequest();
                        req.setId(application.getSettings().getPlayerId());
                        application.runOnIOThread(() -> {
                            RoomDto roomDto = APIClient.serverCall(url, serverApi -> serverApi.opponentJoined(req));
                            application.getModManager().activateMod(Constants.ORIGINAL_MOD);
                            game.startTrack(GameParams.of(GameMode.ONLINE,
                                    application.getModManager().loadLevel(1, 2), //todo track id
                                    roomDto.getId()));
                            latch.countDown();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        actionGoToPlayMenu();
                    }
                    try {
                        latch.await(3l, TimeUnit.SECONDS);//todo
                    } catch (InterruptedException ex) {
                        actionGoToPlayMenu();
                    }
                }));
            }
            s.add(e.menu(str.s(S.campaign), this.get(MenuType.CAMPAIGN), __ -> {
                String modName = modNames[campaignSelector.getSelectedOption()];
                modManager.setTemporallyUnlockedAll(false);
                modManager.activateMod(modName);
                setDifficultyLevels(application.getModManager().getLevelNames().toArray(new String[0]));
                setLeagueNames(application.getModManager().getLeagueNames());
                resetSelectors();
            }));
            s.add(campaignSelector);
            s.add(e.action(Fmt.ra(str.s(S.random_track)), __ -> game.startTrack(GameParams.of(GameMode.RANDOM, modManager.getRandomTrack()))));
            s.add(e.backAction(game::resetState));
            return s;
        });
    }

    private MenuScreen<T> createAchievements(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.achievements), r.get(MenuType.MAIN)).builder((s, data) -> {
            s.clear();
            for (Achievement achievement : Achievement.achievements.values()) {
                String title = Fmt.sp(str.s(achievement.getName()), achievement.getProgressFormatted());
                s.add(e.text(title));//todo remove
                //s.add(e.badge(achievement.getLevel(), title)); //todo refactor
                s.add(e.textHtml(str.s(achievement.getDescription())));
                s.add(e.emptyLine(false));
            }
            s.add(e.backAction());
            return s;
        });
    }

    private MenuScreen<T> createRecordings(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.recordings), r.get(MenuType.WORKSHOP)).builder((s, data) -> {
            s.clear();
            s.add(e.backAction());
            s.add(e.action(str.s(S.import_record), SELECT_FILE, it -> platform.pickFile(Constants.PICKFILE_RECORD_RESULT_CODE)));
            s.add(e.emptyLine(false));
            s.add(e.emptyLine(false));
            int i = 0;
            String[] leagueNames = application.getModManager().getLeagueNames();
            for (String name : application.getFileStorage().listFiles(GDFile.RECORD)) {
                try {
                    String title = GDFile.RECORD.cutExtension(name);
                    IMenuItemElement<T> options = e.menu(Fmt.recordTitle(title, leagueNames), this.get(MenuType.RECORDING_OPTIONS),
                            item -> this.get(MenuType.RECORDING_OPTIONS).build(new MenuData((TrackRecord) null, title)));
                    options.setValue(i);
                    s.add(options);
                    i++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return s;
        });
    }

    private MenuScreen<T> createRecordingOptions(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.record_option), r.get(MenuType.RECORDINGS)).builder((s, data) -> {
            TrackRecord rec = data.getRecording() != null
                    ? data.getRecording()
                    : application.getFileStorage().readRecord(data.getValue());
            s.clear();
            s.add(e.textHtmlBold(str.s(S.name), rec.getTrackName()));
            s.add(e.textHtmlBold(str.s(S.guid), Utils.getTrackId(rec.getTrack())));
            s.add(e.textHtmlBold(str.s(S.league), application.getModManager().getLeagueNames()[rec.getLeague()]));
            s.add(e.textHtmlBold(str.s(S.time), Fmt.durationString(rec.getTime())));
            s.add(e.textHtmlBold(str.s(S.date), rec.getDate()));
            s.add(e.emptyLine(true));
            s.add(e.action(str.s(S.replay), __ -> game.startTrack(GameParams.of(rec))));
            s.add(e.action(str.s(S.share), __ -> application.getPlatform().share(GDFile.RECORD, data.getValue())));
            s.add(e.action(str.s(S.save), __ -> this.application.getFileStorage().save(rec, GDFile.RECORD, Fmt.recordName(rec))));
            s.add(e.action(str.s(S.delete), __ -> this.application.getFileStorage().delete(GDFile.RECORD, data.getValue())));
            s.add(e.backAction(() -> {
                this.get(MenuType.RECORDINGS).build();
                game.startAutoplay(true);
            }));
            return s;
        });
    }

    private void transformWorkshop(MenuScreen<T> s) {
        s.add(e.menu(str.s(S.tracks), this.get(MenuType.TRACKS), __ -> this.get(MenuType.TRACKS).build()));
        s.add(e.menu(str.s(S.mod_packs), this.get(MenuType.MODS), __ -> this.get(MenuType.MODS).build()));
        s.add(e.menu(str.s(S.themes), this.get(MenuType.THEMES), __ -> this.get(MenuType.THEMES).build()));
        s.add(e.menu(str.s(S.recordings), this.get(MenuType.RECORDINGS), __ -> this.get(MenuType.RECORDINGS).build()));
        s.add(e.emptyLine(true));
        s.add(e.backAction());
    }

    private MenuScreen<T> createFinishedRandom(Map<MenuType, MenuScreen<T>> r) {
        return e.screen(str.s(S.finished), r.get(MenuType.PLAY)).builder((finishedMenu, data) -> {
            finishedMenu.clear();
            long millis = data.getLastTrackTime();
            finishedMenu.add(e.textHtmlBold(str.s(S.time), Fmt.durationString(millis)));
            for (String s : application.getHighScoreManager().getFormattedScores(data.getTrackId(), data.getSelectedLeague())) {
                finishedMenu.add(e.text(s));
            }
            finishedMenu.add(e.action(Fmt.ra(str.s(S.random_track)), __ -> game.startTrack(GameParams.of(GameMode.RANDOM, application.getModManager().getRandomTrack()))));
            finishedMenu.add(e.reatart(Fmt.colon(str.s(S.restart), data.getTrackName()), __ -> game.restart()));
            finishedMenu.add(e.backAction());
            return finishedMenu;
        });
    }

    private MenuScreen<T> createInGameRandom(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> ig = e.screen(str.s(S.ingame), r.get(MenuType.PLAY));
        ig.add(e.actionContinue(__ -> application.menuToGame()));
        ig.add(e.restartAction("Name", __ -> game.restart()));
        ig.add(e.action(Fmt.ra(str.s(S.random_track)), __ -> game.startTrack(GameParams.of(GameMode.RANDOM, application.getModManager().getRandomTrack()))));
        ig.add(e.action(str.s(S.training_mode), __ -> {
            application.trainingMode();
            application.menuToGame();
        }));
        ig.add(e.menu(str.s(S.options), r.get(MenuType.OPTIONS), null));
        ig.add(e.backAction(game::resetState));
        ig.builder((s, data) -> {
            if (data != null) {
                s.getActions(RESTART).setText(Fmt.colon(str.s(S.restart), data.getTrackName()));
            }
            s.resetHighlighted();
            return s;
        });
        return ig;
    }

    private void fillMainScreen(MenuScreen<T> s) {
        s.setBeforeShowAction(() -> game.startAutoplay(false));
        s.add(e.menu(str.s(S.competition), this.get(MenuType.PLAY), __ -> this.get(MenuType.PLAY).build()));
        s.add(e.menu(str.s(S.workshop), this.get(MenuType.WORKSHOP)));
        s.add(e.menu(str.s(S.profile), this.get(MenuType.PROFILE)));
        //s.add(e.menu(str.s(S.achievements), this.get(MenuType.ACHIEVEMENTS), __ -> this.get(MenuType.ACHIEVEMENTS).build()));
        s.add(e.menu(str.s(S.options), this.get(MenuType.OPTIONS)));
        s.add(e.menu(str.s(S.help), this.get(MenuType.HELP)));
        s.add(e.menu(str.s(S.about), this.get(MenuType.ABOUT)));
        s.add(e.createAction(EXIT, item -> application.exit()));
    }

    private MenuScreen<T> createOptionsScreen(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = e.screen(str.s(S.options), r.get(MenuType.MAIN));
        String[] keySetStrings = str.getStringArray(S.keyset);
        String[] scaleOptions = Utils.getScaleOptions();

        GdSettings settings = application.getSettings();
        screen.add(e.selector(str.s(S.scale), settings.getScale(), scaleOptions, screen,
                item -> {
                    if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                    application.getModManager().adjustScale(item.getSelectedOption());
                }));
        screen.add(e.toggle(str.s(S.recording_enabled), settings.isRecordingEnabled() ? 0 : 1,
                item -> game.setRecordingEnabled(item.getSelectedOption() == 0)));
        screen.add(e.toggle(str.s(S.ghost_enabled), settings.isGhostEnabled() ? 0 : 1,
                item -> settings.setGhostEnabled(item.getSelectedOption() == 0)));
        screen.add(e.toggle(str.s(S.god_mode), settings.isGodModeEnabled() ? 0 : 1,
                item -> game.setGodModeEnabled(item.getSelectedOption() == 0)));

        int localeIndex = 0;
        for (int i = 0; i < Constants.LOCALES.size(); i++) {
            if (Constants.LOCALES.get(i).equals(settings.getLocale())) {
                localeIndex = i;
                break;
            }
        }
        screen.add(e.selector(str.s(S.locale), localeIndex, Constants.LOCALES.toArray(new String[]{}), screen,
                item -> {
                    if (item._charvZ()) menu.setCurrentMenu(item.getCurrentMenu());
                    String code = Constants.LOCALES.get(item.getSelectedOption());
                    settings.setLocale(code);
                    application.getPlatform().changeLocale(code);
                }));
        screen.add(e.toggle(str.s(S.perspective), settings.isPerspectiveEnabled() ? 0 : 1,
                item -> game.setPerspectiveEnabled(item.getSelectedOption() == 0)));
        screen.add(e.toggle(str.s(S.shadows), settings.isShadowsEnabled() ? 0 : 1,
                item -> game.setShadowsEnabled(item.getSelectedOption() == 0)));
        screen.add(e.toggle(str.s(S.driver_sprite), settings.isDriverSpriteEnabled() ? 0 : 1,
                item -> game.setDrawBiker(item.getSelectedOption() == 0)));
        screen.add(e.toggle(str.s(S.bike_sprite), settings.isBikeSpriteEnabled() ? 0 : 1,
                item1 -> game.setDrawBike(item1.getSelectedOption() == 0)));
        screen.add(e.toggle(str.s(S.active_camera), settings.isLookAheadEnabled() ? 0 : 1,
                item -> game.setLookAhead(item.getSelectedOption() == 0)));
        if (application.getPlatform().getPlatform() == Platform.MOBILE) {
            screen.add(e.selector(str.s(S.input), settings.getInputOption(), keySetStrings, screen, item1 -> {
                if (item1._charvZ()) item1.setSelectedOption(item1.getSelectedOption() + 1);
                game.setInputOption(item1.getSelectedOption());
            }));
            screen.add(e.toggle(str.s(S.vibrate_on_touch), settings.isVibrateOnTouchEnabled() ? 0 : 1,
                    item -> settings.setVibrateOnTouchEnabled(item.getSelectedOption() == 0)));
            screen.add(e.toggle(str.s(S.test_features), settings.isTestFeaturesEnabled() ? 0 : 1,
                    item -> settings.setTestFeaturesEnabled(item.getSelectedOption() == 0)));
            screen.add(e.toggle(str.s(S.show_keyboard), settings.isKeyboardInMenuEnabled() ? 0 : 1,
                    item -> {
                        boolean enabled = item.getSelectedOption() == 0;
                        settings.setKeyboardInMenuEnabled(enabled);
                        if (enabled) platform.showKeyboardLayout();
                        else platform.hideKeyboardLayout();
                    }));
        }

        MenuScreen<T> eraseScreen = e.screen(str.s(S.confirm_clear), screen);
        eraseScreen.add(e.text(str.s(S.erase_text1)));
        eraseScreen.add(e.text(str.s(S.erase_text2)));
        eraseScreen.add(e.emptyLine(true));
        eraseScreen.add(e.createAction(NO, item -> menu.menuBack()));
        eraseScreen.add(e.createAction(YES, item -> {
            application.getHighScoreManager().clearAllHighScores();
            application.getPlatform().showAlert(str.s(S.cleared), str.s(S.cleared_text), null);
            menu.menuBack();
        }));

        MenuScreen<T> resetScreen = e.screen(str.s(S.confirm_reset), eraseScreen);
        resetScreen.add(e.text(str.s(S.reset_text1)));
        resetScreen.add(e.text(str.s(S.reset_text2)));
        resetScreen.add(e.emptyLine(true));
        resetScreen.add(e.createAction(NO, item -> menu.menuBack()));
        resetScreen.add(e.createAction(YES, item -> {
            application.getPlatform().showAlert(str.s(S.reset), str.s(S.reset_text), application::fullReset);
            menu.menuBack();
        }));

        eraseScreen.add(e.menu(str.s(S.full_reset), resetScreen, null));
        screen.add(e.menu(str.s(S.clear_highscore), eraseScreen, null));

        screen.add(e.backAction());
        return screen;
    }

    private MenuScreen<T> createHelpScreen(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = e.screen(str.s(S.help), r.get(MenuType.MAIN));
        MenuScreen<T> objectiveHelpScreen = e.screen(str.s(S.objective), screen);
        objectiveHelpScreen.setIsTextScreen(true);
        objectiveHelpScreen.add(e.textHtml(str.s(S.objective_text)));
        objectiveHelpScreen.add(e.backAction());

        MenuScreen<T> keysHelpScreen = e.screen(str.s(S.keys), screen);
        keysHelpScreen.setIsTextScreen(true);
        keysHelpScreen.add(e.textHtml(str.s(S.keyset_text)));
        keysHelpScreen.add(e.action(str.s(S.back), BACK, item -> menu.menuBack()));

        MenuScreen<T> unlockingHelpScreen = e.screen(str.s(S.unlocking), screen);
        unlockingHelpScreen.setIsTextScreen(true);
        unlockingHelpScreen.add(e.textHtml(str.s(S.unlocking_text)));
        unlockingHelpScreen.add(e.backAction());

        MenuScreen<T> highscoreHelpScreen = e.screen(str.s(S.highscores), screen);
        highscoreHelpScreen.setIsTextScreen(true);
        highscoreHelpScreen.add(e.textHtml(str.s(S.highscore_text)));
        highscoreHelpScreen.add(e.backAction());

        MenuScreen<T> optionsHelpScreen = e.screen(str.s(S.options), screen);
        optionsHelpScreen.setIsTextScreen(true);
        optionsHelpScreen.add(e.textHtml(str.s(S.options_text)));
        optionsHelpScreen.add(e.backAction());

        screen.add(e.menu(str.s(S.objective), objectiveHelpScreen, null));
        screen.add(e.menu(str.s(S.keys), keysHelpScreen, null));
        screen.add(e.menu(str.s(S.unlocking), unlockingHelpScreen, null));
        screen.add(e.menu(str.s(S.highscores), highscoreHelpScreen, null));
        screen.add(e.menu(str.s(S.options), optionsHelpScreen, null));
        screen.add(e.backAction());
        return screen;
    }

    private MenuScreen<T> createAboutScreen(Map<MenuType, MenuScreen<T>> r) {
        String title = String.format("%s v%s", str.s(S.about), application.getPlatform().getAppVersion());
        MenuScreen<T> screen = e.screen(title, r.get(MenuType.MAIN));
        screen.setIsTextScreen(true);
        screen.add(e.textHtml(str.s(S.about_text)));
        screen.add(e.backAction());
        return screen;
    }

    private MenuScreen<T> createProfileScreen(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = e.screen(str.s(S.profile), r.get(MenuType.MAIN));
        IInputTextElement<T> nameInput = e.editText(Fmt.colon(str.s(S.name), ""), application.getSettings().getPlayerName(), null);
        screen.add(nameInput);
        screen.add(e.action(str.s(S.save), item -> {
            application.getSettings().setPlayerName(nameInput.getText());
            application.notify(str.s(S.saved));
        }));
        screen.add(e.backAction());
        return screen;
    }

    private MenuScreen<T> createInGameCampaign(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> inGame = e.screen(str.s(S.ingame), r.get(MenuType.CAMPAIGN));
        inGame.add(e.actionContinue(__ -> application.menuToGame()));
        inGame.add(e.reatart(Fmt.colon(str.s(S.restart), ""), item -> menu.menuToGame()));
        inGame.add(e.action(str.s(S.training_mode), __ -> {
            application.trainingMode();
            application.menuToGame();
        }));
        inGame.add(e.menu(str.s(S.options), r.get(MenuType.OPTIONS), null));
        inGame.add(e.menu(str.s(S.help), r.get(MenuType.HELP), null));
        inGame.add(e.createAction(PLAY_MENU, item -> actionGoToPlayMenu()));
        inGame.builder((s, data) -> {
            if (data != null) {
                s.getActions(RESTART).setText(Fmt.colon(str.s(S.restart), data.getTrackName()));
            }
            s.resetHighlighted();
            return s;
        });
        return inGame;
    }

    private MenuScreen<T> createFinishedCampaign(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> finished = e.screen(str.s(S.finished), r.get(MenuType.CAMPAIGN));
        finished.builder((finishedMenu, data) -> {
            resetSelectors();
            int place = application.getHighScoreManager()
                    .getHighScores(data.getTrackId(), data.getSelectedLeague())
                    .getPlace(data.getSelectedLeague(), data.getLastTrackTime()); //todo npe?
            if (place >= 0 && place <= 2) {
                finishedMenu.clear();
                finishedMenu.add(e.highScore(str.getStringArray(S.finished_places)[place], place, false));
                long millis = data.getLastTrackTime();
                finishedMenu.add(e.text(Fmt.durationString(millis)));
                finishedMenu.add(e.createAction(OK, item -> showFinishMenu(finishedMenu, data)));
                menu.m_blZ = false;
                return finishedMenu;
            } else {
                return showFinishMenu(finishedMenu, data);
            }
        });
        return finished;
    }

    private MenuScreen<T> showFinishMenu(MenuScreen<T> finishedMenu, MenuData data) {
        finishedMenu.clear();
        long millis = data.getLastTrackTime();
        finishedMenu.add(e.textHtml(String.format("<b>%s</b>: %s", str.s(S.time), Fmt.durationString(millis))));
        finishedMenu.add(e.textHtml(String.format("<b>%s</b>: %s", str.s(S.ATTEMPTS), data.getAttemptCount())));
        for (String s : application.getHighScoreManager().getFormattedScores(data.getTrackId(), data.getSelectedLeague())) {
            finishedMenu.add(e.text(s));
        }

        ModManager modManager = application.getModManager();
        int completedCount = modManager.getModState().getUnlockedTracksCount(data.getSelectedLevel()) + 1;
        finishedMenu.add(e.textHtml(String.format(str.s(S.tracks_completed_tpl),
                completedCount,
                modManager.getLevelTracksCount(data.getSelectedLevel()),
                difficultyLevels[data.getSelectedLevel()]
        )));

        //todo fix that
        boolean leagueCompleted = data.getNewSelectedLeague() != data.getSelectedLeague();
        if (leagueCompleted) {
            boolean flag = true;
            for (int i1 = 0; i1 < 3; i1++)
                if (modManager.getModState().getUnlockedTracksCount(i1) != modManager.getLevelTracksCount(i1) - 1)
                    flag = false;

            if (!flag) {
                String title = str.s(S.level_completed_text);
                finishedMenu.add(e.text(title));
            }
            String title = str.s(S.congratulations) + leagueNames[data.getNewSelectedLeague()];
            finishedMenu.add(e.text(title));
            try {
                //todo crashed on league unlock
                application.getPlatform().showAlert(str.s(S.league_unlocked), str.s(S.league_unlocked_text) + leagueNames[data.getNewSelectedLeague()], null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            MenuElement<T> restartAction = this.get(MenuType.IN_GAME_CAMPAIGN).getActions(RESTART);
            if (restartAction != null) {
                restartAction.setText(Fmt.colon(str.s(S.restart), data.getTrackName()));
            }

            finishedMenu.add(e.action(Fmt.colon(str.s(S.next), modManager.getTrackName(data.getSelectedLevel(), data.getNewSelectedTrack())), NEXT,
                    item -> {
                        int league = leagueSelector.getSelectedOption();
                        int level = levelSelector.getSelectedOption();
                        int track = trackSelector.getSelectedOption();
                        game.startTrack(GameParams.of(GameMode.CAMPAIGN, modManager.loadLevel(level, track), league, level, track));
                    }
            ));
        }
        finishedMenu.add(e.reatart(Fmt.colon(str.s(S.restart), data.getTrackName()), item -> menu.menuToGame()));
        finishedMenu.add(e.createAction(PLAY_MENU, item -> actionGoToPlayMenu()));
        finishedMenu.resetHighlighted();
        finishedMenu.highlightElement();
        return finishedMenu;
    }

    private MenuScreen<T> createHighScore(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> hs = e.screen(str.s(S.highscores), r.get(MenuType.CAMPAIGN));
        hs.setBeforeShowAction(() -> {
            MenuData data = new MenuData();
            data.setSelectedLevel(levelSelector.getSelectedOption());
            data.setSelectedTrack(trackSelector.getSelectedOption());
            data.setSelectedLeague(leagueSelector.getSelectedOption());
            this.get(MenuType.HIGH_SCORE).build(data);
        });
        hs.add(e.backAction());
        hs.builder((s, data) -> {
            s.clear();
            s.setTitle(Fmt.colon(str.s(S.highscores), application.getModManager().getTrackName(data.getSelectedLevel(), data.getSelectedTrack())));
            s.add(e.getItem(Fmt.colon(str.s(S.league), application.getModManager().getLeagueNames()[data.getSelectedLeague()]), true));

            List<String> scores = application.getHighScoreManager()
                    .getFormattedScores(data.getSelectedLeague(), data.getSelectedLevel(), data.getSelectedTrack());
            for (int place = 0; place < scores.size(); place++) {
                s.add(e.highScore(scores.get(place), place, true));
            }
            if (scores.isEmpty()) {
                String title = str.s(S.no_highscores);
                s.add(e.text(title));
            }

            s.add(e.backAction());
            s.highlightElement();
            return s;
        });
        return hs;
    }

    private MenuScreen<T> createPlayCampaign(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = e.screen(str.s(S.campaign), r.get(MenuType.PLAY));
        screen.setBeforeShowAction(() -> {
            screen.setTitle(application.getModManager().getMod().getName());
            game.startAutoplay(true);
        });
        return screen;
    }

    public void transformPlayCampaign(MenuScreen<T> s) {
        IOptionsMenuElement<T> trackSelector = this.trackSelector;
        IOptionsMenuElement<T> levelSelector = this.levelSelector;
        IOptionsMenuElement<T> leagueSelector = this.leagueSelector;
        s.add(e.action(Fmt.ra(str.s(S.start)), item -> {
            if (levelSelector.getSelectedOption() > levelSelector.getUnlockedCount()
                    || trackSelector.getSelectedOption() > trackSelector.getUnlockedCount()
                    || leagueSelector.getSelectedOption() > leagueSelector.getUnlockedCount()) {
                application.getPlatform().showAlert("GD Classic", str.s(S.complete_to_unlock), null);
            } else {
                int league = leagueSelector.getSelectedOption();
                int level = levelSelector.getSelectedOption();
                int track = trackSelector.getSelectedOption();
                game.startTrack(GameParams.of(GameMode.CAMPAIGN, application.getModManager().loadLevel(level, track), league, level, track));
            }
        }));
        s.add(levelSelector);
        s.add(trackSelector);
        s.add(leagueSelector);
        s.add(e.menu(str.s(S.highscores), this.get(MenuType.HIGH_SCORE), null));
        s.add(e.action(Fmt.ra(str.s(S.skip_track)), item -> application.getPlatform().showConfirm(str.s(S.skip_track), str.s(S.skip_track_text), () -> {
            application.getModManager().skipTrack(levelSelector.getSelectedOption());
            resetSelectors();
        }, () -> {
        })));
        s.add(e.action(Fmt.ra(str.s(S.unlock_all)), item -> application.getPlatform().showConfirm(str.s(S.unlock_all), str.s(S.unlock_all_text), () -> {
            application.getModManager().setTemporallyUnlockedAll(true);
            int leaguesCount = application.getModManager().getLeagueThemes().size();
            ModEntity modState = application.getModManager().getModState();
            modState.setUnlockedLeagues(leaguesCount);
            modState.setUnlockedLevels(leaguesCount);
            modState.unlockAllTracks();
            setLeagueNames(application.getModManager().getLeagueNames());
            leagueSelector.setUnlockedCount(modState.getUnlockedLeagues());
            levelSelector.setUnlockedCount(modState.getUnlockedLevels());
            application.getModManager().setTemporallyUnlockedAll(true);
            application.notify("Unlocked all tracks, leagues and difficulties");
        }, () -> {
        })));
        s.add(e.backAction());
    }

    private void actionGoToPlayMenu() {
        game.resetState();
        menu.menuBack();
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

    public void createCampaignSelectors() {
        this.difficultyLevels = application.getModManager().getLevelNames().toArray(new String[0]);
        this.leagueNames = application.getModManager().getLeagueNames();

        try {
            selectedTrack[getLevel().getSelectedLevel()] = getLevel().getSelectedTrack();
        } catch (ArrayIndexOutOfBoundsException _ex) {
            getLevel().setSelectedLevel(0);
            getLevel().setSelectedTrack(0);
            selectedTrack[getLevel().getSelectedLevel()] = getLevel().getSelectedTrack();
        }


        levelSelector = e.selector(application.getStr().s(S.level), getLevel().getSelectedLevel(), this.difficultyLevels, this.get(MenuType.CAMPAIGN),
                item -> {
                    if (item._charvZ()) {
                        MenuScreen<T> levelSelectorCurrentMenu = item.getCurrentMenu();
                        menu.setCurrentMenu(levelSelectorCurrentMenu);
                    }
                    trackSelector.setOptions(application.getModManager().getLeagueTrackNames(item.getSelectedOption()), false);
                    trackSelector.setUnlockedCount(getLevel().getUnlockedTracksCount(item.getSelectedOption()));
                    trackSelector.setSelectedOption(selectedTrack[item.getSelectedOption()]);
                });
        trackSelector = e.selector(application.getStr().s(S.track), selectedTrack[getLevel().getSelectedLevel()], application.getModManager().getLeagueTrackNames(getLevel().getSelectedLevel()), this.get(MenuType.CAMPAIGN),
                it -> {
                    if (it._charvZ()) {
                        it.setUnlockedCount(getLevel().getUnlockedTracksCount(levelSelector.getSelectedOption()));
                        it.update();
                        trackSelectorCurrentMenu = it.getCurrentMenu();
                        menu.setCurrentMenu(trackSelectorCurrentMenu);
                    }
                    selectedTrack[levelSelector.getSelectedOption()] = it.getSelectedOption();
                }
        );
        leagueSelector = e.selector(application.getStr().s(S.league), getLevel().getSelectedLeague(), this.leagueNames, this.get(MenuType.CAMPAIGN), it -> {
            if (it._charvZ()) {
                MenuScreen<T> leagueSelectorCurrentMenu = it.getCurrentMenu();
                it.setScreen(menu.getCurrentMenu());
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

    public void resetSelectors() {
        if (leagueSelector == null || levelSelector == null || trackSelector == null) {
            return;
        }
        ModManager modManager = application.getModManager();
        ModEntity modEntity = getLevel();
        leagueSelector.setUnlockedCount(modEntity.getUnlockedLeagues());
        leagueSelector.setSelectedOption(modEntity.getSelectedLeague());
        levelSelector.setUnlockedCount(modEntity.getUnlockedLevels());
        levelSelector.setSelectedOption(modEntity.getSelectedLevel());
        trackSelector.setUnlockedCount(modEntity.getUnlockedTracksCount(modEntity.getSelectedLevel()));
        trackSelector.setSelectedOption(modEntity.getSelectedTrack());
        trackSelector.setOptions(modManager.getLeagueTrackNames(modEntity.getSelectedLevel()), true);
        if (menu.currentMenu == trackSelectorCurrentMenu) {
            selectedTrack[modEntity.getSelectedLevel()] = modEntity.getSelectedTrack();
        }
    }
}
