package com.example.rma_2023270048.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

@Entity(tableName = "container_cache")
public class Container {

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("active")
    private Boolean active;

    @SerializedName("createdAt")
    private LocalDateTime createdAt;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private ContainerStatusEnum status;

    @SerializedName("startedAt")
    private LocalDateTime startedAt;

    @SerializedName("stoppedAt")
    private LocalDateTime stoppedAt;

    @SerializedName("image")
    private Image image;

    @SerializedName("node")
    private Node node;

    @SerializedName("user")
    private User user;

    public Container(int id, Boolean active, LocalDateTime createdAt, String name, ContainerStatusEnum status, LocalDateTime startedAt, LocalDateTime stoppedAt, Image image, Node node, User user) {
        this.id = id;
        this.active = active;
        this.createdAt = createdAt;
        this.name = name;
        this.status = status;
        this.startedAt = startedAt;
        this.stoppedAt = stoppedAt;
        this.image = image;
        this.node = node;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public ContainerStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ContainerStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(LocalDateTime stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
