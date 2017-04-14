package com.yyy.xxx.airspace.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yyy.xxx.airspace.DataBase.BoardCursorWrapper;
import com.yyy.xxx.airspace.DataBase.BoardDataBaseHelper;
import com.yyy.xxx.airspace.DataBase.BoardSchema.BoardTable;
import com.yyy.xxx.airspace.DataBase.BoardSchema.BoardTable.Cols;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by len on 2017. 3. 29..
 */

public class BoardLab {

    private final static String TAG = BoardLab.class.getName();
    private static BoardLab sBoardLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static BoardLab getBoardLab(Context context) {
        if (sBoardLab == null) {
            sBoardLab = new BoardLab(context);
        }
        return sBoardLab;
    }

    public BoardLab(Context context) {
        mContext = context;
        mDatabase = new BoardDataBaseHelper(mContext).getWritableDatabase();
        Log.d(TAG, "BoardLab 생성자 생성완료");

    }

    public void insertBoard(Board board) {
        ContentValues values = getContentValues(board);
        mDatabase.insert(BoardTable.NAME, null, values);
        Log.d(TAG, "데이터베이스에 입력완료");
    }

    public void updateBoard(Board board) {
        String uuidString = Board.getInstance().getUUID().toString();
        ContentValues values = getContentValues(board);

        mDatabase.update(BoardTable.NAME, values, Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void deleteBoard(Board board) {
        String uuidString = Board.getInstance().getUUID().toString();
        ContentValues values = getContentValues(board);

        mDatabase.delete(BoardTable.NAME,
                Cols.UUID + " = ?",
                new String[]{uuidString}
        );
    }

    public static ContentValues getContentValues(Board board) {
        ContentValues values = new ContentValues();
        values.put(Cols.UUID, board.getUUID().toString());
        values.put(Cols.TITLE, board.getTitle());
        values.put(Cols.DATE, board.getDate());
        values.put(Cols.CONTENT, board.getDescription());
        values.put(Cols.MAPPOINT, board.getMapPoint());
        return values;
    }

    /**
     * 모든 게시판 글들을 가져오자!
     */
    public List<Board> getBoards() {
        List<Board> boards = new ArrayList<>();

        BoardCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                boards.add(cursor.getBoard());
                cursor.moveToFirst();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "ParseException 발생");
        } finally {
            cursor.close();
        }
        return boards;
    }


    public Board getBoard(UUID uuid) throws ParseException {

        BoardCursorWrapper cursor = queryCrimes(
                Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBoard();
        } finally {
            cursor.close();
        }
    }


    private BoardCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BoardTable.NAME,
                null, //null일 경우 모든 열을 의미한다.
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new BoardCursorWrapper(cursor);
    }
}
