package com.gui.gui.ny_times;

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
import com.gui.gui.news_feed.NFMainActivity;

public class NYNewsDetailActivity extends Activity {

    CoordinatorLayout parent;
    Toolbar toolbar;
    TextView tvNewsTitle, tvPubDate, tvDocType, tvSource, tvLeadPara;
    Button btnSave, btnViewLink;

    boolean notToAdd;
    NYNews newsData;
    NYNewsDB db;

    String web_url, lead_paragraph, pub_date, title, _id, source, document_type;

    String msgSnkRemoved, msgSnkSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nynews_detail);
        parent = findViewById(R.id.parent);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        newsData = (NYNews) getIntent().getSerializableExtra("NY_NEWS");
        db = new NYNewsDB(NYNewsDetailActivity.this);

        msgSnkRemoved = getResources().getString(R.string.ny_snk_msg_removed);
        msgSnkSaved = getResources().getString(R.string.ny_snk_msg_saved);

        tvNewsTitle = findViewById(R.id.tvNewsTitle);
        tvPubDate = findViewById(R.id.tvPubDate);
        tvSource = findViewById(R.id.tvSource);
        tvLeadPara = findViewById(R.id.tvLeadPara);
        tvDocType = findViewById(R.id.tvDocType);
        btnSave = findViewById(R.id.btnSave);
        btnViewLink = findViewById(R.id.btnViewLink);

        title = newsData.getTitle();
        web_url = newsData.getWeb_url();
        lead_paragraph = newsData.getLead_paragraph();
        pub_date = newsData.getPub_date();
        _id = newsData.get_id();
        source = newsData.getSource();
        document_type = newsData.getDocument_type();

        tvNewsTitle.setText(title);
        tvPubDate.setText(pub_date);
        tvDocType.setText(document_type);
        tvSource.setText(source);
        tvLeadPara.setText(lead_paragraph);

        db.open();
        notToAdd = db.isAlreadyAdded(newsData.get_id());
        db.close();

        updateBtnText();

        btnViewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = web_url;
                //If URL is valid then start the implicit intent (for web browser)
                if (url.startsWith("http")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                //Check in the database whether the news article already exists or not
                if (notToAdd) {
                    //if it already exists, then performing delete operation
                    if (db.deleteNews(newsData.get_id())) {
                        //showing snackbar
                        showSnack(msgSnkRemoved);
                        //notifying the NYSavedFragment that the list has to be refreshed!
                        setResult(101);
                        //complementing the flag
                        notToAdd = !notToAdd;
                    }
                } else {
                    long res = db.addNews(newsData);
                    if (res != -1) {
                        //showing snackbar
                        showSnack(msgSnkSaved);
                        //complementing the flag
                        notToAdd = !notToAdd;
                    }
                }
                updateBtnText();//updating text on a button
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
     * Updates the text on the button, if the news article exists in the database,
     * then button text will be for remove, and if not then add
     * */
    void updateBtnText() {
        String btnText = "";
        if (notToAdd) {
            btnText = getResources().getString(R.string.ny_remove);
        } else {
            btnText = getResources().getString(R.string.ny_save);
        }

        btnSave.setText(btnText);
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
                startActivity(new Intent(NYNewsDetailActivity.this, MerriamMainActivity.class));
                return true;
            case R.id.mnuFlightStatus:
                startActivity(new Intent(NYNewsDetailActivity.this, FlightMainActivity.class));
                return true;
            case R.id.mnuNewsFeed:
                startActivity(new Intent(NYNewsDetailActivity.this, NFMainActivity.class));
                return true;
            case R.id.mnuHelp:
                /*
                 showing custom dialog notification
                 */

                final Dialog dialog = new Dialog(NYNewsDetailActivity.this);
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