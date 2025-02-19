package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.s;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.KeyboardController;
import org.happysanta.gdtralive.android.menu.element.InputTextElement;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuLinearLayout;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.editor.EditorMode;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.external.GdApplication;
import org.happysanta.gdtralive.game.external.GdMenu;
import org.happysanta.gdtralive.game.external.GdSettings;
import org.happysanta.gdtralive.game.external.GdUtils;
import org.happysanta.gdtralive.game.levels.TrackParams;
import org.happysanta.gdtralive.game.mod.ModManager;
import org.happysanta.gdtralive.game.mod.Theme;
import org.happysanta.gdtralive.game.mod.TrackReference;
import org.happysanta.gdtralive.game.modes.GameMode;
import org.happysanta.gdtralive.game.storage.GDFile;
import org.happysanta.gdtralive.game.visual.Fmt;
import org.happysanta.gdtralive.game.visual.GdView;

import java.util.Arrays;
import java.util.UUID;

//todo separate view and controller
public class TrackEditorView {
    private static final int BUTTON_HEIGHT = 50;
    private static final int DEFAULT_OFFSET = 10;
    private static final EditorMode[] POINT_EDIT_MODES = new EditorMode[]{
            EditorMode.POINT_SELECTION,
            EditorMode.POINT_MOVE,
    };
    private static final EditorMode[] OBJECT_EDIT_MODES = new EditorMode[]{
            EditorMode.START_POINT_MOVE,
            EditorMode.START_FLAG_MOVE,
            EditorMode.FINISH_FLAG_MOVE
    };

    private Game game;
    private GdMenu menu;
    private GdView view;
    private Engine engine;
    private GdSettings settings;
    private ModManager modManager;
    private final GdApplication application;

    private int offset = GdUtils.unpackInt(DEFAULT_OFFSET);
    private int currentEditMode = 0;
    public int selectedPointIndex = 0;
    private TrackReference currentTrack;

    private final MenuLinearLayout modeLayout;
    private final MenuLinearLayout inputLayout;
    private final MenuLinearLayout moveLayout;
    private final MenuLinearLayout actionLayout;
    private final MenuImageView left;
    private final MenuImageView right;
    private final MenuImageView up;
    private final MenuImageView down;
    private final MenuImageView add;
    private final MenuImageView remove;
    private final MenuImageView invisible;
    private final MenuImageView cameraMoveMode;
    private final MenuImageView pointModeSelection;
    private final MenuImageView objectEditModeSelection;
    private final InputTextElement offsetInput;

    public TrackEditorView(GDActivity gd) {
        this.application = gd;
        this.modManager = gd.getModManager();
        //https://www.flaticon.com/packs/bigmug-line
        left = button(R.drawable.c_arrow_left, null);
        right = button(R.drawable.c_arrow_right, null);
        up = button(R.drawable.c_arrow_up, null);
        down = button(R.drawable.c_arrow_down, null);
        add = button(R.drawable.c_add, v -> handleAddButton());
        remove = button(R.drawable.c_delete, v -> handleRemoveButton());
        invisible = button(R.drawable.c_invisible, v -> handleInvisibleButton());
        cameraMoveMode = button(R.drawable.c_camera, v -> handleCameraModeButton());
        pointModeSelection = button(R.drawable.c_points, v -> handleTrackEditModeButton());
        objectEditModeSelection = button(R.drawable.c_objects, v -> handleObjectEditMode());
        offsetInput = new InputTextElement(s(R.string.offset) + ":", "" + DEFAULT_OFFSET, this::saveOffset);

        modeLayout = new MenuLinearLayout(gd, false);
        inputLayout = new MenuLinearLayout(gd, false);
        actionLayout = new MenuLinearLayout(gd, false);
        moveLayout = new MenuLinearLayout(gd, false);
        initModeSelection(gd);
        initOffsetInput(gd);
        initTrackEditButtons(gd);
        initArrows(gd);
        hideLayout();
    }

    public void init(Game game, Engine engine, GdMenu menu, GdView view, GdSettings settings) {
        this.game = game;
        this.engine = engine;
        this.menu = menu;
        this.view = view;
        this.settings = settings;
    }

    private void initModeSelection(GDActivity gd) {
        LinearLayout modeRow = new LinearLayout(gd);
        modeRow.setPadding(Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), 0);
        modeRow.setOrientation(LinearLayout.VERTICAL);
        modeRow.addView(cameraMoveMode, getLayoutParams());
        modeRow.addView(objectEditModeSelection, getLayoutParams());
        modeLayout.setOrientation(LinearLayout.VERTICAL);
        modeLayout.addView(modeRow);
        modeLayout.setGravity(Gravity.TOP);
        modeLayout.setPadding(0, 0, 0, Helpers.getDp(KeyboardController.PADDING));
        modeLayout.setLayoutParams(getFrameParams(Gravity.TOP));
    }

    private void initOffsetInput(GDActivity gd) {
        LinearLayout inputRow = new LinearLayout(gd);
        inputRow.setPadding(Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), 0);
        inputRow.setOrientation(LinearLayout.VERTICAL);
        inputRow.addView(offsetInput.getView());
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.addView(inputRow);
        inputLayout.setGravity(Gravity.TOP | Gravity.CENTER);
        inputLayout.setPadding(0, 0, 0, Helpers.getDp(KeyboardController.PADDING));
        inputLayout.setLayoutParams(getFrameParams(Gravity.TOP | Gravity.CENTER));
    }

    private void initTrackEditButtons(GDActivity gd) {
        LinearLayout actionRow = new LinearLayout(gd);
        actionRow.setPadding(Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), 0);
        actionRow.setOrientation(LinearLayout.VERTICAL);
        actionRow.addView(pointModeSelection, getLayoutParams());
        actionRow.addView(add, getLayoutParams());
        actionRow.addView(remove, getLayoutParams());
        actionRow.addView(invisible, getLayoutParams());
        actionLayout.setOrientation(LinearLayout.VERTICAL);
        actionLayout.addView(actionRow);
        actionLayout.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        actionLayout.setPadding(0, 0, 0, Helpers.getDp(KeyboardController.PADDING));
        actionLayout.setLayoutParams(getFrameParams(Gravity.BOTTOM | Gravity.LEFT));
    }

    private void initArrows(GDActivity gd) {
        LinearLayout moveRow1 = new LinearLayout(gd);
        moveRow1.setPadding(Helpers.getDp(KeyboardController.PADDING), 0, Helpers.getDp(KeyboardController.PADDING), 0);
        moveRow1.setOrientation(LinearLayout.HORIZONTAL);
        moveRow1.addView(up, getLayoutParams());
        LinearLayout moveRow2 = new LinearLayout(gd);
        moveRow2.setPadding(Helpers.getDp(KeyboardController.PADDING), 0, Helpers.getDp(KeyboardController.PADDING), 0);
        moveRow2.setOrientation(LinearLayout.HORIZONTAL);
        moveRow2.addView(left, getLayoutParams());
        moveRow2.addView(down, getLayoutParams());
        moveRow2.addView(right, getLayoutParams());
        moveLayout.setOrientation(LinearLayout.VERTICAL);
        moveLayout.addView(moveRow1);
        moveLayout.addView(moveRow2);
        moveLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        moveLayout.setPadding(0, 0, 0, Helpers.getDp(KeyboardController.PADDING));
        moveLayout.setLayoutParams(getFrameParams(Gravity.RIGHT | Gravity.BOTTOM));
    }

    private static FrameLayout.LayoutParams getFrameParams(int gravity) {
        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, gravity);
    }

    private static LinearLayout.LayoutParams getLayoutParams() {
        return new LinearLayout.LayoutParams(Helpers.getDp(BUTTON_HEIGHT), Helpers.getDp(BUTTON_HEIGHT), 1);
    }

    private MenuImageView button(int iconResId, View.OnClickListener listener) {
        MenuImageView button = new MenuImageView(Helpers.getGDActivity());
        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        button.setImageResource(iconResId);
        if (listener != null) {
            button.setOnClickListener(listener);
        }
        return button;
    }

    private void handleObjectEditMode() {
        //todo add deadline edit
        if (currentEditMode + 1 >= OBJECT_EDIT_MODES.length) {
            currentEditMode = 0;
        } else {
            currentEditMode += 1;
        }
        if (OBJECT_EDIT_MODES[currentEditMode] == EditorMode.START_POINT_MOVE) {
            showMode(R.string.mode_start_point_move);
            left.setOnClickListener(v -> engine.getTrackPhysic().track.startX -= offset);
            right.setOnClickListener(v -> engine.getTrackPhysic().track.startX += offset);
            up.setOnClickListener(v -> engine.getTrackPhysic().getTrack().startY += offset);
            down.setOnClickListener(v -> engine.getTrackPhysic().getTrack().startY -= offset);

            up.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
        }
        if (OBJECT_EDIT_MODES[currentEditMode] == EditorMode.START_FLAG_MOVE) {
            showMode(R.string.mode_start_flag_move);
            left.setOnClickListener(v -> {
                if (engine.getTrackPhysic().getTrack().startPointIndex > 0) {
                    engine.getTrackPhysic().getTrack().startPointIndex--;
                }
            });
            right.setOnClickListener(v -> {
                if (engine.getTrackPhysic().getTrack().startPointIndex < engine.getTrackPhysic().getTrack().pointsCount - 1) {
                    engine.getTrackPhysic().getTrack().startPointIndex++;
                }
            });
            up.setOnClickListener(v -> {
            });
            down.setOnClickListener(v -> {
            });

            up.setVisibility(View.GONE);
            down.setVisibility(View.GONE);
        }
        if (OBJECT_EDIT_MODES[currentEditMode] == EditorMode.FINISH_FLAG_MOVE) {
            showMode(R.string.mode_finish_flag_move);
            left.setOnClickListener(v -> {
                if (engine.getTrackPhysic().getTrack().finishPointIndex > 0) {
                    engine.getTrackPhysic().getTrack().finishPointIndex--;
                }
            });
            right.setOnClickListener(v -> {
                if (engine.getTrackPhysic().getTrack().finishPointIndex < engine.getTrackPhysic().getTrack().pointsCount - 1) {
                    engine.getTrackPhysic().getTrack().finishPointIndex++;
                }
            });
            up.setOnClickListener(v -> {
            });
            down.setOnClickListener(v -> {
            });
            up.setVisibility(View.GONE);
            down.setVisibility(View.GONE);
        }
    }

    private void handleTrackEditModeButton() {
        if (currentEditMode + 1 >= POINT_EDIT_MODES.length) {
            currentEditMode = 0;
        } else {
            currentEditMode += 1;
        }
        if (POINT_EDIT_MODES[currentEditMode] == EditorMode.POINT_SELECTION) {
            showMode(R.string.mode_point_selection);
            left.setOnClickListener(v -> {
                if (selectedPointIndex > 0) {
                    selectedPointIndex--;
                    engine.selectedPointIndex = selectedPointIndex;
                }
            });
            right.setOnClickListener(v -> {
                if (selectedPointIndex < engine.getTrackPhysic().getTrack().pointsCount - 1) {
                    selectedPointIndex++;
                    engine.selectedPointIndex = selectedPointIndex;
                }
            });
            up.setOnClickListener(v -> {
            });
            down.setOnClickListener(v -> {
            });
            up.setVisibility(View.GONE);
            down.setVisibility(View.GONE);
        }
        if (POINT_EDIT_MODES[currentEditMode] == EditorMode.POINT_MOVE) {
            showMode(R.string.mode_point_move);
            left.setOnClickListener(v -> engine.getTrackPhysic().getTrack().points[selectedPointIndex][0] -= offset);
            right.setOnClickListener(v -> engine.getTrackPhysic().getTrack().points[selectedPointIndex][0] += offset);
            up.setOnClickListener(v -> engine.getTrackPhysic().getTrack().points[selectedPointIndex][1] += offset);
            down.setOnClickListener(v -> engine.getTrackPhysic().getTrack().points[selectedPointIndex][1] -= offset);
            up.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
        }
    }

    private void handleCameraModeButton() {
        showMode(R.string.mode_camera_move);
        left.setOnClickListener(v -> engine.deltaX -= offset * 10);
        right.setOnClickListener(v -> engine.deltaX += offset * 10);
        up.setOnClickListener(v -> engine.deltaY += offset * 10);
        down.setOnClickListener(v -> engine.deltaY -= offset * 10);
        up.setVisibility(View.VISIBLE);
        down.setVisibility(View.VISIBLE);
    }

    private void handleInvisibleButton() {
        if (engine.getTrackPhysic().getTrack().invisible.contains(selectedPointIndex)) {
            engine.getTrackPhysic().getTrack().invisible.remove(selectedPointIndex);
        } else {
            engine.getTrackPhysic().getTrack().invisible.add(selectedPointIndex);
        }
    }

    private void saveOffset(EditText et) {
        try {
            String value = et.getText().toString();
            int i = Integer.parseInt(value);
            offset = GdUtils.unpackInt(i);
        } catch (Exception e) {
            Helpers.showToast(s(R.string.invalid_value));
        }
    }

    private void handleRemoveButton() {
        engine.getTrackPhysic().getTrack().points = removeElement(engine.getTrackPhysic().getTrack().points, selectedPointIndex);
        engine.getTrackPhysic().getTrack().pointsCount--;
    }

    private void handleAddButton() {
        try {
            int[] point = new int[]{
                    engine.getTrackPhysic().getTrack().points[selectedPointIndex][0] + offset,
                    engine.getTrackPhysic().getTrack().points[selectedPointIndex][1] + offset
            };
            engine.getTrackPhysic().getTrack().points = addPos(engine.getTrackPhysic().getTrack().points, selectedPointIndex, point);
            engine.getTrackPhysic().getTrack().pointsCount++;
            //todo add to the end
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[][] addPos(int[][] a, int pos, int[] point) {
        int[][] result = new int[a.length + 1][2];
        for (int i = 0; i < pos; i++)
            result[i] = a[i];
        result[pos] = point;
        for (int i = pos + 1; i < a.length + 1; i++)
            result[i] = a[i - 1];
        return result;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static int[][] removeElement(int[][] arr, int removedIdx) {
        System.arraycopy(arr, removedIdx + 1, arr, removedIdx, arr.length - 1 - removedIdx);
        return Arrays.copyOf(arr, arr.length - 1);
    }

    private void showMode(int modeId) {
        view.showInfoMessage(Fmt.colon(s(R.string.mode), s(modeId)), 5000000);
    }

    public void createNew() {
        try {
            this.currentTrack = initTrackTemplate();
            engine.loadTrack(currentTrack.getData());
            modManager.setTrackProperties(currentTrack);
        } catch (Exception e) {
            e.printStackTrace();//todo
        }
        startEditing();
    }

    public void startEditing() {
        game.restart(false);
        game.setMode(GameMode.TRACK_EDIT);
        game.getRecorder().setCapturingMode(false);
        game.getRecorder().reset();
        engine.setEditMode(true);
        Helpers.getGDActivity().editMode();
        showLayout();
        game.setShowTimer(false); //todo fix
    }

    public void exitEditor() {
        game.resetState();
        engine.setEditMode(false);
        currentTrack = null;
        modManager.setTrackProperties(null);
        view.showInfoMessage("", 10);
    }

    public void playTrack() {
        engine.setEditMode(false);
        modManager.setTrackProperties(currentTrack);
        engine.loadTrack(currentTrack.getData());
        Helpers.getGDActivity().exitEditMode();
        game.getRecorder().setCapturingMode(true);
        game.setShowTimer(true);
    }

    public void saveLeagueInput(int league) {
        try {
            currentTrack.getData().league = league;
            engine.getTrackPhysic().getTrack().league = league;
            engine.setLeague(league);
            currentTrack.setLeagueProperties(Theme.defaultTheme().getLeagueThemes().get(league).getProps());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTrack() {
        //todo pack level props and unpack on loading
        //copy level object
        application.getFileStorage().writeToFile(currentTrack, GDFile.TRACK, Fmt.us(currentTrack.getGuid(), currentTrack.getName()));
    }

    public TrackReference getCurrentTrack() {
        return currentTrack;
    }

    public MenuLinearLayout[] getViews() {
        return new MenuLinearLayout[]{
                modeLayout,
                moveLayout,
                actionLayout,
                inputLayout,
        };
    }

    public void showLayout() {
        for (MenuLinearLayout view : getViews()) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void hideLayout() {
        for (MenuLinearLayout view : getViews()) {
            view.setVisibility(View.GONE);
        }
    }

    private TrackReference initTrackTemplate() {
        TrackParams track = getTrackDataTemplate();
        TrackReference trackReference = new TrackReference();
        trackReference.setData(track);
        trackReference.setGuid(track.getGuid());
        trackReference.setName(track.getName());

        Theme theme = Theme.defaultTheme();
        trackReference.setGameProperties(theme.getGameTheme().getProps());
        trackReference.setLeagueProperties(theme.getLeagueThemes().get(track.getLeague()).getProps());
        return trackReference;
    }


    private TrackParams getTrackDataTemplate() {
        TrackParams track = new Gson().fromJson("{\"author\":\"unnamed\",\"checkBackwardCollision\":true,\"checkFinishCoordinates\":true,\"finishPointIndex\":2,\"finishX\":7323648,\"finishY\":0,\"guid\":\"123\",\"invisible\":[],\"league\":0,\"name\":\"Unnamed\",\"points\":[[-4710400,-286720],[-4087808,-278528],[-3645440,-278528],[-3104768,-270336]],\"pointsCount\":4,\"startPointIndex\":1,\"startX\":-4374528,\"startY\":81920}", TrackParams.class);
        track.setAuthor(settings.getPlayerName());
        track.setGuid(UUID.randomUUID().toString());
        track.setName(track.getName());
        return track;
    }
}
