package com.example.rma_2023270048.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.rma_2023270048.models.Token;
import com.example.rma_2023270048.ui.main.MainActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.et_login_email);
        etPassword = view.findViewById(R.id.et_login_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvGoToRegister = view.findViewById(R.id.tv_go_to_register);

        tvGoToRegister.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).switchFragment(new RegisterFragment());
            }
        });

        btnLogin.setOnClickListener(v -> handleLogin());

        return view;
    }

    private void handleLogin() {
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
        Auth loginCredentials = new Auth(email, password);

        btnLogin.setEnabled(false);

        authService.login(loginCredentials).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
//                Log.d("TEST", response.toString());
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // save token
                    String tokenValue = response.body().getToken();

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("token", tokenValue).remove("last_viewed_container_id").commit();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
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
            public void onFailure(Call<Token> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}