package com.example.szallashelyfoglalas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RoomList extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<RoomItem> mItemList;
    private RoomItemAdapter rAdapter;
    private int gridNumber = 1;
    private FirebaseUser user;
    private boolean viewRow = false;
    private static final String LOG_TAG = RoomList.class.getName();
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private CollectionReference mReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
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
        rAdapter = new RoomItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(rAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Rooms");
        mReservations = mFirestore.collection("Reservations");

        queryData();
    }
    private void queryData(){
        mItemList.clear();

        mItems.orderBy("hotel")
                        .limit(10)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                         for (QueryDocumentSnapshot document: queryDocumentSnapshots)
                         {
                             RoomItem item = document.toObject(RoomItem.class);
                             mItemList.add(item);
                         }
                         if(mItemList.size() ==0){
                             inicializeDate();
                             queryData();
                         }
                            rAdapter.notifyDataSetChanged();
                        });
    }
    public void deleteItem(RoomItem item){

        DocumentReference ref = mItems.document(item.getId());

        ref.delete().addOnSuccessListener(success ->{
            Log.d(LOG_TAG, "Töröltük"+ item.getId());
        }).addOnFailureListener(failure ->{
            Toast.makeText(this, "Nem sikerült törölni ezt: " + item.getId(), Toast.LENGTH_SHORT).show();
        });
        queryData();
    }
    public void reservation(RoomItem item){

    }
    public void updateItem(RoomItem item){

    }
    private void inicializeDate() {
        mItemList.clear();
        String[] itemHotel = getResources().getStringArray(R.array.room_item_hotel);
        String[] itemType = getResources().getStringArray(R.array.room_item_type);
        String[] itemPrice = getResources().getStringArray(R.array.room_item_price);
        String[] itemCountry = getResources().getStringArray(R.array.room_item_country);
        String[] itemCity = getResources().getStringArray(R.array.room_item_city);
        String[] itemid = getResources().getStringArray(R.array.room_item_id);

        //feltöltés példa::
        for (int i = 0; i < itemHotel.length; i++) {
            Location x = new Location(itemCity[i], itemCountry[i]);
            int price = Integer.parseInt(itemPrice[i]);
            mItems.add(new RoomItem(itemHotel[i],
                    itemid[i],
                    x,
                    price,
                    itemType[i]));
        }
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
            case R.id.reservateions:
                Log.d(LOG_TAG, "onOptionsItemSelected: reservations");
                return true;
            case R.id.reservation:
                Log.d(LOG_TAG, "onOptionsItemSelected: reservation");
                return true;
            case R.id.setting_button:
                Log.d(LOG_TAG, "onOptionsItemSelected: setting button");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "onOptionsItemSelected: view selector ");
                if(viewRow){
                    changeSpanCount(item, R.drawable.view_grid,1);
                }
                else{
                    changeSpanCount(item, R.drawable.view_row,2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void changeSpanCount(MenuItem item, int drawable, int spanCount){
        viewRow =!viewRow;
        item.setIcon(drawable);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

}