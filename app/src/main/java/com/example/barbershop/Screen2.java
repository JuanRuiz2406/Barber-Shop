package com.example.barbershop;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.barbershop.models.Appointment;
import com.example.barbershop.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Screen2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Screen2 extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "Screen2";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView dateTextView ;

    String[] hours = { "8 AM", "9 AM", "10 AM", "11 AM", "1 PM", "2 PM", "3 PM", "4 PM"};

    private FirebaseDatabase database;
    private DatabaseReference userTable;
    private  FirebaseUser currentUser;
    private EditText edit_name ;
    private String hour;
    private Button btnSubmit;
    public Screen2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Screen2.
     */
    // TODO: Rename and change types and number of parameters
    public static Screen2 newInstance(String param1, String param2) {
        Screen2 fragment = new Screen2();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View screen2 = inflater.inflate(R.layout.fragment_screen2, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();
        userTable = database.getReference("Appointment");

        dateTextView = screen2.findViewById(R.id.dateTextView);
        edit_name = screen2.findViewById(R.id.userName);
        btnSubmit = screen2.findViewById(R.id.btnSubmit);

        Spinner spin = (Spinner) screen2.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,hours);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDateButton();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg();
            }
        });

        return screen2;
    }

    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("MM-dd-yyyy", calendar1).toString();

                dateTextView.setText(dateText);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity().getApplicationContext(),hours[position] , Toast.LENGTH_LONG).show();
        hour = hours[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void reg() {

        userTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                User user = snapshot.child(currentUser.getEmail().replaceAll("\\p{Punct}", "")).getValue(User.class);

                Appointment newAppointment = new Appointment(user, edit_name.getText().toString(), dateTextView.getText().toString(),hour , "Pending");

                String newDate = dateTextView.getText().toString().replaceAll("\\p{Punct}", "");
                String key = newDate + hour.replaceAll("\\s","");

                userTable.child(key).setValue(newAppointment);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}
