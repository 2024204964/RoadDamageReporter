package com.example.roaddamagereporter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roaddamagereporter.AboutActivity;
import com.example.roaddamagereporter.MapActivity;
import com.example.roaddamagereporter.ProfileActivity;
import com.example.roaddamagereporter.ReportActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ReportListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private ArrayList<Report> reportList;
    private DatabaseReference dbRef;

    private Button btnCallJKR;

    // Flag to check if opened from push notification
    private boolean openedFromNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        // ✅ SETUP BOTTOM NAVIGATION (REPORT LIST PAGE)
        setupBottomNavigation(R.id.nav_list);

        recyclerView = findViewById(R.id.reportRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportList = new ArrayList<>();

        // ✅ ONLY KEEP CALL JKR BUTTON
        btnCallJKR = findViewById(R.id.btnCallJKR);
        btnCallJKR.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1800885004")); // JKR phone number
            startActivity(intent);
        });

        // Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference("reports");

        // Check if opened from notification
        openedFromNotification = getIntent().getBooleanExtra("fromNotification", false);

        reportAdapter = new ReportAdapter(reportList, openedFromNotification);
        recyclerView.setAdapter(reportAdapter);

        // Load reports from Firebase
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Report report = snapshot.getValue(Report.class);
                if (report != null) {
                    reportList.add(report);
                    reportAdapter.notifyItemInserted(reportList.size() - 1);

                    if (openedFromNotification) {
                        recyclerView.scrollToPosition(reportList.size() - 1);
                    }
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportListActivity.this,
                        "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -------------------- BOTTOM NAVIGATION --------------------
    private void setupBottomNavigation(int selectedItemId) {

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == selectedItemId) return true;

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