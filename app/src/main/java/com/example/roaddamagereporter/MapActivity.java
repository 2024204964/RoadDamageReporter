package com.example.roaddamagereporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

// ✅ EXPLICIT R IMPORT (FIXES RED nav_* IDs)
import com.example.roaddamagereporter.AboutActivity;
import com.example.roaddamagereporter.MarkerDetailActivity;
import com.example.roaddamagereporter.ProfileActivity;
import com.example.roaddamagereporter.R;

import com.example.roaddamagereporter.Report;
import com.example.roaddamagereporter.ReportActivity;
import com.example.roaddamagereporter.ReportListActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference dbRef;
    private List<Report> reportList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // ✅ SETUP BOTTOM NAVIGATION (MAP IS HOME)
        setupBottomNavigation(R.id.nav_map);

        dbRef = FirebaseDatabase.getInstance().getReference("reports");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnCallJKR = findViewById(R.id.btnCallJKR);

        btnCallJKR.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1800885004")); // 🔴 Replace with real JKR number
            startActivity(intent);
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMap.clear();
                reportList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Report report = ds.getValue(Report.class);
                    if (report != null) {
                        LatLng location = new LatLng(
                                report.getLatitude(),
                                report.getLongitude()
                        );

                        Marker marker = mMap.addMarker(
                                new MarkerOptions()
                                        .position(location)
                                        .title(report.getDescription())
                        );

                        if (marker != null) {
                            marker.setTag(report);
                        }

                        reportList.add(report);
                    }
                }

                if (!reportList.isEmpty()) {
                    LatLng first = new LatLng(
                            reportList.get(0).getLatitude(),
                            reportList.get(0).getLongitude()
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 12));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapActivity.this,
                        "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMarkerClickListener(marker -> {
            Report report = (Report) marker.getTag();
            if (report != null) {
                Intent i = new Intent(MapActivity.this, MarkerDetailActivity.class);
                i.putExtra("report", report);
                startActivity(i);
            }
            return true;
        });
    }

    // ✅ BOTTOM NAVIGATION METHOD (FIXED)
    private void setupBottomNavigation(int selectedItemId) {

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == selectedItemId) {
                return true;
            }

            if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));

            } else if (id == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));

            } else if (id == R.id.nav_report) {
                startActivity(new Intent(this, ReportActivity.class));

            } else if (id == R.id.nav_list) {
                startActivity(new Intent(this, ReportListActivity.class));

            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }

            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}