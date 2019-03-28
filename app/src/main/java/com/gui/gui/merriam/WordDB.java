package com.gui.gui.merriam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/***
 * This class is responsible for performing all the database related operations
 * */
public class WordDB {

    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public WordDB(Context context) {
        this.context = context;
    }

    /***
     * Helps to interact with SQLite database
     * */
    class DBHelper extends SQLiteOpenHelper {

        static final String DATABASE_NAME = "WORD_DB";
        static final String WORD_TABLE = "WORD_DATA";
        static final String COL_WORD_TITLE = "wHW";
        static final String COL_WORD_PRON = "wPR";
        static final String COL_WORD_TYPE = "wFL";

        static final String DEF_TABLE = "WORD_DEF";
        static final String COL_DEF = "dDT";

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Here one word can have multiple definitions.
            //So two tables are used to achieve functionality.
            db.execSQL(
                    "CREATE TABLE " + WORD_TABLE + " (" + COL_WORD_TITLE + " text primary key, " +
                            COL_WORD_PRON + " text," +
                            COL_WORD_TYPE + " text"
                            + ")"
            );
            db.execSQL(
                    "CREATE TABLE " + DEF_TABLE + " (" + COL_WORD_TITLE + " text, " +
                            COL_DEF + " text"
                            + ")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + WORD_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DEF_TABLE);
            onCreate(db);
        }
    }

    /**
     * To establish a connection
     */
    public void open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * To close a connection
     */
    public void close() {
        dbHelper.close();
    }

    /***
     * @param wTitle Title of a word to check whether it already exists or not in the database
     * @return true if the word already exists, otherwise false
     * */
    public boolean isAlreadyAdded(String wTitle) {
        String[] columns = new String[]{DBHelper.COL_WORD_TITLE};
        Cursor cursor = database.query(DBHelper.WORD_TABLE, columns, DBHelper.COL_WORD_TITLE + " = ?",
                new String[]{wTitle}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /***
     * @param title Title of a word to be inserted as ForeignKey in definitions table
     * @param def Definition of a word to be inserted
     * */
    private void addDefForWord(String title, String def) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_DEF, def);
        cv.put(DBHelper.COL_WORD_TITLE, title);
        database.insert(DBHelper.DEF_TABLE, null, cv);
    }

    /***
     * @param wordData instance of Word (contains all the information of a word) to be saved
     * */
    public long addWord(Word wordData) {
        ContentValues cv = new ContentValues();
        String title = wordData.getHw();
        cv.put(DBHelper.COL_WORD_TITLE, title);
        cv.put(DBHelper.COL_WORD_TYPE, wordData.getFl());
        cv.put(DBHelper.COL_WORD_PRON, wordData.getPr());
        ArrayList<String> defs = wordData.getDt();
        for (int i = 0; i < defs.size(); i++) {
            addDefForWord(title, defs.get(i));
        }
        return database.insert(DBHelper.WORD_TABLE, null, cv);
    }

    /***
     * @param wordTitle Title of a word to be deleted from the database.
     *                  Both the tables are affected
     * @return true if deletion successful otherwise false
     * */
    public boolean deleteWord(String wordTitle) {
        return database.delete(DBHelper.DEF_TABLE, DBHelper.COL_WORD_TITLE + "=?", new String[]{wordTitle}) > 0
                && (database.delete(DBHelper.WORD_TABLE, DBHelper.COL_WORD_TITLE + "=?", new String[]{wordTitle}) > 0);
    }

    /***
     * Performs a join query operation.
     *
     * @return List of a Word stored in the database
     *
     * */
    ArrayList<Word> getAllWords() {
        ArrayList<Word> words = new ArrayList<>();
        ArrayList<String> defs = new ArrayList<>();
        String cols[] = {DBHelper.COL_WORD_TITLE, DBHelper.COL_WORD_TYPE, DBHelper.COL_WORD_PRON};
        String defCols[] = {DBHelper.COL_WORD_TITLE, DBHelper.COL_DEF};

        //Fetching word list
        Cursor cursor = database.query(DBHelper.WORD_TABLE, cols, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Word word = new Word();
                defs.clear();
                String wordTitle = cursor.getString(0);
                String type = cursor.getString(1);
                String pr = cursor.getString(2);
                word.setHw(wordTitle);
                word.setFl(type);
                word.setPr(pr);

                //Fetching word list with its definition
                //Here one word can have multiple definitions.
                Cursor defCursor = database.query(DBHelper.DEF_TABLE, defCols, DBHelper.COL_WORD_TITLE + " = ?",
                        new String[]{wordTitle}, null, null, null);
                if (defCursor.moveToFirst()) {
                    do {
                        defs.add(defCursor.getString(1));
                    } while (defCursor.moveToNext());
                }
                defCursor.close();
                word.setDt(defs);
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }
}
