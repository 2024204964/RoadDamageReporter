package com.example.roaddamagereporter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private ArrayList<Report> reportList;
    private boolean openedFromNotification;

    public ReportAdapter(ArrayList<Report> reportList, boolean openedFromNotification) {
        this.reportList = reportList;
        this.openedFromNotification = openedFromNotification;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(new Date(report.getTimestamp()));

        holder.title.setText("Report: " +
                report.getDescription() + " (" + formattedTime + ")");

        holder.subtitle.setText("Reported by: " + report.getUserId());

        if (openedFromNotification && position == reportList.size() - 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFF9C4"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        if (report.getImagePath() != null && !report.getImagePath().isEmpty()) {
            Glide.with(holder.image.getContext())
                    .load(report.getImagePath())
                    .placeholder(R.drawable.logo1)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.logo1);
        }

        // ✅ CLICK → DETAIL PAGE
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MarkerDetailActivity.class);
            intent.putExtra("report", report);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imag_id);
            title = itemView.findViewById(R.id.title_id);
            subtitle = itemView.findViewById(R.id.subtitle_id);
        }
    }
}
