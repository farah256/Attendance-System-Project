package com.example.attendanceproject.Retrofit;

import com.example.attendanceproject.Model.Abscence;
import com.example.attendanceproject.Model.Etudiant;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AbsenceApi {
    @GET("/api/absences")
    Call<List<Abscence>> getAllAbsences();

    @GET("/api/absences/etudiant/{etudiantId}")
    Call<List<Abscence>> getAbsenceByEtudiant(@Path("etudiantId") String etudiantId);

    @GET("/api/absences/{id}")
    Call<Abscence> getAbsenceById(@Path("id") String id);

    @POST("/api/absences")
    Call<Abscence> addAbsence(@Body Abscence absence);

    @PUT("/api/absences/{id}")
    Call<Abscence> updateAbsence(@Path("id") String id, @Body Abscence updated);

    @GET("/api/absences/alertes")
    Call<List<Etudiant>> getAlertesAbsences(
            @Query("seuil") int seuil,
            @Query("classeId") String classeId
    );

    @DELETE("/api/absences/{id}")
    Call<Void> deleteAbsence(@Path("id") String id);

    @POST("/api/absences/etudiants/{etudiantId}")
    Call<Map<String, Object>> marquerAbsence(@Path("etudiantId") String etudiantId);

    @Multipart
    @PUT("/api/absences/justifier/{id}")
    Call<Abscence> justifierAbsence(
            @Path("id") String id,
            @Part("motif") String motif,
            @Part MultipartBody.Part fichier
    );

}