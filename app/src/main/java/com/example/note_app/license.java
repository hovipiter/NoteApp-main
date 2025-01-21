package com.example.note_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;

public class license extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infor_license);

        ImageButton backButton = findViewById(R.id.license_back_main);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(license.this, setting.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
