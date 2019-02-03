package com.asiimwetruly.keepsafepolice;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.asiimwetruly.keepsafepolice.PasswordResetActivity;
import com.asiimwetruly.keepsafepolice.R;
import com.asiimwetruly.keepsafepolice.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText emailfield;
    private EditText password;
    private Button emails, email_sign_in_button;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private RelativeLayout app;
    private ProgressBar pl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        toolbar = findViewById(R.id.signin);
        setSupportActionBar(toolbar);
        emails = findViewById(R.id.emails);
        pl = findViewById(R.id.pr);
        app = findViewById(R.id.app);
        emails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AuthActivity.this, PasswordResetActivity.class));
            }
        });


        mAuth = FirebaseAuth.getInstance();
        // Set up the login form.
        progressDialog = new ProgressDialog(this);
        emailfield = findViewById(R.id.emaill);
        password = findViewById(R.id.password);
        email_sign_in_button = findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSigningIn();
            }
        });
    }

    private void startSigningIn() {
        String email = emailfield.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(email) && !Utils.isValidEmail(email)) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {


            if (pass.length() < 6) {
                Toast.makeText(this, "Your password is too short,It should be atleast 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            email_sign_in_button.setEnabled(false);
            email_sign_in_button.setText("signing in...");
            pl.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()) {
                        /*---------------CHECK IF EMAIL IS VERIFIED------------------------*/
                        final FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseMessaging.getInstance().subscribeToTopic("police");

                        final String token = FirebaseInstanceId.getInstance().getToken();
                        Log.d(TAG, "onComplete: Token" + token);
                        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                                        if (documentSnapshot.exists()) {
                                            Log.d(TAG, "onEvent: userexists");
                                            if (documentSnapshot.get("isPolice").equals("")) {
                                                progressDialog.dismiss();
                                                AuthUI.getInstance().signOut(getBaseContext());
                                                email_sign_in_button.setText("Login");
                                                pl.setVisibility(View.GONE);
                                                Toast.makeText(AuthActivity.this, "An unknown error has occured", Toast.LENGTH_SHORT).show();
                                            } else {

                                                Map tokenmap=new HashMap<>();
                                                tokenmap.put("device_token",FirebaseInstanceId.getInstance().getToken());
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .setValue(tokenmap);

                                                FirebaseMessaging.getInstance().subscribeToTopic("police");
                                                Intent i = new Intent(getBaseContext(), MainActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(i);

                                            }
                                            ;


                                        } else {
                                            AuthUI.getInstance().signOut(getBaseContext());
                                            Toast.makeText(AuthActivity.this, "Account not found", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });


                    } else {
                        Toast.makeText(AuthActivity.this, "Sign In Error", Toast.LENGTH_SHORT).show();
                        email_sign_in_button.setEnabled(true);
                        email_sign_in_button.setText("Log In");
                        pl.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@android.support.annotation.NonNull Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "All fields are required,please check your email and a password", Toast.LENGTH_SHORT).show();
        }
    }
}
