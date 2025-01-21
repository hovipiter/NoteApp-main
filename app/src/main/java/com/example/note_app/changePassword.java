package com.example.note_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class changePassword extends AppCompatActivity {

    TextView Username, Password1, Password2;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
        Username = findViewById(R.id.username);
        Password1 = findViewById(R.id.passwd);
        Password2 = findViewById(R.id.passwd2);

        FirebaseUser user = firebaseAuth.getCurrentUser();

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
                        String username = dataSnapshot.child("username").getValue(String.class);

                        Username.setText(username);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu
                    Toast.makeText(changePassword.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent intent = new Intent(changePassword.this, log_in.class);
            startActivity(intent);
            finish();
        }
        findViewById(R.id.btn_changepaswd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserPassword();
            }
        });
    }

            private void changeUserPassword() {
                String newPassword = Password1.getText().toString().trim();
                String confirmPassword = Password2.getText().toString().trim();

                if (!newPassword.equals(confirmPassword)) {
                    // Nếu mật khẩu nhập lại không khớp với mật khẩu mới
                    Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thực hiện đổi mật khẩu
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Thành công khi đổi mật khẩu
                                AlertDialog.Builder builder = new AlertDialog.Builder(changePassword.this);
                                builder.setMessage("Mật khẩu đã được thay đổi")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Perform your action here when "OK" is clicked
                                                Intent intent = new Intent(changePassword.this, log_in.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                // Xảy ra lỗi khi đổi mật khẩu
                                Toast.makeText(changePassword.this, "Thay đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
    }
    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), R.string.send_Email_Verification, Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();// Đăng xuất người dùng
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.fail_to_send_email_verify,Toast.LENGTH_SHORT).show();
        }
    }
}
