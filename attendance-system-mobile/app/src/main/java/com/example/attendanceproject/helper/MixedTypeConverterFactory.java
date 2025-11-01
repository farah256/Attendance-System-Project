package com.example.attendanceproject.helper;

import com.example.attendanceproject.Model.Classe;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MixedTypeConverterFactory extends Converter.Factory {
    private final Gson gson = new Gson();

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations, Retrofit retrofit) {

        // Handle String responses
        if (type == String.class) {
            return (Converter<ResponseBody, String>) ResponseBody::string;
        }

        // Handle List<Classe> responses
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType == List.class) {
                return (Converter<ResponseBody, List<Classe>>) responseBody -> {
                    String json = responseBody.string();
                    return gson.fromJson(json, type);
                };
            }
        }

        // Default to Gson for other response types
        return (Converter<ResponseBody, Object>) responseBody -> {
            String json = responseBody.string();
            return gson.fromJson(json, type);
        };
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

        // Use Gson for all request body conversions
        return (Converter<Object, RequestBody>) value -> {
            String json = gson.toJson(value);
            return RequestBody.create(okhttp3.MediaType.get("application/json; charset=UTF-8"), json);
        };
    }
}