package com.example.rma_2023270048.ui.settings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rma_2023270048.R;
import com.example.rma_2023270048.api.ApiClient;
import com.example.rma_2023270048.api.AuthenticationService;
import com.example.rma_2023270048.models.ChangePassword;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etNewPasswordConfirm;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        etOldPassword = findViewById(R.id.et_settings_old_password);
        etNewPassword = findViewById(R.id.et_settings_new_password);
        etNewPasswordConfirm = findViewById(R.id.et_settings_new_password_confirm);
        btnSave = findViewById(R.id.btn_settings_save);
        btnCancel = findViewById(R.id.btn_settings_cancel);

        // return to main
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> handlePasswordChange());
    }

    private void handlePasswordChange() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String newPasswordConfirm = etNewPasswordConfirm.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(newPasswordConfirm)) {
            Toast.makeText(this, "New Password fields don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);

        AuthenticationService authService = ApiClient.createService(AuthenticationService.class, this);
        ChangePassword updatePayload = new ChangePassword(oldPassword, newPassword);

        authService.changePassword(updatePayload).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, "Failed to change password. Verify current password", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(SettingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}