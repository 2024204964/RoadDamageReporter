package com.example.roaddamagereporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.bumptech.glide.Glide;

public class ReportDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvDesc, tvLat, tvLng, tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Report Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageView = findViewById(R.id.detailImage);
        tvDesc = findViewById(R.id.detailDesc);
        tvLat = findViewById(R.id.detailLat);
        tvLng = findViewById(R.id.detailLng);
        tvTime = findViewById(R.id.detailTime);

        String reportId = getIntent().getStringExtra("reportId");
        if (reportId == null || reportId.isEmpty()) {
            Toast.makeText(this, "No reportId received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("reports")
                .child(reportId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ReportDetailActivity.this, "Report not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                Report report = snapshot.getValue(Report.class);
                if (report == null) {
                    Toast.makeText(ReportDetailActivity.this, "Invalid report data", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                tvDesc.setText("Description: " + report.getDescription());
                tvLat.setText("Latitude: " + report.getLatitude());
                tvLng.setText("Longitude: " + report.getLongitude());
                tvTime.setText("Timestamp: " + report.getTimestamp());

                // Load image URI string
                String img = report.getImageUrl();
                if (img != null && !img.isEmpty()) {
                    Glide.with(ReportDetailActivity.this).load(img).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportDetailActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}