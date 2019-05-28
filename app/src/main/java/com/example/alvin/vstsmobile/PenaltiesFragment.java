package com.example.alvin.vstsmobile;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PenaltiesFragment extends Fragment {

    private PenaltiesAdapter penaltiesAdapter;
    private ArrayList<Penalty> penalties;
    private ProgressDialog progressDialog;
    private ListView listView;
    private TextView totalPenaltiesView;

    public PenaltiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_penalties, container, false);
        progressDialog = new ProgressDialog(getContext());
        penalties = new ArrayList<>();
        listView = v.findViewById(R.id.penalties_list);
        totalPenaltiesView = v.findViewById(R.id.total_penalties_assigned);
        String police_id =  SharedPrefManager.getInstance(getContext()).getoOfficerId();
        getPenalties(police_id);

        return v;
    }

    private void getPenalties(final String police_id){

        progressDialog.setMessage("Fetching");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.GET_PENALTIES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(response);
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
                            listView.setAdapter(penaltiesAdapter);
                             progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

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
                params.put("id",police_id);
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

}
