package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.logDebug;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.external.GdDataSource;
import org.happysanta.gdtralive.game.api.model.HighScores;
import org.happysanta.gdtralive.game.api.model.ModEntity;
import org.happysanta.gdtralive.game.api.model.Score;
import org.happysanta.gdtralive.game.util.Sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADataSource implements GdDataSource {

    private SQLiteDatabase db;
    private final LevelsSQLiteOpenHelper dbHelper;

    public ADataSource(Context context) {
        dbHelper = new LevelsSQLiteOpenHelper(context);
    }

    public synchronized void open() {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public synchronized void close() {
        dbHelper.close();
    }

    @Override
    public ModEntity createMod(ModEntity mod) {
        ContentValues values = new ContentValues();
        values.put(Sql.LEVELS_COLUMN_NAME, mod.getName());
        values.put(Sql.LEVELS_COLUMN_GUID, mod.getGuid());
        values.put(Sql.LEVELS_COLUMN_AUTHOR, mod.getAuthor());
        values.put(Sql.LEVELS_COLUMN_TRACKS_COUNT, mod.getLevelTrackCounts());
        values.put(Sql.LEVELS_COLUMN_ADDED, mod.getAddedTs());
        values.put(Sql.LEVELS_COLUMN_INSTALLED, mod.getInstalledTs());
        values.put(Sql.LEVELS_COLUMN_IS_DEFAULT, mod.isDefault() ? 1 : 0);
        values.put(Sql.LEVELS_COLUMN_API_ID, mod.getApiId());
        values.put(Sql.LEVELS_COLUMN_SELECTED_TRACK, mod.getSelectedTrack());
        values.put(Sql.LEVELS_COLUMN_SELECTED_LEVEL, mod.getSelectedLevel());
        values.put(Sql.LEVELS_COLUMN_SELECTED_LEAGUE, mod.getSelectedLeague());
        values.put(Sql.LEVELS_COLUMN_UNLOCKED_LEVELS, mod.getUnlockedLevels());
        values.put(Sql.LEVELS_COLUMN_UNLOCKED_LEAGUES, mod.getUnlockedLeagues());
        values.put(Sql.LEVELS_COLUMN_TRACKS_UNLOCKED, mod.getUnlockedTracksString());

        long insertId = db.insert(Sql.TABLE_LEVELS, null, values);
        Cursor cursor = db.query(Sql.TABLE_LEVELS, null,
                Sql.LEVELS_COLUMN_ID + " = " + insertId,
                null, null, null, null);

        cursor.moveToFirst();

        ModEntity level = cursorToLevel(cursor);
        cursor.close();
        return level;
    }

    public synchronized void deleteLevel(ModEntity level) {
        long id = level.getId();
        db.delete(Sql.TABLE_LEVELS, Sql.LEVELS_COLUMN_ID + " = " + id, null);
    }

    // This will also reset auto increment counter
    public synchronized void deleteAllLevels() {
        db.delete(Sql.TABLE_LEVELS, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + Sql.TABLE_LEVELS + "'");
    }

    public synchronized void resetAllLevelsSettings() {
        ContentValues values = new ContentValues();
        values.put(Sql.LEVELS_COLUMN_TRACKS_UNLOCKED, "[]");
        values.put(Sql.LEVELS_COLUMN_SELECTED_LEAGUE, 0);
        values.put(Sql.LEVELS_COLUMN_SELECTED_LEVEL, 0);
        values.put(Sql.LEVELS_COLUMN_SELECTED_TRACK, 0);
        values.put(Sql.LEVELS_COLUMN_UNLOCKED_LEAGUES, 0);
        values.put(Sql.LEVELS_COLUMN_UNLOCKED_LEVELS, 0);

        int result = db.update(Sql.TABLE_LEVELS, values, null, null);
        logDebug("LevelsDataSource.resetAllLevelsSettings: result = " + result);
    }

    public synchronized void updateMod(ModEntity level) {
        ContentValues values = new ContentValues();
        values.put(Sql.LEVELS_COLUMN_TRACKS_UNLOCKED, level.getUnlockedTracksString());
        values.put(Sql.LEVELS_COLUMN_SELECTED_LEAGUE, level.getSelectedLeague());
        values.put(Sql.LEVELS_COLUMN_SELECTED_LEVEL, level.getSelectedLevel());
        values.put(Sql.LEVELS_COLUMN_SELECTED_TRACK, level.getSelectedTrack());
        values.put(Sql.LEVELS_COLUMN_UNLOCKED_LEAGUES, level.getUnlockedLeagues());
        values.put(Sql.LEVELS_COLUMN_UNLOCKED_LEVELS, level.getUnlockedLevels());

        // logDebug("LevelsDataSource.updateLevel selectedLeague: " + level.getSelectedLeague());

        db.update(Sql.TABLE_LEVELS, values, Sql.LEVELS_COLUMN_ID + " = " + level.getId(), null);
    }

    public synchronized HashMap<Long, Long> findInstalledLevels(ArrayList<Long> apiIds) {
        HashMap<Long, Long> installed = new HashMap<>();

        String[] apiIdsArray = new String[apiIds.size()];
        for (int i = 0; i < apiIdsArray.length; i++) {
            apiIdsArray[i] = apiIds.get(i).toString();
        }

        Cursor cursor = db.rawQuery("SELECT " + Sql.LEVELS_COLUMN_API_ID + ", " + Sql.LEVELS_COLUMN_ID + " FROM " + Sql.TABLE_LEVELS + " WHERE " + Sql.LEVELS_COLUMN_API_ID + " IN (" + makePlaceholders(apiIdsArray.length) + ")", apiIdsArray);
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
        Cursor cursor = db.query(Sql.TABLE_LEVELS, null, null, null, null, null, null);

        List<ModEntity> levels = levelsFromCursor(cursor);
        cursor.close();

        return levels;
    }

    public synchronized List<ModEntity> getLevels(int offset, int count) {
        Cursor cursor = db.query(Sql.TABLE_LEVELS, null, null, null, null, Sql.LEVELS_COLUMN_ID + " ASC", offset + ", " + count);

        List<ModEntity> levels = levelsFromCursor(cursor);
        cursor.close();

        return levels;
    }

    public synchronized ModEntity getMod(long id) {
        Cursor cursor = db.query(Sql.TABLE_LEVELS, null, Sql.LEVELS_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();

        ModEntity level = null;
        if (cursor.getCount() > 0) {
            level = cursorToLevel(cursor);
        }

        cursor.close();
        return level;
    }

    public synchronized ModEntity getMod(String guid) {
        Cursor cursor = db.query(Sql.TABLE_LEVELS, null, Sql.LEVELS_COLUMN_GUID + " = '" + guid + "'", null, null, null, null);
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

    public synchronized boolean isDefaultModCreated() {
        Cursor cursor = db.query(Sql.TABLE_LEVELS, new String[]{Sql.LEVELS_COLUMN_ID}, Sql.LEVELS_COLUMN_IS_DEFAULT + " = 1", null, null, null, null);
        boolean created = cursor.getCount() > 0;
        cursor.close();
        return created;
    }

    public synchronized boolean isApiIdInstalled(long apiId) {
        Cursor cursor = db.query(Sql.TABLE_LEVELS, new String[]{Sql.LEVELS_COLUMN_ID}, Sql.LEVELS_COLUMN_API_ID + " = " + apiId, null, null, null, null);
        boolean installed = cursor.getCount() > 0;
        cursor.close();
        return installed;
    }

    @SuppressLint("Range")
    public synchronized HighScores getHighScores(String levelGuid, int league) {
        Cursor cursor = db.query(Sql.TABLE_SCORES, null,
                Sql.selectHighScore(levelGuid, league),
                null, null, null, Sql.SCORES_COLUMN_TIME);
        cursor.moveToFirst();

        HighScores highScores = new HighScores(league);
        int i = 0;
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Score score = new Score();
                score.setTrackId(cursor.getString(cursor.getColumnIndex(Sql.SCORES_COLUMN_LEVEL_GUID)));
                score.setLeague(cursor.getInt(cursor.getColumnIndex(Sql.SCORES_COLUMN_LEAGUE)));
                score.setTime(cursor.getLong(cursor.getColumnIndex(Sql.SCORES_COLUMN_TIME)));
                score.setDate(cursor.getString(cursor.getColumnIndex(Sql.SCORES_COLUMN_DATE)));
                score.setName(cursor.getString(cursor.getColumnIndex(Sql.SCORES_COLUMN_NAME)));
                highScores.get(score.getLeague()).add(score); //todo
                cursor.moveToNext();
                i++;
                if (i > Constants.RECORD_COUNT - 1) {
                    break;
                }
            }
        }
        cursor.close();
        return highScores;
    }

    public synchronized void saveHighScore(Score score) {
        ContentValues values = new ContentValues();
        values.put(Sql.SCORES_COLUMN_LEVEL_GUID, score.getTrackId());
        values.put(Sql.SCORES_COLUMN_LEAGUE, score.getLeague());
        values.put(Sql.SCORES_COLUMN_TIME, score.getTime());
        values.put(Sql.SCORES_COLUMN_NAME, score.getName());
        values.put(Sql.SCORES_COLUMN_DATE, score.getDate());


        long result = db.insert(Sql.TABLE_SCORES, null, values);
        logDebug("LevelsDataSource.TABLE_LEVELS: result = " + result);
    }

    public synchronized void clearHighScores(String levelGuid) {
        if (levelGuid == null) {
            db.delete(Sql.TABLE_SCORES, null, null);
        } else {
            db.delete(Sql.TABLE_SCORES,
                    Sql.SCORES_COLUMN_LEVEL_GUID + " = '" + levelGuid + "'",
                    null);
        }

    }

    @SuppressLint("Range")
    private ModEntity cursorToLevel(Cursor cursor) {
        ModEntity level = new ModEntity();
        level.setId(cursor.getLong(cursor.getColumnIndex(Sql.LEVELS_COLUMN_ID)));
        level.setGuid(cursor.getString(cursor.getColumnIndex(Sql.LEVELS_COLUMN_GUID)));
        level.setName(cursor.getString(cursor.getColumnIndex(Sql.LEVELS_COLUMN_NAME)));
        level.setTrackCountsByLevel(cursor.getString(cursor.getColumnIndex(Sql.LEVELS_COLUMN_TRACKS_COUNT)));
        level.setUnlockedTracks(cursor.getString(cursor.getColumnIndex(Sql.LEVELS_COLUMN_TRACKS_UNLOCKED)));
        level.setSelectedLevel(cursor.getInt(cursor.getColumnIndex(Sql.LEVELS_COLUMN_SELECTED_LEVEL)));
        level.setSelectedTrack(cursor.getInt(cursor.getColumnIndex(Sql.LEVELS_COLUMN_SELECTED_TRACK)));
        level.setSelectedLeague(cursor.getInt(cursor.getColumnIndex(Sql.LEVELS_COLUMN_SELECTED_LEAGUE)));
        level.setUnlockedLevels(cursor.getInt(cursor.getColumnIndex(Sql.LEVELS_COLUMN_UNLOCKED_LEVELS)));
        level.setUnlockedLeagues(cursor.getInt(cursor.getColumnIndex(Sql.LEVELS_COLUMN_UNLOCKED_LEAGUES)));

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
}
