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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.barbershop.R;
import com.example.barbershop.UI.ViewImageExtended;
import com.example.barbershop.models.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

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

    String[] hours = {"Elegir", "8 AM", "9 AM", "10 AM", "11 AM", "1 PM", "2 PM", "3 PM", "4 PM"};
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;
    private DatabaseReference appointmentTable;
    private FirebaseUser currentUser;
    private TextView dateTextView;
    private EditText edit_name;
    private String hour;
    private Button btnSubmit;
    private Button btnPhoto;
    private Button btnVideo;
    private ImageView imgView;
    private VideoView videoView;
    private Uri imageUri;
    private Uri videoUri;

    private String photo_link;
    private String video_link;

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

        btnPhoto = screen2.findViewById(R.id.btnPhoto);
        btnVideo = screen2.findViewById(R.id.btnVideo);
        imgView = screen2.findViewById(R.id.imageView);
        videoView = screen2.findViewById(R.id.playVideo);


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


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeVideo();
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


    private void takePhoto() {
        String fileName = "new-photo.jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 200);
    }

    public void takeVideo() {
        String fileName = "new-video.mp4";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Video capture by camera");

        videoUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        startActivityForResult(intent, 201);
    }

    public void reg() {

        String sUsername = edit_name.getText().toString();

        if (dateTextView.getText().toString().isEmpty() || hour == "Elegir" || sUsername.matches("") || imageUri == null) {

            Toast.makeText(getContext(), "Por favor llena los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("Appointment");

        final StorageReference file_name1 = Folder.child("img" + imageUri.getLastPathSegment());

        ProgressDialog mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Por favor espere...");
        mDialog.show();

        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("video/mp4").build();

        file_name1.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
                            photo_link = task.getResult().toString();

                            if (videoUri != null) {

                                final StorageReference file_name2 = Folder.child("video" + videoUri.getLastPathSegment());

                                file_name2.putFile(videoUri, metadata).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                                    video_link = task.getResult().toString();
                                                    Appointment newAppointment = new Appointment(currentUser.getEmail().replaceAll("\\p{Punct}", ""), edit_name.getText().toString(), dateTextView.getText().toString(), hour, "Pending", photo_link, video_link);

                                                    String newDate = dateTextView.getText().toString().replaceAll("\\p{Punct}", "");
                                                    String key = newDate + hour.replaceAll("\\s", "");

                                                    appointmentTable.child(key).setValue(newAppointment);
                                                }
                                            });

                                            mDialog.dismiss();

                                            Toast.makeText(getContext(), "Cita registrada con exito :D", Toast.LENGTH_SHORT).show();

                                            getFragmentManager().popBackStack();

                                            getActivity().recreate();
                                        }

                                    }
                                });

                            } else {
                                video_link = "NO_VIDEO";
                                Appointment newAppointment = new Appointment(currentUser.getEmail().replaceAll("\\p{Punct}", ""), edit_name.getText().toString(), dateTextView.getText().toString(), hour, "Pending", photo_link, video_link);

                                String newDate = dateTextView.getText().toString().replaceAll("\\p{Punct}", "");
                                String key = newDate + hour.replaceAll("\\s", "");

                                appointmentTable.child(key).setValue(newAppointment);

                                mDialog.dismiss();

                                Toast.makeText(getContext(), "Cita registrada con exito :D", Toast.LENGTH_SHORT).show();

                                getFragmentManager().popBackStack();

                                getActivity().recreate();

                            }


                        }
                    });


                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 200) {
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
            } else {
                videoView.setVideoURI(videoUri);
                videoView.start();
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
        // Toast.makeText(getActivity().getApplicationContext(), hours[position], Toast.LENGTH_LONG).show();
        hour = hours[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void askPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 60);
        } else {
            takePhoto();
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
            takePhoto();
        }
    }
}
