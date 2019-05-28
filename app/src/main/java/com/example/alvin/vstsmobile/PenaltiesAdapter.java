package com.example.alvin.vstsmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PenaltiesAdapter extends ArrayAdapter<Penalty> implements AdapterView.OnItemClickListener, View.OnClickListener {

    private Context mContext;

    private ArrayList<Penalty> penalties;


    public PenaltiesAdapter(Context context, ArrayList<Penalty> penalties){
        super(context,0,penalties);
        mContext = context;

    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.row_penalty,parent,false);

        Penalty penalty = getItem(position);

        TextView num_plate = listItem.findViewById(R.id.r_num_plate);
        num_plate.setText(penalty.getNum_plate());

        TextView bus_company = listItem.findViewById(R.id.r_company);
        bus_company.setText(penalty.getCompany());

        TextView speed = listItem.findViewById(R.id.r_speed);
        speed.setText(Double.toString(penalty.getSpeed()));



        listItem.setTag(penalty);
        listItem.setOnClickListener(this);








        return listItem;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        Penalty penalty = (Penalty)v.getTag();

        Bundle bundle = new Bundle();
        bundle.putSerializable("penalty",penalty);

        Fragment fragment = new PenaltyProfileFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
