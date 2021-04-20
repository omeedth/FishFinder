package com.example.fishfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fishfinder.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private Button bntSignIn;
    private Button btnRegister;
    //DatabaseReference firebase;

    FirebaseAuth mAuth; //Initilize authenticaiton with firebase

    private EditText edtUsername;
    private EditText edtPassword;

    private String email;
    private String password;
    private String topBarColor;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        topBarColor = "#2c70cf";

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(topBarColor));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);


        bntSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        edtUsername = (EditText) findViewById(R.id.edtUsername); //Its actually an email input now. Not Username but for convenience just keep the ids the same with the button
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        mAuth = FirebaseAuth.getInstance(); //init firebase auth instance

        bntSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(v.getContext(), MainPageActivity.class);
//                startActivity(intent);
                userLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegisterPage = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(goToRegisterPage);
            }
        });
    }


    public void userLogin() {

        if (edtUsername.getText().toString().trim().isEmpty()) {
            edtUsername.setError("Invalid Email!");
            return;
        }

        //if not a proper email formating deny entry display error
        if (!Patterns.EMAIL_ADDRESS.matcher(edtUsername.getText().toString().trim()).matches()) {
            edtUsername.setError("Invalid Email!");
            return;
        }

        if (edtPassword.getText().toString().isEmpty()) {
            edtPassword.setError("Invalid Password!");
            return;
        }

        email = edtUsername.getText().toString().trim();
        password = edtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);


                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Login Failed, Check Login Information", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}