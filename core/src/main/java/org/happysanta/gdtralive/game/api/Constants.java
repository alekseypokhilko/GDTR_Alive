package org.happysanta.gdtralive.game.api;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
//    public static final String PACK_NAME = "be632fab-ea67-4189-8116-8f53ab1f60c9_Gravity Defied Classic [10.10.10]";
    public static final String PACK_NAME = "be732fab-ea67-4189-8116-8f53ab1f60c9_Gravity Defied Old Tracks[10.10.10]";
//    public static final String PACK_NAME = "aa732fab-ea67-4189-8116-8f53ab1f60c9_Gravity Defied Old Tracks[10.10.10]";
//    public static final String PACK_NAME = "69fc9be2-de23-40b7-b90a-13efe80e5812_Gravity Defied MultiGOD [323.263.452]";

    public static final String APP_DIRECTORY = "GDTR_Alive";
    public static final String JSON = ".json";
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
