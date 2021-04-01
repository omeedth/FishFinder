package com.example.fishfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button bntSignIn;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bntSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        bntSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //button sign in to get to main page
                Intent intent = new Intent(v.getContext(), MainPageActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //button register to get to register page
                Intent goToRegisterPage = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(goToRegisterPage);
            }
        });
    }


}