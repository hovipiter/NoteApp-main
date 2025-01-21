package com.example.note_app;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class item_note extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_note);

        // Đọc các cài đặt font và size từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String fontName = preferences.getString("selectedFont", null);
        Typeface typeface = Typeface.DEFAULT;
        if (fontName != null) {
            try {
                typeface = Typeface.createFromAsset(getAssets(), fontName);
            } catch (Exception e) {
                Log.e("item_note", "Failed to create typeface from file", e);
                Toast.makeText(getApplicationContext(), "Failed to create typeface from file", Toast.LENGTH_SHORT).show();
            }
        }

        float textSize = preferences.getFloat("selectedTextSize", 16);

        TextView tv_notetitle = findViewById(R.id.tv_notetitle);
        TextView tv_noteday = findViewById(R.id.tv_noteday);

        tv_notetitle.setTypeface(typeface);
        tv_notetitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        tv_noteday.setTypeface(typeface);
        tv_noteday.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
}