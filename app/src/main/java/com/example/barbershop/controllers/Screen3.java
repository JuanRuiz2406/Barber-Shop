package com.example.barbershop.controllers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.barbershop.R;
import com.example.barbershop.adapters.ExpandableAdapter;
import com.example.barbershop.models.Appointment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Screen3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Screen3 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    ArrayList<Appointment> appointmentList;
    ExpandableAdapter expandableAdapter;

    public Screen3() {
        // Required empty public constructor
    }

    public static Screen3 newInstance(String param1, String param2) {
        Screen3 fragment = new Screen3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_screen3, container, false);

        appointmentList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.screen3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        setAppointmentsInfo();
        setAdapter();

        return view;
    }

    public void setAppointmentsInfo() {

        appointmentList.add(new Appointment("user", "Juan", "23/06/2021","5:00pm",  "Realizada", "img", "video"));
        appointmentList.add(new Appointment("user", "Juan", "23/06/2021","5:00pm",  "Realizada", "img", "video"));
        appointmentList.add(new Appointment("user", "Juan", "23/06/2021","5:00pm",  "Realizada", "img", "video"));
        appointmentList.add(new Appointment("user", "Juan", "23/06/2021","5:00pm",  "Realizada", "img", "video"));

    }

    private void setAdapter() {
        ExpandableAdapter adapter = new ExpandableAdapter(getContext(), appointmentList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}