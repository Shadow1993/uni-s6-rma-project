package com.example.rma_2023270048.database;

import androidx.room.TypeConverter;

import com.example.rma_2023270048.models.ContainerStatusEnum;
import com.example.rma_2023270048.models.Image;
import com.example.rma_2023270048.models.Node;
import com.example.rma_2023270048.models.NodeStatusEnum;
import com.example.rma_2023270048.models.User;
import com.example.rma_2023270048.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class DataConverter {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @TypeConverter
    public static ContainerStatusEnum toContainerStatus(String status) {
        return status == null ? null : ContainerStatusEnum.valueOf(status);
    }
    @TypeConverter
    public static String fromContainerStatus(ContainerStatusEnum status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static NodeStatusEnum toNodeStatus(String status) {
        return status == null ? null : NodeStatusEnum.valueOf(status);
    }
    @TypeConverter
    public static String fromNodeStatus(NodeStatusEnum status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static LocalDateTime toDateTime(String dateString) {
        if (dateString == null) return null;
        if (dateString.endsWith("Z")) dateString = dateString.substring(0, dateString.length() - 1);
        return LocalDateTime.parse(dateString);
    }
    @TypeConverter
    public static String fromDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

    @TypeConverter
    public static Image toImage(String json) {
        return json == null ? null : gson.fromJson(json, Image.class);
    }
    @TypeConverter
    public static String fromImage(Image image) {
        return image == null ? null : gson.toJson(image);
    }

    @TypeConverter
    public static Node toNode(String json) {
        return json == null ? null : gson.fromJson(json, Node.class);
    }
    @TypeConverter
    public static String fromNode(Node node) {
        return node == null ? null : gson.toJson(node);
    }

    @TypeConverter
    public static User toUser(String json) {
        return json == null ? null : gson.fromJson(json, User.class);
    }
    @TypeConverter
    public static String fromUser(User user) {
        return user == null ? null : gson.toJson(user);
    }
}