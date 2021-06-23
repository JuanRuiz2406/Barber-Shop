package com.example.barbershop.controllers;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.barbershop.R;
import com.example.barbershop.UI.ViewImageExtended;
import com.example.barbershop.activities.LoginActivity;
import com.example.barbershop.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Screen2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Screen2 extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ViewImageExtended viewImageExtended;
    public Bitmap bitmap = null;

    String[] hours = {"8 AM", "9 AM", "10 AM", "11 AM", "1 PM", "2 PM", "3 PM", "4 PM"};
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;
    private DatabaseReference appointmentTable;
    private FirebaseUser currentUser;
    private TextView dateTextView;
    private EditText edit_name;
    private String hour;
    private Button btnSubmit;
    private Button btnCamara;
    private ImageView imgView;
    private Uri imageUri;

    public Screen2() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Screen2.
     */
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
        appointmentTable = database.getReference("Appointment");

        dateTextView = screen2.findViewById(R.id.dateTextView);
        edit_name = screen2.findViewById(R.id.userName);
        btnSubmit = screen2.findViewById(R.id.btnSubmit);

        btnCamara = screen2.findViewById(R.id.btnCamara);
        imgView = screen2.findViewById(R.id.imageView);

        Spinner spin = screen2.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, hours);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);


        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewImageExtended == null || viewImageExtended.getDialog() == null || !viewImageExtended.getDialog().isShowing()) {

                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    // Aqui le pasas el bitmap de la imagen
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("PROFILE_PICTURE", bitmap);

                    viewImageExtended = ViewImageExtended.newInstance(arguments);
                    viewImageExtended.show(fm, "ViewImageExtended");
                }
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDateButton();
            }
        });


        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamara();
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


    private void openCamara() {
        String fileName = "new-photo-name.jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1231);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1231 && resultCode == Activity.RESULT_OK) {
            try {
                ContentResolver cr = getActivity().getContentResolver();
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);

                    imgView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                if (e.getMessage() != null)
                    Log.e("Exception", e.getMessage());
                else
                    Log.e("Exception", "Exception");
                e.printStackTrace();
            }

        }
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

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        calendar.add(Calendar.DAY_OF_MONTH, 30);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity().getApplicationContext(), hours[position], Toast.LENGTH_LONG).show();
        hour = hours[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void reg() {


        if(dateTextView.getText().toString().isEmpty() || hour.isEmpty() || edit_name.toString().isEmpty() || imageUri == null) {

            Toast.makeText(getContext(), "Por favor llena los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Por favor espere...");
        mDialog.show();

        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("Appointment");

        final StorageReference file_name = Folder.child("file" + imageUri.getLastPathSegment());

        file_name.putFile(imageUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {

            Appointment newAppointment = new Appointment(currentUser.getEmail().replaceAll("\\p{Punct}", ""), edit_name.getText().toString(), dateTextView.getText().toString(), hour, "Pending", String.valueOf(uri), "NO_VIDEO");

            String newDate = dateTextView.getText().toString().replaceAll("\\p{Punct}", "");
            String key = newDate + hour.replaceAll("\\s", "");

            appointmentTable.child(key).setValue(newAppointment);

            Log.d("Message", "Everything was uploaded correctly");

            mDialog.dismiss();

            Toast.makeText(getContext(), "Cita registrada con exito :D", Toast.LENGTH_SHORT).show();


            getFragmentManager().popBackStack();

            getActivity().recreate();

        }));
    }

    public void askPermission() {
        if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 60);
        } else {
            openCamara();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allPermissionsGranted = false;

        if (requestCode == 60 && grantResults.length > 0) {

            allPermissionsGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }

        if (allPermissionsGranted) {
            openCamara();
        }
    }
}
