package com.example.note_app;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class main extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Note> listNote;
    NoteAdapter noteAdapter;
    SearchView searchView;
    ImageButton btnAdd, btnFindNote;
    TextView btnDelete;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    ImageButton nightmode;
    SharedPreferences sharedPreferences;
    Boolean mode_status;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findbyviewIds();
        //kiểm tra user
        if(currentUser==null){
            Toast.makeText(this, "Vui lòng đăng nhập để xem ghi chú", Toast.LENGTH_SHORT).show();
        }

        // Các xử lý khác nếu cần thiết cho layout mới
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        mode_status = sharedPreferences.getBoolean("night", false);
        if(mode_status){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setupRecycleView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (noteAdapter!= null) noteAdapter.getFilter().filter(newText);
                return false;
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewNote();
            }
        });
        btnFindNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNewNote();
            }
        });
        nightmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changemode();
            }
        });
    }

    //ngoài onCreate
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
    private void setupRecycleView() {
        if (currentUser != null){
            String userid = currentUser.getUid();
            Utility.getCollectionReferenceForNotes()
                    //.whereEqualTo("user_id", userid)
                    .orderBy("note_day", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                listNote = new ArrayList<>();
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                                {
                                    Note note = documentSnapshot.toObject(Note.class);
                                    listNote.add(note);
                                }
                                loadRecyclerViewAdapter(listNote);
                            }
                        }
                    });
        }
    }
    private void loadRecyclerViewAdapter(ArrayList<Note> notes){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter= new NoteAdapter(this, R.layout.item_note, listNote);
        noteAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.sortNotesByFavorite();
    }


    public void setting_day(View view){
        Intent intent = new Intent(this, setting.class);
        startActivity(intent);
        finish();
    }
    public void openNewNote(){
        if(currentUser==null){
            Toast.makeText(this, "Vui lòng đăng nhập để bắt đầu ghi chú", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, log_in.class));
            finish();
        }else {
            Intent intent = new Intent(main.this, note_take.class);
            startActivity(intent);
            finish();
        }
    }
    public void findNewNote(){
        Intent intent = new Intent(main.this, main.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
    private void findbyviewIds(){
        nightmode=findViewById(R.id.iBt_mode);
        recyclerView= findViewById(R.id.rcv_note);
        searchView = findViewById(R.id.search_View);
        btnAdd = (ImageButton) findViewById(R.id.iBt_Add);
        btnFindNote = (ImageButton) findViewById(R.id.iBtFindNote);
    }
    public void reminder(View view){
        if(currentUser==null){
            Toast.makeText(this, "Vui lòng đăng nhập để bắt đầu nhắc nhở", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, log_in.class));
            finish();
        }else {
            Intent intent = new Intent(main.this, reminder_list.class);
            startActivity(intent);
            finish();
        }
    }
}