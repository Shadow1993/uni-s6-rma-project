package com.example.rma_2023270048.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.rma_2023270048.models.Container;

import java.util.List;

@Dao
public interface ContainerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContainers(List<Container> containers);

    @Query("SELECT * FROM container_cache")
    List<Container> getAllContainers();

    @Query("DELETE FROM container_cache")
    void clearCache();
}
