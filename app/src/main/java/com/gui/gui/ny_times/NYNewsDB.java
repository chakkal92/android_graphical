package com.gui.gui.ny_times;

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
public class NYNewsDB {
    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public NYNewsDB(Context context) {
        this.context = context;
    }

    /***
     * Helps to interact with SQLite database
     * */
    class DBHelper extends SQLiteOpenHelper {

        static final String DATABASE_NAME = "NY_NEWS_DB";
        static final String NEWS_TABLE = "NY_NEWS";
        static final String COL_id = "id";
        static final String COL_title = "title";
        static final String COL_web_url = "web_url";
        static final String COL_pub_date = "pub_date";
        static final String COL_paragraph = "lead_para";
        static final String COL_source = "source";
        static final String COL_doc_type = "docType";

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + NEWS_TABLE + " (" + COL_id + " text primary key, " +
                            COL_title + " text," +
                            COL_web_url + " text," +
                            COL_pub_date + " text," +
                            COL_paragraph + " text," +
                            COL_doc_type + " text," +
                            COL_source + " text"
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
     * @param data Instance of an NYNews (bean) class
     * @return rowID, if row is inserted otherwise -1
     */
    public long addNews(NYNews data) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_id, data.get_id());
        cv.put(DBHelper.COL_title, data.getTitle());
        cv.put(DBHelper.COL_paragraph, data.getLead_paragraph());
        cv.put(DBHelper.COL_source, data.getSource());
        cv.put(DBHelper.COL_web_url, data.getWeb_url());
        cv.put(DBHelper.COL_pub_date, data.getPub_date());
        cv.put(DBHelper.COL_doc_type, data.getDocument_type());
        return database.insert(DBHelper.NEWS_TABLE, null, cv);
    }

    /***
     * Checks whether the article is already exists or not in database
     * @param id ID of a news article
     * @return true if article is already added, otherwise false
     * */
    public boolean isAlreadyAdded(String id) {
        String[] columns = new String[]{DBHelper.COL_id};
        Cursor cursor = database.query(DBHelper.NEWS_TABLE, columns, DBHelper.COL_id + " = ?",
                new String[]{id}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /***
     * @param id ID of a news article to be deleted
     * @return true if delete operation success, otherwise false
     * */
    public boolean deleteNews(String id) {
        return database.delete(DBHelper.NEWS_TABLE, DBHelper.COL_id + "=?", new String[]{id}) > 0;
    }

    /***
     * Retrieves all the news articles from the database.
     * @return List of a News article stored in the database
     * */
    public ArrayList<NYNews> getData() {
        ArrayList<NYNews> newsData = new ArrayList<>();
        String[] columns = new String[]{DBHelper.COL_id,
                DBHelper.COL_title, DBHelper.COL_web_url,
                DBHelper.COL_pub_date, DBHelper.COL_paragraph,
                DBHelper.COL_source, DBHelper.COL_doc_type
        };
        Cursor cursor = database.query(DBHelper.NEWS_TABLE, columns, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                NYNews news = new NYNews();
                String web_url, lead_paragraph, pub_date, title, _id, source, document_type;
                _id = cursor.getString(0);
                title = cursor.getString(1);
                web_url = cursor.getString(2);
                pub_date = cursor.getString(3);
                lead_paragraph = cursor.getString(4);
                source = cursor.getString(5);
                document_type = cursor.getString(6);
                news.set_id(_id);
                news.setTitle(title);
                news.setWeb_url(web_url);
                news.setPub_date(pub_date);
                news.setLead_paragraph(lead_paragraph);
                news.setSource(source);
                news.setDocument_type(document_type);
                newsData.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return newsData;
    }
}
