package com.example.attendanceproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceproject.Model.LoginResponse;
import com.example.attendanceproject.Model.UserLoginRequest;
import com.example.attendanceproject.Retrofit.RetrofitService;
import com.example.attendanceproject.Retrofit.UserApi;
import com.example.attendanceproject.helper.validerFormatEmail;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Connection extends AppCompatActivity {
    EditText email, motDePasse;
    Button connect;
    String mail, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);
        initializeConnection();

    }
    public void initializeConnection(){
        email = findViewById(R.id.email_input);
        motDePasse = findViewById(R.id.password_input);
        connect = findViewById(R.id.connect_button);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = email.getText().toString();
                password = motDePasse.getText().toString();

                if (validerSectionConnection()) {
                    UserLoginRequest request = new UserLoginRequest(mail, password);
                    userApi.login(request).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                LoginResponse loginResponse = response.body();

                                // Save token and user ID to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("auth_token", loginResponse.getToken());
                                editor.putString("user_id", loginResponse.getId());
                                editor.apply();

                                // Update Retrofit service with token
                                RetrofitService retrofitService = new RetrofitService();
                                retrofitService.setAuthToken(loginResponse.getToken());
                                fetchUserProfile(loginResponse.getId(),loginResponse.getToken());
                                Toast.makeText(Connection.this, "Connexion réussie !", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Connection.this, Dashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Toast.makeText(Connection.this, "Erreur: " + errorBody, Toast.LENGTH_LONG).show();
                                    Log.e("Login", "Error body: " + errorBody);
                                } catch (IOException e) {
                                    Toast.makeText(Connection.this, "Erreur de connexion", Toast.LENGTH_LONG).show();
                                    Log.e("Login", "Error parsing error body", e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Toast.makeText(Connection.this, "Échec de connexion !", Toast.LENGTH_LONG).show();
                            Log.e("Login", "Network failure", t);
                        }
                    });
                }

            }
        });

    }

    // Aller au page d'inscription si vous n'avez pas de compte.
    public void inscritPage(View view){
        Intent intent = new Intent(Connection.this, Inscription.class);
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
    public boolean validerMotDePasse(){
        if(password.isEmpty()){
            motDePasse.setError("Mot de passe est vide!");
            return false;
        }else{
            motDePasse.setError(null);
            return true;
        }

    }
    public boolean validerSectionConnection(){
        if(!validerEmail() || !validerMotDePasse()){
            return false;
        }
        return true;
    }
    private void fetchUserProfile(String userId, String token) {
        RetrofitService retrofitService = new RetrofitService();
        retrofitService.setAuthToken(token);
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        userApi.getUserProfile(userId).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse profileResponse = response.body();
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("nom", profileResponse.getLastName());
                    editor.putString("prenom", profileResponse.getFirstName());
                    editor.putString("email", profileResponse.getEmail());
                    // You might want to update other user details here
                    editor.apply();
                } else {
                    Log.e("Profile", "Failed to fetch profile: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Profile", "Network failure when fetching profile", t);
            }
        });
    }



}
