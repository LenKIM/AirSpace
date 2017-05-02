package com.yyy.xxxx.airspace2.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yyy.xxxx.airspace2.DataBase.BoardSchema.BoardTable;

/**
 * Created by len on 2017. 3. 29..
 */

public class BoardDataBaseHelper extends SQLiteOpenHelper {

    private static final String TABLENAME = BoardTable.NAME;
    private static final String DATABASENAME = "Board.db";
    private static final int VERSION = 1;

    public BoardDataBaseHelper(Context context) {
        super(context, DATABASENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BoardTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BoardTable.Cols.UUID + "," +
                BoardTable.Cols.TITLE + "," +
                BoardTable.Cols.CONTENT + "," +
                BoardTable.Cols.DATE + "," +
                BoardTable.Cols.PHOTOURL + "," +
                BoardTable.Cols.MAPPOINT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}