package org.happysanta.gdtralive.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.happysanta.gdtralive.game.util.Sql;

public class LevelsSQLiteOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	LevelsSQLiteOpenHelper(Context context) {
		super(context, Sql.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Sql.TABLE_LEVELS_CREATE);
		createLevelsIndexes(db);

		db.execSQL(Sql.TABLE_SCORES_CREATE);
		createScoresIndexes(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private void createLevelsIndexes(SQLiteDatabase db) {
		db.execSQL(Sql.MODS_GUID_INDEX);
		db.execSQL(Sql.MODS_COLUMN_API_ID_INDEX);
		db.execSQL(Sql.MODS_COLUMN_IS_DEFAULT_INDEX);
	}

	private void createScoresIndexes(SQLiteDatabase db) {
		db.execSQL(Sql.SCORES_LEVEL_GUID_INDEX);
	}

}
