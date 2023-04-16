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

import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RoomList extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<RoomItem> mItemList;
    private RoomItemAdapter rAdapter;
    private int gridNumber = 1;
    private FirebaseUser user;
    private boolean viewRow = true;
    private static final String LOG_TAG = RoomList.class.getName();

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
        inicializeDate();
    }

    private void inicializeDate() {
        String[] itemHotel = getResources().getStringArray(R.array.room_item_hotel);
        String[] itemType = getResources().getStringArray(R.array.room_item_type);
        String[] itemPrice = getResources().getStringArray(R.array.room_item_price);
        String[] itemCountry = getResources().getStringArray(R.array.room_item_country);
        String[] itemCity = getResources().getStringArray(R.array.room_item_city);
        String[] itemid = getResources().getStringArray(R.array.room_item_id);
        mItemList.clear();
        for (int i = 0; i < itemHotel.length; i++) {
            Location x = new Location(itemCity[i], itemCountry[i]);
            int price = Integer.parseInt(itemPrice[i]);
            mItemList.add(new RoomItem(itemHotel[i], itemid[i], x, price, itemType[i]));
        }
        rAdapter.notifyDataSetChanged();
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