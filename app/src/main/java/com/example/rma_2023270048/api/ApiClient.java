package com.example.rma_2023270048.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.rma_2023270048.database.AppDatabase;
import com.example.rma_2023270048.ui.auth.AuthActivity;
import com.example.rma_2023270048.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

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
            Context appContext = context.getApplicationContext();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();

                            // get token from shared preferences
                            SharedPreferences sharedPreferences = appContext.getApplicationContext()
                                    .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                            String token = sharedPreferences.getString("token", "");

                            Response response;
                            if (!token.isEmpty()) {
                                String authHeaderValue = token.startsWith("Bearer ") ? token : "Bearer " + token;

                                Request newReq = request.newBuilder()
                                        .header("Authorization", authHeaderValue)
                                        .build();
                                response = chain.proceed(newReq);
                            } else {
                                response = chain.proceed(request);
                            }
                            // in case token invalidates
                            if (response.code() == 401) {
                                sharedPreferences.edit()
                                        .remove("token")
                                        .remove("last_viewed_container_id")
                                        .apply();

                                Executors.newSingleThreadExecutor().execute(() -> {
                                    AppDatabase.getInstance(appContext)
                                            .containerDao().clearCache();
                                });

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(appContext, "Session expired. Please log in again", Toast.LENGTH_LONG).show();
                                });

                                Intent intent = new Intent(appContext, AuthActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                appContext.startActivity(intent);
                            }

                            return response;
                        }
                    }).build();
            Gson gson = new GsonBuilder()
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
