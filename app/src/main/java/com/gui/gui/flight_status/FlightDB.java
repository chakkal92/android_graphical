package com.gui.gui.flight_status;


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
public class FlightDB {

    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public FlightDB(Context context) {
        this.context = context;
    }

    /***
     * Helps to interact with SQLite database
     * */
    class DBHelper extends SQLiteOpenHelper {

        static final String DATABASE_NAME = "FLIGHT_DB";
        static final String FLIGHT_TABLE = "flights";
        //Column names for the database table
        static final String COL_iataNumber = "iataNumber";
        static final String COL_flightNo = "uuid";
        static final String COL_status = "status";
        static final String COL_icaoNumber = "icaoNumber";
        static final String COL_latitude = "latitude";
        static final String COL_longitude = "longitude";
        static final String COL_altitude = "altitude";
        static final String COL_direction = "direction";
        static final String COL_horizontal = "horizontal";
        static final String COL_aIATA = "aIATA";
        static final String COL_dIATA = "dIATA";

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //creating a table
            db.execSQL(
                    "CREATE TABLE " + FLIGHT_TABLE + " (" + COL_flightNo + " text primary key, " +
                            COL_status + " text," +
                            COL_iataNumber + " text," +
                            COL_icaoNumber + " text," +
                            COL_latitude + " text," +
                            COL_longitude + " text," +
                            COL_altitude + " text," +
                            COL_direction + " text," +
                            COL_horizontal + " text," +
                            COL_aIATA + " text," +
                            COL_dIATA + " text"
                            + ")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FLIGHT_TABLE);
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
     */
    public long addNews(Flight data) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_flightNo, data.getNumber());
        cv.put(DBHelper.COL_status, data.getStatus());
        cv.put(DBHelper.COL_iataNumber, data.getIataNumber());
        cv.put(DBHelper.COL_icaoNumber, data.getIcaoNumber());
        cv.put(DBHelper.COL_status, data.getStatus());
        cv.put(DBHelper.COL_latitude, data.getLatitude());
        cv.put(DBHelper.COL_longitude, data.getLongitude());
        cv.put(DBHelper.COL_altitude, data.getAltitude());
        cv.put(DBHelper.COL_direction, data.getDirection());
        cv.put(DBHelper.COL_horizontal, data.getHorizontal());
        cv.put(DBHelper.COL_aIATA, data.getaIATA());
        cv.put(DBHelper.COL_dIATA, data.getdIATA());
        return database.insert(DBHelper.FLIGHT_TABLE, null, cv);
    }

    /***
     * Helps to check whether the flight is already exists or not
     * @param flightNo Flight no, to be checked exists or not
     * @return true if given flight no. data exists, otherwise false
     * */
    public boolean isAlreadyAdded(String flightNo) {
        String[] columns = new String[]{DBHelper.COL_flightNo};
        Cursor cursor = database.query(DBHelper.FLIGHT_TABLE, columns, DBHelper.COL_flightNo + " = ?",
                new String[]{flightNo}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /***
     * This helps to perform delete operation on the database
     * @param flightNo Flight no. of a Flight to be deleted from the database
     * @return true if the deletion performed successfully otherwise false
     * */
    public boolean deleteFlight(String flightNo) {
        return database.delete(DBHelper.FLIGHT_TABLE, DBHelper.COL_flightNo + "=?", new String[]{flightNo}) > 0;
    }

    /***
     * Retrieves all the data for flights stored previously
     * @return List of Flight class
     * */
    public ArrayList<Flight> getData() {
        ArrayList<Flight> flights = new ArrayList<>();
        String[] columns = new String[]{DBHelper.COL_flightNo,
                DBHelper.COL_status, DBHelper.COL_iataNumber,
                DBHelper.COL_icaoNumber, DBHelper.COL_latitude,
                DBHelper.COL_longitude, DBHelper.COL_altitude,
                DBHelper.COL_direction, DBHelper.COL_horizontal,
                DBHelper.COL_aIATA, DBHelper.COL_dIATA
        };
        Cursor cursor = database.query(DBHelper.FLIGHT_TABLE, columns, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Flight flight = new Flight();
                String flightNo = cursor.getString(0);
                String status = cursor.getString(1);
                String iataNo = cursor.getString(2);
                String icaoNo = cursor.getString(3);
                String lat = cursor.getString(4);
                String lon = cursor.getString(5);
                String alt = cursor.getString(6);
                String direct = cursor.getString(7);
                String hori = cursor.getString(8);
                String aIATA = cursor.getString(9);
                String dIATA = cursor.getString(10);
                flight.setNumber(flightNo);
                flight.setStatus(status);
                flight.setIataNumber(iataNo);
                flight.setIcaoNumber(icaoNo);
                flight.setLatitude(Double.parseDouble(lat));
                flight.setLongitude(Double.parseDouble(lon));
                flight.setAltitude(Double.parseDouble(alt));
                flight.setDirection(Double.parseDouble(direct));
                flight.setHorizontal(Double.parseDouble(hori));
                flight.setaIATA(aIATA);
                flight.setdIATA(dIATA);
                flights.add(flight);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return flights;
    }
}
