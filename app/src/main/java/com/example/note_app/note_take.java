package com.example.note_app;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class note_take extends AppCompatActivity {

    private ImageButton buttonSetting, btnShare;
    EditText edtnotetitle, edtnotecontent;
    ImageButton buttonBack, btnDelete;
    boolean isEditMode = false;
    String notetitle, notecontent, noteday, docId, noteID;
    TextView noteDay, countCharacter;
    Button btnSaveNote;
    boolean isFavorite;
    Integer countword, countword_before_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_take);
        findbyviewIds();
        CheckBox cbFavorite = findViewById(R.id.cbFavorite);
        countCharacter = (TextView) findViewById(R.id.count_character_note);
        // click vào 1 item: 1. lấy dữ liệu từ intent trước (main)
        Intent intent = getIntent();
        if (intent != null) {
            notetitle = intent.getStringExtra("NOTE_TITLE");
            notecontent = intent.getStringExtra("NOTE_CONTENT");
            noteday = intent.getStringExtra("NOTE_DATE");
            isFavorite = intent.getBooleanExtra("IS_FAVORITE", false);
            noteID = intent.getStringExtra("NOTE_ID");
            edtnotecontent.setText(notecontent);
            edtnotetitle.setText(notetitle);
            noteDay.setText(noteday);
            cbFavorite.setChecked(isFavorite);
            if (edtnotecontent!= null)
                countword = edtnotecontent.length();
            countCharacter.setText("Số ký tự: " + countword.toString());
        }

        edtnotecontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countword = s.length();
                countCharacter.setText("Số ký tự: " + countword.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSetting();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharenote();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBack();
            }
        });

        //Save note
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notecontent == null)
                    saveNote();
                else {
                    deleteNoteFromFirebase();
                    saveNote();
                }
                openBack();
            }
        });
        //Delete note
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        // button setting -> popup menu
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        // Thêm sự kiện nghe cho CheckBox
        cbFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Xử lý khi trạng thái của CheckBox thay đổi
            if (isChecked) {
                // Note được đánh dấu là yêu thích
                Utility.showToast(note_take.this, "Note đã được đánh dấu là yêu thích");
            } else {
                // Note không được đánh dấu là yêu thích
                Utility.showToast(note_take.this, "Note không còn là yêu thích");
            }
        });

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

        EditText edt_note_title = findViewById(R.id.edt_note_title);
        TextView note_day = findViewById(R.id.note_day);
        TextView count_character_note = findViewById(R.id.count_character_note);
        EditText edt_note_content = findViewById(R.id.edt_note_content);
        ImageButton btnDeleteNote = findViewById(R.id.btnDeleteNote);

        edt_note_title.setTypeface(typeface);
        //edt_note_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        note_day.setTypeface(typeface);
        note_day.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        count_character_note.setTypeface(typeface);
        count_character_note.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        edt_note_content.setTypeface(typeface);
        edt_note_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        cbFavorite.setTypeface(typeface);
        cbFavorite.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
}
    //ngoài onCreate
    public void sharenote(){
        notetitle = edtnotetitle.getText().toString();
        notecontent = edtnotecontent.getText().toString();
        String fullNote = "Tiêu đề: " + notetitle + "\n\n" + "Nội dung: " +notecontent ;

        //Tạo intent chia sẻ nội dung
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, fullNote);
        startActivity(Intent.createChooser(shareIntent,"Chia sẻ nội dung ghi chú"));
    }
    public void openSetting()
    {
        Intent intent = new Intent(this, note_take.class);
        startActivity(intent);
        finish();
    }

    public void openBack(){
        Intent intent = new Intent(note_take.this, main.class);
        startActivity(intent);
        finish();
    }

        // Save note
    public void saveNote(){
        String noteTitle = edtnotetitle.getText().toString();
        String noteContent = edtnotecontent.getText().toString();
        if(noteTitle == null || noteTitle.isEmpty()){
            edtnotetitle.setError("Hãy nhập chủ đề");
            return;
        }
        Note note = new Note();
        note.setNote_title(noteTitle);
        note.setNote_content(noteContent);

        // Kiểm tra trạng thái của CheckBox
        CheckBox checkBoxFavorite = findViewById(R.id.cbFavorite);
        boolean isFavorite = checkBoxFavorite.isChecked();
        note.setFavorite(isFavorite);
        // note.setTimestamp(Timestamp.now());
        long time = System.currentTimeMillis();
        String formatTimestamp = formatTimestamp(time);
        // save time vào textview ngày tháng năm
        noteDay.setText("" + formatTimestamp);
        String note_day = formatTimestamp;
        // save time vào note_day
        note.setNote_day(note_day);
        saveNoteToFireBase(note);
    }
    String noteId;
    public void saveNoteToFireBase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(noteId);
            //deleteNoteFromFirebase();
        }else{
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
            noteId= documentReference.getId();
            note.setNote_id(noteId);
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is added
                    Utility.showToast(note_take.this,"Note added successfully");
                    //finish();
                }else{
                    Utility.showToast(note_take.this,"Failed while adding note");
                }
            }
        });
    }

    public String formatTimestamp(long timestamp) {
        // Tạo đối tượng SimpleDateFormat với định dạng mong muốn
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        // Tạo đối tượng Date từ timestamp
        Date date = new Date(timestamp);
        // Định dạng Date thành chuỗi ngày/thời gian
        return sdf.format(date);
    }

    // delete note
    public void deleteNoteFromFirebase(){
        DocumentReference documentReference;
            documentReference = Utility.getCollectionReferenceForNotes().document(noteID);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(note_take.this,"Note deleted successfully");
                    //finish();
                }else{
                    Utility.showToast(note_take.this,"Failed while deleting note");
                }
            }
        });
    }
    private void findbyviewIds(){
        edtnotetitle = (EditText) findViewById(R.id.edt_note_title);
        edtnotecontent= (EditText) findViewById(R.id.edt_note_content);
        noteDay = (TextView) findViewById(R.id.note_day);
        buttonSetting = (ImageButton) findViewById(R.id.ImageButtonSetting);
        btnShare = (ImageButton) findViewById(R.id.imgbtn_share);
        buttonBack = (ImageButton) findViewById(R.id.ImageButtonBack);
        btnSaveNote = (Button) findViewById(R.id.iBt_Save);
        btnDelete = (ImageButton) findViewById(R.id.btnDeleteNote);
    }

    public void showPopupMenu(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.remind)
                {
                    showMessage("Bạn đã chọn nhắc nhở");
                    Intent intent = new Intent(note_take.this, reminder_take.class);

                    // Lấy title từ intent note_take
                    String intentTitleNotetake = edtnotetitle.getText().toString();
                    intent.putExtra("Title", intentTitleNotetake);
                    intent.putExtra("source", "intent_note_take");
                    startActivity(intent);
                }
                return true;
            }
        });
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa ghi chú này không?");

        // Nút xác nhận xóa
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteFromFirebase();
                openBack();
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
}
