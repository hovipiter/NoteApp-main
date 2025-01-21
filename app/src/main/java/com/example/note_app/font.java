package com.example.note_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class font extends AppCompatActivity {
    private TextView selectFontTextView;
    private TextView chooseSizeTextView;
    private RadioButton radioSmall;
    private RadioButton radioMedium;
    private RadioButton radioBig, radioOpen_sans, radioDancing, radioComfortaa;
    ImageButton nightmode;
    SharedPreferences sharedPreferences;
    Boolean mode_status;
    SharedPreferences.Editor editor;

    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    ImageButton nhacnho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font);

        selectFontTextView = findViewById(R.id.selectFontTextView);
        chooseSizeTextView = findViewById(R.id.chooseSizeTextView);
        radioSmall = findViewById(R.id.radioSmall);
        radioMedium = findViewById(R.id.radioMedium);
        radioBig = findViewById(R.id.radioBig);
        radioOpen_sans = findViewById(R.id.radioOpen_sans);
        radioDancing = findViewById(R.id.radioDancing);
        radioComfortaa = findViewById(R.id.radioComfortaa);
        nightmode= findViewById(R.id.iBt_mode);
        nhacnho = findViewById(R.id.iBt_settime);

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
        nhacnho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder(v);
            }
        });

        RadioGroup fontRadioGroup = findViewById(R.id.fontRadioGroup);
        fontRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Typeface typeface;
            if (checkedId == R.id.radioOpen_sans) {
                typeface = Typeface.createFromAsset(getAssets(), "open_sans.ttf");
            } else if (checkedId == R.id.radioComfortaa) {
                typeface = Typeface.createFromAsset(getAssets(), "comfortaa.ttf");
            } else if (checkedId == R.id.radioDancing) {
                typeface = Typeface.createFromAsset(getAssets(), "dancing_script_bold.ttf");
            } else {
                // Mặc định sử dụng phông chữ mặc định
                typeface = Typeface.DEFAULT;
            }

            selectFontTextView.setTypeface(typeface);
            chooseSizeTextView.setTypeface(typeface);
            radioSmall.setTypeface(typeface);
            radioMedium.setTypeface(typeface);
            radioBig.setTypeface(typeface);

            // Lưu trữ cài đặt font chữ
            saveFontSettings(typeface);
        });

        RadioGroup textSizeRadioGroup = findViewById(R.id.textSizeRadioGroup);
        textSizeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            float textSize;
            if (checkedId == R.id.radioSmall) {
                textSize = 18;
            } else if (checkedId == R.id.radioMedium) {
                textSize = 24;
            } else {
                textSize = 30;
            }
            radioOpen_sans.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            radioDancing.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            radioComfortaa.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

            // Lưu trữ cài đặt kích thước chữ
            saveTextSizeSettings(textSize);
        });


        // Khôi phục cài đặt font chữ và kích thước chữ
        restoreSettings();
    }
    // Hàm lưu trữ cài đặt font chữ
    private void saveFontSettings(Typeface typeface) {
        if (typeface != null && typeface != Typeface.DEFAULT) {
            SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            String fontName;
            if (typeface.equals(Typeface.createFromAsset(getAssets(), "open_sans.ttf"))) {
                fontName = "open_sans.ttf";
            } else if (typeface.equals(Typeface.createFromAsset(getAssets(), "comfortaa.ttf"))) {
                fontName = "comfortaa.ttf";
            } else if (typeface.equals(Typeface.createFromAsset(getAssets(), "dancing_script_bold.ttf"))) {
                fontName = "dancing_script_bold.ttf";
            } else {
                fontName = "";
            }
            editor.putString("selectedFont", fontName);;
            editor.apply();
        }
    }



    // Hàm lưu trữ cài đặt kích thước chữ
    private void saveTextSizeSettings(float textSize) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("selectedTextSize", textSize);
        editor.apply();
    }

    // Hàm khôi phục cài đặt font chữ và kích thước chữ
    private void restoreSettings() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        // Khôi phục font chữ
        String fontName = preferences.getString("selectedFont", null);
        Typeface typeface = Typeface.DEFAULT;
        if (fontName != null) {
            try {
                typeface = Typeface.createFromAsset(getAssets(), fontName);
            } catch (Exception e) {
                Log.e("FontDayActivity", "Failed to create typeface from file", e);
                Toast.makeText(getApplicationContext(), "Failed to create typeface from file", Toast.LENGTH_SHORT).show();
            }
        }

        selectFontTextView.setTypeface(typeface);
        chooseSizeTextView.setTypeface(typeface);
        radioSmall.setTypeface(typeface);
        radioMedium.setTypeface(typeface);
        radioBig.setTypeface(typeface);

        // Khôi phục kích thước chữ
        float textSize = preferences.getFloat("selectedTextSize", 16);

        radioOpen_sans.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        radioDancing.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        radioComfortaa.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
    public void day_main(View view){
        Intent intent = new Intent(this, main.class);
        startActivity(intent);
        finish();
    }
    public void setting_day(View view) {
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
        if(user==null){
            Toast.makeText(this, "Vui lòng đăng nhập để bắt đầu nhắc nhở", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, log_in.class));
            finish();
        }else {
            Intent intent = new Intent(font.this, reminder_list.class);
            startActivity(intent);
            finish();
        }
    }
}
