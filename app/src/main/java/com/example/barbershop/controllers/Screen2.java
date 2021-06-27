package com.example.barbershop.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.barbershop.R;
import com.example.barbershop.UI.ViewImageExtended;
import com.example.barbershop.models.Appointment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    String[] hours = {"Hora", "8 AM", "9 AM", "10 AM", "11 AM", "1 PM", "2 PM", "3 PM", "4 PM"};
    LinearLayout statusL;
    LinearLayout statusA;
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;
    private DatabaseReference appointmentTable;
    private FirebaseUser currentUser;
    private TextView dateTextView;
    private TextView titleTextView;
    private EditText edit_name;
    private String hour;
    private Button btnSubmit;
    private Button btnDelete;
    private Button btnPhoto;
    private Button btnVideo;
    private Button btnDown;
    private ImageView imgView;
    private VideoView videoView;
    private Uri imageUri;
    private Uri videoUri;
    private String photo_link;
    private String video_link;
    private ArrayList<Appointment> appointmentList;
    private Bundle editData;
    private ArrayAdapter hourAdapter;
    private CheckBox checkboxStatus;
    private CheckBox checkboxStatus2;
    private CheckBox checkboxStatus3;
    private String status = "PENDIENTE";

    private String msg = "Cita registrada con exito :D";

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

        editData = getArguments();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();
        appointmentTable = database.getReference("Appointment");

        titleTextView = screen2.findViewById(R.id.titleTextView);
        dateTextView = screen2.findViewById(R.id.dateTextView);
        edit_name = screen2.findViewById(R.id.userName);
        btnSubmit = screen2.findViewById(R.id.btnSubmit);
        btnDelete = screen2.findViewById(R.id.btnDelete);

        btnPhoto = screen2.findViewById(R.id.btnPhoto);
        btnVideo = screen2.findViewById(R.id.btnVideo);
        btnDown = screen2.findViewById(R.id.btnDown);
        imgView = screen2.findViewById(R.id.imageView);
        videoView = screen2.findViewById(R.id.playVideo);
        checkboxStatus = screen2.findViewById(R.id.checkBox);
        checkboxStatus2 = screen2.findViewById(R.id.checkBox2);
        checkboxStatus3 = screen2.findViewById(R.id.checkBox3);
        statusL = screen2.findViewById(R.id.statusL);
        statusA = screen2.findViewById(R.id.statusA);

        Spinner spin = screen2.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        if (editData != null) {

            msg = "Cita actualizada con exito :D";
            statusL.setVisibility(View.VISIBLE);
            statusA.setVisibility(View.VISIBLE);

            titleTextView.setText("EDITAR CITA");
            titleTextView.setTextColor(Color.parseColor("#F46426"));

            btnSubmit.setText("EDITAR CITA");
            btnSubmit.setBackgroundColor(Color.parseColor("#F46426"));
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setBackgroundColor(Color.parseColor("#EA3722"));
            dateTextView.setText(editData.getString("date"));

            new DownloadImageFromInternet(screen2.findViewById(R.id.imageView)).execute(editData.getString("photo_link"));

            edit_name.setText(editData.getString("client_Name"));

            if (!editData.getString("video_link").equals("NO_VIDEO")) {
                videoView.setVideoURI(Uri.parse(editData.getString("video_link")));
                videoView.start();
                btnDown.setVisibility(View.VISIBLE);

                btnDown.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        downloadManager(editData.getString("video_link"));
                    }
                });

            }

            if (editData.getString("status").equals("PENDIENTE")) {
                checkboxStatus.setChecked(true);

                status = "PENDIENTE";
            } else if (editData.getString("status").equals("REALIZADA")) {
                checkboxStatus2.setChecked(true);

                status = "REALIZADA";
            } else {
                checkboxStatus3.setChecked(true);
                status = "CANCELADA";
            }

            checkboxStatus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (checkboxStatus.isChecked()) {
                        checkboxStatus2.setChecked(false);
                        checkboxStatus3.setChecked(false);
                        status = "PENDIENTE";
                    } else {
                        status = editData.getString("status");
                    }
                }
            });

            checkboxStatus2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (checkboxStatus2.isChecked()) {

                        checkboxStatus.setChecked(false);
                        checkboxStatus3.setChecked(false);

                        status = "REALIZADA";
                    } else {
                        status = editData.getString("status");
                    }
                }
            });
            checkboxStatus3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (checkboxStatus3.isChecked()) {
                        checkboxStatus.setChecked(false);
                        checkboxStatus2.setChecked(false);

                        status = "CANCELADA";
                    } else {
                        status = editData.getString("status");
                    }
                }
            });
            String[] hourArray = {editData.getString("hour")};
            hourAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, hourArray);

        } else {

            dateTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    handleDateButton();
                }
            });

            hourAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, hours);

        }

        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(hourAdapter);

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
                validations();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointment();
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
        values.put(MediaStore.Video.Media.TITLE, fileName);
        values.put(MediaStore.Video.Media.DESCRIPTION, "Video capture by camera");

        videoUri = getActivity().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        startActivityForResult(intent, 201);
    }

    public void reg() {

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

                                ContentResolver cr = getActivity().getContentResolver();
                                String[] projection = {MediaStore.MediaColumns.DATA};
                                Cursor cur = cr.query(Uri.parse(videoUri.toString()), projection, null, null, null);
                                if (cur != null) {
                                    if (cur.moveToFirst()) {
                                        String filePath = cur.getString(0);

                                        if (new File(filePath).exists()) {
                                            // do something if it exists
                                            final StorageReference file_name2 = Folder.child("video" + videoUri.getLastPathSegment());

                                            file_name2.putFile(videoUri, metadata).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                                                    if (task.isSuccessful()) {
                                                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                                                                video_link = task.getResult().toString();
                                                                Appointment newAppointment = new Appointment(currentUser.getEmail().replaceAll("\\p{Punct}", ""), edit_name.getText().toString(), dateTextView.getText().toString(), hour, status, photo_link, video_link);

                                                                String newDate = dateTextView.getText().toString().replaceAll("\\p{Punct}", "");
                                                                String key = newDate + hour.replaceAll("\\s", "");

                                                                appointmentTable.child(key).setValue(newAppointment);
                                                            }
                                                        });

                                                        mDialog.dismiss();

                                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                                                        getFragmentManager().popBackStack();

                                                        getActivity().recreate();
                                                    }

                                                }
                                            });
                                        } else {
                                            if (editData != null) {
                                                video_link = editData.getString("video_link");

                                            } else {
                                                video_link = "NO_VIDEO";
                                            }
                                            Appointment newAppointment = new Appointment(currentUser.getEmail().replaceAll("\\p{Punct}", ""), edit_name.getText().toString(), dateTextView.getText().toString(), hour, status, photo_link, video_link);

                                            String newDate = dateTextView.getText().toString().replaceAll("\\p{Punct}", "");
                                            String key = newDate + hour.replaceAll("\\s", "");

                                            appointmentTable.child(key).setValue(newAppointment);

                                            mDialog.dismiss();

                                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Uri was ok but no entry found.
                                    }
                                    cur.close();
                                } else {
                                    // content Uri was invalid or some other error occurred
                                }
                            } else {
                                if (editData != null) {
                                    video_link = editData.getString("video_link");
                                } else {
                                    video_link = "NO_VIDEO";
                                }

                                Appointment newAppointment = new Appointment(currentUser.getEmail().replaceAll("\\p{Punct}", ""), edit_name.getText().toString(), dateTextView.getText().toString(), hour, status, photo_link, video_link);

                                String newDate = dateTextView.getText().toString().replaceAll("\\p{Punct}", "");
                                String key = newDate + hour.replaceAll("\\s", "");

                                appointmentTable.child(key).setValue(newAppointment);

                                mDialog.dismiss();

                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });
    }

    private void validations() {

        String sUsername = edit_name.getText().toString();

        if (dateTextView.getText().toString().isEmpty() || hour.equals("Hora") || sUsername.matches("") || imageUri == null) {

            Toast.makeText(getContext(), "Por favor llena los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Query query = appointmentTable.orderByChild("date").equalTo(dateTextView.getText().toString());

        appointmentList = new ArrayList<>();

        readData(query, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Appointment app = postSnapshot.getValue(Appointment.class);
                    appointmentList.add(app);
                }

                if (editData == null) {

                    if (appointmentList.size() >= 8) {

                        builder.setTitle("Citas agotadas");
                        builder.setMessage("Lo sentimos, ya no hay citas disponibles para este dia, intenta cambiando la fecha.");
                        builder.setPositiveButton("OK", null);
                        builder.create();
                        builder.show();

                        return;
                    }

                    for (Appointment e : appointmentList) {
                        if (e.getHour().equals(hour)) {
                            builder.setTitle("Hora ya reservada");
                            builder.setMessage("Aun quedan horas disponibles para este dia, intenta  de nuevo.");
                            builder.setPositiveButton("OK", null);
                            builder.create();
                            builder.show();
                            return;
                        }
                    }
                }
                reg();

            }

            @Override
            public void onStart() {
                //when starting
                Log.d("ONSTART", "Started");
            }

            @Override
            public void onFailure() {
                Log.d("onFailure", "Failed");
            }
        });

    }

    private void deleteAppointment(){
        appointmentTable.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("firebase", "Error getting data", task.getException());
                }else{
                    for (DataSnapshot postSnapshot : task.getResult().getChildren()) {
                        Appointment app = postSnapshot.getValue(Appointment.class);
                        if(app.getPhoto_link().equals(editData.getString("photo_link")) && app.getDate().equals(editData.getString("date")) && app.getHour().equals(editData.getString("hour"))) {
                            Log.e("firebase", "ENCONTRADOOOO");
                            postSnapshot.getRef().setValue(null);
                            Log.e("firebase", "ELIMINADO");
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(android.R.id.content, new Screen1());
                            transaction.addToBackStack(null);
                            transaction.commit();

                        }
                    }
                }
            }
        });

    }

    public void readData(Query ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                listener.onFailure();
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

        if (editData != null) {
            hour = editData.getString("hour");
        } else {
            hour = hours[position];
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void downloadManager(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("download");
        request.setTitle(url.replaceAll("\\p{Punct}", ""));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + url.replaceAll("\\p{Punct}", "") + ".mp4");

        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public interface OnGetDataListener {
        void onSuccess(DataSnapshot dataSnapshot);

        void onStart();

        void onFailure();
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {

        public DownloadImageFromInternet(ImageView imageView) {
            imgView = imageView;
            Toast.makeText(getActivity().getApplicationContext(), "Descargando foto de perfil", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];

            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                imageUri = getImageUri(getContext(), bitmap);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imgView.setImageBitmap(result);
        }

        public Uri getImageUri(Context inContext, Bitmap inImage) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Foto de perfil", null);
            return Uri.parse(path);
        }
    }

}
