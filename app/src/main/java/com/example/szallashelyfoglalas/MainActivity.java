package com.example.szallashelyfoglalas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        EditText userEmail = findViewById(R.id.editTextEmail);
        EditText userPassword = findViewById(R.id.editTextPassword);

        String email =  userEmail.getText().toString();
        String password = userPassword.getText().toString();

        Log.i(LOG_TAG,"Bejelentkezett: "+ email+ " Jelszó: " + password);

    }
}