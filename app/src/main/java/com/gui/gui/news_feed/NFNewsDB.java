package com.gui.gui.news_feed;

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
public class NFNewsDB {
    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public NFNewsDB(Context context) {
        this.context = context;
    }

    /***
     * Helps to interact with SQLite database
     * */
    class DBHelper extends SQLiteOpenHelper {

        static final String DATABASE_NAME = "NEWS_DB";
        static final String NEWS_TABLE = "NEWS";
        static final String COL_uuid = "uuid";
        static final String COL_url = "url";
        static final String COL_author = "author";
        static final String COL_published = "pub";
        static final String COL_title = "title";
        static final String COL_text = "text";
        static final String COL_language = "lang";

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + NEWS_TABLE + " (" + COL_uuid + " text primary key, " +
                            COL_title + " text," +
                            COL_text + " text," +
                            COL_url + " text," +
                            COL_author + " text," +
                            COL_language + " text," +
                            COL_published + " text"
                            + ")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NEWS_TABLE);
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

    /**
     * @param data Instance of an bean class
     * @return rowID, if row is inserted otherwise -1
     * */
    public long addNews(NFNewsData data) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_uuid, data.getUuid());
        cv.put(DBHelper.COL_title, data.getTitle());
        cv.put(DBHelper.COL_text, data.getText());
        cv.put(DBHelper.COL_author, data.getAuthor());
        cv.put(DBHelper.COL_published, data.getPublished());
        cv.put(DBHelper.COL_url, data.getUrl());
        cv.put(DBHelper.COL_language, data.getLanguage());
        return database.insert(DBHelper.NEWS_TABLE, null, cv);
    }

    /***
     * Checks whether the article is already exists or not in database
     * @param uuid UUID of a news article
     * @return true if article is already added, otherwise false
     * */
    public boolean isAlreadyAdded(String uuid) {
        String[] columns = new String[]{DBHelper.COL_uuid};
        Cursor cursor = database.query(DBHelper.NEWS_TABLE, columns, DBHelper.COL_uuid + " = ?",
                new String[]{uuid}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /***
     * @param uuid UUID of a news article to be deleted
     * @return true if delete operation success, otherwise false
     * */
    public boolean deleteNews(String uuid) {
        return database.delete(DBHelper.NEWS_TABLE, DBHelper.COL_uuid + "=?", new String[]{uuid}) > 0;
    }

    /***
     * Retrieves all the news articles from the database.
     * @return List of a News article stored in the database
     * */
    public ArrayList<NFNewsData> getData() {
        ArrayList<NFNewsData> newsData = new ArrayList<>();
        String[] columns = new String[]{DBHelper.COL_uuid,
                DBHelper.COL_title, DBHelper.COL_text,
                DBHelper.COL_author, DBHelper.COL_published,
                DBHelper.COL_url, DBHelper.COL_language
        };
        Cursor cursor = database.query(DBHelper.NEWS_TABLE, columns, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                NFNewsData news = new NFNewsData();
                String uuid = cursor.getString(0);
                String title = cursor.getString(1);
                String text = cursor.getString(2);
                String author = cursor.getString(3);
                String published = cursor.getString(4);
                String url = cursor.getString(5);
                String language = cursor.getString(6);
                news.setUuid(uuid);
                news.setTitle(title);
                news.setText(text);
                news.setAuthor(author);
                news.setPublished(published);
                news.setUrl(url);
                news.setLanguage(language);
                newsData.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return newsData;
    }
}
