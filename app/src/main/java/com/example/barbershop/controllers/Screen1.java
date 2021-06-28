package com.example.barbershop.controllers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.activities.LoginActivity;
import com.example.barbershop.adapters.RecyclerAdapter;
import com.example.barbershop.models.Appointment;
import com.example.barbershop.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

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
    private static final String TAG = "MyActivity";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Appointment> appointmentList;
    private RecyclerView recyclerView;
    private FloatingActionButtonExpandable btnFAB;
    private SearchView search_bar;
    private RecyclerAdapter adapter;
    private TextView tvDate;
    private DatePickerDialog.OnDateSetListener setListener;
    private Spinner comboStatus;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference appointmentTable;
    private DatabaseReference userTable;
    private boolean deleteItemConfirmed = false;
    private boolean deleteUserAuth = false;
    private User user;
    private String userKey;
    private String selectedFilter = "all";

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

        tvDate = view.findViewById(R.id.tv_date);
        comboStatus = view.findViewById(R.id.spinnerStatus);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.status, android.R.layout.simple_spinner_item);
        comboStatus.setAdapter(adapter);

        comboStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(),"Selecionado: "+parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                stateFilter(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //barra de busqueda
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Appointment> appointments = new ArrayList<Appointment>();
                for(Appointment a: appointmentList){
                    if(a.getClientName().toLowerCase().contains(newText.toLowerCase())){
                        appointments.add(a);
                    }
                }
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(),appointments);
                recyclerView.setAdapter(recyclerAdapter);

                return false;
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);



        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
            }
        });


        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String mes = String.valueOf(month);
                String dia = String.valueOf(dayOfMonth);
                String date;
                if(month <= 9){
                    mes="0"+mes;
                }
                if(dayOfMonth <= 9){
                    dia = "0"+dia;
                }
                date = mes+"-"+dia+"-"+year;
                tvDate.setText(date);
                Toast.makeText(getContext(),date,Toast.LENGTH_SHORT).show();
                dateFilter(date);
            }

        };

        btnFAB = view.findViewById(R.id.fab);
        btnFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Appointment app = appointmentList.get(recyclerView.getChildAdapterPosition(v));
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                database = FirebaseDatabase.getInstance();
                userTable = database.getReference("User");
                userTable.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if(currentUser == null){

                            Intent loginActivity = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            startActivity(loginActivity);
                            getActivity().finish();

                            Toast.makeText(getActivity().getApplicationContext(), "Necesitamos corroborar que eres tú", Toast.LENGTH_LONG).show();
                        }
                        userKey = currentUser.getEmail().replaceAll("\\p{Punct}", "");
                        user = task.getResult().child(userKey).getValue(User.class);
                        if(app.getUser().equals(userKey) || user.getRole().equals("Administrator")){

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                            alertBuilder.setMessage("Desea eliminar esta cita?").setCancelable(false)
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String newDate = app.getDate().replaceAll("\\p{Punct}", "");
                                            String key = newDate + app.getHour().replaceAll("\\s", "");
                                            appointmentTable.child(key).setValue(null);
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.setTitle("Eliminar Cita");
                            alert.show();
                        } else {
                            Toast.makeText(getContext(), "Solo puedes borrar tus propias citas", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                return false;
            }
        });

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Appointment app = appointmentList.get(recyclerView.getChildAdapterPosition(view));
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                database = FirebaseDatabase.getInstance();
                userTable = database.getReference("User");
                userTable.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if(currentUser == null){

                            Intent loginActivity = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            startActivity(loginActivity);
                            getActivity().finish();

                            Toast.makeText(getActivity().getApplicationContext(), "Necesitamos corroborar que eres tú", Toast.LENGTH_LONG).show();
                        }
                        userKey = currentUser.getEmail().replaceAll("\\p{Punct}", "");
                        user = task.getResult().child(userKey).getValue(User.class);

                        Log.e(TAG, "KEY " + userKey);
                        Log.e(TAG, "USER IN APP " + app.getUser());
                        if(app.getUser().equals(userKey) || user.getRole().equals("Administrator")){
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
                        } else {
                            Toast.makeText(getContext(), "Solo puedes editar tus propias citas", Toast.LENGTH_SHORT).show();
                        }

                    }
                });





            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }


    private void filterList(String status){
        selectedFilter = status;
        ArrayList<Appointment> filterAppointment = new ArrayList<Appointment>();

        for(Appointment a: appointmentList){
            if(a.getClientName().toLowerCase().contains(status)){
                filterAppointment.add(a);
            }
        }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(),filterAppointment);
        recyclerView.setAdapter(recyclerAdapter);

    }
    private void filterListState(String status){
        selectedFilter = status;
        ArrayList<Appointment> filterAppointment = new ArrayList<Appointment>();

        for(Appointment a: appointmentList){
            if(a.getStatus().toLowerCase().contains(status.toLowerCase())){
                filterAppointment.add(a);
            }
        }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(),filterAppointment);
        recyclerView.setAdapter(recyclerAdapter);

    }
    private void filterListDate(String status){
        selectedFilter = status;
        ArrayList<Appointment> filterAppointment = new ArrayList<Appointment>();

        for(Appointment a: appointmentList){
            if(a.getDate().toLowerCase().contains(status.toLowerCase())){
                filterAppointment.add(a);
            }
        }
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(),filterAppointment);
        recyclerView.setAdapter(recyclerAdapter);

    }

    public void allFilter(String strFilter){
        selectedFilter = "all";
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getContext(),appointmentList);
        recyclerView.setAdapter(recyclerAdapter);

    }
    public void stateFilter(String strFilter){
        filterListState(strFilter);

    }

    public void dateFilter(String strFilter){
        filterListDate(strFilter);
    }
}