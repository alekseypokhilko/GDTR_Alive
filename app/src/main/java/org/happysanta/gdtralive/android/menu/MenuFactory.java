package org.happysanta.gdtralive.android.menu;

import static org.happysanta.gdtralive.android.Helpers.boldAttr;
import static org.happysanta.gdtralive.android.Helpers.getAppVersion;
import static org.happysanta.gdtralive.android.Helpers.getStringArray;
import static org.happysanta.gdtralive.android.Helpers.s;
import static org.happysanta.gdtralive.android.Helpers.showAlert;

import android.text.Html;
import android.view.View;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.AndroidFileStorage;
import org.happysanta.gdtralive.android.GDActivity;
import org.happysanta.gdtralive.android.Helpers;
import org.happysanta.gdtralive.android.MrgPicker;
import org.happysanta.gdtralive.android.TrackEditorView;
import org.happysanta.gdtralive.android.menu.element.BadgeWithTextElement;
import org.happysanta.gdtralive.android.menu.element.InputTextElement;
import org.happysanta.gdtralive.android.menu.element.MenuAction;
import org.happysanta.gdtralive.android.menu.element.MenuItem;
import org.happysanta.gdtralive.android.menu.element.OptionsMenuElement;
import org.happysanta.gdtralive.android.menu.element.PropInput;
import org.happysanta.gdtralive.android.menu.element.TextMenuElement;
import org.happysanta.gdtralive.android.menu.screens.ClassicPlayMenuState;
import org.happysanta.gdtralive.game.Achievement;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.external.GdUtils;
import org.happysanta.gdtralive.game.mod.Mod;
import org.happysanta.gdtralive.game.mod.Theme;
import org.happysanta.gdtralive.game.mod.ThemeHeader;
import org.happysanta.gdtralive.game.mod.TrackReference;
import org.happysanta.gdtralive.game.modes.MenuData;
import org.happysanta.gdtralive.game.modes.MenuType;
import org.happysanta.gdtralive.game.recorder.TrackRecord;
import org.happysanta.gdtralive.game.storage.GDFile;
import org.happysanta.gdtralive.game.visual.Fmt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuFactory {
    public static int[] ACHIEVEMENT_ICONS = {
            R.drawable.s_lock1,
            R.drawable.levels_wheel0,
            R.drawable.levels_wheel1,
            R.drawable.levels_wheel2
    };

    private final Map<MenuType, MenuScreen> menus = new HashMap<>();
    private final GDActivity application;
    private Menu menu;

    private ClassicPlayMenuState classicPlayMenu;
    private TrackEditorView trackEditor;
    private List<String> packNames = new ArrayList<>(); //todo remove
    public List<Theme> themes = new ArrayList<>(); //todo remove

    public MenuFactory(GDActivity application) {
        this.application = application;
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
            throw new IllegalStateException("FIX ME"); //todo remove
        }
        return screen;
    }

    //Все переплетено
    //Потяни за нить
    //За ней потянется клубок
    public void init(Menu menu) {
        this.menu = menu;
        this.trackEditor = application.trackEditor;
        add(MenuType.MAIN, r -> new MenuScreen(s(R.string.main), null));
        add(MenuType.PLAY, r -> new MenuScreen(s(R.string.play), r.get(MenuType.MAIN)));
        add(MenuType.PROFILE, this::createProfileScreen);
        add(MenuType.ABOUT, this::createAboutScreen);
        add(MenuType.HELP, this::createHelpScreen);
        add(MenuType.OPTIONS, this::createOptionsScreen);
        add(MenuType.PLAY_CLASSIC, this::createPlayClassic);

        add(MenuType.FINISHED_PLAY, this::createFinishedPlay);
        add(MenuType.IN_GAME_PLAY, this::createInGamePlay);
        add(MenuType.WORKSHOP, r -> new MenuScreen(s(R.string.workshop), r.get(MenuType.MAIN)));
        add(MenuType.MODS, this::createMods);
        add(MenuType.MOD_OPTIONS, this::createModOptions);
        add(MenuType.THEMES, this::createThemes);
        add(MenuType.THEME_OPTIONS, this::createThemeOptions);

        add(MenuType.TRACK_EDITOR_OPTIONS, this::createTrackOptions);
        add(MenuType.IN_GAME_TRACK_EDITOR, this::createInGameEditor);
        transform(MenuType.WORKSHOP, this::transformWorkshop);

        //        add(MenuType.DAILY, r -> new MenuScreen(s(R.string.daily_challenge), r.get(MenuType.PLAY)));//todo
//        DailyMenu dailyMenu = new DailyMenu(menu, playMenuScreen, application, this); //todo

        add(MenuType.RECORDINGS, this::createRecordings);
        add(MenuType.RECORDING_OPTIONS, this::createRecordingOptions);
        add(MenuType.ACHIEVEMENTS, this::createAchievements);
        transform(MenuType.PLAY, this::fillPlay);

        this.classicPlayMenu = new ClassicPlayMenuState(menu, application, this);
        add(MenuType.HIGH_SCORE, classicPlayMenu::createHighScore);
        add(MenuType.IN_GAME_CLASSIC, classicPlayMenu::createInGameClassic);
        add(MenuType.FINISHED_CLASSIC, classicPlayMenu::createFinishedClassic);
        transform(MenuType.PLAY_CLASSIC, classicPlayMenu::transformPlayClassic);

        transform(MenuType.MAIN, this::fillMainScreen);
    }

    private MenuScreen createTEMPLATE(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.main), r.get(MenuType.MAIN));
        screen.setBuilder((s, data) -> {
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
            TrackReference track = data.getTrackRef();
            s.clear();
            s.addItem(menu.backAction());
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, track.getGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.author, track.getData().getAuthor())));
            s.addItem(new InputTextElement(Fmt.colon(s(R.string.name)), track.getName(), item1 -> {
                TrackReference currentTrack = trackEditor.getCurrentTrack();
                String name = item1.getText().toString().trim();
                currentTrack.setName(name);
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
            for (Map.Entry<String, String> entry : track.getLeagueProperties().entrySet()) {
                s.addItem(new PropInput(Fmt.colon(entry.getKey()), entry.getValue(), entry.getKey(), item -> {
                    trackEditor.getCurrentTrack().getLeagueProperties().put(item.getKey(), item.getText());
                    application.getModManager().setTrackProperties(trackEditor.getCurrentTrack());
                }));
            }

            s.addItem(MenuUtils.emptyLine(false));
            s.addItem(new TextMenuElement(boldAttr(R.string.track_properties, null)));
            for (Map.Entry<String, String> entry : track.getGameProperties().entrySet()) {
                s.addItem(new PropInput(Fmt.colon(entry.getKey()), entry.getValue(), entry.getKey(), item -> {
                    trackEditor.getCurrentTrack().getGameProperties().put(item.getKey(), item.getText());
                    application.getModManager().setTrackProperties(trackEditor.getCurrentTrack());
                }));
            }
            application.hideKeyboardLayout();
            System.gc(); //hopefully
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
                String packName = mod.getName();
                application.getModManager().setMod(mod);
                //game.levelsManager.activateMod(mod);
                Helpers.showToast(Fmt.colon(s(R.string.mod_activated), packName));
                // todo game.getLevelsManager().install(file, name, "", 0);
                classicPlayMenu.getScreen().setTitle(Fmt.sp(s(R.string.play), packName));
                classicPlayMenu.resetSelectors();
            }));
            s.addItem(new MenuAction(s(R.string.save), -1, menu,
                    __ -> this.application.getFileStorage().writeToFile(mod, GDFile.MOD, Fmt.us(mod.getGuid(), mod.getName()))));
            s.addItem(menu.backAction(() -> this.get(MenuType.MODS).build()));
            return s;
        });
        return screen;
    }

    private MenuScreen createThemes(Map<MenuType, MenuScreen> r) {
        themes.add(Theme.defaultTheme());
        themes.add(Theme.amoledMod());
        MenuScreen screen = new MenuScreen(s(R.string.themes), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            s.clear();
            s.addItem(menu.backAction());
            s.addItem(MenuUtils.emptyLine(true));
            s.addItem(MenuUtils.emptyLine(true));
            for (int i = 0; i < themes.size(); i++) {
                try {
                    Theme theme = themes.get(i);
                    MenuItem options = new MenuItem(theme.getHeader().getName(), this.get(MenuType.THEME_OPTIONS), menu,
                            item -> this.get(MenuType.THEME_OPTIONS).build(new MenuData(themes.get(((MenuItem) item).getValue()))));
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
            Theme theme = data.getTheme();
            s.clear();
            ThemeHeader header = theme.getHeader();
            s.addItem(new TextMenuElement(boldAttr(R.string.name, header.getName())));
            s.addItem(new TextMenuElement(boldAttr(R.string.description, header.getDescription())));
            s.addItem(new TextMenuElement(boldAttr(R.string.guid, header.getGuid())));
            s.addItem(new TextMenuElement(boldAttr(R.string.author, header.getAuthor())));
            s.addItem(new TextMenuElement(boldAttr(R.string.date, header.getDate())));
            s.addItem(MenuUtils.emptyLine(true));

            s.addItem(new MenuAction(s(R.string.install), -1, menu, item -> {
                application.getModManager().installTheme(theme);
                Achievement.achievements.get(Achievement.Type.ESTHETE).increment();
            }));
            s.addItem(new MenuAction(s(R.string.save), -1, menu, item -> {
                String fileName = Fmt.us(theme.getHeader().getName(), theme.getHeader().getAuthor());
                new AndroidFileStorage().writeToFile(theme, GDFile.THEME, fileName);
            }));
            s.addItem(menu.backAction(() -> this.get(MenuType.THEMES).build()));
            return s;
        });
        return screen;
    }

    private MenuScreen createMods(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.mod_packs), r.get(MenuType.WORKSHOP));
        screen.setBuilder((s, data) -> {
            s.clear();
            packNames.clear();
            s.addItem(menu.backAction());
            s.addItem(new MenuAction(s(R.string.convert_mrg), MenuAction.SELECT_FILE, menu, it -> MrgPicker.convertMrgFromFileBrowse()));
            int i = 0;
            //todo refactoring
            try {
                for (File file : new File(AndroidFileStorage.EXTERNAL_MODS_FOLDER).listFiles()) {
                    try {
                        String name = file.getName();
                        packNames.add(name);
                        String packName = name.substring(name.indexOf("_") + 1);
                        MenuItem options = new MenuItem(packName, this.get(MenuType.MOD_OPTIONS), menu,
                                item -> {
                                    int value = ((MenuItem) item).getValue();
                                    try {
                                        String rec = packNames.get(value);
                                        Mod mod = this.application.getFileStorage().loadMod(rec);
                                        this.get(MenuType.MOD_OPTIONS).build(new MenuData(mod));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                        options.setValue(i);
                        s.addItem(options);
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); //todo
            }
            try {
                for (String filename : Objects.requireNonNull(GDActivity.shared.getAssets().list(AndroidFileStorage.ASSETS_MODS_FOLDER))) {
                    try {
                        String name = filename.replace(".json", "");
                        packNames.add(name);
                        String packName = name.substring(name.indexOf("_") + 1);
                        MenuItem options = new MenuItem(packName, this.get(MenuType.MOD_OPTIONS), menu,
                                item -> {
                                    int value = ((MenuItem) item).getValue();
                                    try {
                                        String rec = packNames.get(value);
                                        Mod mod = this.application.getFileStorage().loadMod(rec);
                                        this.get(MenuType.MOD_OPTIONS).build(new MenuData(mod));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                        options.setValue(i);
                        s.addItem(options);
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); //todo
            }
            return s;
        });
        return screen;
    }

    private void fillPlay(MenuScreen s) {
        Game game = application.getGame();
        s.setBeforeShowAction(game::startAutoplay);
//        s.addItem(new MenuItem(s(R.string.daily_challenge), this.get(MenuType.DAILY), menu, __ -> this.get(MenuType.DAILY).build())); //todo
        s.addItem(new MenuItem(s(R.string.play_menu), this.get(MenuType.PLAY_CLASSIC), menu, null));
        s.addItem(new MenuAction(Fmt.ra(s(R.string.random_track)), menu, __ -> game.startRandomTrack(application.getModManager().getMod())));
        s.addItem(new MenuAction(Fmt.ra(s(R.string.tracks)), menu, null));
        s.addItem(new MenuItem(s(R.string.achievements), this.get(MenuType.ACHIEVEMENTS), menu, __ -> this.get(MenuType.ACHIEVEMENTS).build()));
        s.addItem(new MenuItem(s(R.string.recordings), this.get(MenuType.RECORDINGS), menu, __ -> this.get(MenuType.RECORDINGS).build()));
        s.addItem(menu.backAction(game::resetState));
    }

    private MenuScreen createAchievements(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.achievements), r.get(MenuType.PLAY));
        screen.setBuilder((s, data) -> {
            s.clear();
            for (Achievement achievement : Achievement.achievements.values()) {
                String title = Fmt.sp(application.getUtils().s(achievement.getName()), achievement.getProgressFormatted());
                s.addItem(new BadgeWithTextElement(ACHIEVEMENT_ICONS[achievement.getLevel()], title, menu, item -> {
                }));
                s.addItem(new TextMenuElement(Html.fromHtml(application.getUtils().s(achievement.getDescription()))));
                s.addItem(MenuUtils.emptyLine(false));
            }
            s.addItem(menu.backAction());
            return s;
        });
        return screen;
    }

    private MenuScreen createRecordings(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.recordings), r.get(MenuType.PLAY));
        screen.setBuilder((s, data) -> {
                    s.clear();
                    s.addItem(menu.backAction());
                    s.addItem(MenuUtils.emptyLine(false));
                    int i = 0;
                    for (TrackRecord rec : application.getFileStorage().getAllRecords()) {
                        try {
                            String time = GdUtils.getDurationString(rec.getTime());
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
            s.addItem(new TextMenuElement(boldAttr(R.string.time, GdUtils.getDurationString(millis))));
            s.addItem(new TextMenuElement(boldAttr(R.string.date, rec.getDate())));
            s.addItem(MenuUtils.emptyLine(true));

            s.addItem(new MenuAction(s(R.string.replay), -1, menu, item -> application.getGame().startReplay(rec)));
            s.addItem(new MenuAction(s(R.string.save), -1, menu, item -> {
                application.getFileStorage().writeToFile(rec, GDFile.RECORD, Fmt.us(rec.getTrackName(), rec.getDate()));
            }));
            s.addItem(menu.backAction(() -> {
                this.get(MenuType.RECORDINGS).build();
                application.getGame().getPlayer().reset();
                application.getGame().startAutoplay();
            }));

            application.getGame().setTrack(rec.getTrack());
            application.getGame().getPlayer().reset();
            application.getGame().getPlayer().setTrackRecord(rec);
            application.getGame().getPlayer().setReplayMode(true);
            return s;
        });
        return screen;
    }

    private void transformWorkshop(MenuScreen s) {
        s.addItem(new MenuAction(Fmt.ra(s(R.string.create_new_track)), menu, item -> trackEditor.createNew()));
        s.addItem(new MenuItem(s(R.string.mod_packs), this.get(MenuType.MODS), menu, __ -> this.get(MenuType.MODS).build()));
        s.addItem(new MenuItem(s(R.string.themes), this.get(MenuType.THEMES), menu, __ -> this.get(MenuType.THEMES).build()));
//        TrackEditorMenu trackEditor = new TrackEditorMenu(menu, s, application);
//        s.addItem(new MenuItem(s(R.string.track_editor), trackEditor.getScreen(), menu, __ -> trackEditor.buildScreen()));
//        s.addItem(new MenuItem(s(R.string.track_editor), this.get(MenuType.TRACK_EDITOR), menu, __ -> this.get(MenuType.TRACK_EDITOR).build()));
        s.addItem(MenuUtils.emptyLine(true));
        s.addItem(menu.backAction());
    }

    private MenuScreen createFinishedPlay(Map<MenuType, MenuScreen> r) {
        MenuScreen fm = new MenuScreen(s(R.string.finished), r.get(MenuType.PLAY));
        fm.setBuilder((finishedMenu, data) -> {
            Game game = application.getGame();
            finishedMenu.clear();
            finishedMenu.addItem(new TextMenuElement(boldAttr(R.string.time, GdUtils.getDurationString(game.getLastTrackTime()))));
            for (String s : application.getHighScoreManager().getFormattedScores(game.getCurrentTrackGuid(), game.getSelectedLeague())) {
                finishedMenu.addItem(new TextMenuElement(s));
            }
            finishedMenu.addItem(new MenuAction(Fmt.ra(s(R.string.random_track)), this.menu, __ -> game.startRandomTrack(application.getModManager().getMod())));
            finishedMenu.addItem(new MenuAction(Fmt.colon(s(R.string.restart), game.getCurrentTrackName()), MenuAction.RESTART, this.menu, __ -> game.restart()));
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
            application.actionButton.setVisibility(View.VISIBLE);
            application.menuToGame();
        }));
        ig.addItem(new MenuAction(Fmt.colon(s(R.string.restart), game.getCurrentTrackName()), MenuAction.RESTART, menu, __ -> game.restart()));
//        ig.addItem(createAction(ActionMenuElement.LIKE, item -> { }));
        ig.addItem(menu.backAction(game::resetState));
        ig.setBuilder((s, data) -> {
            if (data != null) {
                s.getActions(MenuAction.RESTART).setText(Fmt.colon(s(R.string.restart), game.getCurrentTrackName()));
            }
            s.resetHighlighted();
            return s;
        });
        return ig;
    }

    private void fillMainScreen(MenuScreen s) {
        s.setBeforeShowAction(application.getGame()::startAutoplay);
        s.addItem(new MenuItem(s(R.string.competition), this.get(MenuType.PLAY), menu));
        s.addItem(new MenuItem(s(R.string.workshop), this.get(MenuType.WORKSHOP), menu));
        s.addItem(new MenuItem(s(R.string.profile), this.get(MenuType.PROFILE), menu));
        s.addItem(new MenuItem(s(R.string.options), this.get(MenuType.OPTIONS), menu));
        s.addItem(new MenuItem(s(R.string.help), this.get(MenuType.HELP), menu));
        s.addItem(new MenuItem(s(R.string.about), this.get(MenuType.ABOUT), menu));
        s.addItem(menu.createAction(MenuAction.EXIT, item -> application.exit()));
    }

    private MenuScreen createPlayClassic(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.play), r.get(MenuType.PLAY));
        screen.setBeforeShowAction(application.getGame()::startAutoplay);
        return screen;
    }

    private MenuScreen createOptionsScreen(Map<MenuType, MenuScreen> r) {
        MenuScreen screen = new MenuScreen(s(R.string.options), r.get(MenuType.MAIN));
        String[] onOffStrings = getStringArray(R.array.on_off);
        String[] keySetStrings = getStringArray(R.array.keyset);

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
        screen.addItem(new OptionsMenuElement(s(R.string.look_ahead), application.getSettings().isLookAheadEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> {
                    OptionsMenuElement it = (OptionsMenuElement) item;
                    application.getGame().setLookAhead(it.getSelectedOption() == 0);
                }));
        screen.addItem(new OptionsMenuElement(s(R.string.vibrate_on_touch), application.getSettings().isVibrateOnTouchEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> application.getSettings().setVibrateOnTouchEnabled(((OptionsMenuElement) item).getSelectedOption() == 0)
        ));
        screen.addItem(new OptionsMenuElement(s(R.string.keyboard_in_menu), application.getSettings().isKeyboardInMenuEnabled() ? 0 : 1, menu, onOffStrings, true, screen,
                item -> {
                    boolean enabled = ((OptionsMenuElement) item).getSelectedOption() == 0;
                    application.getSettings().setKeyboardInMenuEnabled(enabled);
                    if (enabled) application.showKeyboardLayout();
                    else application.hideKeyboardLayout();
                }));

        MenuScreen eraseScreen = new MenuScreen(s(R.string.confirm_clear), screen);
        eraseScreen.addItem(new TextMenuElement(s(R.string.erase_text1)));
        eraseScreen.addItem(new TextMenuElement(s(R.string.erase_text2)));
        eraseScreen.addItem(MenuUtils.emptyLine(true));
        eraseScreen.addItem(menu.createAction(MenuAction.NO, item -> menu.menuBack()));
        eraseScreen.addItem(menu.createAction(MenuAction.YES, item -> {
            application.getHighScoreManager().clearHighScores(application.getModManager().getTrackGuid(
                    classicPlayMenu.levelSelector.getSelectedOption(),
                    classicPlayMenu.trackSelector.getSelectedOption() //todo get dto from state
            ));
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

                application.fullResetting = true;
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
            Helpers.showToast(s(R.string.saved));
        }));
        screen.addItem(menu.backAction());
        return screen;
    }
}
