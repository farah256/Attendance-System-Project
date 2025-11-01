package com.example.attendanceproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
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

import com.example.attendanceproject.Adapters.StudentAttendanceAdapter;
import com.example.attendanceproject.Model.Abscence;
import com.example.attendanceproject.Model.Classe;
import com.example.attendanceproject.Model.Etudiant;
import com.example.attendanceproject.Retrofit.AbsenceApi;
import com.example.attendanceproject.Retrofit.EtudiantApi;
import com.example.attendanceproject.Retrofit.RetrofitService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClasseAttendance extends AppCompatActivity {
    private static final String TAG = "ClasseAttendance";
    private static final int MAX_FILE_SIZE_MB = 10; // Maximum file size in MB

    private TextView classeText, moduleText;
    private ImageView retour, submitAttendance;
    private RecyclerView studentsRecyclerView;
    private StudentAttendanceAdapter studentAdapter;
    private Classe currentClasse;
    private Map<String, Boolean> attendanceMap = new HashMap<>();
    private Uri selectedFileUri;
    private EditText justificationEditText;
    private EditText fileEditText;
    private RetrofitService retrofitService;
    private EtudiantApi etudiantApi;
    private AbsenceApi absenceApi;
    private Map<String, String> studentAbsenceMap = new HashMap<>();

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    if (selectedFileUri != null && fileEditText != null) {
                        String fileName = getFileName(selectedFileUri);
                        fileEditText.setText(fileName);

                        // Validate file size
                        validateFileSize(selectedFileUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classe_attendance);

        // Initialize Retrofit services once
        retrofitService = new RetrofitService();
        etudiantApi = retrofitService.getRetrofit().create(EtudiantApi.class);
        absenceApi = retrofitService.getRetrofit().create(AbsenceApi.class);

        initializeViews();
        setupRecyclerView();
        loadClassData();
        fetchStudents();
    }

    private void initializeViews() {
        retour = findViewById(R.id.retour_icon);
        submitAttendance = findViewById(R.id.true_icon);
        classeText = findViewById(R.id.classe_name_text);
        moduleText = findViewById(R.id.module_name_text);
        studentsRecyclerView = findViewById(R.id.recycler_attendance);

        retour.setOnClickListener(v -> finish());
        submitAttendance.setOnClickListener(v -> submitAttendance());
    }

    private void setupRecyclerView() {
        studentAdapter = new StudentAttendanceAdapter(new ArrayList<>(), (student, isPresent) -> {
            attendanceMap.put(student.getId(), isPresent);
        }, this::showJustificationDialog);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsRecyclerView.setAdapter(studentAdapter);
    }

    private void loadClassData() {
        currentClasse = (Classe) getIntent().getSerializableExtra("CLASS_EXTRA");
        if (currentClasse != null) {
            classeText.setText(currentClasse.getNom());
            moduleText.setText(currentClasse.getModule());
        }
    }

    private void fetchStudents() {
        if (currentClasse == null || currentClasse.getId() == null) {
            Toast.makeText(this, "Class information missing", Toast.LENGTH_SHORT).show();
            return;
        }

        etudiantApi.getEtudiantsByClasse(currentClasse.getId()).enqueue(new Callback<List<Etudiant>>() {
            @Override
            public void onResponse(Call<List<Etudiant>> call, Response<List<Etudiant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Etudiant> students = response.body();
                    updateStudentList(students);
                } else {
                    Toast.makeText(ClasseAttendance.this,
                            "Failed to load students: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Etudiant>> call, Throwable t) {
                Toast.makeText(ClasseAttendance.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateStudentList(List<Etudiant> students) {
        for (Etudiant student : students) {
            attendanceMap.put(student.getId(), true); // Default to present
        }
        studentAdapter.updateStudents(students);
    }

    private void submitAttendance() {
        if (currentClasse == null || currentClasse.getId() == null) {
            Toast.makeText(this, "Class information missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean hasAbsences = false;

        // First check if there are any absences to mark
        for (Boolean isPresent : attendanceMap.values()) {
            if (!isPresent) {
                hasAbsences = true;
                break;
            }
        }

        if (!hasAbsences) {
            Toast.makeText(this, "All students are present", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Process attendance for each student
        for (Map.Entry<String, Boolean> entry : attendanceMap.entrySet()) {
            String studentId = entry.getKey();
            boolean isPresent = entry.getValue();

            if (!isPresent) {
                markAbsence(studentId);
            }
        }

        Toast.makeText(this, "Attendance submitted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void markAbsence(String studentId) {
        absenceApi.marquerAbsence(studentId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the absence ID from the response and store it
                    try {
                        Map<String, Object> responseData = response.body();
                        if (responseData.containsKey("id")) {
                            String absenceId = responseData.get("id").toString();
                            studentAbsenceMap.put(studentId, absenceId);
                            Log.d(TAG, "Absence marked for student: " + studentId + ", absence ID: " + absenceId);
                        } else {
                            Log.e(TAG, "Absence ID not found in response for student: " + studentId);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing absence response: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(ClasseAttendance.this,
                            "Failed to mark absence for student: " + studentId,
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to mark absence. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ClasseAttendance.this,
                        "Network error for student: " + studentId,
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error marking absence: " + t.getMessage());
            }
        });
    }

    private void showJustificationDialog(Etudiant student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_justification, null);
        builder.setView(dialogView);

        justificationEditText = dialogView.findViewById(R.id.classe);
        fileEditText = dialogView.findViewById(R.id.fichier_edit);
        ImageView exportIcon = dialogView.findViewById(R.id.export);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        AlertDialog dialog = builder.create();

        // Handle file selection - accepts all file types
        View.OnClickListener fileSelectListener = v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(intent);
        };

        exportIcon.setOnClickListener(fileSelectListener);
        fileEditText.setOnClickListener(fileSelectListener);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        okButton.setOnClickListener(v -> {
            String justification = justificationEditText.getText().toString().trim();

            if (justification.isEmpty()) {
                Toast.makeText(this, "Please enter a justification", Toast.LENGTH_SHORT).show();
                return;
            }

            // First check if we have the absence ID for this student
            if (studentAbsenceMap.containsKey(student.getId())) {
                String absenceId = studentAbsenceMap.get(student.getId());
                submitJustification(absenceId, justification, dialog);
            } else {
                // If we don't have the absence ID yet, fetch it first
                fetchAbsenceIdAndSubmitJustification(student.getId(), justification, dialog);
            }
        });

        dialog.show();
    }

    private void fetchAbsenceIdAndSubmitJustification(String studentId, String justification, AlertDialog dialog) {
        // This method fetches the absence ID for a student before submitting justification
        // You'll need to implement an API method to get absences by student ID
        absenceApi.getAbsenceByEtudiant(studentId).enqueue(new Callback<List<Abscence>>() {
            @Override
            public void onResponse(Call<List<Abscence>> call, Response<List<Abscence>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Assuming we want the most recent absence
                    Abscence absence = response.body().get(0);
                    String absenceId = absence.getId();
                    studentAbsenceMap.put(studentId, absenceId);

                    // Now submit the justification with the correct absence ID
                    submitJustification(absenceId, justification, dialog);
                } else {
                    Toast.makeText(ClasseAttendance.this,
                            "No absence record found for this student",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Abscence>> call, Throwable t) {
                Toast.makeText(ClasseAttendance.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching absences: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private boolean validateFileSize(Uri fileUri) {
        try {
            Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                long fileSize = cursor.getLong(sizeIndex);
                cursor.close();

                // Check if file exceeds maximum size
                if (fileSize > MAX_FILE_SIZE_MB * 1024 * 1024) {
                    Toast.makeText(this,
                            "File is too large (max " + MAX_FILE_SIZE_MB + "MB)",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error validating file size: " + e.getMessage());
        }
        return false;
    }

    private void submitJustification(String absenceId, String justification, AlertDialog dialog) {
        Log.d(TAG, "Submitting justification for absence ID: " + absenceId);

        // Create a part for the justification text
        RequestBody justificationBody = RequestBody.create(MediaType.parse("text/plain"), justification);

        if (selectedFileUri != null) {
            try {
                // Validate file size again before uploading
                if (!validateFileSize(selectedFileUri)) {
                    return;
                }

                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                byte[] fileBytes = readFileContent(inputStream);

                String mimeType = getContentResolver().getType(selectedFileUri);
                if (mimeType == null) {
                    mimeType = "application/octet-stream"; // Default MIME type
                }

                RequestBody requestFile = RequestBody.create(
                        MediaType.parse(mimeType),
                        fileBytes
                );

                MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                        "fichier",
                        getFileName(selectedFileUri),
                        requestFile
                );

                // Call the API with the correct parameters
                absenceApi.justifierAbsence(absenceId, justification, filePart)
                        .enqueue(new Callback<Abscence>() {
                            @Override
                            public void onResponse(Call<Abscence> call, Response<Abscence> response) {
                                handleJustificationResponse(response, dialog);
                            }

                            @Override
                            public void onFailure(Call<Abscence> call, Throwable t) {
                                handleJustificationFailure(t, dialog);
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Error processing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error processing file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Call the API without a file
            absenceApi.justifierAbsence(absenceId, justification, null)
                    .enqueue(new Callback<Abscence>() {
                        @Override
                        public void onResponse(Call<Abscence> call, Response<Abscence> response) {
                            handleJustificationResponse(response, dialog);
                        }

                        @Override
                        public void onFailure(Call<Abscence> call, Throwable t) {
                            handleJustificationFailure(t, dialog);
                        }
                    });
        }
    }

    private void handleJustificationResponse(Response<Abscence> response, AlertDialog dialog) {
        if (response.isSuccessful()) {
            Toast.makeText(ClasseAttendance.this,
                    "Justification submitted successfully",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Justification submitted successfully");
        } else {
            try {
                String errorBody = response.errorBody() != null ?
                        response.errorBody().string() : "Unknown error";
                Log.e(TAG, "Justification error: " + response.code() + " - " + errorBody);
                Toast.makeText(ClasseAttendance.this,
                        "Failed to submit justification: " + response.code() + " - " + errorBody,
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, "Error reading error body: " + e.getMessage());
                e.printStackTrace();
            }
        }
        dialog.dismiss();
    }

    private void handleJustificationFailure(Throwable t, AlertDialog dialog) {
        Log.e(TAG, "Network error: " + t.getMessage());
        Toast.makeText(ClasseAttendance.this,
                "Network error: " + t.getMessage(),
                Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    private byte[] readFileContent(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}
