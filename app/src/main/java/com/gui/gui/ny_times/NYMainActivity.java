package com.gui.gui.ny_times;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import com.gui.gui.R;
import com.gui.gui.flight_status.FlightMainActivity;
import com.gui.gui.merriam.MerriamMainActivity;
import com.gui.gui.news_feed.NFMainActivity;

/**
 * First Screen that users interacts with.
 * Contains two different fragments.
 * */
public class NYMainActivity extends Activity {

    Button btnSearch, btnSaved;
    FrameLayout frame;
    Fragment fragment;
    NYSearchFragment NYSearchFragment;
    FragmentTransaction transaction;
    FragmentManager fm;
    String TAG = "";

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nymain);

        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        frame = findViewById(R.id.frame);
        btnSearch = findViewById(R.id.btnSearch);
        btnSaved = findViewById(R.id.btnSaved);
        NYSearchFragment = new NYSearchFragment();
        fm = getFragmentManager();
        showSearchFragment();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchFragment();
            }
        });
        btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavedFragment();
            }
        });
    }

    /***
     * Displays NYSearchFragment
     * */
    void showSearchFragment() {
        //begin transaction
        transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); //animation
        fragment = NYSearchFragment;
        //adding tag to it to retrieve it back later on
        TAG = "SEARCH";
        //if it is already added then display it
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            //if it is not on the top, display it
            transaction.replace(R.id.frame, fragment, TAG);
        }
        transaction.commit();
    }

    /***
     * Displays NYSavedFragment
     * */
    void showSavedFragment() {
        //begin transaction
        transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); //animation
        fragment = new NYSavedFragment();
        //adding tag to it to retrieve it back later on
        TAG = "SAVED";
        //if it is already added then display it
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            //if it is not on the top, display it
            transaction.replace(R.id.frame, fragment, TAG);
        }
        transaction.commit();
    }

    /**
     * Inflate menu to this screen (Activity)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ny_all_app, menu);
        return true;
    }

    /**
     * Handles onClick method for menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuDictionary:
                startActivity(new Intent(NYMainActivity.this, MerriamMainActivity.class));
                return true;
            case R.id.mnuFlightStatus:
                startActivity(new Intent(NYMainActivity.this, FlightMainActivity.class));
                return true;
            case R.id.mnuNewsFeed:
                startActivity(new Intent(NYMainActivity.this, NFMainActivity.class));
                return true;
            case R.id.mnuHelp:
                /*
                 showing custom dialog notification
                 */

                final Dialog dialog = new Dialog(NYMainActivity.this);
                //Removing title for a dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ny_dialog_about);
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
