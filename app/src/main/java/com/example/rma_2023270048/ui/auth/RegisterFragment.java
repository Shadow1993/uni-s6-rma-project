package com.example.rma_2023270048.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.rma_2023270048.R;
import com.example.rma_2023270048.api.ApiClient;
import com.example.rma_2023270048.api.AuthenticationService;
import com.example.rma_2023270048.models.Auth;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etEmail = view.findViewById(R.id.et_register_email);
        etPassword = view.findViewById(R.id.et_register_password);
        btnRegister = view.findViewById(R.id.btn_register);
        tvGoToLogin = view.findViewById(R.id.tv_go_to_login);

        tvGoToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnRegister.setOnClickListener(v -> handleRegister());

        return view;
    }

    private void handleRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        AuthenticationService authService = ApiClient.createService(AuthenticationService.class, getContext());
        Auth registrationData = new Auth(email, password);

        btnRegister.setEnabled(false);

        authService.register(registrationData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnRegister.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    try {
                        String raw = response.errorBody().string();
                        JSONObject errorObject = new JSONObject(raw);
                        String serverMessage = errorObject.getString("message");
                        Toast.makeText(getContext(), "Error: " + serverMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnRegister.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}