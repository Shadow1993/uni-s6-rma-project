package com.example.rma_2023270048.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.rma_2023270048.models.Container;

@Database(entities = {Container.class}, version = 1, exportSchema = false)
@TypeConverters({DataConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ContainerDao containerDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "container_manager_db"
                    )
                    .fallbackToDestructiveMigration()  //for dev
                    .build();
        }
        return instance;
    }
}
