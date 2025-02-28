package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.boldAttr;
import static org.happysanta.gdtralive.android.Helpers.getAppVersion;
import static org.happysanta.gdtralive.android.Helpers.getStringArray;
import static org.happysanta.gdtralive.android.Helpers.s;
import static org.happysanta.gdtralive.android.Helpers.showAlert;
import static org.happysanta.gdtralive.android.Helpers.showConfirm;

import android.text.Html;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.TrackEditorView;
import org.happysanta.gdtralive.android.menu.element.BadgeWithTextElement;
import org.happysanta.gdtralive.android.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.android.menu.element.InputTextElement;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.element.MenuItem;
import org.happysanta.gdtralive.android.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.android.menu.screens.CampaignSelectors;
import org.happysanta.gdtralive.game.Achievement;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.GdApplication;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.ThemeHeader;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.api.util.Consumer;
import org.happysanta.gdtralive.game.api.util.Function;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFactory {
    public static int[] ACHIEVEMENT_ICONS = {
            R.drawable.s_lock1,
            R.drawable.levels_wheel0,
            R.drawable.levels_wheel1,
            R.drawable.levels_wheel2
    };

    private final Map<MenuType, MenuScreen> menus = new HashMap<>();
    private final GdApplication application;
    private final GdPlatform platform;

    private AMenu menu;

    private CampaignSelectors campaignSelectors;
    private TrackEditorView trackEditor;
    private List<String> modNames = new ArrayList<>(); //todo remove
    public List<String> themeNames = new ArrayList<>(); //todo remove

    public MenuFactory(GdApplication application, GdPlatform platform) {
        this.application = application;
        this.platform = platform;
    }

    public MenuScreen add(MenuType type, Function<Map<MenuType, MenuScreen>, MenuScreen> builder) {
        MenuScreen screen = builder.apply(menus);
        menus.put(type, screen);
        return screen;
    }

    public MenuScreen transform(MenuType type, Consumer<MenuScreen> transformer) {
        MenuScreen screen = menus.get(type);
        transformer.accept(screen);
        return screen;
    }

    public MenuScreen get(MenuType type) {
        MenuScreen screen = menus.get(type);
        if (screen == null) {
            throw new IllegalStateException("FIX ME: no " + type); //todo remove
        }
        return screen;
    }

    //Все переплетено
    //Потяни за нить
    //За ней потянется клубок
    public void init(AMenu menu, TrackEditorView trackEditor) {
        this.menu = menu;
        this.trackEditor = trackEditor;
        add(MenuType.MAIN, r -> new MenuScreen(s(R.string.main), null));
        add(MenuType.PLAY, r -> new MenuScreen(s(R.string.play), r.get(MenuType.MAIN)));
        add(MenuType.PROFILE, this::createProfileScreen);
        add(MenuType.ABOUT, this::createAboutScreen);
        add(MenuType.HELP, this::createHelpScreen);
        add(MenuType.OPTIONS, this::createOptionsScreen);
        add(MenuType.CAMPAIGN, this::createPlayCampaign);

        add(MenuType.FINISHED_PLAY, this::createFinishedPlay);
        add(MenuType.IN_GAME_PLAY, this::createInGamePlay);
        add(MenuType.WORKSHOP, r -> new MenuScreen(s(R.string.workshop), r.get(MenuType.MAIN)));
        add(MenuType.MODS, this::createMods);
        add(MenuType.MOD_OPTIONS, this::createModOptions);
        add(MenuType.THEMES, this::createThemes);
        add(MenuType.THEME_OPTIONS, this::createThemeOptions);

        add(MenuType.TRACK_EDITOR_OPTIONS, this::createTrackOptions);
        add(MenuType.IN_GAME_TRACK_EDITOR, this::createInGameEditor);
        add(MenuType.RECORDINGS, this::createRecordings);
        transform(MenuType.WORKSHOP, this::transformWorkshop);

        //        add(MenuType.DAILY, r -> new MenuScreen(s(R.string.daily_challenge), r.get(MenuType.PLAY)));//todo
//        DailyMenu dailyMenu = new DailyMenu(menu, playMenuScreen, application, this); //todo

        add(MenuType.RECORDING_OPTIONS, this::createRecordingOptions);
        add(MenuType.IN_GAME_REPLAY, this::createInGameReplay);
        add(MenuType.ACHIEVEMENTS, this::createAchievements);
        transform(MenuType.PLAY, this::fillPlay);

        this.campaignSelectors = new CampaignSelectors(menu, application, this);
        add(MenuType.HIGH_SCORE, this::createHighScore);
        add(MenuType.IN_GAME_CAMPAIGN, this::createInGameCampaign);
        add(MenuType.FINISHED_CAMPAIGN, this::createFinishedCampaign);
        transform(MenuType.CAMPAIGN, this::transformPlayCampaign);

        transform(MenuType.MAIN, this::fillMainScreen);
    }

    private MenuScreen createTEMPLATE(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.main), r.get(MenuType.MAIN));
        screen.setBuilder((s, data) -> {
            return s;
        });
        return screen;
    }

    private MenuScreen createInGameReplay(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.replay), r.get(MenuType.RECORDING_OPTIONS));
        screen.setBuilder((s, data) -> {
            s.clear();
            s.addItem(new MenuAction(s(R.string._continue), MenuAction.CONTINUE, menu, __ -> application.menuToGame()));
            s.addItem(new MenuItem(s(R.string.options), this.get(MenuType.OPTIONS), menu, null));
            s.addItem(new MenuAction(s(R.string.back), menu, it -> {
                application.getGame().resetState();
                menu.menuBack();
            }));
            s.resetHighlighted();
            return s;
        });
        return screen;
    }

    private MenuScreen createInGameEditor(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.track_editor), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            s.clear();
            s.addItem(new MenuAction(s(R.string.back), menu, it -> trackEditor.startEditing()));
            s.addItem(new MenuAction(s(R.string.play), menu, it -> trackEditor.playTrack()));
            s.addItem(new MenuItem(s(R.string.track_options), this.get(MenuType.TRACK_EDITOR_OPTIONS), menu, item -> this.get(MenuType.TRACK_EDITOR_OPTIONS).build(new MenuData(trackEditor.getCurrentTrack()))));
            s.addItem(new MenuItem(s(R.string.options), this.get(MenuType.OPTIONS), menu, null));
            s.addItem(new MenuAction(s(R.string.save), menu, it -> trackEditor.saveTrack()));
            s.addItem(new MenuAction(s(R.string.exit_editor), menu, it -> {
                trackEditor.exitEditor();
                menu.menuBack();
            }));

            s.resetHighlighted();
            trackEditor.hideLayout();
            return s;
        });
        return screen;
    }

    private MenuScreen createTrackOptions(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.track_editor), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            TrackParams track = data.getTrackRef();
            s.clear();
            s.addItem(menu.backAction());
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, track.getData().getGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.author, track.getData().getAuthor())));
            s.addItem(new InputTextElement(Fmt.colon(s(R.string.name)), track.getData().getName(), item1 -> {
                TrackParams currentTrack = trackEditor.getCurrentTrack();
                String name = item1.getText().toString().trim();
                currentTrack.getData().setName(name);
            }));

            s.addItem(MenuUtils.emptyLine(false));
            s.addItem(new TextMenuElement(boldAttr(R.string.league_properties, null)));
            s.addItem(new OptionsMenuElement(s(R.string.league), track.getData().league, menu, application.getModManager().getLeagueNames(), false, screen,
                    item -> {
                        OptionsMenuElement it = (OptionsMenuElement) item;
                        if (it._charvZ()) {
                            MenuScreen leagueSelectorCurrentMenu = it.getCurrentMenu();
                            it.setScreen(menu.currentMenu);
                            menu.setCurrentMenu(leagueSelectorCurrentMenu);
                        } else {
                            trackEditor.saveLeagueInput(it.getSelectedOption());
                            this.get(MenuType.TRACK_EDITOR_OPTIONS).build(new MenuData(trackEditor.getCurrentTrack()));
                        }
                    }));
//            for (Map.Entry<String, String> entry : track.getLeagueProperties().entrySet()) {
//                s.addItem(new PropInput(Fmt.colon(entry.getKey()), entry.getValue(), entry.getKey(), item -> {
//                    trackEditor.getCurrentTrack().getLeagueProperties().put(item.getKey(), item.getText());
//                    application.getModManager().setTrackProperties(trackEditor.getCurrentTrack());
//                }));
//            }

            s.addItem(MenuUtils.emptyLine(false));
            s.addItem(new TextMenuElement(boldAttr(R.string.track_properties, null)));
//            for (Map.Entry<String, String> entry : track.getGameProperties().entrySet()) {
//                s.addItem(new PropInput(Fmt.colon(entry.getKey()), entry.getValue(), entry.getKey(), item -> {
//                    trackEditor.getCurrentTrack().getGameProperties().put(item.getKey(), item.getText());
//                    application.getModManager().setTrackProperties(trackEditor.getCurrentTrack());
//                }));
//            }
            platform.hideKeyboardLayout();
            System.gc(); //hopefully
            return s;
        });
        return screen;
    }

    private MenuScreen createThemes(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.themes), r.get(MenuType.WORKSHOP));
        screen.setBeforeShowAction(()-> this.get(MenuType.THEMES).build());
        screen.setBuilder((s, data) -> {
            s.clear();
            themeNames.clear();
            themeNames.add(Theme.defaultTheme().getHeader().getName());
            themeNames.add(Theme.amoledMod().getHeader().getName());
            List<String> filenames = application.getFileStorage().listFiles(GDFile.THEME);
            for (String filename : filenames) {
                themeNames.add(GDFile.THEME.cutExtension(filename));
            }
            s.addItem(menu.backAction());
            s.addItem(new MenuAction(s(R.string.import_theme), MenuAction.SELECT_FILE, menu, it -> {
                platform.pickFile(Constants.PICKFILE_THEME_RESULT_CODE);
            }));
            s.addItem(MenuUtils.emptyLine(true));
            s.addItem(MenuUtils.emptyLine(true));
            for (int i = 0; i < themeNames.size(); i++) { //todo
                try {
                    String theme = themeNames.get(i);
                    MenuItem options = new MenuItem(theme, this.get(MenuType.THEME_OPTIONS), menu,
                            item -> this.get(MenuType.THEME_OPTIONS).build(new MenuData(themeNames.get(((MenuItem) item).getValue()))));
                    options.setValue(i);
                    screen.addItem(options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return s;
        });
        return screen;
    }

    private MenuScreen createThemeOptions(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.theme), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            String name = GDFile.THEME.cutExtension(data.getFileName());
            Theme theme = data.getTheme() == null
                    ? application.getModManager().loadTheme(name)
                    : data.getTheme();
            s.clear();
            ThemeHeader header = theme.getHeader();
            s.addItem(new TextMenuElement(boldAttr(R.string.name, header.getName())));
            s.addItem(new TextMenuElement(boldAttr(R.string.description, header.getDescription())));
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, header.getGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.author, header.getAuthor())));
            s.addItem(new TextMenuElement(boldAttr(R.string.date, header.getDate())));
            s.addItem(MenuUtils.emptyLine(true));

            s.addItem(new MenuAction(s(R.string.install), -1, menu, item -> {
                application.getFileStorage().save(theme, GDFile.THEME, theme.getHeader().getName()); //todo move to method
                application.getModManager().installTheme(theme.getHeader().getName());
                Achievement.achievements.get(Achievement.Type.ESTHETE).increment();
            }));
            s.addItem(new MenuAction(s(R.string.save), -1, menu, item -> {
                application.getFileStorage().save(theme, GDFile.THEME, theme.getHeader().getName());
            }));
            s.addItem(new MenuAction(s(R.string.delete), -1, menu,
                    __ -> this.application.getFileStorage().delete(GDFile.THEME, theme.getHeader().getName())));
            s.addItem(menu.backAction(() -> this.get(MenuType.THEMES).build()));
            return s;
        });
        return screen;
    }

    private MenuScreen createModOptions(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.mod), r.get(MenuType.MODS));
        screen.setBuilder((s, data) -> {
            Mod mod = data.getMod();
            s.clear();
            s.addItem(new TextMenuElement(boldAttr(R.string.name, mod.getName())));
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, mod.getGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.tracks, Fmt.formatLevelsCount(mod))));
            s.addItem(new TextMenuElement(boldAttr(R.string.author, mod.getAuthor())));
            s.addItem(new TextMenuElement(boldAttr(R.string.date, mod.getDate())));
            s.addItem(MenuUtils.emptyLine(false));

            s.addItem(new MenuAction(s(R.string.install), -1, menu, item -> {
                String modName = mod.getName();
                application.getModManager().activateMod(mod);
                this.get(MenuType.CAMPAIGN).setTitle(Fmt.sp(s(R.string.play), modName));
                campaignSelectors.resetSelectors();
            }));
//            s.addItem(new MenuAction(s(R.string.save), -1, menu,
//                    __ -> this.application.getFileStorage().save(mod, GDFile.MOD, mod.getName())));
            s.addItem(new MenuAction(s(R.string.delete), -1, menu,
                    __ -> this.application.getModManager().deleteMod(mod.getName())));
            s.addItem(menu.backAction(() -> this.get(MenuType.MODS).build()));
            return s;
        });
        return screen;
    }

    private MenuScreen createMods(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.mod_packs), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            s.clear();
            modNames.clear();
            s.addItem(menu.backAction());
            s.addItem(new MenuAction(s(R.string.import_mod), MenuAction.SELECT_FILE, menu, it -> {
                platform.pickFile(Constants.PICKFILE_MOD_RESULT_CODE);
            }));
            s.addItem(new MenuAction(s(R.string.import_mrg), MenuAction.SELECT_FILE, menu, it -> {
                platform.pickFile(Constants.PICKFILE_MRG_RESULT_CODE);
            }));
            s.addItem(MenuUtils.emptyLine(true));
            s.addItem(MenuUtils.emptyLine(true));
            int i = 0;
            List<String> list = application.getFileStorage().listFiles(GDFile.MOD);
            for (String filename : list) {
                String name = GDFile.MOD.cutExtension(filename);
                modNames.add(name);
                MenuItem options = new MenuItem(name, this.get(MenuType.MOD_OPTIONS), menu,
                        item -> {
                            int value = ((MenuItem) item).getValue();
                            try {
                                String rec = modNames.get(value);
                                Mod mod = this.application.getModManager().loadMod(rec);
                                if (mod != null) {
                                    this.get(MenuType.MOD_OPTIONS).build(new MenuData(mod, name));
                                } else {
                                    menu.back();
                                }
                            } catch (Exception e) {
                                application.notify("Failed to load mod");
                                menu.back();
                            }
                        });
                options.setValue(i);
                s.addItem(options);
                i++;
            }
            return s;
        });
        return screen;
    }

    private void fillPlay(MenuScreen screen) {
        screen.setBeforeShowAction(screen::build);
        screen.setBuilder((s, data) -> {
            Game game = application.getGame();
            ModManager modManager = application.getModManager();
            s.clear();
            s.setBeforeShowAction(() -> game.startAutoplay(false));
//        s.addItem(new MenuItem(s(R.string.daily_challenge), this.get(MenuType.DAILY), menu, __ -> this.get(MenuType.DAILY).build())); //todo
            String[] modNames = modManager.getAllInstalledModNames();
            String currentModName = modManager.getMod().getName();
            int currentModIndex = 0;
            for (int i = 0; i < modNames.length; i++) {
                if (currentModName.equals(modNames[i])){
                    currentModIndex = i;
                }
            }
            OptionsMenuElement campaignSelector = new OptionsMenuElement(s(R.string.campaign_select), currentModIndex, menu, modNames, false, this.get(MenuType.PLAY),
                    item -> {
                        OptionsMenuElement it = (OptionsMenuElement) item;
                        if (it._charvZ()) {
                            MenuScreen leagueSelectorCurrentMenu = it.getCurrentMenu();
                            it.setScreen(menu.currentMenu);
                            menu.setCurrentMenu(leagueSelectorCurrentMenu);
                        }
                    });
            s.addItem(new MenuItem(s(R.string.campaign), this.get(MenuType.CAMPAIGN), menu, __ -> {
                String modName = modNames[campaignSelector.getSelectedOption()];
                modManager.activateMod(modName);
                campaignSelectors.setDifficultyLevels(application.getModManager().getLevelNames().toArray(new String[0]));
//                campaignSelectors.setLeagueNames();
                campaignSelectors.resetSelectors();
            }));
            s.addItem(campaignSelector);
            s.addItem(new MenuAction(Fmt.ra(s(R.string.random_track)), menu, __ -> game.startTrack(GameParams.of(GameMode.RANDOM, modManager.getRandomTrack()))));
            s.addItem(new MenuAction(Fmt.ra(s(R.string.tracks)), menu, __ -> application.notify("Coming soon")));
            s.addItem(new MenuItem(s(R.string.achievements), this.get(MenuType.ACHIEVEMENTS), menu, __ -> this.get(MenuType.ACHIEVEMENTS).build()));
            s.addItem(menu.backAction(game::resetState));
            return s;
        });
    }

    private MenuScreen createAchievements(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.achievements), r.get(MenuType.PLAY));
        screen.setBuilder((s, data) -> {
            s.clear();
            for (Achievement achievement : Achievement.achievements.values()) {
                String title = Fmt.sp(application.getStr().s(achievement.getName()), achievement.getProgressFormatted());
                s.addItem(new BadgeWithTextElement(ACHIEVEMENT_ICONS[achievement.getLevel()], title, menu, item -> {
                }));
                s.addItem(new TextMenuElement(Html.fromHtml(application.getStr().s(achievement.getDescription()))));
                s.addItem(MenuUtils.emptyLine(false));
            }
            s.addItem(menu.backAction());
            return s;
        });
        return screen;
    }

    private MenuScreen createRecordings(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.recordings), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
                    s.clear();
                    s.addItem(menu.backAction());
                    s.addItem(new MenuAction(s(R.string.import_record), MenuAction.SELECT_FILE, menu, it -> {
                        platform.pickFile(Constants.PICKFILE_RECORD_RESULT_CODE);
                    }));
                    s.addItem(MenuUtils.emptyLine(false));
                    s.addItem(MenuUtils.emptyLine(false));
                    int i = 0;
                    //todo
                    for (TrackRecord rec : application.getFileStorage().getAllRecords()) {
                        try {
                            long millis = rec.getTime();
                            String time = Utils.getDurationString(millis);
                            String name = String.format("[%s] %s", time, rec.getTrackName());
                            MenuItem options = new MenuItem(name, this.get(MenuType.RECORDING_OPTIONS), menu,
                                    item -> {
                                        TrackRecord recording = application.getFileStorage().getAllRecords().get(((MenuItem) item).getValue());
                                        this.get(MenuType.RECORDING_OPTIONS).build(new MenuData(recording));
                                    });
                            options.setValue(i);
                            s.addItem(options);
                            i++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return s;
                }
        );
        return screen;
    }

    private MenuScreen createRecordingOptions(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.record_option), r.get(MenuType.RECORDINGS));
        screen.setBuilder((s, data) -> {
            TrackRecord rec = data.getRecording();
            s.clear();
            s.addItem(new TextMenuElement(boldAttr(R.string.name, rec.getTrackName())));
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, rec.getTrackGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.league, "" + rec.getLeague())));
            long millis = rec.getTime();
            s.addItem(new TextMenuElement(boldAttr(R.string.time, Utils.getDurationString(millis))));
            s.addItem(new TextMenuElement(boldAttr(R.string.date, rec.getDate())));
            s.addItem(MenuUtils.emptyLine(true));

            s.addItem(new MenuAction(s(R.string.replay), -1, menu, item -> application.getGame().startTrack(GameParams.of(rec))));
            s.addItem(new MenuAction(s(R.string.save), -1, menu,
                    item -> application.getFileStorage().save(rec, GDFile.RECORD, Fmt.us(rec.getTrackName(), rec.getDate()))));
            s.addItem(menu.backAction(() -> {
                this.get(MenuType.RECORDINGS).build();
                application.getGame().startAutoplay(true);
            }));
            return s;
        });
        return screen;
    }

    private void transformWorkshop(MenuScreen s) {
        s.addItem(new MenuAction(Fmt.ra(s(R.string.create_new_track)), menu, item -> {
            application.notify("Coming soon");
//            trackEditor.createNew(application.getSettings().getPlayerName());
        }));
        s.addItem(new MenuItem(s(R.string.mod_packs), this.get(MenuType.MODS), menu, __ -> this.get(MenuType.MODS).build()));
        s.addItem(new MenuItem(s(R.string.themes), this.get(MenuType.THEMES), menu, __ -> this.get(MenuType.THEMES).build()));
        s.addItem(new MenuItem(s(R.string.recordings), this.get(MenuType.RECORDINGS), menu, __ -> this.get(MenuType.RECORDINGS).build()));
        s.addItem(MenuUtils.emptyLine(true));
        s.addItem(menu.backAction());
    }

    private MenuScreen createFinishedPlay(Map<MenuType, MenuScreen> r) {
        MenuScreen fm = new MenuScreen(s(R.string.finished), r.get(MenuType.PLAY));
        fm.setBuilder((finishedMenu, data) -> {
            Game game = application.getGame();
            finishedMenu.clear();
            long millis = data.getLastTrackTime();
            finishedMenu.addItem(new TextMenuElement(boldAttr(R.string.time, Utils.getDurationString(millis))));
            for (String s : application.getHighScoreManager().getFormattedScores(data.getTrackGuid(), data.getSelectedLeague())) {
                finishedMenu.addItem(new TextMenuElement(s));
            }
            finishedMenu.addItem(new MenuAction(Fmt.ra(s(R.string.random_track)), this.menu, __ -> game.startTrack(GameParams.of(GameMode.RANDOM, application.getModManager().getRandomTrack()))));
            finishedMenu.addItem(new MenuAction(Fmt.colon(s(R.string.restart), data.getTrackName()), MenuAction.RESTART, this.menu, __ -> game.restart()));
            finishedMenu.addItem(this.menu.backAction());
            return finishedMenu;
        });
        return fm;
    }

    private MenuScreen createInGamePlay(Map<MenuType, MenuScreen> r) {
        Game game = application.getGame();
        MenuScreen ig = new MenuScreen(s(R.string.ingame), r.get(MenuType.PLAY));
        ig.addItem(new MenuAction(s(R.string._continue), MenuAction.CONTINUE, menu, __ -> application.menuToGame()));
        ig.addItem(new MenuAction(s(R.string.training_mode), menu, __ -> {
            application.trainingMode();
            application.menuToGame();
        }));
        ig.addItem(new MenuAction(Fmt.colon(s(R.string.restart), "Name"), MenuAction.RESTART, menu, __ -> game.restart()));
        ig.addItem(menu.createAction(MenuAction.LIKE, item -> application.notify("Coming soon")));
        ig.addItem(menu.backAction(game::resetState));
        ig.setBuilder((s, data) -> {
            if (data != null) {
                s.getActions(MenuAction.RESTART).setText(Fmt.colon(s(R.string.restart), data.getTrackName()));
            }
            s.resetHighlighted();
            return s;
        });
        return ig;
    }

    private void fillMainScreen(MenuScreen s) {
        s.setBeforeShowAction(() -> application.getGame().startAutoplay(false));
        s.addItem(new MenuItem(s(R.string.competition), this.get(MenuType.PLAY), menu));
        s.addItem(new MenuItem(s(R.string.workshop), this.get(MenuType.WORKSHOP), menu));
        s.addItem(new MenuItem(s(R.string.profile), this.get(MenuType.PROFILE), menu));
        s.addItem(new MenuItem(s(R.string.options), this.get(MenuType.OPTIONS), menu));
        s.addItem(new MenuItem(s(R.string.help), this.get(MenuType.HELP), menu));
        s.addItem(new MenuItem(s(R.string.about), this.get(MenuType.ABOUT), menu));
        s.addItem(menu.createAction(MenuAction.EXIT, item -> application.exit()));
    }

    private MenuScreen createOptionsScreen(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.options), r.get(MenuType.MAIN));
        String[] onOffStrings = getStringArray(R.array.on_off);
        String[] keySetStrings = getStringArray(R.array.keyset);
        String[] scaleOptions = new String[401];
        for (int i = 0; i < scaleOptions.length; i++) {
            scaleOptions[i] = "" + i;
        }

        screen.addItem(new OptionsMenuElement(s(R.string.scale), application.getSettings().getScale(), menu, scaleOptions, false, screen,
                item -> {
                    int option = ((OptionsMenuElement) item).getSelectedOption();
                    application.getSettings().setScale(option);
                    application.getModManager().adjustScale();
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.recording_enabled), application.getSettings().isRecordingEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> {
                    int option = ((OptionsMenuElement) item).getSelectedOption();
                    application.getGame().setRecordingEnabled(option == 0);
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.perspective), application.getSettings().isPerspectiveEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> {
                    int option = ((OptionsMenuElement) item).getSelectedOption();
                    application.getGame().setPerspectiveEnabled(option == 0);
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.shadows), application.getSettings().isShadowsEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item1 -> {
                    int option = ((OptionsMenuElement) item1).getSelectedOption();
                    application.getGame().setShadowsEnabled(option == 0);
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.driver_sprite), application.getSettings().isDriverSpriteEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item1 -> {
                    OptionsMenuElement it1 = (OptionsMenuElement) item1;
                    if (it1._charvZ()) it1.setSelectedOption(it1.getSelectedOption() + 1);
                    application.getGame().setDrawBiker(it1.getSelectedOption() == 0);
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.bike_sprite), application.getSettings().isBikeSpriteEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item1 -> {
                    OptionsMenuElement it1 = (OptionsMenuElement) item1;
                    if (it1._charvZ()) it1.setSelectedOption(it1.getSelectedOption() + 1);
                    application.getGame().setDrawBike(it1.getSelectedOption() == 0);
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.input), application.getSettings().getInputOption(), menu, keySetStrings, false, screen,
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    if (it._charvZ()) it.setSelectedOption(it.getSelectedOption() + 1);
                    application.getGame().setInputOption(it.getSelectedOption());
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.active_camera), application.getSettings().isLookAheadEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    application.getGame().setLookAhead(it.getSelectedOption() == 0);
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.vibrate_on_touch), application.getSettings().isVibrateOnTouchEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> application.getSettings().setVibrateOnTouchEnabled(((OptionsMenuElement) item).getSelectedOption() == 0)
        ));
        screen.addItem(new OptionsMenuElement(s(R.string.show_keyboard), application.getSettings().isKeyboardInMenuEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> {
                    boolean enabled = ((OptionsMenuElement) item).getSelectedOption() == 0;
                    application.getSettings().setKeyboardInMenuEnabled(enabled);
                    if (enabled) platform.showKeyboardLayout();
                    else platform.hideKeyboardLayout();
                }));

        MenuScreen eraseScreen = new MenuScreen(s(R.string.confirm_clear), screen);
        eraseScreen.addItem(new TextMenuElement(s(R.string.erase_text1)));
        eraseScreen.addItem(new TextMenuElement(s(R.string.erase_text2)));
        eraseScreen.addItem(MenuUtils.emptyLine(true));
        eraseScreen.addItem(menu.createAction(MenuAction.NO, item -> menu.menuBack()));
        eraseScreen.addItem(menu.createAction(MenuAction.YES, item -> {
            application.getHighScoreManager().clearAllHighScores();
            showAlert(s(R.string.cleared), s(R.string.cleared_text), null);
            menu.menuBack();
        }));

        MenuScreen resetScreen = new MenuScreen(s(R.string.confirm_reset), eraseScreen);
        resetScreen.addItem(new TextMenuElement(s(R.string.reset_text1)));
        resetScreen.addItem(new TextMenuElement(s(R.string.reset_text2)));
        resetScreen.addItem(MenuUtils.emptyLine(true));
        resetScreen.addItem(menu.createAction(MenuAction.NO, item -> menu.menuBack()));
        resetScreen.addItem(menu.createAction(MenuAction.YES, item -> {
            showAlert(s(R.string.reset), s(R.string.reset_text), () -> {
                application.getSettings().resetAll();
                application.getHighScoreManager().resetAllLevelsSettings();
                application.getHighScoreManager().clearAllHighScores();

                application.setFullResetting(true);
                application.destroyApp(true);
            });
            menu.menuBack();
        }));

        eraseScreen.addItem(new MenuItem(s(R.string.full_reset), resetScreen, menu, null));
        screen.addItem(new MenuItem(s(R.string.clear_highscore), eraseScreen, menu, null));

        screen.addItem(menu.backAction());
        return screen;
    }

    private MenuScreen createHelpScreen(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.help), r.get(MenuType.MAIN));
        MenuScreen objectiveHelpScreen = new MenuScreen(s(R.string.objective), screen);
        objectiveHelpScreen.setIsTextScreen(true);
        objectiveHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.objective_text))));
        objectiveHelpScreen.addItem(menu.backAction());

        MenuScreen keysHelpScreen = new MenuScreen(s(R.string.keys), screen);
        keysHelpScreen.setIsTextScreen(true);
        keysHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.keyset_text))));
        keysHelpScreen.addItem(new MenuAction(s(R.string.back), MenuAction.BACK, menu, item -> menu.menuBack()));

        MenuScreen unlockingHelpScreen = new MenuScreen(s(R.string.unlocking), screen);
        unlockingHelpScreen.setIsTextScreen(true);
        unlockingHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.unlocking_text))));
        unlockingHelpScreen.addItem(menu.backAction());

        MenuScreen highscoreHelpScreen = new MenuScreen(s(R.string.highscores), screen);
        highscoreHelpScreen.setIsTextScreen(true);
        highscoreHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.highscore_text))));
        highscoreHelpScreen.addItem(menu.backAction());

        MenuScreen optionsHelpScreen = new MenuScreen(s(R.string.options), screen);
        optionsHelpScreen.setIsTextScreen(true);
        optionsHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.options_text))));
        optionsHelpScreen.addItem(menu.backAction());

        screen.addItem(new MenuItem(s(R.string.objective), objectiveHelpScreen, menu, null));
        screen.addItem(new MenuItem(s(R.string.keys), keysHelpScreen, menu, null));
        screen.addItem(new MenuItem(s(R.string.unlocking), unlockingHelpScreen, menu, null));
        screen.addItem(new MenuItem(s(R.string.highscores), highscoreHelpScreen, menu, null));
        screen.addItem(new MenuItem(s(R.string.options), optionsHelpScreen, menu, null));
        screen.addItem(menu.backAction());
        return screen;
    }

    private MenuScreen createAboutScreen(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(String.format("%s v%s", s(R.string.about), getAppVersion()), r.get(MenuType.MAIN));
        screen.setIsTextScreen(true);
        screen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.about_text))));
        screen.addItem(menu.backAction());
        return screen;
    }

    private MenuScreen createProfileScreen(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.profile), r.get(MenuType.MAIN));
        InputTextElement nameInput = new InputTextElement(Fmt.colon(s(R.string.name), ""), application.getSettings().getPlayerName(), null);
        screen.addItem(nameInput);
        screen.addItem(new MenuAction(s(R.string.save), menu, item -> {
            application.getSettings().setPlayerName(nameInput.getText());
            application.notify(s(R.string.saved));
        }));
        screen.addItem(menu.backAction());
        return screen;
    }

    public MenuScreen createInGameCampaign(Map<MenuType, MenuScreen> r) {
        MenuScreen inGame = new MenuScreen(s(R.string.ingame), r.get(MenuType.CAMPAIGN));
        inGame.addItem(new MenuAction(s(R.string._continue), MenuAction.CONTINUE, menu, item -> application.menuToGame()));
        inGame.addItem(new MenuAction(Fmt.colon(s(R.string.restart), ""), MenuAction.RESTART, menu, item -> menu.menuToGame()));
        inGame.addItem(new MenuAction(s(R.string.training_mode), menu, item -> {
            application.trainingMode();
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

    public MenuScreen createFinishedCampaign(Map<MenuType, MenuScreen> r) {
        MenuScreen finished = new MenuScreen(s(R.string.finished), r.get(MenuType.CAMPAIGN));
        finished.setBuilder((finishedMenu, data) -> {
            campaignSelectors.updateSelectors(data);
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
        });
        return finished;
    }

    private MenuScreen showFinishMenu(MenuScreen finishedMenu, MenuData data) {
        finishedMenu.clear();
        long millis = data.getLastTrackTime();
        finishedMenu.addItem(new TextMenuElement(Html.fromHtml("<b>" + s(R.string.time) + "</b>: " + Utils.getDurationString(millis))));
        for (String s : application.getHighScoreManager().getFormattedScores(data.getTrackGuid(), data.getSelectedLeague())) {
            finishedMenu.addItem(new TextMenuElement(s));
        }

        ModManager modManager = application.getModManager();
        int completedCount = modManager.getModState().getUnlockedTracksCount(data.getSelectedLevel()) + 1;
        finishedMenu.addItem(new TextMenuElement(Html.fromHtml(String.format(s(R.string.tracks_completed_tpl),
                completedCount,
                modManager.getLevelTracksCount(data.getSelectedLevel()),
                campaignSelectors.getDifficultyLevels()[data.getSelectedLevel()]
        ))));

        //todo fix that
        boolean leagueCompleted = data.getNewSelectedLeague() != data.getSelectedLeague();
        if (leagueCompleted) {
            boolean flag = true;
            for (int i1 = 0; i1 < 3; i1++)
                if (modManager.getModState().getUnlockedTracksCount(i1) != modManager.getLevelTracksCount(i1) - 1)
                    flag = false;

            if (!flag)
                finishedMenu.addItem(new TextMenuElement(s(R.string.level_completed_text)));
            finishedMenu.addItem(new TextMenuElement(s(R.string.congratulations) + campaignSelectors.getLeagueNames()[data.getNewSelectedLeague()]));
            try {
                //todo crashed on league unlock
                showAlert(s(R.string.league_unlocked), s(R.string.league_unlocked_text) + campaignSelectors.getLeagueNames()[data.getNewSelectedLeague()], null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.get(MenuType.IN_GAME_CAMPAIGN)
                    .getActions(MenuAction.RESTART)
                    .setText(Fmt.colon(s(R.string.restart), data.getTrackName()));

            finishedMenu.addItem(new MenuAction(Fmt.colon(s(R.string.next), modManager.getTrackName(data.getSelectedLevel(), data.getNewSelectedTrack())), MenuAction.NEXT, menu,
                    item -> {
                        int league = campaignSelectors.getLeagueSelector().getSelectedOption();
                        int level = campaignSelectors.getLevelSelector().getSelectedOption();
                        int track = campaignSelectors.getTrackSelector().getSelectedOption();
                        application.getGame().startTrack(GameParams.of(GameMode.CAMPAIGN, modManager.loadLevel(level, track), league, level, track));
                    }
            ));
        }
        finishedMenu.addItem(new MenuAction(Fmt.colon(s(R.string.restart), data.getTrackName()), MenuAction.RESTART, menu, item -> menu.menuToGame()));
        finishedMenu.addItem(menu.createAction(MenuAction.PLAY_MENU, item -> actionGoToPlayMenu()));
        finishedMenu.resetHighlighted();
        finishedMenu.highlightElement();
        return finishedMenu;
    }

    public MenuScreen createHighScore(Map<MenuType, MenuScreen> r) {
        MenuScreen hs = new MenuScreen(s(R.string.highscores), r.get(MenuType.CAMPAIGN));
        hs.setBeforeShowAction(() -> {
            MenuData data = new MenuData();
            data.setSelectedLevel(campaignSelectors.getLevelSelector().getSelectedOption());
            data.setSelectedTrack(campaignSelectors.getTrackSelector().getSelectedOption());
            data.setSelectedLeague(campaignSelectors.getLeagueSelector().getSelectedOption());
            this.get(MenuType.HIGH_SCORE).build(data);
        });
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

    private MenuScreen createPlayCampaign(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.campaign), r.get(MenuType.PLAY));
        screen.setBeforeShowAction(() -> {
            screen.setTitle(application.getModManager().getMod().getName());
            application.getGame().startAutoplay(true);
        });
        return screen;
    }

    public void transformPlayCampaign(MenuScreen s) {
        OptionsMenuElement trackSelector = campaignSelectors.getTrackSelector();
        OptionsMenuElement levelSelector = campaignSelectors.getLevelSelector();
        OptionsMenuElement leagueSelector = campaignSelectors.getLeagueSelector();
        s.addItem(new MenuAction(Fmt.ra(s(R.string.start)), menu, item -> {
            if (levelSelector.getSelectedOption() > levelSelector.getUnlockedCount()
                    || trackSelector.getSelectedOption() > trackSelector.getUnlockedCount()
                    || leagueSelector.getSelectedOption() > leagueSelector.getUnlockedCount()) {
                showAlert("GD Classic", s(R.string.complete_to_unlock), null);
            } else {
                int league = leagueSelector.getSelectedOption();
                int level = levelSelector.getSelectedOption();
                int track = trackSelector.getSelectedOption();
                application.getGame().startTrack(GameParams.of(GameMode.CAMPAIGN, application.getModManager().loadLevel(level, track), league, level, track));
            }
        }));
        s.addItem(levelSelector);
        s.addItem(trackSelector);
        s.addItem(leagueSelector);
        s.addItem(new MenuItem(s(R.string.highscores), this.get(MenuType.HIGH_SCORE), menu, null));
        s.addItem(new MenuAction(Fmt.ra(s(R.string.unlock_all)), -1, menu,
                item -> {
                    application.notify("Coming soon");
                    if (true) { //todo
                        return;
                    }
                    showConfirm(s(R.string.unlock_all), s(R.string.unlock_all_text), () -> {
                        int leaguesCount = application.getModManager().getLeagueThemes().size();
                        ModEntity modState = application.getModManager().getModState();
                        modState.setUnlockedLeagues(leaguesCount);
                        modState.setUnlockedLevels(leaguesCount);
                        modState.unlockAllTracks();
                        campaignSelectors.setLeagueNames(application.getModManager().getLeagueNames());
                        leagueSelector.setUnlockedCount(modState.getUnlockedLeagues());
                        levelSelector.setUnlockedCount(modState.getUnlockedLevels());
                        application.getModManager().setTemporallyUnlockedAll(true);
                        application.notify("Unlocked all tracks, leagues and difficulties");
                    }, () -> {
                    });
                }));
        s.addItem(menu.backAction());
    }

    private void actionGoToPlayMenu() {
        application.getGame().resetState();
        menu.menuBack();
    }
}
