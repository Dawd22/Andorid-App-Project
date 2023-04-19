package com.example.szallashelyfoglalas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    EditText userEmailET;
    EditText passwordET;
    private SharedPreferences preferences;
    private FirebaseAuth nAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        nAuth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG, "onCreate");

    }

    public void login(View view) {
        String email = userEmailET.getText().toString();
        String password = passwordET.getText().toString();
        nAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Sikeres bejelentkezni");
                    startRoom();
                }
                else {
                    Log.d(LOG_TAG, "Nem sikerült bejelentkezni");
                    Toast.makeText(MainActivity.this, "Nem sikerült bejelentkezni:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void startRoom() {
        Intent roomIntent = new Intent(this, RoomList.class);
        startActivity(roomIntent);

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