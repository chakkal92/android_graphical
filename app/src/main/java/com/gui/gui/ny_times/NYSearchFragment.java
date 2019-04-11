package com.gui.gui.ny_times;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.gui.gui.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NYSearchFragment extends Fragment {

    private View view;
    private EditText edtQuery;
    private NYNewsListAdapter listAdapter;

    String msgPleaseWait;
    ArrayList<NYNews> newsData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.ny_search_fragment, container, false);
            msgPleaseWait = getResources().getString(R.string.ny_please_wait);
            edtQuery = view.findViewById(R.id.edtQuery);
            ImageButton btnSearch = view.findViewById(R.id.btnSearch);
            ListView listFlights = view.findViewById(R.id.listNews);
            newsData = new ArrayList<>();
            listAdapter = new NYNewsListAdapter(getActivity(), newsData);
            listFlights.setAdapter(listAdapter);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = edtQuery.getText().toString();
                    if (input.trim().length() == 0) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.ny_sugg_enter), Toast.LENGTH_SHORT).show();
                    } else {
                        new GetData(input).execute();
                    }
                }
            });
            listFlights.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), NYNewsDetailActivity.class);
                    intent.putExtra("NY_NEWS",newsData.get(position));
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

    /***
     * AsyncTask for calling API in background.
     * */
    @SuppressLint("StaticFieldLeak")
    class GetData extends AsyncTask<Void, Void, String> {
        String reqURL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=89kmL9QdZSaSnHNrZtgRuPmf11e3mPQh&q=";
        String query;

        private ProgressDialog dialog;

        /***
         * @param query Input from the user as topic to be searched for news articles
         * */
        GetData(String query) {
            String param = "";
            try {
                this.query = query;
                //Encoding the character(s) for safety
                param = URLEncoder.encode(query, "UTF-8");
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //URL with a topic to be searched
            reqURL = reqURL + param;
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
         * The actual peer to peer communication is performed here.
         * the HttpURLConnection is responsible for communication
         * */
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            URL url = null;
            String response = "";
            String inputLine;
            InputStream inputStream = null;
            try {
                url = new URL(reqURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                inputStream = (InputStream) httpURLConnection.getContent();
                //Reading the response received by calling an API
                InputStreamReader streamReader = new
                        InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Reading the contents from the input stream
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                return stringBuilder.toString();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        /***
         * ProgressDialog will be dismissed in here.
         * JSON parsing is done here.
         * @param s response of an API
         * */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("OK")){
                    newsData.clear();
                    JSONObject response = jsonObject.getJSONObject("response");
                    JSONArray docs = response.getJSONArray("docs");
                    for (int i = 0; i < docs.length(); i++) {
                        JSONObject doc = docs.getJSONObject(i);
                        String web_url = doc.getString("web_url");
                        String lead_paragraph = doc.getString("lead_paragraph");
                        String pub_date = doc.getString("pub_date");
                        JSONObject headline = doc.getJSONObject("headline");
                        String title = headline.getString("main");
                        String _id = doc.getString("_id");
                        String source = doc.getString("source");
                        String document_type = doc.getString("document_type");
                        NYNews news = new NYNews();
                        news.set_id(_id);
                        news.setWeb_url(web_url);
                        news.setLead_paragraph(lead_paragraph);
                        news.setPub_date(pub_date);
                        news.setTitle(title);
                        news.setSource(source);
                        news.setDocument_type(document_type);
                        newsData.add(news);
                    }
                    //updating the ListView data by notifying its adapter
                    listAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}