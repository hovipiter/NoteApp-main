package com.example.note_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class reminder_list extends AppCompatActivity implements ReminderAdapter.OnItemClickListener{
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    ImageButton nightmode;
    SharedPreferences sharedPreferences;
    Boolean mode_status;
    SharedPreferences.Editor editor;
    String title, date, time;
    SearchView searchView;
    ReminderAdapter reminderAdapter;
    ArrayList<Reminder> listReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);
        searchView = findViewById(R.id.search_View);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(reminderAdapter != null) reminderAdapter.getFilter().filter(newText);
                return false;
            }
        });

        nightmode=findViewById(R.id.iBt_mode);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        mode_status = sharedPreferences.getBoolean("night", false);
        if(mode_status){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        nightmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changemode();
            }
        });

        // Khởi tạo RecyclerView và adapter
        recyclerView = findViewById(R.id.rcv_remind);
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(reminderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        // Khởi tạo Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userUID = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("reminder").child(userUID);

            // Lắng nghe sự thay đổi dữ liệu từ Firebase Realtime Database
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reminderList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        title = snapshot.child("Content").getValue(String.class);
                        date = snapshot.child("Date").getValue(String.class);
                        time = snapshot.child("Time").getValue(String.class);
                        Reminder reminder = new Reminder(title, date, time);
                        reminder.setKey(key);
                        reminderList.add(reminder);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Reminder", "Error: " + databaseError.getMessage());
                }
            });
        }
    }
    @Override
    public void onItemClick(int position) {
        Reminder clickedReminder = reminderList.get(position);
        String reminderKey = clickedReminder.getKey();
        Intent intent = new Intent(this, reminder_take.class);
        intent.putExtra("reminder_id", reminderKey);
        intent.putExtra("title", clickedReminder.getTitle());
        intent.putExtra("date", clickedReminder.getDate());
        intent.putExtra("time", clickedReminder.getTime());
        intent.putExtra("source", "intent_remind_list");
        startActivity(intent);
    }

    public void new_reminder(View view){
        if(currentUser==null){
            Toast.makeText(this, "Vui lòng đăng nhập để bắt đầu nhắc nhở", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, log_in.class));
            finish();
        }else {
            Intent intent = new Intent(reminder_list.this, reminder_take.class);
            startActivity(intent);
            finish();
        }
    }
    public void main(View view){
        Intent intent = new Intent(this, main.class);
        startActivity(intent);
        finish();
    }
    public void setting(View view){
        Intent intent = new Intent(this, setting.class);
        startActivity(intent);
        finish();
    }
    private void changemode(){
        if (mode_status == true) mode_status=false;
        else mode_status = true;
        if (mode_status){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor = sharedPreferences.edit();
            editor.putBoolean("night", mode_status);
            editor.apply();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor = sharedPreferences.edit();
            editor.putBoolean("night", mode_status);
            editor.apply();
        }
    }
    public void reminder(View view){
        Intent intent = new Intent(this, reminder_list.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
