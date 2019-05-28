package com.example.alvin.vstsmobile;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class PenaltyProfileFragment extends Fragment {
    private ProgressDialog progressDialog;
    Penalty penalty;

    public PenaltyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_penalty_profile, container, false);

        Bundle bundle = getArguments();
        penalty = (Penalty) bundle.getSerializable("penalty");

        TextView bus_name_text = v.findViewById(R.id.p_num_plate);
        bus_name_text.setText(penalty.getNum_plate());

        TextView bus_company_text = v.findViewById(R.id.p_bus_company);
        bus_company_text.setText(penalty.getCompany());



        TextView speed_text = v.findViewById(R.id.p_speed);
        speed_text.setText(penalty.getSpeed().toString());

        TextView latitude_text = v.findViewById(R.id.p_latitude);
        latitude_text.setText(penalty.getLatitude().toString());

        TextView longitude_text = v.findViewById(R.id.p_longitude);
        longitude_text.setText(penalty.getLongitude().toString());

        TextView status = v.findViewById(R.id.p_status);
        status.setText(penalty.getStatus());


        TextView time = v.findViewById(R.id.p_time);
        time.setText(new SimpleDateFormat("yyyy-mm-dd").format(penalty.getAssigned_at()));

        String cleared_by = penalty.getClearer_name();

        if (cleared_by != null){
            TextView clearer = v.findViewById(R.id.p_cleared_by);
            clearer.setText(cleared_by);


            TextView clear_date = v.findViewById(R.id.p_clearance_date);
            clear_date.setText(new SimpleDateFormat("dd/MM/yyyy").format(penalty.getCleared_at()));
        }




        return v;
    }

}
