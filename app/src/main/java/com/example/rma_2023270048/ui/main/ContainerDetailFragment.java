package com.example.rma_2023270048.ui.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rma_2023270048.R;
import com.example.rma_2023270048.api.ApiClient;
import com.example.rma_2023270048.api.ContainerService;
import com.example.rma_2023270048.database.AppDatabase;
import com.example.rma_2023270048.models.Container;

import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContainerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContainerDetailFragment extends Fragment {

    private static final String ARG_CONTAINER_ID = "container_id";
    private int containerId;
    private Container currentContainer;

    private TextView tvName, tvStatus, tvImage, tvNode, tvUser, tvCreated, tvStarted, tvStopped;
    private Button btnStart, btnStop;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isCurrentlyOnline = false;

    public ContainerDetailFragment() {
        // Required empty public constructor
    }

    public static ContainerDetailFragment newInstance(int id) {
        ContainerDetailFragment fragment = new ContainerDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CONTAINER_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            containerId = getArguments().getInt(ARG_CONTAINER_ID);
        }
        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container_detail, container, false);

        tvName = view.findViewById(R.id.tv_detail_name);
        tvStatus = view.findViewById(R.id.tv_detail_status);
        tvImage = view.findViewById(R.id.tv_detail_image);
        tvNode = view.findViewById(R.id.tv_detail_node);
        tvUser = view.findViewById(R.id.tv_detail_user);
        tvCreated = view.findViewById(R.id.tv_detail_created);
        tvStarted = view.findViewById(R.id.tv_detail_started);
        tvStopped = view.findViewById(R.id.tv_detail_stopped);
        btnStart = view.findViewById(R.id.btn_start);
        btnStop = view.findViewById(R.id.btn_stop);

        isCurrentlyOnline = checkInitialNetworkCapabilities();

        setButtonsEnabled(false);
        loadContainerDetails();

        btnStart.setOnClickListener(v -> performPowerAction(true));
        btnStop.setOnClickListener(v -> performPowerAction(false));

        startNetworkMonitoring();

        return view;
    }

    private void loadContainerDetails() {
        // load from cache
        Executors.newSingleThreadExecutor().execute(() -> {
            Container container = AppDatabase.getInstance(requireContext().getApplicationContext())
                                    .containerDao()
                                    .getContainerById(containerId);

            if (container != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    currentContainer = container;
                    populateUi(container);
                });
            }
        });
    }

    private void populateUi(Container c) {
        tvName.setText(c.getName());
        tvStatus.setText("STATUS: " + (c.getStatus() != null ? c.getStatus().name() : "UNKNOWN"));

        if (!isCurrentlyOnline) {
            btnStart.setEnabled(false);
            btnStart.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AFA9A9")));
            tvStatus.setText("OFFLINE MODE");
        } else if (c.getStatus() != null) {
            switch (c.getStatus()) {
                case RUNNING:
                    btnStart.setEnabled(false);
                    btnStart.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AFA9A9")));
                    btnStop.setEnabled(true);
                    btnStop.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
                    break;
                case STOPPED:
                    btnStart.setEnabled(true);
                    btnStart.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
                    btnStop.setEnabled(false);
                    btnStop.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AFA9A9")));
                    break;

                default:
                    setButtonsEnabled(true);
                    break;
            }
        } else {
            setButtonsEnabled(true);
        }

        if (c.getImage() != null) {
            tvImage.setText("Image: " + c.getImage().getName() + " (v" + c.getImage().getVersion() + ")");
        }
        if (c.getNode() != null) {
            tvNode.setText("Node: " + c.getNode().getName() + " [" + c.getNode().getStatus() + "]");
        }
        if (c.getUser() != null) {
            tvUser.setText("Owner: " + c.getUser().getEmail());
        }

        // Use custom datetime conversions cleanly
        tvCreated.setText("Created At: " + (c.getCreatedAt() != null ? c.getCreatedAt().format(timeFormatter) : "--"));
        tvStarted.setText("Started At: " + (c.getStartedAt() != null ? c.getStartedAt().format(timeFormatter) : "--"));
        tvStopped.setText("Stopped At: " + (c.getStoppedAt() != null ? c.getStoppedAt().format(timeFormatter) : "--"));
    }

    private void performPowerAction(boolean start) {
        setButtonsEnabled(false);
        ContainerService service = ApiClient.createService(ContainerService.class, getContext());
        Call<Void> call = start ? service.startContainer(containerId) : service.stopContainer(containerId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setButtonsEnabled(true);
                if (response.isSuccessful()) {
                    String msg = start ? "Container started successfully!" : "Container stopped successfully!";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    // return to main screen
                    if (getParentFragmentManager() != null) {
                        getParentFragmentManager().popBackStack();
                    }
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
                setButtonsEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        btnStart.setEnabled(enabled);
        btnStop.setEnabled(enabled);

        btnStart.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(enabled ? "#FF5722" : "#AFA9A9")));
        btnStop.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(enabled ? "#FF5722" : "#AFA9A9")));
    }

    private boolean checkInitialNetworkCapabilities() {
        if (connectivityManager == null) return false;
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) return false;
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void startNetworkMonitoring() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isCurrentlyOnline = true;
                refreshUiOnMainThread();
            }

            @Override
            public void onLost(@NonNull Network network) {
                noNetwork();
            }

            @Override
            public void onUnavailable() {
                noNetwork();
            }

        };
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    private void noNetwork() {
        isCurrentlyOnline = false;
        refreshUiOnMainThread();
    }

    private void refreshUiOnMainThread() {
        if (getActivity() != null && currentContainer != null) {
            getActivity().runOnUiThread(() -> populateUi(currentContainer));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {



    }
}