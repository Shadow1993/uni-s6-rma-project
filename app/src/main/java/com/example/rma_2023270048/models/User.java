package com.example.rma_2023270048.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class User {
    @SerializedName("id")
    private Long id;
    @SerializedName("active")
    private Boolean active;
    @SerializedName("createdAt")
    private LocalDateTime createdAt;
    @SerializedName("email")
    private String email;

    public User(Long id, Boolean active, LocalDateTime createdAt, String email) {
        this.id = id;
        this.active = active;
        this.createdAt = createdAt;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
