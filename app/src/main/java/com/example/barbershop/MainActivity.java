package com.example.barbershop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;

    private FirebaseAuth mAuth;
    private TextView text_ID, text_name, text_email;
    private ImageView image_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Clientes con Citas Agendadas"));
        tabLayout.addTab(tabLayout.newTab().setText("Actualizar Cita (Float BTN)"));
        tabLayout.addTab(tabLayout.newTab().setText("Citas Pendientes"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

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