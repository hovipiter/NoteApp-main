package com.example.note_app;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class reminder_take extends AppCompatActivity {
    Button btnPickDate;
    Button btnPickTime;
    EditText editTextDate;
    EditText editTextTime;
    EditText editTextContent;
    private EditText edtContent, edtDate, edtTime;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, nDatabase;
    String key;
    String title, date, time;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_take);
        edtContent = findViewById(R.id.edt_content);
        edtDate = findViewById(R.id.date);
        edtTime = findViewById(R.id.time);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String source = intent.getStringExtra("source");

            // Kiểm tra xem Intent nào đã gửi dữ liệu
            if ("intent_remind_list".equals(source)) {
                // Dữ liệu từ Intent 1
                key = intent.getStringExtra("reminder_id");
                String title = intent.getStringExtra("title");
                date = intent.getStringExtra("date");
                time = intent.getStringExtra("time");

                // Hiển thị dữ liệu lên giao diện
                edtContent.setText(title);
                edtDate.setText(date);
                edtTime.setText(time);


            } else if ("intent_note_take".equals(source)) {
                // Dữ liệu từ Intent 2
                String contentIntentFromNotetake = intent.getStringExtra("Title");
                edtContent.setText(contentIntentFromNotetake);
            }
        }

        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        editTextDate = findViewById(R.id.date);
        editTextTime = findViewById(R.id.time);
        editTextContent = findViewById(R.id.edt_content);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userUID = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("reminder").child(userUID);
            if(key == null){
            key = mDatabase.push().getKey();
            }
            nDatabase = mDatabase.child(key);
        }

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String selectedDate = dateFormat.format(calendar.getTime());
                editTextDate.setText(selectedDate);
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                String selectedTime = timeFormat.format(calendar.getTime());
                editTextTime.setText(selectedTime);
            }
        };
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                reminder_take.this, dateSetListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                reminder_take.this, timeSetListener,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }


    public void save(View view) {
        date = editTextDate.getText().toString().trim();
        time = editTextTime.getText().toString().trim();
        title = editTextContent.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (!date.isEmpty() && !time.isEmpty()) {
            // Sử dụng key để xác định reminder cần chỉnh sửa trong Firebase Realtime Database
            DatabaseReference reminderRef = FirebaseDatabase.getInstance().getReference()
                    .child("reminder").child(currentUser.getUid()).child(key);

            // Update dữ liệu của reminder tương ứng trong Firebase Realtime Database
            Map<String, Object> reminderUpdates = new HashMap<>();
            reminderUpdates.put("Content", title);
            reminderUpdates.put("Date", date);
            reminderUpdates.put("Time", time);

            reminderRef.updateChildren(reminderUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(reminder_take.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(reminder_take.this, reminder_list.class);
                            startActivity(intent);
                            finish(); // Đóng activity sau khi chỉnh sửa
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(reminder_take.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Vui lòng nhập ngày và giờ", Toast.LENGTH_SHORT).show();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date dateTime = dateFormat.parse(date + " " + time);
            if (dateTime != null) {
                calendar.setTime(dateTime);
                setAlarm(calendar.getTimeInMillis());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void delete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhắc nhở này không?");
        // Nút xác nhận xóa
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performDeleteAction();
                dialog.dismiss();
            }
        });
        // Nút hủy
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Hiển thị AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void returned(View view){
        Intent intent = new Intent(reminder_take.this, reminder_list.class);
        startActivity(intent);
        finish();
    }
    private void setAlarm(long timeInMillis) {

        // Tạo Intent để gửi đến AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", editTextContent.getText().toString());
        intent.putExtra("date", editTextDate.getText().toString());
        intent.putExtra("time", editTextTime.getText().toString());
        alarmIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Đặt báo thức
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
        Toast.makeText(this, "Báo thức được đặt", Toast.LENGTH_SHORT).show();
    }
//private void sendNotification(long timeInMillis) {
//    // Tạo thông báo
//    Intent intent = new Intent(this, ReminderNotificationReceiver.class);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE);
//
//    // ... (Tạo notification)
//
//    // Hiển thị thông báo tại thời điểm xác định
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        NotificationChannel channel = new NotificationChannel("default", "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
//        notificationManager.createNotificationChannel(channel);
//    }
//
//    Notification notification = new Notification.Builder(this, "default")
//            .setContentTitle("Nhắc nhở: " + title)
//            .setContentText(date+" "+time)
//            .setSmallIcon(R.drawable.alarm)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build();
//
//    notificationManager.notify(0, notification);
//}
    private void performDeleteAction() {
        if (nDatabase != null) {
            nDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(reminder_take.this, "Đã xóa nhắc nhở", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(reminder_take.this, reminder_list.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(reminder_take.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
