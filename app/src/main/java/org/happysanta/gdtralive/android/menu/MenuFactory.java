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
import org.happysanta.gdtralive.android.menu.element.EmptyLineMenuElement;
import org.happysanta.gdtralive.android.menu.element.HighScoreTextMenuElement;
import org.happysanta.gdtralive.android.menu.element.InputTextElement;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.element.MenuItem;
import org.happysanta.gdtralive.android.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.android.menu.screens.CampaignSelectors;
import org.happysanta.gdtralive.game.Achievement;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.MenuType;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.ThemeHeader;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.external.GdPlatform;
import org.happysanta.gdtralive.game.api.menu.MenuElement;
import org.happysanta.gdtralive.game.api.menu.MenuScreen;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.MenuData;
import org.happysanta.gdtralive.game.api.model.Mod;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.TrackRecord;
import org.happysanta.gdtralive.game.api.util.ActionHandler;
import org.happysanta.gdtralive.game.api.util.Consumer;
import org.happysanta.gdtralive.game.api.util.Function;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFactory<T> {
    public static int[] ACHIEVEMENT_ICONS = {
            R.drawable.s_lock1,
            R.drawable.levels_wheel0,
            R.drawable.levels_wheel1,
            R.drawable.levels_wheel2
    };

    private final Map<MenuType, MenuScreen<T>> menus = new HashMap<>();
    private final Application application;
    private final GdPlatform platform;

    private AMenu<T> menu;

    private CampaignSelectors campaignSelectors;
    private TrackEditorView trackEditor;
    private List<String> modNames = new ArrayList<>(); //todo remove
    public List<String> themeNames = new ArrayList<>(); //todo remove

    public MenuFactory(Application application, GdPlatform platform) {
        this.application = application;
        this.platform = platform;
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
            throw new IllegalStateException("FIX ME: no " + type); //todo remove
        }
        return screen;
    }

    //Все переплетено
    //Потяни за нить
    //За ней потянется клубок
    public void init(AMenu<T> menu, TrackEditorView trackEditor) {
        this.menu = menu;
        this.trackEditor = trackEditor;
        add(MenuType.MAIN, r -> {
            String title = s(R.string.main);
            return screen(title, null);
        });
        add(MenuType.PLAY, r -> screen(s(R.string.play), r.get(MenuType.MAIN)));
        add(MenuType.PROFILE, this::createProfileScreen);
        add(MenuType.ABOUT, this::createAboutScreen);
        add(MenuType.HELP, this::createHelpScreen);
        add(MenuType.OPTIONS, this::createOptionsScreen);
        add(MenuType.CAMPAIGN, this::createPlayCampaign);

        add(MenuType.FINISHED_PLAY, this::createFinishedPlay);
        add(MenuType.IN_GAME_PLAY, this::createInGamePlay);
        add(MenuType.WORKSHOP, r -> screen(s(R.string.workshop), r.get(MenuType.MAIN)));
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

    private MenuScreen<T> createTEMPLATE(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.main), r.get(MenuType.MAIN));
        screen.setBuilder((s, data) -> {
            return s;
        });
        return screen;
    }

    private MenuScreen<T> createInGameReplay(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.replay), r.get(MenuType.RECORDING_OPTIONS));
        screen.setBuilder((s, data) -> {
            s.clear();
            s.addItem(actionContinue(__ -> application.menuToGame()));
            s.addItem(getItem(s(R.string.options), this.get(MenuType.OPTIONS), null));
            s.addItem(getMenuAction(s(R.string.back), it -> {
                application.getGame().resetState();
                menu.menuBack();
            }));
            s.resetHighlighted();
            return s;
        });
        return screen;
    }

    private MenuScreen<T> createInGameEditor(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.track_editor), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            s.clear();
            s.addItem(getMenuAction(s(R.string.back), it -> trackEditor.startEditing()));
            s.addItem(getMenuAction(s(R.string.play), it -> trackEditor.playTrack()));
            s.addItem(getItem(s(R.string.track_options), this.get(MenuType.TRACK_EDITOR_OPTIONS), item -> this.get(MenuType.TRACK_EDITOR_OPTIONS).build(new MenuData(trackEditor.getCurrentTrack()))));
            s.addItem(getItem(s(R.string.options), this.get(MenuType.OPTIONS), null));
            s.addItem(getMenuAction(s(R.string.save), it -> trackEditor.saveTrack()));
            s.addItem(getMenuAction(s(R.string.exit_editor), it -> {
                trackEditor.exitEditor();
                menu.menuBack();
            }));

            s.resetHighlighted();
            trackEditor.hideLayout();
            return s;
        });
        return screen;
    }

    private MenuScreen<T> createTrackOptions(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.track_editor), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            TrackParams track = data.getTrackRef();
            s.clear();
            s.addItem(menu.backAction());
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, track.getData().getGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.author, track.getData().getAuthor())));
            s.addItem(new InputTextElement(Fmt.colon(s(R.string.name)), track.getData().getName(), item1 -> {
                TrackParams currentTrack = trackEditor.getCurrentTrack();
                String name = ((InputTextElement)item1).getText().toString().trim();
                currentTrack.getData().setName(name);
            }));

            s.addItem(emptyLine(false));
            s.addItem(new TextMenuElement(boldAttr(R.string.league_properties, null)));
            s.addItem(new OptionsMenuElement(s(R.string.league), track.getData().league, menu, application.getModManager().getLeagueNames(), false, screen,
                    item -> {
                        OptionsMenuElement it = (OptionsMenuElement) item;
                        if (it._charvZ()) {
                            MenuScreen<T> leagueSelectorCurrentMenu = it.getCurrentMenu();
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

            s.addItem(emptyLine(false));
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

    private MenuScreen<T> createThemes(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.themes), r.get(MenuType.WORKSHOP));
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
            s.addItem(emptyLine(true));
            s.addItem(emptyLine(true));
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

    private MenuScreen<T> createThemeOptions(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.theme), r.get(MenuType.WORKSHOP));
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
            s.addItem(emptyLine(true));

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

    private MenuScreen<T> createModOptions(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.mod), r.get(MenuType.MODS));
        screen.setBuilder((s, data) -> {
            Mod mod = data.getMod();
            s.clear();
            s.addItem(new TextMenuElement(boldAttr(R.string.name, mod.getName())));
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, mod.getGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.tracks, Fmt.formatLevelsCount(mod))));
            s.addItem(new TextMenuElement(boldAttr(R.string.author, mod.getAuthor())));
            s.addItem(new TextMenuElement(boldAttr(R.string.date, mod.getDate())));
            s.addItem(emptyLine(false));

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

    private MenuScreen<T> createMods(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.mod_packs), r.get(MenuType.WORKSHOP));
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
            s.addItem(emptyLine(true));
            s.addItem(emptyLine(true));
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

    private void fillPlay(MenuScreen<T> screen) {
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
                            MenuScreen<T> leagueSelectorCurrentMenu = it.getCurrentMenu();
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
            s.addItem(getMenuAction(Fmt.ra(s(R.string.random_track)), __ -> game.startTrack(GameParams.of(GameMode.RANDOM, modManager.getRandomTrack()))));
            s.addItem(getMenuAction(Fmt.ra(s(R.string.tracks)), __ -> application.notify("Coming soon")));
            s.addItem(getItem(s(R.string.achievements), this.get(MenuType.ACHIEVEMENTS), __ -> this.get(MenuType.ACHIEVEMENTS).build()));
            s.addItem(menu.backAction(game::resetState));
            return s;
        });
    }

    private MenuScreen<T> createAchievements(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.achievements), r.get(MenuType.PLAY));
        screen.setBuilder((s, data) -> {
            s.clear();
            for (Achievement achievement : Achievement.achievements.values()) {
                String title = Fmt.sp(application.getStr().s(achievement.getName()), achievement.getProgressFormatted());
                s.addItem(new BadgeWithTextElement(ACHIEVEMENT_ICONS[achievement.getLevel()], title, menu, item -> {
                }));
                s.addItem(new TextMenuElement(Html.fromHtml(application.getStr().s(achievement.getDescription()))));
                s.addItem(emptyLine(false));
            }
            s.addItem(menu.backAction());
            return s;
        });
        return screen;
    }

    private MenuScreen<T> createRecordings(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.recordings), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
                    s.clear();
                    s.addItem(menu.backAction());
                    s.addItem(new MenuAction(s(R.string.import_record), MenuAction.SELECT_FILE, menu, it -> {
                        platform.pickFile(Constants.PICKFILE_RECORD_RESULT_CODE);
                    }));
                    s.addItem(emptyLine(false));
                    s.addItem(emptyLine(false));
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

    private MenuScreen<T> createRecordingOptions(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.record_option), r.get(MenuType.RECORDINGS));
        screen.setBuilder((s, data) -> {
            TrackRecord rec = data.getRecording();
            s.clear();
            s.addItem(new TextMenuElement(boldAttr(R.string.name, rec.getTrackName())));
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, rec.getTrackGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.league, "" + rec.getLeague())));
            long millis = rec.getTime();
            s.addItem(new TextMenuElement(boldAttr(R.string.time, Utils.getDurationString(millis))));
            s.addItem(new TextMenuElement(boldAttr(R.string.date, rec.getDate())));
            s.addItem(emptyLine(true));

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

    private void transformWorkshop(MenuScreen<T> s) {
        s.addItem(getMenuAction(Fmt.ra(s(R.string.create_new_track)), item -> {
            application.notify("Coming soon");
//            trackEditor.createNew(application.getSettings().getPlayerName());
        }));
        s.addItem(getItem(s(R.string.mod_packs), this.get(MenuType.MODS), __ -> this.get(MenuType.MODS).build()));
        s.addItem(getItem(s(R.string.themes), this.get(MenuType.THEMES), __ -> this.get(MenuType.THEMES).build()));
        s.addItem(getItem(s(R.string.recordings), this.get(MenuType.RECORDINGS), __ -> this.get(MenuType.RECORDINGS).build()));
        s.addItem(emptyLine(true));
        s.addItem(menu.backAction());
    }

    public MenuElement<T> emptyLine(boolean beforeAction) {
        return new EmptyLineMenuElement<T>(beforeAction ? 10 : 20);
    }
    private MenuElement<T> getItem(String title, MenuScreen<T> parent, ActionHandler handler) {
        return new MenuItem<T>(title, parent, menu, handler);
    }

    private MenuScreen<T> createFinishedPlay(Map<MenuType, MenuScreen<T>> r) {
        String title = s(R.string.finished);
        MenuScreen<T> parent = r.get(MenuType.PLAY);
        MenuScreen<T> fm = screen(title, parent);
        fm.setBuilder((finishedMenu, data) -> {
            Game game = application.getGame();
            finishedMenu.clear();
            long millis = data.getLastTrackTime();
            finishedMenu.addItem(new TextMenuElement(boldAttr(R.string.time, Utils.getDurationString(millis))));
            for (String s : application.getHighScoreManager().getFormattedScores(data.getTrackGuid(), data.getSelectedLeague())) {
                finishedMenu.addItem(new TextMenuElement(s));
            }
            finishedMenu.addItem(this.getMenuAction(Fmt.ra(s(R.string.random_track)), __ -> game.startTrack(GameParams.of(GameMode.RANDOM, application.getModManager().getRandomTrack()))));
            finishedMenu.addItem(this.getItdasd(Fmt.colon(s(R.string.restart), data.getTrackName()), __ -> game.restart()));
            finishedMenu.addItem(this.menu.backAction());
            return finishedMenu;
        });
        return fm;
    }

    private MenuScreen<T> createInGamePlay(Map<MenuType, MenuScreen<T>> r) {
        Game game = application.getGame();
        String title = s(R.string.ingame);
        MenuScreen<T> parent = r.get(MenuType.PLAY);
        MenuScreen<T> ig = screen(title, parent);
        ig.addItem(actionContinue(__ -> application.menuToGame()));
        ig.addItem(getMenuAction(s(R.string.training_mode), __ -> {
            application.trainingMode();
            application.menuToGame();
        }));
        ig.addItem(restartAction("Name", __ -> game.restart()));
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

    private MenuElement<T> getMenuAction(String title, ActionHandler handler) {
        return new MenuAction<T>(title, menu, handler);
    }

    private MenuElement<T> restartAction(String name, ActionHandler handler) {
        return getItdasd(Fmt.colon(s(R.string.restart), name), handler);
    }

    private MenuElement<T> actionContinue(ActionHandler handler) {
        return new MenuAction<T>(s(R.string._continue), MenuAction.CONTINUE, menu, handler);
    }

    public MenuScreen<T> screen(String title, MenuScreen<T> parent) {
        return new AMenuScreen(title, parent);
    }

    private void fillMainScreen(MenuScreen<T> s) {
        s.setBeforeShowAction(() -> application.getGame().startAutoplay(false));
        s.addItem(new MenuItem(s(R.string.competition), this.get(MenuType.PLAY), menu));
        s.addItem(new MenuItem(s(R.string.workshop), this.get(MenuType.WORKSHOP), menu));
        s.addItem(new MenuItem(s(R.string.profile), this.get(MenuType.PROFILE), menu));
        s.addItem(new MenuItem(s(R.string.options), this.get(MenuType.OPTIONS), menu));
        s.addItem(new MenuItem(s(R.string.help), this.get(MenuType.HELP), menu));
        s.addItem(new MenuItem(s(R.string.about), this.get(MenuType.ABOUT), menu));
        s.addItem(menu.createAction(MenuAction.EXIT, item -> application.exit()));
    }

    private MenuScreen<T> createOptionsScreen(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.options), r.get(MenuType.MAIN));
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

        String title1 = s(R.string.confirm_clear);
        MenuScreen<T> eraseScreen = screen(title1, screen);
        eraseScreen.addItem(new TextMenuElement(s(R.string.erase_text1)));
        eraseScreen.addItem(new TextMenuElement(s(R.string.erase_text2)));
        eraseScreen.addItem(emptyLine(true));
        eraseScreen.addItem(menu.createAction(MenuAction.NO, item -> menu.menuBack()));
        eraseScreen.addItem(menu.createAction(MenuAction.YES, item -> {
            application.getHighScoreManager().clearAllHighScores();
            showAlert(s(R.string.cleared), s(R.string.cleared_text), null);
            menu.menuBack();
        }));

        String title = s(R.string.confirm_reset);
        MenuScreen<T> resetScreen = screen(title, eraseScreen);
        resetScreen.addItem(new TextMenuElement(s(R.string.reset_text1)));
        resetScreen.addItem(new TextMenuElement(s(R.string.reset_text2)));
        resetScreen.addItem(emptyLine(true));
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

        eraseScreen.addItem(getItem(s(R.string.full_reset), resetScreen, null));
        screen.addItem(getItem(s(R.string.clear_highscore), eraseScreen, null));

        screen.addItem(menu.backAction());
        return screen;
    }

    private MenuScreen<T> createHelpScreen(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.help), r.get(MenuType.MAIN));
        String title4 = s(R.string.objective);
        MenuScreen<T> objectiveHelpScreen = screen(title4, screen);
        objectiveHelpScreen.setIsTextScreen(true);
        objectiveHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.objective_text))));
        objectiveHelpScreen.addItem(menu.backAction());

        String title3 = s(R.string.keys);
        MenuScreen<T> keysHelpScreen = screen(title3, screen);
        keysHelpScreen.setIsTextScreen(true);
        keysHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.keyset_text))));
        keysHelpScreen.addItem(new MenuAction(s(R.string.back), MenuAction.BACK, menu, item -> menu.menuBack()));

        String title2 = s(R.string.unlocking);
        MenuScreen<T> unlockingHelpScreen = screen(title2, screen);
        unlockingHelpScreen.setIsTextScreen(true);
        unlockingHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.unlocking_text))));
        unlockingHelpScreen.addItem(menu.backAction());

        String title1 = s(R.string.highscores);
        MenuScreen<T> highscoreHelpScreen = screen(title1, screen);
        highscoreHelpScreen.setIsTextScreen(true);
        highscoreHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.highscore_text))));
        highscoreHelpScreen.addItem(menu.backAction());

        String title = s(R.string.options);
        MenuScreen<T> optionsHelpScreen = screen(title, screen);
        optionsHelpScreen.setIsTextScreen(true);
        optionsHelpScreen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.options_text))));
        optionsHelpScreen.addItem(menu.backAction());

        screen.addItem(getItem(s(R.string.objective), objectiveHelpScreen, null));
        screen.addItem(getItem(s(R.string.keys), keysHelpScreen, null));
        screen.addItem(getItem(s(R.string.unlocking), unlockingHelpScreen, null));
        screen.addItem(getItem(s(R.string.highscores), highscoreHelpScreen, null));
        screen.addItem(getItem(s(R.string.options), optionsHelpScreen, null));
        screen.addItem(menu.backAction());
        return screen;
    }

    private MenuScreen<T> createAboutScreen(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(String.format("%s v%s", s(R.string.about), getAppVersion()), r.get(MenuType.MAIN));
        screen.setIsTextScreen(true);
        screen.addItem(new TextMenuElement(Html.fromHtml(s(R.string.about_text))));
        screen.addItem(menu.backAction());
        return screen;
    }

    private MenuScreen<T> createProfileScreen(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.profile), r.get(MenuType.MAIN));
        InputTextElement nameInput = new InputTextElement(Fmt.colon(s(R.string.name), ""), application.getSettings().getPlayerName(), null);
        screen.addItem(nameInput);
        screen.addItem(getMenuAction(s(R.string.save), item -> {
            application.getSettings().setPlayerName(nameInput.getText());
            application.notify(s(R.string.saved));
        }));
        screen.addItem(menu.backAction());
        return screen;
    }

    private MenuScreen<T> createInGameCampaign(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> inGame = screen(s(R.string.ingame), r.get(MenuType.CAMPAIGN));
        inGame.addItem(actionContinue(__ -> application.menuToGame()));
        inGame.addItem(getItdasd(Fmt.colon(s(R.string.restart), ""), item -> menu.menuToGame()));
        inGame.addItem(getMenuAction(s(R.string.training_mode), __ -> {
            application.trainingMode();
            application.menuToGame();
        }));
        inGame.addItem(menu.createAction(MenuAction.LIKE, item -> application.notify("Coming soon")));
        inGame.addItem(getItem(s(R.string.options), r.get(MenuType.OPTIONS), null));
        inGame.addItem(getItem(s(R.string.help), r.get(MenuType.HELP), null));
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

    private MenuScreen<T> createFinishedCampaign(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> finished = screen(s(R.string.finished), r.get(MenuType.CAMPAIGN));
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

    private MenuScreen<T> showFinishMenu(MenuScreen<T> finishedMenu, MenuData data) {
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
        finishedMenu.addItem(getItdasd(Fmt.colon(s(R.string.restart), data.getTrackName()), item -> menu.menuToGame()));
        finishedMenu.addItem(menu.createAction(MenuAction.PLAY_MENU, item -> actionGoToPlayMenu()));
        finishedMenu.resetHighlighted();
        finishedMenu.highlightElement();
        return finishedMenu;
    }

    private MenuScreen<T> createHighScore(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> hs = screen(s(R.string.highscores), r.get(MenuType.CAMPAIGN));
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

    private MenuScreen<T> createPlayCampaign(Map<MenuType, MenuScreen<T>> r) {
        MenuScreen<T> screen = screen(s(R.string.campaign), r.get(MenuType.PLAY));
        screen.setBeforeShowAction(() -> {
            screen.setTitle(application.getModManager().getMod().getName());
            application.getGame().startAutoplay(true);
        });
        return screen;
    }

    public void transformPlayCampaign(MenuScreen<T> s) {
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
        s.addItem(getItem(s(R.string.highscores), this.get(MenuType.HIGH_SCORE), null));
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

    private MenuElement<T> getItdasd(String title, ActionHandler handler) {
        return new MenuAction<T>(title, MenuAction.RESTART, menu, handler);
    }
}
