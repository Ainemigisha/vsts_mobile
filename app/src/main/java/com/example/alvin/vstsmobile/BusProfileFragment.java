package com.example.alvin.vstsmobile;


import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusProfileFragment extends Fragment {


    private ProgressDialog progressDialog;
    Locations locations;

    private TextView latitude_p_text;
    private TextView latitude_p_text_label;
    private TextView longitude_p_text;
    private TextView longitude_p_text_label;
    private TextView speed_p_text;
    private TextView speed_p_text_label;
    private TextView place_p_text;
    private TextView place_p_text_label;
    private TextView time_p_text;
    private TextView time_p_text_label;
    private Button assignButton;



    public BusProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bus_details, container, false);
        Bundle bundle = getArguments();
        locations = (Locations)bundle.getSerializable("location");

        TextView bus_name_text = v.findViewById(R.id.bus_name);
        bus_name_text.setText(locations.getNumber_plate());

        TextView bus_company_text = v.findViewById(R.id.bus_company);
        bus_company_text.setText(locations.getBus_company());

        TextView driver_name_text = v.findViewById(R.id.driver_name);
        driver_name_text.setText(locations.getDriver_name());

        TextView speed_text = v.findViewById(R.id.speed);
        speed_text.setText(locations.getSpeed().toString());

        TextView latitude_text = v.findViewById(R.id.latitude);
        latitude_text.setText(locations.getLatitude().toString());

        TextView longitude_text = v.findViewById(R.id.longitude);
        longitude_text.setText(locations.getLongitude().toString());

        TextView place_text = v.findViewById(R.id.place);
        place_text.setText(getRoadName(locations.getLatitude(),locations.getLongitude()));

        latitude_p_text = v.findViewById(R.id.provisional_latitude);
        latitude_p_text_label = v.findViewById(R.id.provisional_latitude_label);
        longitude_p_text = v.findViewById(R.id.provisional_longitude);
        longitude_p_text_label = v.findViewById(R.id.provisional_longitude_label);
        speed_p_text = v.findViewById(R.id.provisional_speed);
        speed_p_text_label = v.findViewById(R.id.provisional_speed_label);
        place_p_text = v.findViewById(R.id.provisional_place);
        place_p_text_label = v.findViewById(R.id.provisional_place_label);
        time_p_text = v.findViewById(R.id.provisional_time);
        time_p_text_label = v.findViewById(R.id.provisional_time_label);

        assignButton = v.findViewById(R.id.assign);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignPenalty();
            }
        });
        progressDialog = new ProgressDialog(getContext());
        if (locations.getFlag() == 1)
            getProvisionalPenalty(locations.getLf_id());
        else {
            latitude_p_text.setVisibility(View.INVISIBLE);
            latitude_p_text_label.setVisibility(View.INVISIBLE);
            longitude_p_text.setVisibility(View.INVISIBLE);
            longitude_p_text_label.setVisibility(View.INVISIBLE);
            speed_p_text.setVisibility(View.INVISIBLE);
            speed_p_text_label.setVisibility(View.INVISIBLE);
            place_p_text.setVisibility(View.INVISIBLE);
            place_p_text_label.setVisibility(View.INVISIBLE);
            time_p_text.setVisibility(View.INVISIBLE);
            time_p_text_label.setVisibility(View.INVISIBLE);
            assignButton.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void assignPenalty(){
        final Penalty penalty = new Penalty(locations.getId(),locations.getLf_id(),Integer.parseInt(SharedPrefManager.getInstance(getContext()).getoOfficerId()),locations.getLatitude(),locations.getLongitude(),"pending",locations.getSpeed());

        progressDialog.setMessage("Assigning");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ASSIGN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if (jsonObject.getBoolean("success")){
                                progressDialog.dismiss();
                                Fragment fragment = new PenaltiesFragment();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame,fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext()," Error",Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("location_id",""+penalty.getLocation_id());
                params.put("location_finder_id",""+penalty.getLf_id());
                params.put("latitude",""+penalty.getLatitude());
                params.put("longitude",""+penalty.getLongitude());
                params.put("assigner_id",""+penalty.getAssigner_id());
                params.put("status",penalty.getStatus());
                params.put("speed",""+penalty.getSpeed());
                params.put("place",getRoadName(penalty.getLatitude(),penalty.getLongitude()));

                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);



    }

    private String getRoadName(Double latitude, Double longitude){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);

            return obj.getThoroughfare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void getProvisionalPenalty(final int id){

        progressDialog.setMessage("Fetching");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.GET_PROVISIONAL_PENALTY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            latitude_p_text.setText(""+jsonObject.getDouble("latitude"));
                            longitude_p_text.setText(""+jsonObject.getDouble("longitude"));
                            speed_p_text.setText(""+jsonObject.getDouble("speed"));
                            place_p_text.setText(getRoadName(jsonObject.getDouble("latitude"),jsonObject.getDouble("longitude")));
                            time_p_text.setText(""+jsonObject.getString("created_at"));
                            /*JSONArray jsonArray = new JSONArray(response);
                            totalPenaltiesView.setText(""+jsonArray.length());
                            for (int i = 0; i <jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Penalty penalty = new Penalty(jsonObject.getInt("location_id"),jsonObject.getInt("location_finder_id"),jsonObject.getInt("assigned_by"),jsonObject.getDouble("latitude"),jsonObject.getDouble("longitude"),
                                        jsonObject.getString("status"),jsonObject.getDouble("speed"));
                                penalty.setCompany(jsonObject.getJSONObject("location_finder").getJSONObject("bus").getJSONObject("bus_company").getString("company_name"));
                                penalty.setNum_plate(jsonObject.getJSONObject("location_finder").getJSONObject("bus").getString("number_plate"));
                                penalty.setAssigned_at(new SimpleDateFormat("yyyy-mm-dd HH:MM:SS").parse(jsonObject.getString("created_at")));
                                String cleared_date = jsonObject.getString("cleared_date");
                                if (cleared_date != "null") {
                                    penalty.setCleared_at(new SimpleDateFormat("yyyy-mm-dd HH:MM:SS").parse(cleared_date));
                                    penalty.setClearer_name(jsonObject.getJSONObject("clearer").getJSONObject("user").getString("name"));

                                }
                                penalties.add(penalty);


                            }
                            penaltiesAdapter = new PenaltiesAdapter(getContext(),penalties);
                            listView.setAdapter(penaltiesAdapter);*/
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        } /*catch (ParseException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }*/

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getContext()," Error",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",""+id);
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
