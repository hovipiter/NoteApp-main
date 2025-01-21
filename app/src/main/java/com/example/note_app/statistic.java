package com.example.note_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class statistic extends AppCompatActivity {
    ImageButton nightmode;
    SharedPreferences sharedPreferences;
    Boolean mode_status;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);

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

        // Đọc các cài đặt font và size từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String fontName = preferences.getString("selectedFont", null);
        float textSize = preferences.getFloat("selectedTextSize", 16);
        Typeface typeface = Typeface.DEFAULT;
        if (fontName != null) {
            try {
                typeface = Typeface.createFromAsset(getAssets(), fontName);
            } catch (Exception e) {
                Log.e("SettingDayActivity", "Failed to create typeface from file", e);
                Toast.makeText(getApplicationContext(), "Failed to create typeface from file", Toast.LENGTH_SHORT).show();
            }
        }
        TextView title = findViewById(R.id.title);
        TextView txtFavorite = findViewById(R.id.txtFavorite);
        TextView txtNote = findViewById(R.id.txtNote);
        TextView txtReminder = findViewById(R.id.txtReminder);
        TextView countFavorite = findViewById(R.id.countFavorite);
        TextView countNote = findViewById(R.id.countNote);
        TextView countReminder = findViewById(R.id.countReminder);

        title.setTypeface(typeface);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        txtFavorite.setTypeface(typeface);
        txtFavorite.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        txtNote.setTypeface(typeface);
        txtNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        txtReminder.setTypeface(typeface);
        txtReminder.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        countFavorite.setTypeface(typeface);
        countFavorite.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        countNote.setTypeface(typeface);
        countNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        countReminder.setTypeface(typeface);
        countReminder.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
    public void btnBacktoMain_day(View view){
        Intent intent = new Intent(this, main.class);
        startActivity(intent);
        finish();
    }
    public void reminder(View view){
        if(user==null){
            Toast.makeText(this, "Vui lòng đăng nhập để bắt đầu nhắc nhở", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, log_in.class));
            finish();
        }else {
            Intent intent = new Intent(statistic.this, reminder_list.class);
            startActivity(intent);
            finish();
        }
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
}
