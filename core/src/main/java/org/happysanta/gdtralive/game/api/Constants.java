package org.happysanta.gdtralive.game.api;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public static final String DEFAULT_MOD = "GDTR Alive";
    public static final String ORIGINAL_MOD = "GDTR Original";
    public static final String APP_DIRECTORY = "GDTR_Alive";
    public static int RECORD_COUNT = 5;
    public static final long WAIT_TIME = 30L;
    public static final long IMAGES_DELAY = 10L; //todo 1000L;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss", Locale.US);

    // Keyboard
    public static final String[] BUTTON_RESOURCES = {
            "btn_br", "btn_br", "btn_b",
            "btn_br", "btn_br", "btn_b",
            "btn_r", "btn_r", "btn_n"
    };

    public static int[] m_foraI = {0x1c000, 0x10000, 32768};
}
