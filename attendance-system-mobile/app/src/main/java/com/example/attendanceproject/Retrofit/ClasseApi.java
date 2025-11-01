package com.example.attendanceproject.Retrofit;


import com.example.attendanceproject.Model.Classe;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ClasseApi {


    @GET("/api/classes/professeur/{profId}")
    Call<List<Classe>> getClassesByProfesseur(@Path("profId") String profId);

    @GET("/api/classes/{id}")
    Call<Classe> getClasseById(@Path("id") String id);

    @POST("/api/classes")
    Call<Classe> addClasse(@Body Classe classe);

    @DELETE("/api/classes/{id}")
    Call<Void> deleteClasse(@Path("id") String id);

    @PATCH("/api/classes/{id}")
    Call<Classe> updateNomEtModule(
            @Path("id") String id,
            @Body Map<String, String> updates);

}
