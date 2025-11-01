package com.example.attendanceproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceproject.Model.SignupResponse;
import com.example.attendanceproject.Model.UserSignupRequest;
import com.example.attendanceproject.Retrofit.RetrofitService;
import com.example.attendanceproject.Retrofit.UserApi;
import com.example.attendanceproject.helper.validerFormatEmail;

import java.io.IOException;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inscription extends AppCompatActivity {
    EditText email, lastName ,firstName ,motDePasse;
    Button inscrit;
    String mail, nom, prenom, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);
        initializeInscription();

    }
    public void initializeInscription(){
        email = findViewById(R.id.email_input);
        lastName = findViewById(R.id.name_input);
        firstName = findViewById(R.id.prenom_input);
        motDePasse = findViewById(R.id.password_input);
        inscrit = findViewById(R.id.inscrit_button);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        inscrit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = email.getText().toString();
                nom = lastName.getText().toString();
                prenom = firstName.getText().toString();
                password = motDePasse.getText().toString();

                if (validerSectionInscription()) {
                    UserSignupRequest request = new UserSignupRequest(prenom, nom, mail, password);
                    userApi.registerUser(request).enqueue(new Callback<SignupResponse>() {
                        @Override
                        public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(Inscription.this, "Inscription avec succès ! Vérifiez votre email.", Toast.LENGTH_LONG).show();
                                connectPage(view);
                            } else {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Toast.makeText(Inscription.this, "Erreur: " + errorBody, Toast.LENGTH_LONG).show();
                                    Log.e("Inscription", "Error body: " + errorBody);
                                } catch (IOException e) {
                                    Toast.makeText(Inscription.this, "Erreur d'inscription", Toast.LENGTH_LONG).show();
                                    Log.e("Inscription", "Error parsing error body", e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SignupResponse> call, Throwable t) {
                            Toast.makeText(Inscription.this, "Échec d'inscription !", Toast.LENGTH_LONG).show();
                            Log.e("Inscription", "Network failure", t);
                        }
                    });
                }

            }
        });

    }
    // Aller au page de connection si vous avez déja un compte.
    public void connectPage(View view){
        Intent intent = new Intent(Inscription.this, Connection.class);
        startActivity(intent);
        finish();
    }

    public boolean validerEmail(){
        if(mail.isEmpty()){
            email.setError("Email est vide!");
            return false;

        }else if(!validerFormatEmail.regexEmailValidationPattern(mail)){
            email.setError("Email n'est pas valide!");
            return false;

        }else{
            email.setError(null);
            return true;
        }

    }

    public boolean validerNom(){
        if(nom.isEmpty()){
            lastName.setError("Nom est vide!");
            return false;
        }else{
            lastName.setError(null);
            return true;
        }

    }
    public boolean validerPrenom(){
        if(prenom.isEmpty()){
            firstName.setError("Prénom est vide!");
            return false;
        }else{
            firstName.setError(null);
            return true;
        }

    }
    public boolean validerMotDePasse(){
        if(password.isEmpty()){
            motDePasse.setError("Mot de passe est vide!");
            return false;
        }else{
            motDePasse.setError(null);
            return true;
        }

    }

    public boolean validerSectionInscription(){
        if(!validerEmail() || !validerNom() || !validerPrenom() || !validerMotDePasse()){
            return false;
        }
        return true;
    }

}
