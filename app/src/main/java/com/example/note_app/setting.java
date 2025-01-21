package com.example.note_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class setting extends AppCompatActivity {

    ImageButton nightmode;
    SharedPreferences sharedPreferences;
    Boolean mode_status;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

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

        Button thongke = findViewById(R.id.thongke);
        //Button dongbo = findViewById(R.id.dongbo);
        Button font = findViewById(R.id.font);
        Button manage_user = findViewById(R.id.manage_user);
        Button phienban = findViewById(R.id.license_app);
        phienban.setTypeface(typeface);
        phienban.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        thongke.setTypeface(typeface);
        thongke.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        //dongbo.setTypeface(typeface);
        //dongbo.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        font.setTypeface(typeface);
        font.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        manage_user.setTypeface(typeface);
        manage_user.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        phienban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLicense(v);
                //font_day(v);
            }
        });
    }

    public  void openLicense(View view){
        Intent intent = new Intent(this, license.class);
        startActivity(intent);
        finish();
    }
    public void btnBacktoMain_day(View view){
        Intent intent = new Intent(this, main.class);
        startActivity(intent);
        finish();
    }
    public void login_day(View view){
        if (firebaseAuth.getCurrentUser() != null) {
            // Người dùng đã đăng nhập, chuyển tới user.class
            Intent intent = new Intent(this, user_manager.class);
            startActivity(intent);
            finish();
        } else {
            // Người dùng chưa đăng nhập, chuyển tới trang đăng nhập (log_in.class)
            Intent intent = new Intent(this, log_in.class);
            startActivity(intent);
            finish();
        }
    }

    public void font_day(View view){
        Intent intent = new Intent(this, font.class);
        startActivity(intent);
        finish();
    }

    public void statistic(View view){
        Intent intent = new Intent(this, statistic.class);
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
        if(user==null){
            Toast.makeText(this, "Vui lòng đăng nhập để bắt đầu nhắc nhở", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, log_in.class));
            finish();
        }else {
            Intent intent = new Intent(setting.this, reminder_list.class);
            startActivity(intent);
            finish();
        }
    }
    public void setting(View view){
        Intent intent = new Intent(this, setting.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}