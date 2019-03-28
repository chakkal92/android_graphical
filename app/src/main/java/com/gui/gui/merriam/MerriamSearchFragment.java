package com.gui.gui.merriam;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.gui.gui.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This fragment is shown at first to the user, with the initialization of MerriamMainActivity
 *
 * This fragment is responsible for calling the API
 * and retrieve back the result and display result onto a listview.
 * */
public class MerriamSearchFragment extends Fragment {

    private View view;
    EditText edtQuery;
    ListView listWords;

    ArrayList<Word> wordArrayList;
    WordListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.merriam_search_fragment, container, false);
            edtQuery = view.findViewById(R.id.edtQuery);
            ImageButton btnSearch = view.findViewById(R.id.btnSearch);
            listWords = view.findViewById(R.id.listWords);

            wordArrayList = new ArrayList<>();
            listAdapter = new WordListAdapter(getActivity(), wordArrayList);
            listWords.setAdapter(listAdapter);

            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = edtQuery.getText().toString().trim();
                    if (query.length() == 0) {
                        Toast.makeText(getActivity(), "Please enter word to get results!", Toast.LENGTH_SHORT).show();
                    } else {
                        new GetData(query).execute();
                    }
                }
            });

            listWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(getActivity(), MerriamDetailActivity.class)
                    .putExtra("WORD_DATA", wordArrayList.get(position)));
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

    /**
     * AsyncTask for calling API in background.
     * */
    private class GetData extends AsyncTask<Void, Void, String> {

        String wordURL;

        private ProgressDialog dialog;

        /***
         * @param word word to be searched in API
         * if the word is not in the safe URL form,
         * then constructor will fix it by URLEncoder.encode();
         * */
        GetData(String word) {
            try {
                //Encoding the characters
                word = URLEncoder.encode(word, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //initializing for progress dialog
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            //URL with a word to be searched
            wordURL = "https://www.dictionaryapi.com/api/v1/references/sd3/xml/" + word + "?key=4556541c-b8ed-4674-9620-b6cba447184f";
        }

        /***
         * Displays the progress dialog with message please wait.
         * User won't be able to interact with the UI until it got dismissed.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String message = getResources().getString(R.string.merriam_message_wait);
            dialog.setMessage(message);
            dialog.show();
        }

        /***
         * XML parsing is done in here.
         * The actual peer to peer communication is performed here.
         * */
        @Override
        protected String doInBackground(Void... voids) {
            URL url = null;
            try {
                url = new URL(wordURL);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                //Reading the content received by calling an API
                Document doc = dBuilder.parse(url.openConnection().getInputStream());
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("entry");

                String hw, pr, fl;
                //Initialing all the objects for preparing to save word data
                ArrayList<String> defs = new ArrayList<>();
                wordArrayList.clear();
                //Reading the XML tags inside <entry>
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        Word word = new Word();
                        defs.clear();
                        //reading all the <dt> tags inside <entry> and putting them into a list
                        for (int i = 0; i < eElement
                                .getElementsByTagName("dt").getLength(); i++) {
                            defs.add((i+1) + " "+eElement
                                    .getElementsByTagName("dt").item(i).getTextContent());
                        }
                        hw = eElement
                                .getElementsByTagName("hw")
                                .item(0)
                                .getTextContent();
                        fl = eElement
                                .getElementsByTagName("fl")
                                .item(0)
                                .getTextContent();
                        pr = eElement
                                .getElementsByTagName("pr")
                                .item(0)
                                .getTextContent();
                        word.setFl(fl);
                        word.setHw(hw);
                        word.setPr(pr);
                        word.setDt(defs);
                        wordArrayList.add(word);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
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
        }
    }
}
