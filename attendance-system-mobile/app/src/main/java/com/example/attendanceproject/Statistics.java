package com.example.attendanceproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceproject.Adapters.StatisticsClassesAdapter;
import com.example.attendanceproject.Model.Classe;
import com.example.attendanceproject.Retrofit.ClasseApi;
import com.example.attendanceproject.Retrofit.RetrofitService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Statistics extends AppCompatActivity {

    private RecyclerView statisticsRecyclerView;
    private View emptyStateView;
    private StatisticsClassesAdapter statisticsAdapter;
    private String professorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        initializeViews();
        setupRecyclerView();
        fetchClassesData();
        setupBottomNavigation();
    }

    private void initializeViews() {
        statisticsRecyclerView = findViewById(R.id.statistics_classes_recycler_view);
        emptyStateView = findViewById(R.id.empty_state_view);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_home) {
                startActivity(new Intent(this, Dashboard.class));
                return true;
            } else if (itemId == R.id.page_note) {
                startActivity(new Intent(this, Notes.class));
                return true;
            } else if (itemId == R.id.page_statistic) {
                return true;
            }
            return false;
        });
        // Set statistics item as selected
        bottomNavigationView.setSelectedItemId(R.id.page_statistic);
    }

    private void setupRecyclerView() {
        statisticsAdapter = new StatisticsClassesAdapter(new ArrayList<>(), classe -> {
            // Handle class click - maybe open statistics details for this class
            Intent intent = new Intent(this, StatisticsDetails.class);
            intent.putExtra("CLASS_EXTRA", classe);
            startActivity(intent);
        });

        statisticsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statisticsRecyclerView.setAdapter(statisticsAdapter);
    }

    private void fetchClassesData() {
        professorId = getProfessorId();
        if (professorId == null) return;

        RetrofitService retrofitService = new RetrofitService();
        ClasseApi classeApi = retrofitService.getRetrofit().create(ClasseApi.class);

        classeApi.getClassesByProfesseur(professorId).enqueue(new Callback<List<Classe>>() {
            @Override
            public void onResponse(Call<List<Classe>> call, Response<List<Classe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Classe> classes = response.body();
                    updateClassesUI(classes);
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Classe>> call, Throwable t) {
                Toast.makeText(Statistics.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void updateClassesUI(List<Classe> classes) {
        if (classes.isEmpty()) {
            showEmptyState();
        } else {
            showClassesList(classes);
        }
    }

    private void showClassesList(List<Classe> classes) {
        statisticsRecyclerView.setVisibility(View.VISIBLE);
        emptyStateView.setVisibility(View.GONE);
        statisticsAdapter.updateClasses(classes);
    }

    private void showEmptyState() {
        statisticsRecyclerView.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.VISIBLE);
    }

    private String getProfessorId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Connection.class));
            finish();
        }
        return userId;
    }
}