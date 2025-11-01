package com.example.attendanceproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceproject.Model.Note;
import com.example.attendanceproject.Retrofit.NoteApi;
import com.example.attendanceproject.Retrofit.RetrofitService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class EditNote extends AppCompatActivity {
    private EditText noteTitleEditText;
    private EditText noteContentEditText;
    private String noteId;
    private RetrofitService retrofitService;
    private NoteApi noteApi;

    // Constants for Intent extras
    public static final String EXTRA_NOTE_ID = "NOTE_ID";
    public static final String EXTRA_NOTE_TITLE = "NOTE_TITLE";
    public static final String EXTRA_NOTE_CONTENT = "NOTE_CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using the layout R.layout.add_note (as specified in original code)
        setContentView(R.layout.add_note);

        initializeViews();
        setupRetrofit();
        loadNoteDataFromIntent();
        setupButtonListeners();
    }

    /**
     * Initializes the views from the layout.
     */
    private void initializeViews() {
        noteTitleEditText = findViewById(R.id.noteTitleEditText);
        noteContentEditText = findViewById(R.id.noteContentEditText);

        // Ensure EditTexts are focusable if needed (already focusable by default)
        // noteTitleEditText.setFocusableInTouchMode(true);
        // noteContentEditText.setFocusableInTouchMode(true);
    }

    /**
     * Sets up Retrofit service and API interface.
     */
    private void setupRetrofit() {
        retrofitService = new RetrofitService();
        noteApi = retrofitService.getRetrofit().create(NoteApi.class);
    }

    /**
     * Loads the note data passed via Intent into the EditText fields.
     */
    private void loadNoteDataFromIntent() {
        Intent intent = getIntent();
        noteId = intent.getStringExtra(EXTRA_NOTE_ID);
        String title = intent.getStringExtra(EXTRA_NOTE_TITLE);
        String content = intent.getStringExtra(EXTRA_NOTE_CONTENT);

        if (noteId == null) {
            Toast.makeText(this, "Error: Note ID missing.", Toast.LENGTH_LONG).show();
            Log.e("EditNote", "Note ID was not passed in the intent.");
            finish(); // Cannot edit without ID
            return;
        }

        noteTitleEditText.setText(title != null ? title : "");
        noteContentEditText.setText(content != null ? content : "");
    }

    /**
     * Sets up listeners for the save and back buttons.
     */
    private void setupButtonListeners() {
        // Save button (checkmark icon)
        findViewById(R.id.true_icon).setOnClickListener(v -> updateNote());

        // Back button (return icon)
        findViewById(R.id.retour_icon).setOnClickListener(v -> finish());
    }

    /**
     * Validates input and updates the existing note via API call.
     */
    private void updateNote() {
        String title = noteTitleEditText.getText().toString().trim();
        String content = noteContentEditText.getText().toString().trim();

        if (title.isEmpty()) {
            noteTitleEditText.setError("Title cannot be empty");
            noteTitleEditText.requestFocus();
            return;
        }

        Note updatedNote = new Note();
        updatedNote.setTitle(title);
        updatedNote.setContenu(content);

        noteApi.updateNote(noteId, updatedNote).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditNote.this, "Note updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Indicate success
                    finish(); // Close this activity and return to NoteDetails
                } else {
                    // ... error handling ...
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                // ... error handling ...
            }
        });
    }
}
