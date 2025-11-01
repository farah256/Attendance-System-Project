package com.example.attendanceproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.attendanceproject.Model.Classe;

public class StatisticsDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.static_date);

        Classe classe = (Classe) getIntent().getSerializableExtra("CLASS_EXTRA");

        if (classe != null) {
            TextView classeText = findViewById(R.id.classe_name_text);
            TextView moduleText = findViewById(R.id.module_name_text);
            classeText.setText(String.format(classe.getNom()));
            moduleText.setText(String.format(classe.getModule()));

            // Here you would fetch and display the statistics for this class
        }
    }
}