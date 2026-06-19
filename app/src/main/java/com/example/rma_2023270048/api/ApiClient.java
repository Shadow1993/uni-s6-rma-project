package com.example.rma_2023270048.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rma_2023270048.utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // http://10.0.2.2:8080/ = local pc emulator address

    private static synchronized Retrofit getClient(Context context) {
        if (retrofit == null) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();

                            // get token from shared preferences
                            SharedPreferences sharedPreferences = context.getApplicationContext()
                                    .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                            String token = sharedPreferences.getString("token", "");

                            if (!token.isEmpty()) {
                                String authHeaderValue = token.startsWith("Bearer ") ? token : "Bearer " + token;

                                Request newReq = request.newBuilder()
                                        .header("Authorization", authHeaderValue)
                                        .build();
                                return chain.proceed(newReq);
                            }

                            return chain.proceed(request);
                        }
                    }).build();
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
    public static synchronized <T> T createService(Class<T> serviceClass, Context context) {
        return getClient(context).create(serviceClass);
    }
}
