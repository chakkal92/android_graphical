package com.gui.gui.merriam;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gui.gui.R;

import java.util.ArrayList;

/**
 * This fragment shows the list of saved words data from the database
 * */
public class MerriamSavedFragment extends Fragment {

    private View view;
    WordListAdapter adapter;
    ArrayList<Word> words;
    ListView savedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            //inflating the layout
            view = inflater.inflate(R.layout.merriam_saved_fragment, container, false);
            savedList = view.findViewById(R.id.savedList);
            fetchData();
            savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), MerriamDetailActivity.class);
                    intent.putExtra("WORD_DATA", words.get(position));
                    //for getting back the result, whether it has to update the list items or not
                    startActivityForResult(intent,100);
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
     * Fetches the word list from the database
     * */
    void fetchData(){
        WordDB db = new WordDB(getActivity());
        db.open();
        //set all the data into an ArrayList
        words = db.getAllWords();
        db.close();
        //Mapping the ArrayList with the Adapter
        adapter = new WordListAdapter(getActivity(), words);
        //setting adapter to a listview.
        savedList.setAdapter(adapter);
    }

    /**
     * This function refreshes the listview, if the database entries are modified (deleted).
     * @param resultCode It will be received from the MerriamDetailActivity in this
     * @param requestCode It was sent to MerriamDetailActivity
     *
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            fetchData(); //fetching data again from the database and refreshing the listview
        }
    }
}
