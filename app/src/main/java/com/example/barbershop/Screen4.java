package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.barbershop.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Screen4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Screen4 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private TextView text_role, text_name, text_email;
    private ImageView image_user;
    private Button button_logout;
    private Button button_delete_account;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private FirebaseDatabase database;
    private DatabaseReference userTable;

    public Screen4() {
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
    public static Screen4 newInstance(String param1, String param2) {
        Screen4 fragment = new Screen4();
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
        View view_screen4 = inflater.inflate(R.layout.fragment_screen4, container, false);

        image_user = view_screen4.findViewById(R.id.image_user);
        text_role = view_screen4.findViewById(R.id.text_role);
        text_name = view_screen4.findViewById(R.id.text_name);
        text_email = view_screen4.findViewById(R.id.text_email);
        button_logout = view_screen4.findViewById(R.id.btnLogout);
        button_delete_account = view_screen4.findViewById(R.id.btnDeleteAccount);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        database = FirebaseDatabase.getInstance();
        userTable = database.getReference("User");

        userTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                User user = snapshot.child(currentUser.getEmail().replaceAll("\\p{Punct}", "")).getValue(User.class);

                if (user.getRole().equals("Client")) {
                    text_role.setText("Cliente");
                } else {
                    text_role.setText("Administrador");
                }
                text_name.setText(user.getFullName());
                text_email.setText(user.getEmail());
                Glide.with(Screen4.this).load(currentUser.getPhotoUrl()).into(image_user);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
                            Intent loginActivity = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            startActivity(loginActivity);
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Could not log out with google", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        button_delete_account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());

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

        return view_screen4;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent IntentLoginActivity = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                IntentLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(IntentLoginActivity);
                getActivity().finish();
            }
        });
    }
}
