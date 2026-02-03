package com.example.roaddamagereporter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    private TextView titleText, aboutText, meetText, githubLink;
    private ImageView member1Image, member2Image, member3Image, member4Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // SETUP BOTTOM NAVIGATION (ABOUT PAGE)
        setupBottomNavigation(R.id.nav_about);

        // Initialize TextViews
        titleText = findViewById(R.id.titleText);
        aboutText = findViewById(R.id.aboutText);
        meetText = findViewById(R.id.meetText);
        githubLink = findViewById(R.id.githubLink);

        // Initialize ImageViews
        member1Image = findViewById(R.id.member1Image);
        member2Image = findViewById(R.id.member2Image);
        member3Image = findViewById(R.id.member3Image);
        member4Image = findViewById(R.id.member4Image);

        // Set images
        member1Image.setImageResource(R.drawable.aqmar);
        member2Image.setImageResource(R.drawable.faizatul);
        member3Image.setImageResource(R.drawable.ain);
        member4Image.setImageResource(R.drawable.fannin);

        // Optional: set text (already defined in XML)
        titleText.setText("Road Damage Reporter");
        aboutText.setText("This app allows users to report road damage in real-time, including location, image, and description.");
        meetText.setText("Meet Our Team Members");
        githubLink.setText("Click Here to View Our Github Page");
    }

    // BOTTOM NAVIGATION METHOD (NO SWITCH, SAFE VERSION)
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