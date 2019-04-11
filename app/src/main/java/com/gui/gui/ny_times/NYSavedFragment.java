package com.gui.gui.ny_times;

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

public class NYSavedFragment extends Fragment {

    private View view;
    NYNewsListAdapter adapter;
    ArrayList<NYNews> newsData;
    ListView savedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.ny_saved_fragment, container, false);
            savedList = view.findViewById(R.id.savedList);
            fetchData();
            savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), NYNewsDetailActivity.class);
                    intent.putExtra("NY_NEWS",newsData.get(position));
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
     * Fetches the news article list from the database
     * */
    void fetchData(){
        NYNewsDB db = new NYNewsDB(getActivity());
        db.open();
        //set all the data into an ArrayList
        newsData = db.getData();
        db.close();
        //Mapping the ArrayList with the Adapter
        adapter = new NYNewsListAdapter(getActivity(), newsData);
        //setting adapter to a listview.
        savedList.setAdapter(adapter);
    }

    /***
     * This function refreshes the listview, if the database entries are modified (deleted).
     * @param resultCode It will be received from the NYNewsDetailActivity in this
     * @param requestCode It was sent to NYNewsDetailActivity
     *
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            fetchData(); //refreshing the listview
        }
    }
}
