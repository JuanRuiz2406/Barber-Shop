package com.example.barbershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.barbershop.models.Appointment;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Screen1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Screen1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Appointment> appointmentList;
    private RecyclerView recyclerView;
    private FloatingActionButtonExpandable btnFAB;

    public Screen1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Screen1.
     */
    // TODO: Rename and change types and number of parameters
    public static Screen1 newInstance(String param1, String param2) {
        Screen1 fragment = new Screen1();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        appointmentList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.screen1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnFAB = view.findViewById(R.id.fab);
        btnFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, new Screen2());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    btnFAB.collapse(true);
                else
                    btnFAB.expand(true);

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //setAppointmentInfo();
        setAdapter();

        return view;
    }

    /*
    public void setAppointmentInfo() {
        appointmentList.add(new Appointment("Juan", "Hoy", "Realizada"));
        appointmentList.add(new Appointment("Pepe", "Mañana", "Pendiente"));
        appointmentList.add(new Appointment("Juan", "Hoy", "Realizada"));
        appointmentList.add(new Appointment("Pepe", "Mañana", "Pendiente"));
        appointmentList.add(new Appointment("Juan", "Hoy", "Realizada"));
        appointmentList.add(new Appointment("Pepe", "Mañana", "Pendiente"));
        appointmentList.add(new Appointment("Juan", "Hoy", "Realizada"));
        appointmentList.add(new Appointment("Pepe", "Mañana", "Pendiente"));
        appointmentList.add(new Appointment("Juan", "Hoy", "Realizada"));
        appointmentList.add(new Appointment("Pepe", "Mañana", "Pendiente"));
    }

     */

    private void setAdapter() {
        RecyclerAdapter adapter = new RecyclerAdapter(appointmentList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

}