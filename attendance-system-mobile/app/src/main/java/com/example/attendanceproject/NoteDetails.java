package com.example.attendanceproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceproject.Model.Note;
import com.example.attendanceproject.Retrofit.NoteApi;
import com.example.attendanceproject.Retrofit.RetrofitService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class NoteDetails extends AppCompatActivity {
    private TextView noteTitle, noteContent, noteTime;
    private String noteId;
    private RetrofitService retrofitService;
    private NoteApi noteApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);

        initializeViews();
        setupRetrofit();
        loadNoteData();
        setupButtonListeners();
    }

    private void initializeViews() {
        noteTitle = findViewById(R.id.noteText);
        noteContent = findViewById(R.id.description_note);
        noteTime = findViewById(R.id.timeText);
    }

    private void setupRetrofit() {
        retrofitService = new RetrofitService();
        noteApi = retrofitService.getRetrofit().create(NoteApi.class);
    }

    private void loadNoteData() {
        // Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            noteId = intent.getStringExtra("NOTE_ID");

            // Safely set text (handle null cases)
            noteTitle.setText(intent.getStringExtra("NOTE_TITLE"));
            noteContent.setText(intent.getStringExtra("NOTE_CONTENT"));

            // Format and display date
            String rawDate = intent.getStringExtra("NOTE_TIME");
            String formattedDate = formatDate(rawDate); // Reuse your date formatter
            noteTime.setText(formattedDate);
        } else {
            Toast.makeText(this, "No note data found", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no data
        }
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) {
            return "No date";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date date = inputFormat.parse(rawDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return rawDate; // Fallback to raw date if parsing fails
        }
    }

    private void setupButtonListeners() {
        // Back button
        findViewById(R.id.retour_icon).setOnClickListener(v -> finish());

        // Menu button
        findViewById(R.id.menu_icon).setOnClickListener(v -> showPopupMenu(v));
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.inflate(R.menu.note_menu);

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_edit) {
                launchEditNote();
                return true;
            } else if (id == R.id.menu_delete) {
                showDeleteConfirmation();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void launchEditNote() {
        if (noteId == null) {
            Toast.makeText(this, "Cannot edit: Invalid note", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, EditNote.class);
        intent.putExtra("NOTE_ID", noteId);
        intent.putExtra("NOTE_TITLE", noteTitle.getText().toString());
        intent.putExtra("NOTE_CONTENT", noteContent.getText().toString());
        startActivity(intent);
    }

    private void showDeleteConfirmation() {
        if (noteId == null) {
            Toast.makeText(this, "Cannot delete: Invalid note", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNote())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote() {
        noteApi.deleteNote(noteId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NoteDetails.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(NoteDetails.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(NoteDetails.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}