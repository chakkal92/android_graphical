package com.gui.gui.news_feed;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.gui.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NFSearchFragment extends Fragment {

    private View view;
    private EditText edtQuery;
    private TextView tvLastSearched;
    private ArrayList<NFNewsData> newsData;
    private NFNewsTitlesAdapter listAdapter;

    String msgPleaseWait;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            sharedPreferences = getActivity().getSharedPreferences("LAST_PREF", Context.MODE_PRIVATE);
            view = inflater.inflate(R.layout.nf_search_fragment, container, false);
            msgPleaseWait = getResources().getString(R.string.nf_please_wait);
            edtQuery = view.findViewById(R.id.edtQuery);
            tvLastSearched = view.findViewById(R.id.tvLastSearched);
            ImageButton btnSearch = view.findViewById(R.id.btnSearch);
            ListView listNews = view.findViewById(R.id.listNews);
            newsData = new ArrayList<>();
            getLastSearched();
            listAdapter = new NFNewsTitlesAdapter(newsData, getActivity());
            listNews.setAdapter(listAdapter);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = edtQuery.getText().toString();
                    if (input.trim().length() == 0) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.nf_sugg_enter_keyword), Toast.LENGTH_SHORT).show();
                    } else {
                        sharedPreferences.edit().putString("INPUT", input).apply();
                        new GetData(input).execute();
                    }
                }
            });
            listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), NFNewsDetailActivity.class);
                    intent.putExtra("NEWS", newsData.get(position));
                    getActivity().startActivity(intent);
                }
            });
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    class GetData extends AsyncTask<Void, Void, String> {
        String newsURL = "http://webhose.io/filterWebContent?token=86940a5c-b094-4465-942e-81ce096fe5c9&format=xml&sort=crawled&q=";
        String query;

        private ProgressDialog dialog;

        /***
         * @param query topic to be searched in API
         * if the topic is not in the safe URL form,
         * then constructor will fix it by URLEncoder.encode();
         * */
        GetData(String query) {
            try {
                //Encoding the characters
                this.query = URLEncoder.encode(query, "UTF-8");
                //initializing for progress dialog
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //URL with a news topic to be searched
            newsURL = newsURL + this.query;
        }

        /***
         * Displays the progress dialog with message please wait.
         * User won't be able to interact with the UI until it got dismissed.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(msgPleaseWait);
            dialog.show();
        }

        /***
         * XML parsing is done in here.
         * The actual peer to peer communication is performed here.
         * */
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            URL url = null;
            String response = "";
            String inputLine;
            InputStream inputStream = null;
            try {
                url = new URL(newsURL);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(url.openConnection().getInputStream(), "UTF_8");

                boolean insideResult = false;
                boolean insidePosts = false;
                boolean insidePost = false;
                boolean insideThread = false;

                NFNewsData news = null;
                newsData.clear();

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tag = xpp.getName();
                    switch (eventType){
                        case XmlPullParser.START_TAG:
                        if(tag != null && tag.equals("results")){
                            insideResult = true;
                        }else if(tag != null && tag.equals("posts")){
                            if(insideResult){
                                insidePosts = true;
                            }
                        }else if(tag != null && tag.equals("post")){
                            if(insideResult && insidePosts){
                                insidePost = true;
                                news = new NFNewsData();
                            }
                        }else if(tag != null && tag.equals("thread")){
                            if(insidePost){
                                insideThread = true;
                            }
                        }else if(tag != null && tag.equals("uuid")){
                            if(insideThread){
                                String text = xpp.nextText();
                                Log.d("Scanning","uuid: " + text);
                                news.setUuid(text);
                            }
                        }else if(tag != null && tag.equals("url")){
                            if(insidePost){
                                String text = xpp.nextText();
                                Log.d("Scanning","url: " + text);
                                news.setUrl(text);
                            }
                        }else if(tag != null && tag.equals("author")){
                            if(insidePost){
                                String text = xpp.nextText();
                                Log.d("Scanning","author: " + text);
                                news.setAuthor(text);
                            }
                        }else if(tag != null && tag.equals("published")){
                            if(insidePost){
                                String text = xpp.nextText();
                                Log.d("Scanning","published: " + text);
                                news.setPublished(text);
                            }
                        }else if(tag != null && tag.equals("title")){
                            if(insideThread){
                                String text = xpp.nextText();
                                Log.d("Scanning","title: " + text);
                                news.setTitle(text);
                            }
                        }else if(tag != null && tag.equals("text")){
                            if(insidePost){
                                news.setText(xpp.nextText());
                            }
                        }else if(tag != null && tag.equals("language")){
                            if(insidePost){
                                String text = xpp.nextText();
                                Log.d("Scanning","language: " + text);
                                news.setLanguage(text);
                            }
                        }
                        break;
                        case XmlPullParser.END_TAG:
                            if(tag != null && tag.equals("results")){
                                insideResult = false;
                            }else if(tag != null && tag.equals("posts")){
                                if(insideResult){
                                    insidePosts = false;
                                }
                            }else if(tag != null && tag.equals("post")){
                                if(insideResult && insidePosts){
                                    insidePost = false;
                                    newsData.add(news);
                                }
                            }else if(tag != null && tag.equals("thread")){
                                if(insidePost){
                                    insideThread = false;
                                }
                            }
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        /***
         * ProgressDialog will be dismissed in here.
         * */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //updating the ListView data by notifying its adapter
            listAdapter.notifyDataSetChanged();
            getLastSearched(); //getting last item searched
        }
    }

    /***
     * Retrieves the last searched item from the shared-preferences
     * and sets the text to the TextView
     * */
    void getLastSearched() {
        String ip = sharedPreferences.getString("INPUT", "");
        tvLastSearched.setText(ip);
    }
}
