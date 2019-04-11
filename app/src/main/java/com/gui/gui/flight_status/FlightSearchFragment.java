package com.gui.gui.flight_status;

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
import android.widget.Spinner;
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

/**
 * This fragment is shown at first to the user, with the initialization of FlightMainActivity
 *
 * This fragment is responsible for calling the API
 * and retrieve back the result and display result onto a listview.
 * */
public class FlightSearchFragment extends Fragment {

    private View view;
    private EditText edtQuery;
    private Spinner spinner;
    private FlightAdapter listAdapter;
    ArrayList<Flight> flightArrayList;

    String pleaseWaitString;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.flight_search_fragment, container, false);
            edtQuery = view.findViewById(R.id.edtQuery);
            spinner = view.findViewById(R.id.spinner);
            ImageButton btnSearch = view.findViewById(R.id.btnSearch);
            ListView listFlights = view.findViewById(R.id.listFlight);

            pleaseWaitString = getActivity().getResources().getString(R.string.flight_please_wait);

            flightArrayList = new ArrayList<>();
            listAdapter = new FlightAdapter(getActivity(), flightArrayList);
            listFlights.setAdapter(listAdapter);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = edtQuery.getText().toString();
                    if (input.trim().length() == 0) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.flight_sugg_enter_keyword), Toast.LENGTH_SHORT).show();
                    } else {
                        if(spinner.getSelectedItemPosition() == 0){
                            Toast.makeText(getActivity(), getResources().getString(R.string.flight_sugg_select_type), Toast.LENGTH_SHORT).show();
                        }else if(spinner.getSelectedItemPosition() == 1){
                            new GetData(input, "Arrival").execute();
                        }else if(spinner.getSelectedItemPosition() == 2){
                            new GetData(input, "Departure").execute();
                        }
                    }
                }
            });
            listFlights.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), FlightDetailActivity.class);
                    intent.putExtra("FLIGHT",flightArrayList.get(position));
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
        String reqURL = "http://aviation-edge.com/v2/public/flights?key=71e182-5ec3d9&";
        String aCode, type;

        private ProgressDialog dialog;

        /***
         * initialize the local variables
         * @param aCode airport code
         * @param type Departure or arrival
         * */
        GetData(String aCode, String type) {
            String param = "";
            try {
                this.aCode = aCode;
                this.type = type;
                //Encoding the character(s) for safety
                param = URLEncoder.encode(aCode, "UTF-8");
                if (type.equals("Arrival")) {
                    param = "arrIata=" + param;
                } else if (type.equals("Departure")) {
                    param = "depIata=" + param;
                }
                //initializing for progress dialog
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //URL with a word to be searched
            reqURL = reqURL + param;
        }

        /***
         * Displays the progress dialog with message please wait.
         * User won't be able to interact with the UI until it got dismissed.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(pleaseWaitString);
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
                //Reading the contents from the input stream
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
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
                flightArrayList.clear();
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String status = jsonObject.getString("status");
                    JSONObject flightObject = jsonObject.getJSONObject("flight");
                    String number = flightObject.getString("number");
                    String iataNumber = flightObject.getString("iataNumber");
                    String icaoNumber = flightObject.getString("icaoNumber");
                    JSONObject geography = jsonObject.getJSONObject("geography");
                    double latitude = geography.getDouble("latitude");
                    double longitude = geography.getDouble("longitude");
                    double altitude = geography.getDouble("altitude");
                    double direction = geography.getDouble("direction");
                    JSONObject speed = jsonObject.getJSONObject("speed");
                    double horizontal = speed.getDouble("horizontal");
                    String dIATA = jsonObject.getJSONObject("departure").getString("iataCode");
                    String aIATA = jsonObject.getJSONObject("arrival").getString("iataCode");
                    Flight flight = new Flight();
                    flight.setStatus(status);
                    flight.setNumber(number);
                    flight.setIataNumber(iataNumber);
                    flight.setIcaoNumber(icaoNumber);
                    flight.setLatitude(latitude);
                    flight.setLongitude(longitude);
                    flight.setAltitude(altitude);
                    flight.setDirection(direction);
                    flight.setHorizontal(horizontal);
                    flight.setaIATA(aIATA);
                    flight.setdIATA(dIATA);
                    flightArrayList.add(flight);
                }
                //updating the ListView data by notifying its adapter
                listAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}