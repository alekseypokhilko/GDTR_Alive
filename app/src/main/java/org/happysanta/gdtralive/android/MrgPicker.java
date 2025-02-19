package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;
import static org.happysanta.gdtralive.android.Helpers.s;
import static org.happysanta.gdtralive.android.Helpers.showAlert;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Environment;
import android.text.InputType;
import android.widget.EditText;

import org.happysanta.gdtralive.R;
import org.happysanta.gdtralive.android.menu.FileDialog;
import org.happysanta.gdtralive.game.levels.mrg.MrgUtils;
import org.happysanta.gdtralive.game.mod.Mod;
import org.happysanta.gdtralive.game.modes.MenuData;
import org.happysanta.gdtralive.game.modes.MenuType;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class MrgPicker {
    public static void convertMrgFromFileBrowse() {
        if (!isExternalStorageReadable()) {
            showAlert(s(R.string.error), s(R.string.e_external_storage_is_not_readable), null);
            return;
        }

        final GDActivity gd = getGDActivity();
        FileDialog fileDialog = new FileDialog(gd, Environment.getExternalStorageDirectory(), ".mrg");
        fileDialog.addFileListener(file -> {
            final EditText input = new EditText(gd);
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            AlertDialog.Builder alert = new AlertDialog.Builder(gd)
                    .setTitle(s(R.string.enter_levels_name_title))
                    .setMessage(s(R.string.enter_levels_name))
                    .setView(input)
                    .setPositiveButton(s(R.string.ok), (dialog, whichButton) -> {
                        try {
                            String name = input.getText().toString();
                            final File file1 = new File(Objects.requireNonNull(Uri.fromFile(file).getPath()));
                            try (InputStream in = Helpers.getGDActivity().getContentResolver().openInputStream(Uri.fromFile(file))) {
                                Mod mod = MrgUtils.convertMrg(name, file1.getName(), new AndroidFileStorage().readAllBytes(in));
                                gd.menu.setCurrentMenu(gd.getMenuFactory().get(MenuType.MOD_OPTIONS).build(new MenuData())); //todo
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert(s(R.string.error), e.getMessage(), null);
                        }
                    })
                    .setNegativeButton(s(R.string.cancel), null);
            alert.show();
        });
        fileDialog.showDialog();
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
