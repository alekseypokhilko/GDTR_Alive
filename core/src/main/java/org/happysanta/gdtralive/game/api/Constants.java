package org.happysanta.gdtralive.game.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Constants {
    public static final String DEFAULT_MOD = "GDTR Alive";
    public static final String ORIGINAL_MOD = "GDTR Original";
    public static final String ORIGINAL_THEME = "GDTR Original";
    public static final String BLACK_THEME = "GDTR Black";
    public static final String APP_DIRECTORY = "GDTR_Alive";
    public static final int PICKFILE_MRG_RESULT_CODE = 234567;
    public static final int PICKFILE_MOD_RESULT_CODE = 734567;
    public static final int PICKFILE_THEME_RESULT_CODE = 345672;
    public static final int PICKFILE_RECORD_RESULT_CODE = 824256;
    public static final int PICKFILE_TRACK_RESULT_CODE = 465134;
    public static int RECORD_COUNT = 5;
    public static final long WAIT_TIME = 30L;
    public static final long IMAGES_DELAY = 500L; //todo 1000L;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss", Locale.US);

    public static final String GITHUB_BASE_URL = "https://raw.githubusercontent.com";
    public static final String SERVER_CONFIG_URL = "/alekseypokhilko/GDTR_Alive/refs/heads/main/server/src/main/resources/servers.json";

    // Keyboard
    public static final String[] BUTTON_RESOURCES = {
            "btn_br", "btn_br", "btn_b",
            "btn_br", "btn_br", "btn_b",
            "btn_r", "btn_r", "btn_n"
    };

    public static int[] m_foraI = {0x1c000, 0x10000, 32768};

    public static Set<String> IGNORE_SAVE = new HashSet<>();
    public static Set<String> DEFAULT_THEMES = new HashSet<>();
    public static List<String> LOCALES = new ArrayList<>();

    static {
        IGNORE_SAVE.add(ORIGINAL_MOD);
        IGNORE_SAVE.add(DEFAULT_MOD);
        IGNORE_SAVE.add(BLACK_THEME);
        IGNORE_SAVE.add(ORIGINAL_THEME);
        IGNORE_SAVE.add("GDTR ULTIMATE Δ");
        IGNORE_SAVE.add("GDTR ULTIMATE β");
        IGNORE_SAVE.add("GDTR ULTIMATE π");
        IGNORE_SAVE.add("GDTR ULTIMATE Ω");
        IGNORE_SAVE.add("GDTR ULTIMATE μ");
        IGNORE_SAVE.add("GDTR ULTIMATE α");
        IGNORE_SAVE.add("GDTR ULTIMATE λ");
        IGNORE_SAVE.add("GDTR ULTIMATE Ξ");
        IGNORE_SAVE.add("GDTR ULTIMATE γ");
        DEFAULT_THEMES.add(ORIGINAL_THEME);
        DEFAULT_THEMES.add(BLACK_THEME);

        LOCALES.add("en");
        LOCALES.add("ru");
        LOCALES.add("uk");
    }
}
