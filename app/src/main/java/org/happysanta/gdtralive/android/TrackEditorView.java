package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.s;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.KeyboardController;
import org.happysanta.gdtralive.android.menu.views.MenuImageView;
import org.happysanta.gdtralive.android.menu.views.MenuLinearLayout;
import org.happysanta.gdtralive.game.Application;
import org.happysanta.gdtralive.game.Game;
import org.happysanta.gdtralive.game.ModManager;
import org.happysanta.gdtralive.game.api.EditorMode;
import org.happysanta.gdtralive.game.api.GDFile;
import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.external.GdTrackEditor;
import org.happysanta.gdtralive.game.api.menu.element.IInputTextElement;
import org.happysanta.gdtralive.game.api.menu.element.TextMenuElement;
import org.happysanta.gdtralive.game.api.model.GameParams;
import org.happysanta.gdtralive.game.api.model.TrackData;
import org.happysanta.gdtralive.game.engine.Engine;
import org.happysanta.gdtralive.game.util.Fmt;
import org.happysanta.gdtralive.game.util.Utils;

//todo separate view and controller
public class TrackEditorView implements GdTrackEditor {
    private static final int BUTTON_HEIGHT = 50;
    private static final int DEFAULT_OFFSET = 10;
    private static final EditorMode[] MOVE_EDIT_MODES = new EditorMode[]{
            EditorMode.POINT_SELECTION,
            EditorMode.POINT_MOVE,
    };
    private static final EditorMode[] SELECTION_EDIT_MODES = new EditorMode[]{
            EditorMode.START_POINT_MOVE,
            EditorMode.START_FLAG_SELECTION,
            EditorMode.FINISH_FLAG_SELECTION
    };

    private final View.OnClickListener NO_OP = v -> {
    };

    private Game game;
    private Engine engine;
    private final ModManager modManager;
    private final Application application;

    private int offset = Utils.unpackInt(DEFAULT_OFFSET);
    public int currentEditMode = 0;
    public EditorMode editorMode = EditorMode.CAMERA_MOVE;
    public int selectedPointIndex = 0;
    private TrackParams currentTrack;

    private final MenuLinearLayout modeLayout;
    private final MenuLinearLayout inputLayout;
    private final MenuLinearLayout moveLayout;
    private final MenuLinearLayout actionLayout;
    private final MenuImageView left;
    private final MenuImageView right;
    private final MenuImageView up;
    private final MenuImageView down;
    private final TextMenuElement pointText;

    public TrackEditorView(GDActivity gd, Application application, ModManager modManager) {
        this.application = application;
        this.modManager = modManager;
        //https://www.flaticon.com/packs/bigmug-line
        left = button(R.drawable.c_arrow_left, null);
        right = button(R.drawable.c_arrow_right, null);
        up = button(R.drawable.c_arrow_up, null);
        down = button(R.drawable.c_arrow_down, null);
        MenuImageView add = button(R.drawable.c_add, v -> handleAddButton());
        MenuImageView remove = button(R.drawable.c_delete, v -> handleRemoveButton());
        MenuImageView invisible = button(R.drawable.c_invisible, v -> handleInvisibleButton());
        MenuImageView cameraMoveMode = button(R.drawable.c_camera, v -> handleCameraModeButton());
        MenuImageView pointModeSelection = button(R.drawable.c_points, v -> handleTrackMoveModeButton());
        MenuImageView objectEditModeSelection = button(R.drawable.c_objects, v -> handleObjectEditMode());
        IInputTextElement offsetInput = application.getPlatform().getPlatformMenuElementFactory().editText(Fmt.colon(s(R.string.offset)), "" + DEFAULT_OFFSET, this::saveOffset);
        pointText = (TextMenuElement) application.getPlatform().getPlatformMenuElementFactory().text("[x, y]");

        modeLayout = new MenuLinearLayout(gd, false);
        inputLayout = new MenuLinearLayout(gd, false);
        actionLayout = new MenuLinearLayout(gd, false);
        moveLayout = new MenuLinearLayout(gd, false);
        //initModeSelection
        LinearLayout modeRow = new LinearLayout(gd);
        modeRow.setPadding(Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), 0);
        modeRow.setOrientation(LinearLayout.VERTICAL);
        modeRow.addView(cameraMoveMode, getLayoutParams());
        modeRow.addView(objectEditModeSelection, getLayoutParams());
        modeRow.addView(pointModeSelection, getLayoutParams());
        modeLayout.setOrientation(LinearLayout.VERTICAL);
        modeLayout.addView(modeRow);
        modeLayout.setGravity(Gravity.TOP);
        modeLayout.setPadding(0, 0, 0, Helpers.getDp(KeyboardController.PADDING));
        modeLayout.setLayoutParams(getFrameParams(Gravity.TOP));
        //initOffsetInput
        LinearLayout inputRow = new LinearLayout(gd);
        inputRow.setPadding(Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), 0);
        inputRow.setOrientation(LinearLayout.VERTICAL);
        inputRow.addView((View) offsetInput.getView());
        inputRow.addView((View) pointText.getView());
        inputLayout.setOrientation(LinearLayout.VERTICAL);
        inputLayout.addView(inputRow);
        inputLayout.setGravity(Gravity.TOP | Gravity.CENTER);
        inputLayout.setPadding(0, 40, 0, Helpers.getDp(KeyboardController.PADDING));
        inputLayout.setLayoutParams(getFrameParams(Gravity.TOP | Gravity.CENTER));
        //initTrackEditButtons
        LinearLayout actionRow = new LinearLayout(gd);
        actionRow.setPadding(Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), Helpers.getDp(KeyboardController.PADDING), 0);
        actionRow.setOrientation(LinearLayout.VERTICAL);
//        actionRow.addView(pointModeSelection, getLayoutParams());
        actionRow.addView(add, getLayoutParams());
        actionRow.addView(remove, getLayoutParams());
        actionRow.addView(invisible, getLayoutParams());
        actionLayout.setOrientation(LinearLayout.VERTICAL);
        actionLayout.addView(actionRow);
        actionLayout.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        actionLayout.setPadding(0, 0, 0, Helpers.getDp(KeyboardController.PADDING));
        actionLayout.setLayoutParams(getFrameParams(Gravity.BOTTOM | Gravity.LEFT));
        //initArrows
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

        hideLayout();
    }

    public void init(Game game) {
        this.game = game;
        this.engine = game.getEngine();
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
        if (currentEditMode + 1 >= SELECTION_EDIT_MODES.length) {
            currentEditMode = 0;
        } else {
            currentEditMode += 1;
        }
        if (SELECTION_EDIT_MODES[currentEditMode] == EditorMode.START_FLAG_SELECTION) {
            showMode(R.string.mode_start_flag_move);
            editorMode = EditorMode.START_FLAG_SELECTION;
            left.setOnClickListener(v -> startFlagBack());
            right.setOnClickListener(v -> startFlagForward());
            up.setOnClickListener(NO_OP);
            down.setOnClickListener(NO_OP);

            up.setVisibility(View.GONE);
            down.setVisibility(View.GONE);
        }
        if (SELECTION_EDIT_MODES[currentEditMode] == EditorMode.FINISH_FLAG_SELECTION) {
            showMode(R.string.mode_finish_flag_move);
            editorMode = EditorMode.FINISH_FLAG_SELECTION;
            left.setOnClickListener(v -> finishFlagBack());
            right.setOnClickListener(v -> finishFlagForward());
            up.setOnClickListener(NO_OP);
            down.setOnClickListener(NO_OP);

            up.setVisibility(View.GONE);
            down.setVisibility(View.GONE);
        }
        if (SELECTION_EDIT_MODES[currentEditMode] == EditorMode.START_POINT_MOVE) {
            showMode(R.string.mode_start_point_move);
            editorMode = EditorMode.START_POINT_MOVE;
            left.setOnClickListener(v -> {
                track().startX -= offset;
                updateUi();
            });
            right.setOnClickListener(v -> {
                track().startX += offset;
                updateUi();
            });
            up.setOnClickListener(v -> {
                track().startY += offset;
                updateUi();
            });
            down.setOnClickListener(v -> {
                track().startY -= offset;
                updateUi();
            });

            up.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
        }
    }

    private void handleCameraModeButton() {
        showMode(R.string.mode_camera_move);
        editorMode = EditorMode.CAMERA_MOVE;
        left.setOnClickListener(v -> {
            engine.deltaX -= offset * 10;
            updateUi();
        });
        right.setOnClickListener(v -> {
            engine.deltaX += offset * 10;
            updateUi();
        });
        up.setOnClickListener(v -> {
            engine.deltaY += offset * 10;
            updateUi();
        });
        down.setOnClickListener(v -> {
            engine.deltaY -= offset * 10;
            updateUi();
        });

        up.setVisibility(View.VISIBLE);
        down.setVisibility(View.VISIBLE);
    }

    private void finishFlagForward() {
        if (track().finishPointIndex < track().pointsCount - 1) {
            track().finishPointIndex++;
        }
        updateUi();
    }

    private TrackData track() {
        return engine.getTrackPhysic().getTrack();
    }

    private void finishFlagBack() {
        if (track().finishPointIndex > 0) {
            track().finishPointIndex--;
        }
        updateUi();
    }

    private void startFlagForward() {
        if (track().startPointIndex >= 0) {
            track().startPointIndex++;
        }
        updateUi();
    }

    private void startFlagBack() {
        if (track().startPointIndex < track().pointsCount - 1) {
            if (track().startPointIndex > 0) {
                track().startPointIndex--;
            }
        }
        updateUi();
    }

    private void handleTrackMoveModeButton() {
        if (currentEditMode + 1 >= MOVE_EDIT_MODES.length) {
            currentEditMode = 0;
        } else {
            currentEditMode += 1;
        }
        if (MOVE_EDIT_MODES[currentEditMode] == EditorMode.POINT_SELECTION) {
            showMode(R.string.mode_point_selection);
            editorMode = EditorMode.POINT_SELECTION;
            left.setOnClickListener(v -> selectPreviousPoint());
            right.setOnClickListener(v -> selectNextPoint());
            up.setOnClickListener(NO_OP);
            down.setOnClickListener(NO_OP);
            up.setVisibility(View.GONE);
            down.setVisibility(View.GONE);
        }
        if (MOVE_EDIT_MODES[currentEditMode] == EditorMode.POINT_MOVE) {
            showMode(R.string.mode_point_move);
            editorMode = EditorMode.POINT_MOVE;
            left.setOnClickListener(v -> {
                track().points[selectedPointIndex][0] -= offset;
                updateUi();
            });
            right.setOnClickListener(v -> {
                track().points[selectedPointIndex][0] += offset;
                updateUi();
            });
            up.setOnClickListener(v -> {
                track().points[selectedPointIndex][1] += offset;
                updateUi();
            });
            down.setOnClickListener(v -> {
                track().points[selectedPointIndex][1] -= offset;
                updateUi();
            });
            up.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
        }
    }

    public void shiftTrackPoint(int shiftX, int shiftY) {
        track().points[selectedPointIndex][0] += shiftX;
        track().points[selectedPointIndex][1] += shiftY;
        updateUi();
    }

    public void shiftStartPoint(int shiftX, int shiftY) {
        track().startX += shiftX;
        track().startY += shiftY;
        updateUi();
    }

    private void selectPreviousPoint() {
        if (selectedPointIndex > 0) {
            selectedPointIndex--;
            engine.selectedPointIndex = selectedPointIndex;
        }
        updateUi();
    }

    private void handleInvisibleButton() {
        if (track().invisible.contains(selectedPointIndex)) {
            track().invisible.remove(selectedPointIndex);
        } else {
            track().invisible.add(selectedPointIndex);
        }
        updateUi();
    }

    private void saveOffset(Object et) {
        try {
            String value = ((IInputTextElement) et).getText();
            int i = Integer.parseInt(value);
            offset = Utils.unpackInt(i);
        } catch (Exception e) {
            application.notify(s(R.string.invalid_value));
        }
    }

    private void handleRemoveButton() {
        try {
            track().points = Utils.removeElement(track().points, selectedPointIndex);
            track().pointsCount--;
            updateUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAddButton() {
        try {
            int[] point = new int[]{
                    track().points[selectedPointIndex][0] + offset,
                    track().points[selectedPointIndex][1]
            };
            track().points = Utils.addPos(track().points, selectedPointIndex + 1, point);
            track().pointsCount++;
            selectNextPoint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectNextPoint() {
        if (selectedPointIndex < track().pointsCount - 1) {
            selectedPointIndex++;
            engine.selectedPointIndex = selectedPointIndex;
        }
        updateUi();
    }

    private void updateUi() {
        try {
            pointText.setText(String.format(
                    "[%s, %s]",
                    Utils.packInt(track().points[selectedPointIndex][0]),
                    Utils.packInt(track().points[selectedPointIndex][1]))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMode(int modeId) {
        game.showInfoMessage(Fmt.colon(s(R.string.mode), s(modeId)), 10000);
    }

    public void createNew(String playerName) {
        try {
            this.currentTrack = Utils.initTrackTemplate(playerName);
        } catch (Exception e) {
            e.printStackTrace();//todo
        }
        startEditing();
    }

    public void startEditing() {
        if (currentTrack == null) {
            currentTrack = game.getParams().getTrackParams();
        }
        game.startTrack(GameParams.of(GameMode.TRACK_EDITOR, currentTrack.getData()));
        application.editMode();
        showLayout();
        updateUi();
    }

    public void exitEditor() {
        game.resetState();
        engine.setEditMode(false);
        currentTrack = null;
        selectedPointIndex = 0;
        editorMode = null;
        game.getView().resetShift();
        modManager.setTrackTheme(null);
        game.showInfoMessage("", 10);
        application.getMenu().back();
    }

    public void playTrack() {
        if (currentTrack == null) {
            currentTrack = game.getParams().getTrackParams();
        }
        game.getView().resetShift();
        modManager.setTrackTheme(currentTrack);
        game.startTrack(GameParams.of(GameMode.TRACK_EDITOR_PLAY, currentTrack.getData()));
        application.getPlatform().exitEditMode();
    }

    public void saveLeagueInput(int league) {
        try {
            currentTrack.getData().league = league;
            track().league = league;
            engine.setLeague(league);
            //currentTrack.setLeagueTheme(Theme.defaultTheme().getLeagueThemes().get(league));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTrack() {
        //todo pack level props and unpack on loading
        String trackName = Fmt.trackName(currentTrack.getData());
        TrackParams track = new TrackParams(Utils.packTrack(currentTrack.getData()));
        application.getFileStorage().save(track, GDFile.TRACK, trackName);
    }

    public TrackParams getCurrentTrack() {
        return currentTrack;
    }

    @Override
    public void setCurrentTrack(TrackParams currentTrack) {
        this.currentTrack = currentTrack;
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

}
