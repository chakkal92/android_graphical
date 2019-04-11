package com.gui.gui.flight_status;

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
 * This fragment shows the list of saved Flight details from the database
 * */
public class FlightSavedFragment extends Fragment {

    private View view;
    FlightAdapter adapter;
    ArrayList<Flight> flights;
    ListView savedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            //inflating the layout
            view = inflater.inflate(R.layout.flight_saved_fragment, container, false);
            savedList = view.findViewById(R.id.savedList);
            fetchData();
            savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), FlightDetailActivity.class);
                    intent.putExtra("FLIGHT",flights.get(position));
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
     * Fetches the saved flight's list from the database
     * */
    void fetchData(){
        FlightDB db = new FlightDB(getActivity());
        db.open();
        //set all the data into an ArrayList
        flights = db.getData();
        db.close();
        //Mapping the ArrayList with the Adapter
        adapter = new FlightAdapter(getActivity(), flights);
        //setting adapter to a listview.
        savedList.setAdapter(adapter);
    }

    /**
     * This function refreshes the listview, if the database entries are modified (deleted).
     * @param resultCode It will be received from the FlightDetailActivity in this
     * @param requestCode It was sent to FlightDetailActivity
     *
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            fetchData();
        }
    }
}
