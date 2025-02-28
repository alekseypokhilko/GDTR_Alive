package org.happysanta.gdtralive.game.util;

public class Sql {
    public static final String DATABASE_NAME = "levels.db";
    public static final String TABLE_LEVELS = "levels";
    public static final String TABLE_SCORES = "scores";
    public static final String LEVELS_COLUMN_ID = "_id";
    public static final String LEVELS_COLUMN_GUID = "guid";
    public static final String LEVELS_COLUMN_NAME = "name";
    public static final String LEVELS_COLUMN_AUTHOR = "author";
    public static final String LEVELS_COLUMN_TRACKS_COUNT = "count"; //"[30,30,30,100,1000]"
    public static final String LEVELS_COLUMN_TRACKS_UNLOCKED = "unlocked"; //"[30,30,30,100,1000]"
    public static final String LEVELS_COLUMN_ADDED = "added_ts";
    public static final String LEVELS_COLUMN_INSTALLED = "installed_ts";
    public static final String LEVELS_COLUMN_IS_DEFAULT = "is_default";
    public static final String LEVELS_COLUMN_API_ID = "api_id";
    public static final String LEVELS_COLUMN_SELECTED_LEVEL = "selected_level";
    public static final String LEVELS_COLUMN_SELECTED_TRACK = "selected_track";
    public static final String LEVELS_COLUMN_SELECTED_LEAGUE = "selected_league";
    public static final String LEVELS_COLUMN_UNLOCKED_LEVELS = "unlocked_levels";
    public static final String LEVELS_COLUMN_UNLOCKED_LEAGUES = "unlocked_leagues";

    public static final String SCORES_COLUMN_LEAGUE = "league";
    public static final String SCORES_COLUMN_LEVEL_GUID = "level_guid";
    public static final String SCORES_COLUMN_NAME = "name";
    public static final String SCORES_COLUMN_TIME = "time";
    public static final String SCORES_COLUMN_DATE = "date";

    public static final String TABLE_LEVELS_CREATE = "CREATE TABLE "
            + TABLE_LEVELS + "("
            + LEVELS_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + LEVELS_COLUMN_NAME + " TEXT NOT NULL, "
            + LEVELS_COLUMN_GUID + " TEXT NOT NULL, "
            + LEVELS_COLUMN_AUTHOR + " TEXT NOT NULL, "
            + LEVELS_COLUMN_TRACKS_COUNT + " TEXT NOT NULL, "
            + LEVELS_COLUMN_TRACKS_UNLOCKED + " TEXT NOT NULL, "
            + LEVELS_COLUMN_ADDED + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_INSTALLED + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_IS_DEFAULT + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_API_ID + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_SELECTED_LEVEL + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_SELECTED_TRACK + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_SELECTED_LEAGUE + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_UNLOCKED_LEVELS + " INTEGER NOT NULL, "
            + LEVELS_COLUMN_UNLOCKED_LEAGUES + " INTEGER NOT NULL"
            + ");";

    public static final String TABLE_SCORES_CREATE = " CREATE TABLE "
            + TABLE_SCORES + "("
            + SCORES_COLUMN_LEAGUE + " INTEGER NOT NULL, "
            + SCORES_COLUMN_LEVEL_GUID + " TEXT, "
            + SCORES_COLUMN_TIME + " INTEGER NOT NULL, "
            + SCORES_COLUMN_NAME + " TEXT, "
            + SCORES_COLUMN_DATE + " TEXT"
            + ");";

    public static final String SCORES_LEVEL_GUID_INDEX = String.format("CREATE INDEX scores_level_guid_index ON %s(%s)", TABLE_SCORES, SCORES_COLUMN_LEVEL_GUID);
    public static final String MODS_GUID_INDEX = String.format("CREATE INDEX mods_level_guid_index ON %s(%s)", TABLE_LEVELS, LEVELS_COLUMN_GUID);
    public static final String MODS_COLUMN_API_ID_INDEX = String.format("CREATE INDEX %s_index ON %s(%s)", Sql.LEVELS_COLUMN_API_ID, Sql.TABLE_LEVELS, Sql.LEVELS_COLUMN_API_ID);
    public static final String MODS_COLUMN_IS_DEFAULT_INDEX = String.format("CREATE INDEX %s_index ON %s(%s)", Sql.LEVELS_COLUMN_IS_DEFAULT, Sql.TABLE_LEVELS, Sql.LEVELS_COLUMN_IS_DEFAULT);

    public static String selectHighScore(String levelGuid, int league) {
        return String.format("%s = '%s' AND %s = %s", SCORES_COLUMN_LEVEL_GUID, levelGuid, SCORES_COLUMN_LEAGUE, league);
    }
}
