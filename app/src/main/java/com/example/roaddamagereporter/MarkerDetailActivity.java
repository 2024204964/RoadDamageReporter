package com.example.roaddamagereporter;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.util.Date;

public class MarkerDetailActivity extends AppCompatActivity {

    private ImageView imageViewDetail;
    private TextView textDescription, textUser, textDate, textLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Report Detail");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageViewDetail = findViewById(R.id.imageViewDetail);
        textDescription = findViewById(R.id.textDescription);
        textUser = findViewById(R.id.textUser);
        textDate = findViewById(R.id.textDate);
        textLocation = findViewById(R.id.textLocation);

        Report report = (Report) getIntent().getSerializableExtra("report");

        if (report != null) {
            textDescription.setText(report.getDescription());
            textUser.setText("Reported by: " + report.getUserId());
            textDate.setText("Date: " + DateFormat.getDateTimeInstance().format(new Date(report.getTimestamp())));
            textLocation.setText("Location: " + report.getLatitude() + ", " + report.getLongitude());

            if (report.getImagePath() != null && !report.getImagePath().isEmpty()) {
                Glide.with(this)
                        .load(report.getImagePath())
                        .placeholder(R.drawable.logo1)
                        .into(imageViewDetail);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}