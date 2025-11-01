package com.example.attendanceproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceproject.Model.Note;
import com.example.attendanceproject.Retrofit.NoteApi;
import com.example.attendanceproject.Retrofit.RetrofitService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for adding a new note.
 */
public class AddNote extends AppCompatActivity {
    private EditText noteTitleEditText;
    private EditText noteContentEditText;
    private RetrofitService retrofitService;
    private NoteApi noteApi;
    private String professorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        initializeViews();
        setupRetrofit();
        setupButtonListeners();
    }

    /**
     * Initializes the views from the layout.
     */
    private void initializeViews() {
        noteTitleEditText = findViewById(R.id.noteTitleEditText);
        noteContentEditText = findViewById(R.id.noteContentEditText);
    }

    /**
     * Sets up Retrofit service and API interface.
     * Also retrieves the professor ID.
     */
    private void setupRetrofit() {
        retrofitService = new RetrofitService();
        noteApi = retrofitService.getRetrofit().create(NoteApi.class);
        professorId = getProfessorId();

        if (professorId == null) {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_LONG).show();
            // Optionally navigate to login screen
            finish(); // Close the activity if user is not authenticated
        }
    }

    /**
     * Sets up listeners for the save and back buttons.
     */
    private void setupButtonListeners() {
        // Save button (checkmark icon)
        findViewById(R.id.true_icon).setOnClickListener(v -> saveNote());

        // Back button (return icon)
        findViewById(R.id.retour_icon).setOnClickListener(v -> finish());
    }

    /**
     * Validates input and saves the new note via API call.
     */
    private void saveNote() {
        String title = noteTitleEditText.getText().toString().trim();
        String content = noteContentEditText.getText().toString().trim();

        if (title.isEmpty()) {
            noteTitleEditText.setError("Title cannot be empty");
            noteTitleEditText.requestFocus(); // Focus the field with error
            return;
        }

        // Get current timestamp in ISO 8601 format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        Note newNote = new Note();
        newNote.setTitle(title);
        // Assuming Note model has setContent method. Original used setContenu.
        newNote.setContenu(content);
        newNote.setProfesseurId(professorId);
        newNote.setCreatedAt(currentTime);

        noteApi.createNote(newNote).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddNote.this, "Note saved successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Indicate success to the calling activity
                    finish(); // Close this activity
                } else {
                    String errorMsg = "Failed to save note.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " Error: " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e("AddNote", "Error reading error body", e);
                    }
                    Toast.makeText(AddNote.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("AddNote", "Failed to save note. Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Toast.makeText(AddNote.this, "Network error. Please check connection.", Toast.LENGTH_LONG).show();
                Log.e("AddNote", "Network error while saving note", t);
            }
        });
    }

    /**
     * Retrieves the professor ID from SharedPreferences.
     *
     * @return The stored professor ID, or null if not found.
     */
    private String getProfessorId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        // Consider adding a check if "user_id" exists before getting string
        return prefs.getString("user_id", null);
    }
}