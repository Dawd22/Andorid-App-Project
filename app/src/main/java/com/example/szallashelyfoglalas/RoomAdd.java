package com.example.szallashelyfoglalas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RoomAdd extends AppCompatActivity {
    private FirebaseUser user;
    private static final String LOG_TAG = RoomAdd.class.getName();
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private NotificationHandler mNotificationHandler;
    private EditText Hotel;
    private EditText City;
    private EditText Country;
    private EditText Price;
    private EditText Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_add);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Van bejelentkezett felhasználó!");
        } else {
            Log.d(LOG_TAG, "Nincs bejelentkezett felhasználó!");
            finish();
        }
        Hotel = findViewById(R.id.Hotel);
        City = findViewById(R.id.city);
        Country = findViewById(R.id.country);
        Price = findViewById(R.id.price);
        Type = findViewById(R.id.type);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Rooms");
        mNotificationHandler = new NotificationHandler(this);
    }


    public void addRoom(View view) {
        String hotel = Hotel.getText().toString();
        String city = City.getText().toString();
        String country = Country.getText().toString();
        int price = Integer.parseInt(Price.getText().toString());
        String type = Type.getText().toString();
        Location location = new Location(city, country);
        RoomItem newRoom = new RoomItem(hotel, "", location, price, type);
        mItems.add(newRoom).addOnSuccessListener(documentReference -> {
            newRoom.setId(documentReference.getId());
            documentReference.set(newRoom);
            mNotificationHandler.send("Új szoba hozzáadása megtörtént!");
        }).addOnFailureListener(e -> {
            Log.d(LOG_TAG, "Valami baj történt a hozzáadás során!");
        });
    }

    public void cancel(View view) {
        Intent roomIntent = new Intent(this, RoomList.class);
        startActivity(roomIntent);
    }
}