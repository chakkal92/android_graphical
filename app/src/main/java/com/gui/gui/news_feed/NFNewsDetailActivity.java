package com.gui.gui.news_feed;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import com.gui.gui.flight_status.FlightMainActivity;
import com.gui.gui.merriam.MerriamMainActivity;
import com.gui.gui.ny_times.NYMainActivity;

public class NFNewsDetailActivity extends Activity {

    TextView tvNewsTitle, tvAuthor, tvLanguage, tvText;
    Button btnSave, btnViewLink;

    CoordinatorLayout parent;
    Toolbar toolbar;

    NFNewsData newsData;
    NFNewsDB db;
    boolean notToAdd;

    String msgSnackSaved, msgSnackRemoved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nf_news_detail);

        parent = findViewById(R.id.parent);

        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        newsData = (NFNewsData) getIntent().getSerializableExtra("NEWS");
        db = new NFNewsDB(NFNewsDetailActivity.this);

        tvNewsTitle = findViewById(R.id.tvNewsTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvText = findViewById(R.id.tvText);
        btnSave = findViewById(R.id.btnSave);
        btnViewLink = findViewById(R.id.btnViewLink);

        tvText.setText(newsData.getText());
        tvAuthor.setText(newsData.getAuthor());
        tvLanguage.setText(newsData.getLanguage());
        tvNewsTitle.setText(newsData.getTitle());

        msgSnackRemoved = getResources().getString(R.string.nf_removed_snck);
        msgSnackSaved = getResources().getString(R.string.nf_saved_snck);

        db.open();
        notToAdd = db.isAlreadyAdded(newsData.getUuid());
        db.close();

        updateBtnText();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                //Check in the database whether the news article already exists or not
                if (notToAdd) {
                    //if it already exists, then performing delete operation
                    if(db.deleteNews(newsData.getUuid())){
                        //showing snackbar
                        showSnack(msgSnackRemoved);
                        //notifying the NFSavedFragment that the list has to be refreshed!
                        setResult(101);
                        //complementing the flag
                        notToAdd = !notToAdd;
                    }
                }else{
                    //if it does not exists, then performing INSERT operation
                    long res = db.addNews(newsData);
                    if(res != -1){
                        //showing snackbar
                        showSnack(msgSnackSaved);
                        //complementing the flag
                        notToAdd = !notToAdd;
                    }
                }
                updateBtnText(); //updating text on a button
                db.close();
            }
        });

        btnViewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = newsData.getUrl();
                if(url.startsWith("http")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
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
     * Updates the text on the button, if the news article exists in the database,
     * then button text will be for remove, and if not then add
     * */
    void updateBtnText(){
        String btnText = "";
        if(notToAdd){
            btnText = getResources().getString(R.string.nf_remove);
        }else{
            btnText = getResources().getString(R.string.nf_save);
        }

        btnSave.setText(btnText);
    }

    /**
     * Inflate menu to this screen (Activity)
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nf_all_app, menu);
        return true;
    }

    /**
     * Handles onClick method for menu items
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuDictionary:
                startActivity(new Intent(NFNewsDetailActivity.this, MerriamMainActivity.class));
                return true;
            case R.id.mnuFlightStatus:
                startActivity(new Intent(NFNewsDetailActivity.this, FlightMainActivity.class));
                return true;
            case R.id.mnuNYTimes:
                startActivity(new Intent(NFNewsDetailActivity.this, NYMainActivity.class));
                return true;
            case R.id.mnuHelp:
                /*
                 showing custom dialog notification
                 */

                final Dialog dialog = new Dialog(NFNewsDetailActivity.this);
                //Removing title for a dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.nf_dialog_about);
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
