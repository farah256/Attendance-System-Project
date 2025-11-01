package com.example.attendanceproject.Retrofit;

import com.example.attendanceproject.Model.Note;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface NoteApi {

    @GET("/api/notes/{professeurId}")
    Call<List<Note>> getNotesByProf(@Path("professeurId") String professeurId);

    @POST("/api/notes")
    Call<Note> createNote(@Body Note note);

    @PATCH("/api/notes/{id}")
    Call<Note> updateNote(@Path("id") String id, @Body Note updatedNote);

    @DELETE("/api/notes/{id}")
    Call<Void> deleteNote(@Path("id") String id);

}