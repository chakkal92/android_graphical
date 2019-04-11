package com.gui.gui.flight_status;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.gui.R;
import com.gui.gui.merriam.MerriamMainActivity;
import com.gui.gui.news_feed.NFMainActivity;
import com.gui.gui.ny_times.NYMainActivity;

/**
 * This activity shows the detailed information for a Flight.
 * user can save/remove Flight data offline on this activity.
 * */
public class FlightDetailActivity extends Activity {

    FlightDB db;
    Flight flight;
    boolean notToAdd;

    CoordinatorLayout parent;
    Toolbar toolbar;

    Button btnSave;

    String msgSnkSaved, msgSnkRemoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_activity_flight_detail);

        parent = findViewById(R.id.parent);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        msgSnkRemoved = getResources().getString(R.string.flight_snck_removed);
        msgSnkSaved = getResources().getString(R.string.flight_snck_saved);

        flight = (Flight) getIntent().getSerializableExtra("FLIGHT");
        TextView tvIATANo = findViewById(R.id.tvIATANo);
        TextView tvICAONo = findViewById(R.id.tvICAONo);
        TextView tvFlightNo = findViewById(R.id.tvFlightNo);
        TextView tvLat = findViewById(R.id.tvLat);
        TextView tvLon = findViewById(R.id.tvLon);
        TextView tvAlt = findViewById(R.id.tvAlt);
        TextView tvDirection = findViewById(R.id.tvDirection);
        TextView tvSpeed = findViewById(R.id.tvSpeed);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvdIATA = findViewById(R.id.tvdIATA);
        TextView tvaIATA = findViewById(R.id.tvaIATA);
        btnSave = findViewById(R.id.btnSave);

        String iataNo = flight.getIataNumber();
        String icaoNo = flight.getIcaoNumber();
        String flightNo = flight.getNumber();
        String lati = "" + flight.getLatitude();
        String longi = "" + flight.getLongitude();
        String alti = "" + flight.getAltitude();
        String direct = "" + flight.getDirection();
        String speed = "" + flight.getHorizontal();
        String status = "" + flight.getStatus();
        String dIATA = "" + flight.getdIATA();
        String aIATA = "" + flight.getaIATA();

        tvIATANo.setText(iataNo);
        tvICAONo.setText(icaoNo);
        tvFlightNo.setText(flightNo);
        tvLat.setText(lati);
        tvLon.setText(longi);
        tvAlt.setText(alti);
        tvDirection.setText(direct);
        tvSpeed.setText(speed);
        tvStatus.setText(status);
        tvdIATA.setText(dIATA);
        tvaIATA.setText(aIATA);

        db = new FlightDB(this);

        db.open();
        notToAdd = db.isAlreadyAdded(flight.getNumber());
        db.close();

        updateBtnText();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                //Check in the database whether the word already exists or not
                if (notToAdd) {
                    //if it already exists, then performing delete operation
                    if (db.deleteFlight(flight.getNumber())) {
                        //showing snackbar
                        showSnack(msgSnkRemoved);
                        //notifying the FlightSavedFragment that the list has to be refreshed!
                        setResult(101);
                        //complementing the flag
                        notToAdd = !notToAdd;
                    }
                } else {
                    //if it does not exists, then performing INSERT operation
                    long res = db.addNews(flight);
                    if (res != -1) {
                        //showing snackbar
                        showSnack(msgSnkSaved);
                        //complementing the flag
                        notToAdd = !notToAdd;
                    }
                }
                updateBtnText(); //updating text on a button
                db.close();
            }
        });
    }

    /**
     * @param message Message to be displayed on the snackbar
     * */
    void showSnack(String message){
        Snackbar snackbar = Snackbar
                .make(parent, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Updates the text on the button, if the Flight details exists in the database,
     * then button text will be for remove, and if not then add
     * */
    void updateBtnText(){
        String btnText = "";
        if(notToAdd){
            btnText = getResources().getString(R.string.flight_remove);
        }else{
            btnText = getResources().getString(R.string.flight_save);
        }

        btnSave.setText(btnText);
    }

    /**
     * Inflate menu to this screen (Activity)
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flight_all_app, menu);
        return true;
    }

    /**
     * Handles onClick method for menu items
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuDictionary:
                startActivity(new Intent(FlightDetailActivity.this, MerriamMainActivity.class));
                return true;
            case R.id.mnuNewsFeed:
                startActivity(new Intent(FlightDetailActivity.this, NFMainActivity.class));
                return true;
            case R.id.mnuNYTimes:
                startActivity(new Intent(FlightDetailActivity.this, NYMainActivity.class));
                return true;
            case R.id.mnuHelp:
                /*
                 showing custom dialog notification
                 */
                final Dialog dialog = new Dialog(FlightDetailActivity.this);
                //Removing title for a dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.flight_dialog_about);
                Button btnDismiss = dialog.findViewById(R.id.btnDismiss);
                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //stretching the layout parameters for a dialog
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);

                dialog.setCancelable(true);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
