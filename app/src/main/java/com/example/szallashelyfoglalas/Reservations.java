package com.example.szallashelyfoglalas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Reservations extends AppCompatActivity {
    private int gridNumber = 1;
    private boolean viewRow = false;
    private FirebaseFirestore mFirestore;
    private CollectionReference mReservations;
    private NotificationHandler mNotificationHandler;
    private ReservationAdapter rAdapter;

    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<Reservation> mItemList;

    private static final String LOG_TAG = Reservations.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Van bejelentkezett felhasználó!");
        } else {
            Log.d(LOG_TAG, "Nincs bejelentkezett felhasználó!");
            finish();
        }
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();
        rAdapter = new ReservationAdapter(this, mItemList);
        mRecyclerView.setAdapter(rAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mReservations = mFirestore.collection("Reservations");
        mNotificationHandler = new NotificationHandler(this);

        queryData();
    }
    private void queryData() {
        mItemList.clear();

        mReservations.whereEqualTo("user_email",user.getEmail())
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        Reservation item = document.toObject(Reservation.class);
                        item.setId(document.getId());
                        mItemList.add(item);
                    }
                    if(mItemList.size() ==0){
                        roomToRoom();
                    }
                    rAdapter.notifyDataSetChanged();
                });
    }
    public void deleteItem(Reservation item) {

        DocumentReference ref = mReservations.document(item.getId());

        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Töröltük" + item.getId());
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Nem sikerült törölni ezt: " + item.getId(), Toast.LENGTH_SHORT).show();
            mNotificationHandler.send("Töröltük a föglalást");
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                queryData();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                rAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Log.d(LOG_TAG, "onOptionsItemSelected: logout");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.reservations:
                Log.d(LOG_TAG, "onOptionsItemSelected: reservations");
                roomToReservation();
                return true;
            case R.id.reservation:
                Log.d(LOG_TAG, "onOptionsItemSelected: reservation");
                return true;
            case R.id.room:
                Log.d(LOG_TAG, "onOptionsItemSelected: setting button");
                roomToRoom();
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "onOptionsItemSelected: view selector ");
                if (viewRow) {
                    changeSpanCount(item, R.drawable.view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void roomToReservation(){
        Intent roomIntent = new Intent(this, Reservations.class);
        startActivity(roomIntent);
    }
    public void roomToRoom(){
        Intent roomIntent = new Intent(this, RoomList.class);
        startActivity(roomIntent);
    }
    private void changeSpanCount(MenuItem item, int drawable, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawable);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }
}