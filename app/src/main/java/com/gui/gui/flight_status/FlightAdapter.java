package com.gui.gui.flight_status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.gui.R;

import java.util.ArrayList;


/***
 * Custom list adapter class
 * Helps to implement a ListView with a custom layout
 * Used for displaying Flight list on FlightSearchFragment and FlightSavedFragment
 * */
public class FlightAdapter extends BaseAdapter {

    Context context;
    ArrayList<Flight> flights;
    static LayoutInflater inflater;

    /***
     * @param context context of an activity to get layout inflater service
     * @param flights List of an instance of Flight class
     * */
    public FlightAdapter(Context context, ArrayList<Flight> flights) {
        this.context = context;
        this.flights = flights;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return flights.size();
    }

    @Override
    public Object getItem(int position) {
        return flights.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.flight_item_flight, parent, false);
            holder.tvFlightNo = convertView.findViewById(R.id.tvFlightNo);
            holder.tvaIATA = convertView.findViewById(R.id.tvaIATA);
            holder.tvdIATA = convertView.findViewById(R.id.tvdIATA);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            holder.tvFlightIATA = convertView.findViewById(R.id.tvFlightIATA);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Flight flight = (Flight) getItem(position);
        String status = flight.getStatus();
        String aIATA = flight.getaIATA();
        String dIATA = flight.getdIATA();
        String flightNo = flight.getNumber();
        String flightIATA = flight.getIataNumber();
        holder.tvStatus.setText(status);
        holder.tvaIATA.setText(aIATA);
        holder.tvdIATA.setText(dIATA);
        holder.tvFlightNo.setText(flightNo);
        holder.tvFlightIATA.setText(flightIATA);
        return convertView;
    }

    /***
     * ViewHolder pattern
     * */
    static class ViewHolder{
        TextView tvFlightNo, tvaIATA, tvdIATA, tvStatus, tvFlightIATA;
    }
}
