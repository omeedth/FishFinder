package com.example.fishfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    /* Components */
    EditText editTextTextEmail;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextConfirmPassword;
    Button buttonSignUp;

    /* Variables */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* Pre-Setup */

        /* Variable Initialization */

        /* Component Initialization */
        editTextTextEmail = findViewById(R.id.editTextTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        /* Post-Setup */

        /* Listeners */
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    /* Logic Methods */
    public void signup() {
        //to be done
    }

}