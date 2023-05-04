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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signup extends AppCompatActivity {
    private static final int SECRET_KEY = 101;
    private static final String LOG_TAG = Signup.class.getName();
    private static final String PREF_KEY = Signup.class.getPackage().toString();

    EditText usernameET;
    EditText userEmailET;
    EditText passwordET;
    private FirebaseFirestore mFirestore;
    private SharedPreferences preferences;
    private FirebaseAuth nAuth;
    private CollectionReference mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Bundle signupBundle = getIntent().getExtras();
        int secret_key = signupBundle.getInt("SECRET_KEY");
        if (secret_key != 99)
            finish();

        usernameET = findViewById(R.id.usernameEditText);
        userEmailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("userEmail", "");
        String password = preferences.getString("password", "");
        mFirestore = FirebaseFirestore.getInstance();

        mUsers = mFirestore.collection("Users");
        userEmailET.setText(email);
        passwordET.setText(password);
        nAuth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG, "onCreate");
    }

    public void signup(View view) {
        String username = usernameET.getText().toString();
        String email = userEmailET.getText().toString();
        String password = passwordET.getText().toString();
        if (username.equals("") || username == null || email.equals("") || email == null || password.equals("") || password == null) {
            Log.d(LOG_TAG, "Figyelj oda pajtás hogy ne legyen üres");
            Toast.makeText(this, "Nem lehet üresen hagyni a mezőket!", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(LOG_TAG, "Regisztrált: " + email
                    + " Jelszó: "
                    + password
                    + " Felhasznaló neve: "
                    + username);

            nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    User newUser = new User("", email, "user", username);
                    mUsers.add(newUser).addOnSuccessListener(documentReference -> {
                        Log.d(LOG_TAG, "Felhasználó feltöltve");
                        newUser.setId(documentReference.getId());
                        documentReference.set(newUser);
                    }).addOnFailureListener(e -> {
                        Log.d(LOG_TAG, "Felhasználó nem lett feltöltve");
                    });

                    Log.d(LOG_TAG, "Felhasználó regisztrált sikeresen");
                    startRoom();
                } else {
                    Log.d(LOG_TAG, "Nem sikerült regisztrálni");
                    Toast.makeText(Signup.this, "Nem sikerült regisztrálni:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void cancel(View view) {
        finish();
    }

    private void startRoom(/*User data*/) {
        Intent roomIntent = new Intent(this, RoomList.class);
        // roomIntent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(roomIntent);

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
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }
}