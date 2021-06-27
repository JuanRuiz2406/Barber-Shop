package com.example.barbershop.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.RecyclerAdapter;
import com.example.barbershop.models.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Screen1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Screen1 extends Fragment implements SearchView.OnQueryTextListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MyActivity";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Appointment> appointmentList;
    private RecyclerView recyclerView;
    private FloatingActionButtonExpandable btnFAB;
    private SearchView search_bar;
    private RecyclerAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference appointmentTable;


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


        //

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
        search_bar = view.findViewById(R.id.search_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setAppointmentInfo();

        btnFAB = view.findViewById(R.id.fab);
        btnFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "List: " + appointmentList.size());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, new Screen2());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        );

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

        return view;
    }

    private void initListener() {
        search_bar.setOnQueryTextListener(this);
    }

    public void setAppointmentInfo() {
        database = FirebaseDatabase.getInstance();
        appointmentTable = database.getReference("Appointment");
        appointmentTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Appointment app = postSnapshot.getValue(Appointment.class);
                    appointmentList.add(app);
                }
                setAdapter();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }

        });
    }



    private void setAdapter() {


        Bundle selectedAppointment = new Bundle();

        Fragment fragment = new Screen2();

        fragment.setArguments(selectedAppointment);

        adapter = new RecyclerAdapter(getContext(), appointmentList);

        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("LONG CLICKKKK", "PRESIONADO LARGO");

                Appointment app = appointmentList.get(recyclerView.getChildAdapterPosition(v));


                String newDate = app.getDate().replaceAll("\\p{Punct}", "");
                String key = newDate + app.getHour().replaceAll("\\s", "");
                Log.e("LONG CLICKKKK", key);
                //aún falta la validación de que se borre si es admin o si es su propio reporte
                appointmentTable.child(key).setValue(null);
                Toast.makeText(getContext(), "Se ha borrado la cita", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedAppointment.putString("date", appointmentList.get(recyclerView.getChildAdapterPosition(view)).getDate());
                selectedAppointment.putString("hour", appointmentList.get(recyclerView.getChildAdapterPosition(view)).getHour());
                selectedAppointment.putString("video_link", appointmentList.get(recyclerView.getChildAdapterPosition(view)).getVideo_link());
                selectedAppointment.putString("photo_link", appointmentList.get(recyclerView.getChildAdapterPosition(view)).getPhoto_link());
                selectedAppointment.putString("client_Name", appointmentList.get(recyclerView.getChildAdapterPosition(view)).getClientName());
                selectedAppointment.putString("status", appointmentList.get(recyclerView.getChildAdapterPosition(view)).getStatus());

                fragment.setArguments(selectedAppointment);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        initListener();


    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }
}