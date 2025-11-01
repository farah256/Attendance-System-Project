package com.example.attendanceproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceproject.Adapters.CalendarAdapter;
import com.example.attendanceproject.Adapters.ClassesAdapter;
import com.example.attendanceproject.Model.CalendarDay;
import com.example.attendanceproject.Model.Classe;
import com.example.attendanceproject.Retrofit.ClasseApi;
import com.example.attendanceproject.Retrofit.EtudiantApi;
import com.example.attendanceproject.Retrofit.RetrofitService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    private RecyclerView classesRecyclerView;
    private View emptyStateView;
    private ClassesAdapter classesAdapter;
    private String professorId;
    private TextView profName;
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private ImageView notifIcon;
    private EditText dialogFichierEdit;
    private Uri selectedFileUri;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleSelectedFile(result.getData().getData());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        initializeViews();
        setupCalendar();
        setupRecyclerView();
        fetchClassesData();
        setProfessorName();
        setupBottomNavigation();
        setupFloatingActionButton();
    }

    private void initializeViews() {
        classesRecyclerView = findViewById(R.id.classes_recycler_view);
        emptyStateView = findViewById(R.id.empty_state_view);
        profName = findViewById(R.id.professeur_name);
        calendarRecyclerView = findViewById(R.id.calendar_recycler_view);
        notifIcon = findViewById(R.id.notif);

        notifIcon.setOnClickListener(v -> startActivity(new Intent(this, Notifications.class)));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_home) {
                return true;
            } else if (itemId == R.id.page_note) {
                startActivity(new Intent(this, Notes.class));
                return true;
            } else if (itemId == R.id.page_statistic) {
                startActivity(new Intent(this, Statistics.class));
                return true;
            }
            return false;
        });
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> showAddClassDialog());
    }

    private void setupRecyclerView() {
        classesAdapter = new ClassesAdapter(new ArrayList<>(), classe -> {
            Intent intent = new Intent(this, ClasseAttendance.class);
            intent.putExtra("CLASS_EXTRA", classe);
            startActivity(intent);
        });

        classesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        classesRecyclerView.setAdapter(classesAdapter);
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
                Toast.makeText(Dashboard.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
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
        classesRecyclerView.setVisibility(View.VISIBLE);
        emptyStateView.setVisibility(View.GONE);
        classesAdapter.updateClasses(classes);
    }

    private void showEmptyState() {
        classesRecyclerView.setVisibility(View.GONE);
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

    private void setProfessorName() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nom = prefs.getString("nom", "");
        String prenom = prefs.getString("prenom", "");
        profName.setText(String.format("%s %s", nom, prenom));
    }

    private void setupCalendar() {
        List<CalendarDay> days = generateDays();
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        calendarRecyclerView.setLayoutManager(layoutManager);

        calendarAdapter = new CalendarAdapter(days, day -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            Toast.makeText(this, "Date: " + dateFormat.format(day.getDate()), Toast.LENGTH_SHORT).show();
        });
        calendarRecyclerView.setAdapter(calendarAdapter);

        scrollToToday(days);
    }

    private List<CalendarDay> generateDays() {
        List<CalendarDay> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -180);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.FRENCH);
        Calendar today = Calendar.getInstance();

        for (int i = 0; i < 365; i++) {
            Date date = calendar.getTime();
            int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            String dayName = dayFormat.format(date).substring(0, 3);

            boolean isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);

            days.add(new CalendarDay(dayNumber, dayName, isToday, date));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    private void scrollToToday(List<CalendarDay> days) {
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).isToday()) {
                calendarRecyclerView.scrollToPosition(i);
                break;
            }
        }
    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_classe_dialog, null);
        builder.setView(dialogView);

        EditText classeEditText = dialogView.findViewById(R.id.classe);
        EditText moduleEditText = dialogView.findViewById(R.id.module);
        dialogFichierEdit = dialogView.findViewById(R.id.fichier_edit);
        ImageView exportIcon = dialogView.findViewById(R.id.export);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        AlertDialog dialog = builder.create();

        // Update the filename display if a file was previously selected
        if (selectedFileUri != null) {
            dialogFichierEdit.setText(getFileName(selectedFileUri));
        }

        View.OnClickListener fileSelectListener = v -> openFilePicker();
        exportIcon.setOnClickListener(fileSelectListener);
        dialogFichierEdit.setOnClickListener(fileSelectListener);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        okButton.setOnClickListener(v -> {
            String className = classeEditText.getText().toString().trim();
            String moduleName = moduleEditText.getText().toString().trim();

            if (className.isEmpty() || moduleName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Classe newClasse = createNewClass(className, moduleName);
            if (selectedFileUri != null) {
                processCsvFile(newClasse, dialog);
            } else {
                addClassToBackend(newClasse, dialog);
            }
        });

        dialog.show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"text/csv", "text/comma-separated-values", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(intent);
    }

    private void handleSelectedFile(Uri uri) {
        String fileName = getFileName(uri);
        if (fileName != null && (fileName.toLowerCase().endsWith(".csv") || fileName.toLowerCase().endsWith(".xlsx"))) {
            selectedFileUri = uri;

            // Update the dialog's EditText if it exists
            if (dialogFichierEdit != null) {
                dialogFichierEdit.setText(fileName);
            }
        } else {
            Toast.makeText(this, "Please select a CSV or Excel file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;

        // First try with ContentResolver
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // Try different column names for compatibility
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex == -1) {
                        nameIndex = cursor.getColumnIndex("_display_name");
                    }
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }

        return result;
    }

    private Classe createNewClass(String className, String moduleName) {
        return new Classe(
                className,
                getCurrentAcademicYear(),
                moduleName,
                getProfessorId(),
                new ArrayList<>()
        );
    }

    private void addClassToBackend(Classe classe, AlertDialog dialog) {
        RetrofitService retrofitService = new RetrofitService();
        ClasseApi classeApi = retrofitService.getRetrofit().create(ClasseApi.class);

        classeApi.addClasse(classe).enqueue(new Callback<Classe>() {
            @Override
            public void onResponse(Call<Classe> call, Response<Classe> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Dashboard.this, "Class added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fetchClassesData();
                } else {
                    Toast.makeText(Dashboard.this, "Failed to add class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Classe> call, Throwable t) {
                Toast.makeText(Dashboard.this, "Network error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private String getCurrentAcademicYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        if (month >= Calendar.SEPTEMBER) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }
    private File createTempFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload", ".csv", getCacheDir());

        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    private void processCsvFile(Classe classe, AlertDialog dialog) {
        try {
            File csvFile = createTempFileFromUri(selectedFileUri);
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(selectedFileUri)),
                    csvFile
            );

            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    "file",
                    getFileName(selectedFileUri),
                    requestFile
            );

            // First add the class to get an ID, then upload the file
            addClassToBackendAndThenUploadFile(classe, dialog, filePart);
        } catch (Exception e) {
            Toast.makeText(this, "Error processing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void addClassToBackendAndThenUploadFile(Classe classe, AlertDialog dialog, MultipartBody.Part filePart) {
        RetrofitService retrofitService = new RetrofitService();
        ClasseApi classeApi = retrofitService.getRetrofit().create(ClasseApi.class);

        classeApi.addClasse(classe).enqueue(new Callback<Classe>() {
            @Override
            public void onResponse(Call<Classe> call, Response<Classe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Classe createdClasse = response.body();
                    String classeId = createdClasse.getId();

                    if (classeId != null && !classeId.isEmpty()) {
                        // Now we have a valid ID from the server
                        RequestBody classeIdBody = RequestBody.create(
                                MediaType.parse("text/plain"),
                                classeId
                        );

                        // Upload the file with the valid class ID
                        EtudiantApi etudiantApi = retrofitService.getRetrofit().create(EtudiantApi.class);
                        etudiantApi.importer(filePart, classeIdBody).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(Dashboard.this, "Class and students added successfully",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Dashboard.this, "Failed to import students: " +
                                            response.message(), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                fetchClassesData();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(Dashboard.this, "Network error during import: " +
                                        t.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(Dashboard.this, "Created class has no ID", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(Dashboard.this, "Failed to create class", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Classe> call, Throwable t) {
                Toast.makeText(Dashboard.this, "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

}