package com.example.note_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.content.SharedPreferences;
import android.graphics.Typeface;

public class user_manager extends AppCompatActivity {

    TextView Email, Username;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    DatabaseReference databaseReference;

    ImageButton nightmode;
    SharedPreferences sharedPreferences;
    Boolean mode_status;
    SharedPreferences.Editor editor;
    FirebaseUser user = firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_manage);
        Username=findViewById(R.id.username);
        Email=findViewById(R.id.email);
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

        if (user != null) {
            String userUID = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("user").child(userUID);
            // Lắng nghe sự thay đổi trong dữ liệu
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Kiểm tra xem có dữ liệu không
                    if (dataSnapshot.exists()) {
                        // Lấy dữ liệu từ dataSnapshot và hiển thị lên TextView
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        Email.setText(email);
                        Username.setText(username);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu
                    Toast.makeText(user_manager.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu người dùng không đăng nhập
            Intent intent = new Intent(user_manager.this, log_in.class);
            startActivity(intent);
            finish();
        }

        // Đọc các cài đặt font và size từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        String fontName = preferences.getString("selectedFont", null);
        Typeface typeface = Typeface.DEFAULT;
        if (fontName != null) {
            try {
                typeface = Typeface.createFromAsset(getAssets(), fontName);
            } catch (Exception e) {
                Log.e("SettingDayActivity", "Failed to create typeface from file", e);
                Toast.makeText(getApplicationContext(), "Failed to create typeface from file", Toast.LENGTH_SHORT).show();
            }
        }

        float textSize = preferences.getFloat("selectedTextSize", 16);

        TextView textView= findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
        TextView username = findViewById(R.id.username);
        TextView textView3 = findViewById(R.id.textView3);
        TextView email = findViewById(R.id.email);
        Button changepsw = findViewById(R.id.changepasw);
        Button logout = findViewById(R.id.logout);

        textView.setTypeface(typeface);
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView2.setTypeface(typeface);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView3.setTypeface(typeface);
        textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        username.setTypeface(typeface);
        username.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        email.setTypeface(typeface);
        email.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        changepsw.setTypeface(typeface);
        changepsw.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        logout.setTypeface(typeface);
        logout.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }



    public void logout(View view){
        FirebaseAuth.getInstance().signOut(); // Đăng xuất người dùng
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(user_manager.this, setting.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa các Activity trước đó khỏi Stack
        startActivity(intent);
        finish(); // Đóng Activity hiện tại
    }
    public void changepass(View view) {
        // Chuyển hướng đến màn hình đổi mật khẩu
        Intent intent = new Intent(user_manager.this, changePassword.class);
        startActivity(intent);
        finish();
    }
    public void setting_day(View view){
        Intent intent = new Intent(this, setting.class);
        startActivity(intent);
        finish();
    }

    public void day_main(View view){
        Intent intent = new Intent(this, main.class);
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
            Intent intent = new Intent(user_manager.this, reminder_list.class);
            startActivity(intent);
            finish();
        }
    }
}