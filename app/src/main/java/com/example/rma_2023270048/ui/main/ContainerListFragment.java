package com.example.rma_2023270048.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rma_2023270048.R;
import com.example.rma_2023270048.api.ApiClient;
import com.example.rma_2023270048.api.ContainerService;
import com.example.rma_2023270048.database.AppDatabase;
import com.example.rma_2023270048.models.Container;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContainerListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContainerListFragment extends Fragment {

    private RecyclerView rvContainers;
    private TextView tvEmptyView;
    private ContainerAdapter adapter;
    private AppDatabase database;

    public ContainerListFragment() {
        // Required empty public constructor
    }

    public static ContainerListFragment newInstance() {
        ContainerListFragment fragment = new ContainerListFragment();
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
        View view = inflater.inflate(R.layout.fragment_container_list, container, false);

        rvContainers = view.findViewById(R.id.rv_containers);
        tvEmptyView = view.findViewById(R.id.tv_empty_view);

        rvContainers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContainerAdapter(new ContainerAdapter.OnContainerClickListener() {
            @Override
            public void onContainerClick(Container container) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchFragment(ContainerDetailFragment.newInstance(container.getId()));
                }
            }
        });
        rvContainers.setAdapter(adapter);

        database = AppDatabase.getInstance(requireContext().getApplicationContext());

        loadCachedData();

        fetchLiveNetworkData();

        return view;
    }

    private void loadCachedData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Container> cachedList = database.containerDao().getAllContainers();

            // update data
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> updateUiState(cachedList));
            }
        });
    }

    private void fetchLiveNetworkData() {
        ContainerService containerService = ApiClient.createService(ContainerService.class, getContext());

        containerService.getContainers().enqueue(new Callback<List<Container>>() {
            @Override
            public void onResponse(Call<List<Container>> call, Response<List<Container>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Container> dynamicList = response.body();

                    updateUiState(dynamicList);

                    // cache update
                    Executors.newSingleThreadExecutor().execute(() -> {
                        database.containerDao().clearCache();
                        database.containerDao().insertContainers(dynamicList);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> updateUiState(dynamicList));
                        }
                    });
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
            public void onFailure(Call<List<Container>> call, Throwable t) {
                // debug
//                android.util.Log.e("NetworkError", "Network sync failed!", t);
                Toast.makeText(getContext(), "Offline mode: Showing cached data.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUiState(List<Container> list) {
        if (list == null || list.isEmpty()) {
            rvContainers.setVisibility(View.GONE);
            tvEmptyView.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            rvContainers.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
            adapter.setContainers(list);
        }
    }
}