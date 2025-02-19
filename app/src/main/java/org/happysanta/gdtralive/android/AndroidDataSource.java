package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.logDebug;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import org.happysanta.gdtralive.game.external.GdDataSource;
import org.happysanta.gdtralive.game.storage.HighScores;
import org.happysanta.gdtralive.game.storage.ModEntity;
import org.happysanta.gdtralive.game.storage.Score;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AndroidDataSource implements GdDataSource {

    private SQLiteDatabase db;
    private LevelsSQLiteOpenHelper dbHelper;

    public AndroidDataSource(Context context) {
        dbHelper = new LevelsSQLiteOpenHelper(context);
    }

    public synchronized void open() throws SQLException {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public synchronized void close() {
        dbHelper.close();
    }

    public synchronized ModEntity createLevel(String name, String author, List<Integer> tracksCount, long addedTs, long installedTs, boolean isDefault, long apiId) {
        ContentValues values = new ContentValues();
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_NAME, name);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_GUID, name);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_AUTHOR, author);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_TRACKS_COUNT, new Gson().toJson(tracksCount));
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_TRACKS_UNLOCKED, resetToZero(tracksCount));
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_ADDED, addedTs);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_INSTALLED, installedTs);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_IS_DEFAULT, isDefault ? 1 : 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_API_ID, apiId);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_TRACK, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEVEL, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEAGUE, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEVELS, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEAGUES, 0);

        long insertId = db.insert(LevelsSQLiteOpenHelper.TABLE_LEVELS, null, values);
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_LEVELS, null,
                LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID + " = " + insertId,
                null, null, null, null);

        cursor.moveToFirst();

        ModEntity level = cursorToLevel(cursor);
        cursor.close();
        return level;
    }

    public synchronized void deleteLevel(ModEntity level) {
        long id = level.getId();
        db.delete(LevelsSQLiteOpenHelper.TABLE_LEVELS, LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID + " = " + id, null);
    }

    // This will also reset auto increment counter
    public synchronized void deleteAllLevels() {
        db.delete(LevelsSQLiteOpenHelper.TABLE_LEVELS, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + LevelsSQLiteOpenHelper.TABLE_LEVELS + "'");
    }

    public synchronized void resetAllLevelsSettings() {
        ContentValues values = new ContentValues();
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_TRACKS_UNLOCKED, "[0,0,0]"); //todo
//        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_TRACKS_COUNT, "[2,2,2]"); //todo
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEAGUE, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEVEL, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_TRACK, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEAGUES, 0);
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEVELS, 0);

        int result = db.update(LevelsSQLiteOpenHelper.TABLE_LEVELS, values, null, null);
        logDebug("LevelsDataSource.resetAllLevelsSettings: result = " + result);
    }

    public synchronized void updateLevel(ModEntity level) {
        ContentValues values = new ContentValues();
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_TRACKS_UNLOCKED, level.getUnlockedTracks());
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEAGUE, level.getSelectedLeague());
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEVEL, level.getSelectedLevel());
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_TRACK, level.getSelectedTrack());
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEAGUES, level.getUnlockedLeagues());
        values.put(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEVELS, level.getUnlockedLevels());

        // logDebug("LevelsDataSource.updateLevel selectedLeague: " + level.getSelectedLeague());

        db.update(LevelsSQLiteOpenHelper.TABLE_LEVELS, values, LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID + " = " + level.getId(), null);
    }

    public synchronized HashMap<Long, Long> findInstalledLevels(ArrayList<Long> apiIds) {
        HashMap<Long, Long> installed = new HashMap<>();

        String[] apiIdsArray = new String[apiIds.size()];
        for (int i = 0; i < apiIdsArray.length; i++) {
            apiIdsArray[i] = apiIds.get(i).toString();
        }

        Cursor cursor = db.rawQuery("SELECT " + LevelsSQLiteOpenHelper.LEVELS_COLUMN_API_ID + ", " + LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID + " FROM " + LevelsSQLiteOpenHelper.TABLE_LEVELS + " WHERE " + LevelsSQLiteOpenHelper.LEVELS_COLUMN_API_ID + " IN (" + makePlaceholders(apiIdsArray.length) + ")", apiIdsArray);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            long apiId = cursor.getLong(0),
                    id = cursor.getLong(1);
            installed.put(apiId, id);
            cursor.moveToNext();
        }
        cursor.close();

        return installed;
    }

    public synchronized List<ModEntity> getAllLevels() {
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_LEVELS, null, null, null, null, null, null);

        List<ModEntity> levels = levelsFromCursor(cursor);
        cursor.close();

        return levels;
    }

    public synchronized List<ModEntity> getLevels(int offset, int count) {
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_LEVELS, null, null, null, null, LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID + " ASC", offset + ", " + count);

        List<ModEntity> levels = levelsFromCursor(cursor);
        cursor.close();

        return levels;
    }

    public synchronized ModEntity getLevel(long id) {
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_LEVELS, null, LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();

        ModEntity level = null;
        if (cursor.getCount() > 0) {
            level = cursorToLevel(cursor);
        }

        cursor.close();
        return level;
    }

    public synchronized ModEntity getLevel(String guid) {
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_LEVELS, null, LevelsSQLiteOpenHelper.LEVELS_COLUMN_GUID + " = '" + guid +"'", null, null, null, null);
        cursor.moveToFirst();

        ModEntity level = null;
        if (cursor.getCount() > 0) {
            level = cursorToLevel(cursor);
        }

        cursor.close();
        return level;
    }

    public List<ModEntity> levelsFromCursor(Cursor cursor) {
        cursor.moveToFirst();
        List<ModEntity> levels = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            ModEntity level = cursorToLevel(cursor);
            levels.add(level);
            cursor.moveToNext();
        }
        return levels;
    }

    public synchronized boolean isDefaultLevelCreated() {
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_LEVELS, new String[]{LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID}, LevelsSQLiteOpenHelper.LEVELS_COLUMN_IS_DEFAULT + " = 1", null, null, null, null);
        boolean created = cursor.getCount() > 0;
        cursor.close();
        return created;
    }

    public synchronized boolean isApiIdInstalled(long apiId) {
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_LEVELS, new String[]{LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID}, LevelsSQLiteOpenHelper.LEVELS_COLUMN_API_ID + " = " + apiId, null, null, null, null);
        boolean installed = cursor.getCount() > 0;
        cursor.close();
        return installed;
    }

    public synchronized HighScores getHighScores(String levelGuid, int league) {
        Cursor cursor = db.query(LevelsSQLiteOpenHelper.TABLE_SCORES, null,
                LevelsSQLiteOpenHelper.SCORES_COLUMN_LEVEL_GUID + " = '" + levelGuid + "' AND "
                        + LevelsSQLiteOpenHelper.SCORES_COLUMN_LEAGUE  + " = " + league,
                null, null, null, LevelsSQLiteOpenHelper.SCORES_COLUMN_TIME);
        cursor.moveToFirst();

        HighScores highScores = new HighScores(league);
        int i = 0;
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Score score = new Score();
                score.setLevelGuid(cursor.getString(cursor.getColumnIndex(LevelsSQLiteOpenHelper.SCORES_COLUMN_LEVEL_GUID)));
                score.setLeague(cursor.getInt(cursor.getColumnIndex(LevelsSQLiteOpenHelper.SCORES_COLUMN_LEAGUE)));
                score.setTime(cursor.getLong(cursor.getColumnIndex(LevelsSQLiteOpenHelper.SCORES_COLUMN_TIME)));
                score.setDate(cursor.getString(cursor.getColumnIndex(LevelsSQLiteOpenHelper.SCORES_COLUMN_DATE)));
                score.setName(cursor.getString(cursor.getColumnIndex(LevelsSQLiteOpenHelper.SCORES_COLUMN_NAME)));
                highScores.get(score.getLeague()).add(score); //todo
                cursor.moveToNext();
                i++;
                if (i > 9) {
                    break;
                }
            }
        }
        cursor.close();
        return highScores;
    }

    public synchronized void saveHighScore(Score score) {
        ContentValues values = new ContentValues();
        values.put(LevelsSQLiteOpenHelper.SCORES_COLUMN_LEVEL_GUID, score.getLevelGuid());
        values.put(LevelsSQLiteOpenHelper.SCORES_COLUMN_LEAGUE, score.getLeague());
        values.put(LevelsSQLiteOpenHelper.SCORES_COLUMN_TIME, score.getTime());
        values.put(LevelsSQLiteOpenHelper.SCORES_COLUMN_NAME, score.getName());
        values.put(LevelsSQLiteOpenHelper.SCORES_COLUMN_DATE, score.getDate());


        long result = db.insert(LevelsSQLiteOpenHelper.TABLE_SCORES, null, values);
        logDebug("LevelsDataSource.TABLE_LEVELS: result = " + result);
    }

    public synchronized void clearHighScores(String levelGuid) {
        if (levelGuid == null) {
            db.delete(LevelsSQLiteOpenHelper.TABLE_SCORES, null, null);
        } else {
            db.delete(LevelsSQLiteOpenHelper.TABLE_SCORES,
                    LevelsSQLiteOpenHelper.SCORES_COLUMN_LEVEL_GUID + " = '" + levelGuid + "'",
                    null);
        }

    }

    private ModEntity cursorToLevel(Cursor cursor) {
        ModEntity level = new ModEntity();
        level.setId(cursor.getLong(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_ID)));
        level.setName(cursor.getString(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_NAME)));
        level.setCount(cursor.getString(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_TRACKS_COUNT)));
        level.setUnlockedTracks(cursor.getString(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_TRACKS_UNLOCKED)));
        level.setSelectedLevel(cursor.getInt(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEVEL)));
        level.setSelectedTrack(cursor.getInt(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_TRACK)));
        level.setSelectedLeague(cursor.getInt(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_SELECTED_LEAGUE)));
        level.setUnlockedLevels(cursor.getInt(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEVELS)));
        level.setUnlockedLeagues(cursor.getInt(cursor.getColumnIndex(LevelsSQLiteOpenHelper.LEVELS_COLUMN_UNLOCKED_LEAGUES)));

        return level;
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public static String resetToZero(List<Integer> counts) {
        for (int i = 0; i < counts.size(); i++) {
            counts.set(i, 0);
        }
        return new Gson().toJson(counts);
    }
}
