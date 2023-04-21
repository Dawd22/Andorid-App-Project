package com.example.szallashelyfoglalas;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private NotificationHandler mNotificationHandler;
    private AlarmManager mAlarmManager;

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
        mNotificationHandler = new NotificationHandler(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setAlarmManager();
        }


        queryData();
    }

    private void queryData() {
        mItemList.clear();

        mItems.orderBy("hotel")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        RoomItem item = document.toObject(RoomItem.class);
                        item.setId(document.getId());
                        mItemList.add(item);
                    }
                    if (mItemList.size() == 0) {
                        inicializeDate();
                        queryData();
                    }
                    rAdapter.notifyDataSetChanged();
                });
    }

    public boolean checkDate(Date endDay, Date firstday, Date startDay, Date lastday) {

        return (endDay.getTime() <= firstday.getTime() || startDay.getTime() >= lastday.getTime());
    }

    public void deleteItem(RoomItem item) {

        DocumentReference ref = mItems.document(item.getId());

        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Töröltük" + item.getId());
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Nem sikerült törölni ezt: " + item.getId(), Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                queryData();
        });
    }

    public void reservation(RoomItem item, DatePicker firstday, DatePicker lastday) {
        mReservations
                .whereEqualTo("room_id", item.getId())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean free = true;
                    int yearFirst = firstday.getYear();
                    int monthFirst = firstday.getMonth();
                    int dayFirst = firstday.getDayOfMonth();
                    Calendar calendarFirst = Calendar.getInstance();
                    calendarFirst.set(yearFirst, monthFirst, dayFirst);
                    calendarFirst.set(Calendar.HOUR_OF_DAY, 12);
                    calendarFirst.set(Calendar.MINUTE, 0);
                    Date startDay = calendarFirst.getTime();

                    int yearSecond = lastday.getYear();
                    int monthSecond = lastday.getMonth();
                    int daySecond = lastday.getDayOfMonth();
                    Calendar calendarSecond = Calendar.getInstance();
                    calendarFirst.set(yearSecond, monthSecond, daySecond);
                    calendarSecond.set(Calendar.HOUR_OF_DAY, 7);
                    calendarSecond.set(Calendar.MINUTE, 0);
                    Date endDay = calendarSecond.getTime();


                    long diffInMs = Math.abs(endDay.getTime() - startDay.getTime());
                    long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs);
                    int full_price = ((int) diffInDays + 1) * item.getPrice();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Reservation reservation = document.toObject(Reservation.class);

                            System.out.println(startDay.getTime() + " " + reservation.firstday.getTime() + " " + endDay.getTime() + " " + reservation.lastday.getTime());

                            if (!checkDate(endDay, reservation.firstday, startDay, reservation.lastday)) {
                                free = false;
                                break;
                            }
                        }
                        if (free) {
                            Reservation newReservation
                                    = new Reservation(startDay, endDay, "", full_price, item.getHotel(), item.getId(), item.getType(), user.getEmail());
                            mReservations.add(newReservation).addOnSuccessListener(documentReference -> {
                                Log.d(LOG_TAG, "Sikerült a foglalás!");
                                newReservation.setId(documentReference.getId());
                                documentReference.set(newReservation);
                                mNotificationHandler.send("Foglalás történt: " + newReservation.room_hotel);
                            }).addOnFailureListener(e -> Log.d(LOG_TAG, "Nem sikerült a foglalás!"));
                        } else {
                            Log.d(LOG_TAG, "Foglalt a szoba!");
                        }
                    } else {

                        if (endDay.getTime() <= startDay.getTime()) {

                            Reservation newReservation
                                    = new Reservation(startDay, endDay, "", full_price, item.getHotel(), item.getId(), item.getType(), user.getEmail());
                            mReservations.add(newReservation).addOnSuccessListener(documentReference -> {
                                Log.d(LOG_TAG, "Sikerült a foglalás!");
                                newReservation.setId(documentReference.getId());
                                documentReference.set(newReservation);
                                mNotificationHandler.send("Foglalás történt: " + newReservation.room_hotel);
                            }).addOnFailureListener(e -> Log.d(LOG_TAG, "Nem sikerült a foglalás!"));
                        } else {
                            Log.d(LOG_TAG, "Dátumok nem megfelelőek!");
                        }

                    }
                }).addOnFailureListener(e -> Log.e(LOG_TAG, "Nem sikerült a keresés"));
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

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void setAlarmManager() {
        long triggerTime = System.currentTimeMillis() + 60 * 1000;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
        mAlarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

}