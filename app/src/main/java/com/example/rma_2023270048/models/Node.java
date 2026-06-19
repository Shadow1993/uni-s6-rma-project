package com.example.rma_2023270048.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Node {

    @SerializedName("id")
    private Long id;
    @SerializedName("active")
    private Boolean active;
    @SerializedName("createdAt")
    private LocalDateTime createdAt;
    @SerializedName("name")
    private String name;
    @SerializedName("ram")
    private Long ram;
    @SerializedName("storage")
    private Long storage;
    @SerializedName("url")
    private String url;
    @SerializedName("status")
    private NodeStatusEnum status;

    public Node(Long id, Boolean active, LocalDateTime createdAt, String name, Long ram, Long storage, String url, NodeStatusEnum status) {
        this.id = id;
        this.active = active;
        this.createdAt = createdAt;
        this.name = name;
        this.ram = ram;
        this.storage = storage;
        this.url = url;
        this.status = status;
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

    public Long getRam() {
        return ram;
    }

    public void setRam(Long ram) {
        this.ram = ram;
    }

    public Long getStorage() {
        return storage;
    }

    public void setStorage(Long storage) {
        this.storage = storage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NodeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(NodeStatusEnum status) {
        this.status = status;
    }
}
