package com.example.barbershop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView text_ID, text_name, text_email;
    private ImageView image_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image_user = findViewById(R.id.image_user);
        text_ID = findViewById(R.id.text_ID);
        text_name = findViewById(R.id.text_name);
        text_email = findViewById(R.id.text_email);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        text_ID.setText(currentUser.getUid());
        text_name.setText(currentUser.getDisplayName());
        text_email.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(image_user);
    }
}