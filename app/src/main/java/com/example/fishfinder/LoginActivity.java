package com.example.fishfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    /* Variables */
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private Context ctx;

    /* Components */
    private EditText edtUsername;
    private EditText edtPassword;
    private Button bntSignIn;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Initialize Variables */

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ctx = this.getBaseContext();

        /* Initialize Components */
        bntSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        bntSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Sign In TODO: Add checks to see if the EditText objects have the right values
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                signIn(username, password);

//                Intent intent = new Intent(v.getContext(), MainPageActivity.class);
//                startActivity(intent);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Switch To MainPage
                Intent goToRegisterPage = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(goToRegisterPage);
            }
        });
    }

    // Check to see if the user is already signed in onStart.
    // If the user is authenticated, direct them to the MainPage
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            // Switch to MainPage
                            Intent goToMainPageActivity = new Intent(ctx, MainPageActivity.class);
//                            Bundle loadInfo = new Bundle();
//                            loadInfo.putSerializable("user", user);
                            startActivity(goToMainPageActivity);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }


}