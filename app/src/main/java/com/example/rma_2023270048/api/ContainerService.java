package com.example.rma_2023270048.api;

import com.example.rma_2023270048.models.Container;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ContainerService {

    @GET("api/containers")
    Call<List<Container>> getContainers();

    @PUT("api/containers/{id}/start")
    Call<Void> startContainer(@Path("id") int containerId);

    @PUT("api/containers/{id}/stop")
    Call<Void> stopContainer(@Path("id") int containerId);

}
