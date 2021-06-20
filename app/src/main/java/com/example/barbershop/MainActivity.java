package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;

    private FirebaseAuth mAuth;
    private TextView text_ID, text_name, text_email;
    private ImageView image_user;
    private Button button_logout;
    private Button button_delete_account;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_user = findViewById(R.id.image_user);
        text_ID = findViewById(R.id.text_ID);
        text_name = findViewById(R.id.text_name);
        text_email = findViewById(R.id.text_email);
        button_logout = findViewById(R.id.btnLogout);
        button_delete_account = findViewById(R.id.btnDeleteAccount);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        text_ID.setText(currentUser.getUid());
        text_name.setText(currentUser.getDisplayName());
        text_email.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(image_user);

        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Citas Agendadas"));
        tabLayout.addTab(tabLayout.newTab().setText("Citas Pendientes"));
        tabLayout.addTab(tabLayout.newTab().setText("Perfil"));

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

        button_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mAuth.signOut();

                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(loginActivity);
                            MainActivity.this.finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "Could not log out with google", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        button_delete_account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                if (signInAccount != null) {

                    AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Log.d("MainActivity", "onSuccess: User Deleted");
                                        signOut();

                                    }
                                });
                            } else {
                                Log.e("MainActivity", "onComplete: Failed to delete user", task.getException());
                            }
                        }
                    });
                } else {
                    Log.d("MainActivity", "Error: reAuthenticateUser: user account is null");
                }
            }
        });
    }

    private void signOut() {

        FirebaseAuth.getInstance().signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent IntentLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                IntentLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(IntentLoginActivity);
                MainActivity.this.finish();

            }
        });
    }
}