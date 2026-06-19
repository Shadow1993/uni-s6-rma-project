package com.example.rma_2023270048.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Image {

    @SerializedName("id")
    private Long id;
    @SerializedName("active")
    private Boolean active;
    @SerializedName("createdAt")
    private LocalDateTime createdAt;
    @SerializedName("name")
    private String name;
    @SerializedName("requiredRam")
    private Long requiredRam;
    @SerializedName("requiredStorage")
    private Long requiredStorage;
    @SerializedName("url")
    private String url;
    @SerializedName("version")
    private Long version;

    public Image(Long id, Boolean active, LocalDateTime createdAt, String name, Long requiredRam, Long requiredStorage, String url, Long version) {
        this.id = id;
        this.active = active;
        this.createdAt = createdAt;
        this.name = name;
        this.requiredRam = requiredRam;
        this.requiredStorage = requiredStorage;
        this.url = url;
        this.version = version;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRequiredRam() {
        return requiredRam;
    }

    public void setRequiredRam(Long requiredRam) {
        this.requiredRam = requiredRam;
    }

    public Long getRequiredStorage() {
        return requiredStorage;
    }

    public void setRequiredStorage(Long requiredStorage) {
        this.requiredStorage = requiredStorage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
