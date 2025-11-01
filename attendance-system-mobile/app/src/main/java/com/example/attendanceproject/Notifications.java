package com.example.attendanceproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.attendanceproject.Adapters.AbsenceNotificationAdapter;
import com.example.attendanceproject.Model.Classe;
import com.example.attendanceproject.Model.Etudiant;
import com.example.attendanceproject.Retrofit.AbsenceApi;
import com.example.attendanceproject.Retrofit.ClasseApi;
import com.example.attendanceproject.Retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notifications extends AppCompatActivity {
    private String professorId;
    private RecyclerView notifRecyclerView;
    private List<Etudiant> etudiantList = new ArrayList<>();
    private List<Classe> classeList = new ArrayList<>();
    private RetrofitService retrofitService;
    private AbsenceApi absenceApi;
    private ClasseApi classeApi;
    private AbsenceNotificationAdapter notifAdapter;
    private Map<String, String> classModuleMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        initializeViews();
        setupRetrofit();
        setupRecyclerView();

        findViewById(R.id.retour_icon).setOnClickListener(v -> finish());

        professorId = getProfessorId();
        if (professorId != null) {
            fetchClassModuleMapping();
        }
    }

    private void initializeViews() {
        notifRecyclerView = findViewById(R.id.notif_recycler);
    }

    private void setupRetrofit() {
        retrofitService = new RetrofitService();
        absenceApi = retrofitService.getRetrofit().create(AbsenceApi.class);
        classeApi = retrofitService.getRetrofit().create(ClasseApi.class);
    }

    private void setupRecyclerView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        notifRecyclerView.setLayoutManager(layoutManager);
        notifAdapter = new AbsenceNotificationAdapter(this, etudiantList,classModuleMap);
        notifRecyclerView.setAdapter(notifAdapter);
    }

    private String getProfessorId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_id", null);
    }
//
//    private void fetchClasses() {
//        classeApi.getClassesByProfesseur(professorId).enqueue(new Callback<List<Classe>>() {
//            @Override
//            public void onResponse(Call<List<Classe>> call, Response<List<Classe>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    classeList = response.body();
//                    // You might want to add a Spinner here to select classes like in web
//                    // For now, we'll fetch alerts for all classes
//                    fetchAlertes(null);
//                } else {
//                    Toast.makeText(Notifications.this, "Failed to fetch classes", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Classe>> call, Throwable t) {
//                Toast.makeText(Notifications.this, "Network error", Toast.LENGTH_SHORT).show();
//                Log.e("Notifications", "Failed to fetch classes", t);
//            }
//        });
//    }

    private void fetchAlertes(String classeId) {
        absenceApi.getAlertesAbsences(3, classeId).enqueue(new Callback<List<Etudiant>>() {
            @Override
            public void onResponse(Call<List<Etudiant>> call, Response<List<Etudiant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    etudiantList.clear();
                    etudiantList.addAll(response.body());
                    notifAdapter.notifyDataSetChanged();

                    if (etudiantList.isEmpty()) {
                        Toast.makeText(Notifications.this,
                                "No students with 3+ absences found",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Notifications.this, "Failed to fetch alerts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Etudiant>> call, Throwable t) {
                Toast.makeText(Notifications.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("Notifications", "Failed to fetch alerts", t);
            }
        });
    }

    private void fetchClassModuleMapping() {
        classeApi.getClassesByProfesseur(professorId).enqueue(new Callback<List<Classe>>() {
            @Override
            public void onResponse(Call<List<Classe>> call, Response<List<Classe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    classModuleMap.clear(); // Clear existing mappings
                    for (Classe classe : response.body()) {
                        classModuleMap.put(classe.getId(),
                                classe.getNom() + " - " + classe.getModule());
                    }
                    fetchAlertes(null);
                } else {
                    Toast.makeText(Notifications.this, "Failed to fetch classes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Classe>> call, Throwable t) {
                Toast.makeText(Notifications.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("Notifications", "Failed to fetch classes", t);
            }
        });
    }



}