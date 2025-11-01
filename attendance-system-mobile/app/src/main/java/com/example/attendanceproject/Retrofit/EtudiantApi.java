package com.example.attendanceproject.Retrofit;

import com.example.attendanceproject.Model.Etudiant;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import java.util.List;

public interface EtudiantApi {
    @Multipart
    @POST("/api/etudiants/importer")
    Call<String> importer(
            @Part MultipartBody.Part file,
            @Part("classeId") RequestBody classeId
    );

    @GET("/api/etudiants/classe/{classeId}")
    Call<List<Etudiant>> getEtudiantsByClasse(@Path("classeId") String classeId);


}