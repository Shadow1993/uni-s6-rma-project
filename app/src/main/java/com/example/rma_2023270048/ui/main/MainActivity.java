package com.example.rma_2023270048.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.rma_2023270048.R;
import com.example.rma_2023270048.database.AppDatabase;
import com.example.rma_2023270048.ui.auth.AuthActivity;
import com.example.rma_2023270048.ui.settings.SettingsActivity;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String LAST_VIEWED_CONTAINER = "last_viewed_container_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageButton btnMenu = findViewById(R.id.btn_action_menu);
        btnMenu.setOnClickListener(v -> showPopupMenu(btnMenu));

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int lastContainerId = prefs.getInt(LAST_VIEWED_CONTAINER, -1);
        if (savedInstanceState == null) {
            if (lastContainerId != -1) {
                // backstack setup
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, ContainerListFragment.newInstance())
                        .commit();

                switchFragment(ContainerDetailFragment.newInstance(lastContainerId));
            } else {
                switchFragment(ContainerListFragment.newInstance());
            }
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();

                    getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            .edit()
                            .remove(LAST_VIEWED_CONTAINER)
                            .commit();
                } else {
                    // fixes loop bug
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    private void showPopupMenu(ImageButton anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Settings");
        popup.getMenu().add(0, 2, 1, "Logout");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (item.getItemId() == 2) {
                handleLogout();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void handleLogout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .remove("token")
                .remove(LAST_VIEWED_CONTAINER)
                .apply();

//        clearcache
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(getApplicationContext())
                    .containerDao().clearCache();
        });

        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void switchFragment(Fragment fragment) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        if (fragment instanceof ContainerDetailFragment) {
            // save state for restoring
            int containerId = fragment.getArguments() != null ? fragment.getArguments().getInt("container_id", -1) : -1;
            prefs.edit().putInt(LAST_VIEWED_CONTAINER, containerId).apply();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            prefs.edit().remove(LAST_VIEWED_CONTAINER).apply();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().commit();
    }

}