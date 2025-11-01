package com.example.attendanceproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.attendanceproject.Adapters.NoteAdapter;
import com.example.attendanceproject.Model.Note;
import com.example.attendanceproject.Retrofit.NoteApi;
import com.example.attendanceproject.Retrofit.RetrofitService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notes extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {
    private String professorId;
    private View emptyStateView;
    private RecyclerView notesRecyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notesList = new ArrayList<>();
    private FloatingActionButton fabAddNote;
    private RetrofitService retrofitService;
    private NoteApi noteApi;

    private static final int NOTE_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        initializeViews();
        setupRetrofit();
        setupRecyclerView();
        setupFabListener();
        fetchNotes();
        setupBottomNavigation();

        professorId = getProfessorId();
        if (professorId != null) {
            fetchNotes();
        }
    }

    private void initializeViews() {
        // Corrected RecyclerView ID to match notes.xml
        notesRecyclerView = findViewById(R.id.notes_recycler_view);
        fabAddNote = findViewById(R.id.floatingActionButton);
        emptyStateView = findViewById(R.id.empty_note);
    }

    private void setupRetrofit() {
        retrofitService = new RetrofitService();
        noteApi = retrofitService.getRetrofit().create(NoteApi.class);
    }
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_home) {
                startActivity(new Intent(this, Dashboard.class));
                return true;
            } else if (itemId == R.id.page_note) {
                return true;
            } else if (itemId == R.id.page_statistic) {
                startActivity(new Intent(this, Statistics.class));
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        notesRecyclerView.setLayoutManager(layoutManager);

        noteAdapter = new NoteAdapter(this, notesList, this);
        notesRecyclerView.setAdapter(noteAdapter);
    }

    private void setupFabListener() {
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(Notes.this, AddNote.class);
            startActivityForResult(intent, NOTE_ACTIVITY_REQUEST_CODE);
        });
    }

    private void fetchNotes() {
        if (professorId == null || noteApi == null) {
            Log.e("Notes", "Cannot fetch notes: professorId or noteApi is null.");
            return;
        }

        noteApi.getNotesByProf(professorId).enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notesList.clear();
                    notesList.addAll(response.body());
                    noteAdapter.updateNotes(notesList); // Use updateNotes instead of notifyDataSetChanged
                    updateEmptyStateView();
                } else {
                    Toast.makeText(Notes.this, "Failed to load notes.", Toast.LENGTH_SHORT).show();
                    updateEmptyStateView();
                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                Toast.makeText(Notes.this, "Network error loading notes.", Toast.LENGTH_SHORT).show();
                updateEmptyStateView();
            }
        });
    }

    private void updateEmptyStateView() {
        if (notesList.isEmpty()) {
            notesRecyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            notesRecyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, NoteDetails.class);
        intent.putExtra("NOTE_ID", note.getId());
        intent.putExtra("NOTE_TITLE", note.getTitle());
        intent.putExtra("NOTE_CONTENT", note.getContenu());
        intent.putExtra("NOTE_TIME", formatDate(note.getCreatedAt()));
        startActivityForResult(intent, NOTE_ACTIVITY_REQUEST_CODE);
    }

    private String formatDate(String originalDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(originalDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return originalDate;
        }
    }

    private String getProfessorId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_id", null);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchNotes();
        }
    }
}