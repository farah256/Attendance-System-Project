package com.example.attendanceproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    // Afficher l'activité d'inscription ou de connexion après 3 secondes au lancement de l'activité main..
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent  = new Intent(MainActivity.this, Connection.class);
                    startActivity(intent);
                    finish();
                }
            },3000);
        }
    }
