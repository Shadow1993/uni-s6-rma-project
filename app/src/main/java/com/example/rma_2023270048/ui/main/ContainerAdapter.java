package com.example.rma_2023270048.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rma_2023270048.R;
import com.example.rma_2023270048.models.Container;

import java.util.ArrayList;
import java.util.List;

public class ContainerAdapter extends RecyclerView.Adapter<ContainerAdapter.ContainerViewHolder> {

    private List<Container> containerList = new ArrayList<>();
    private final OnContainerClickListener clickListener;

    // on row click
    public interface OnContainerClickListener {
        void onContainerClick(Container container);
    }

    public ContainerAdapter(OnContainerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setContainers(List<Container> containers) {
        this.containerList = containers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContainerAdapter.ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container, parent, false);
        return new ContainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerAdapter.ContainerViewHolder holder, int position) {
        Container container = containerList.get(position);
        holder.bind(container, clickListener);
    }

    @Override
    public int getItemCount() {
        return containerList != null ? containerList.size() : 0;
    }

    static class ContainerViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvImageDetails;
        private final TextView tvStatus;

        public ContainerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvImageDetails = itemView.findViewById(R.id.tv_item_image_details);
            tvStatus = itemView.findViewById(R.id.tv_item_status);
        }

        public void bind(Container container, OnContainerClickListener listener) {
            tvName.setText(container.getName());

            if (container.getImage() != null) {
                tvName.setText(container.getName());
                tvImageDetails.setText("Image: " + container.getImage().getName() + " (v" + container.getImage().getVersion() + ")");
            } else {
                tvImageDetails.setText("Image: Unknown");
            }

            if (container.getStatus() != null) {
                tvStatus.setText(container.getStatus().toString());
            } else {
                tvStatus.setText("UNKNOWN");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContainerClick(container);
                }
            });
        }
    }
}
