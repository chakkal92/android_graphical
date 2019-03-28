package com.gui.gui.merriam;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.gui.gui.R;
import com.gui.gui.flight_status.FlightMainActivity;
import com.gui.gui.news_feed.NFMainActivity;
import com.gui.gui.ny_times.NYMainActivity;

/**
 * This activity shows the detailed information for a word.
 * user can save/remove word data offline on this activity.
 * */
public class MerriamDetailActivity extends Activity {

    TextView tvWord, tvWordType, tvWordPro;
    ListView listDefs;
    Button btnSaveOffline;

    CoordinatorLayout parent;
    Toolbar toolbar;

    Word wordData;
    WordDB db;
    boolean notToAdd;
    String wordTitle;

    String msgRemoved, msgSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merriam_detail);

        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        parent = findViewById(R.id.parent);
        tvWord = findViewById(R.id.tvWord);
        tvWordType = findViewById(R.id.tvWordType);
        tvWordPro = findViewById(R.id.tvWordPro);
        listDefs = findViewById(R.id.listDefs);
        btnSaveOffline = findViewById(R.id.btnSaveOffline);

        wordData = (Word) getIntent().getSerializableExtra("WORD_DATA");
        db = new WordDB(MerriamDetailActivity.this);

        wordTitle = wordData.getHw();
        db.open();
        notToAdd = db.isAlreadyAdded(wordData.getHw());
        db.close();

        msgRemoved = getResources().getString(R.string.merriam_msg_removed);
        msgSaved = getResources().getString(R.string.merriam_msg_saved);

        updateBtnText();

        String pr = wordData.getPr();
        String fl = wordData.getFl();
        tvWord.setText(wordTitle);
        tvWordPro.setText(pr);
        tvWordType.setText(fl);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MerriamDetailActivity.this, android.R.layout.simple_list_item_1, wordData.getDt());
        listDefs.setAdapter(adapter);

        btnSaveOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                //Check in the database whether the word already exists or not
                if (notToAdd) {
                    //if it already exists, then performing delete operation
                    if(db.deleteWord(wordTitle)){
                        //showing snackbar
                        showSnack(msgRemoved);
                        //notifying the MerriamSearchFragment that the list has to be refreshed!
                        setResult(101);
                        //complementing the flag
                        notToAdd = !notToAdd;
                    }
                }else{
                    //if it does not exists, then performing INSERT operation
                    long res = db.addWord(wordData);
                    if(res != -1){
                        //showing snackbar
                        showSnack(msgSaved);
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
    void showSnack(String message) {
        Snackbar snackbar = Snackbar
                .make(parent, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Updates the text on the button, if the word exists in the database,
     * then button text will be for remove, and if not then add
     * */
    void updateBtnText() {
        String btnText = "";
        if (notToAdd) {
            btnText = getResources().getString(R.string.remove);
        } else {
            btnText = getResources().getString(R.string.save);
        }

        btnSaveOffline.setText(btnText);
    }

    /**
     * Inflate menu to this screen (Activity)
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_merri_all_app, menu);
        return true;
    }


    /**
     * Handles onClick method for menu items
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuNewsFeed:
                startActivity(new Intent(MerriamDetailActivity.this, NFMainActivity.class));
                return true;
            case R.id.mnuFlightStatus:
                startActivity(new Intent(MerriamDetailActivity.this, FlightMainActivity.class));
                return true;
            case R.id.mnuNYTimes:
                startActivity(new Intent(MerriamDetailActivity.this, NYMainActivity.class));
                return true;
            case R.id.mnuHelp:
                /*
                 showing custom dialog notification
                 */

                final Dialog dialog = new Dialog(MerriamDetailActivity.this);
                //Removing title for a dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.merriam_dialog_about);
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
