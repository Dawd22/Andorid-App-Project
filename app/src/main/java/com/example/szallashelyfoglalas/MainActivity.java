package com.example.szallashelyfoglalas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    EditText userEmailET;
    EditText passwordET;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        Log.i(LOG_TAG, "onCreate");

    }

    public void login(View view) {
        String email = userEmailET.getText().toString();
        String password = passwordET.getText().toString();

        Log.i(LOG_TAG, "Bejelentkezett: " + email + " Jelszó: " + password);

    }

    public void signupPage(View view) {
        Intent signupIntent = new Intent(this, Signup.class);
        signupIntent.putExtra("SECRET_KEY", SECRET_KEY);
        //TODO.
        startActivity(signupIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userEmail", userEmailET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }
}