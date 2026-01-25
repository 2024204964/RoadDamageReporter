package com.example.roaddamagereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView profileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar (title only)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ✅ SETUP BOTTOM NAVIGATION (PROFILE PAGE)
        setupBottomNavigation(R.id.nav_profile);

        auth = FirebaseAuth.getInstance();
        profileText = findViewById(R.id.textView);

        user = auth.getCurrentUser();
        if (user != null) {
            profileText.setText(user.getEmail());
        }
    }

    // Logout button (unchanged)
    public void signout(View v) {
        auth.signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    // ✅ BOTTOM NAVIGATION METHOD (SAFE IF–ELSE VERSION)
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