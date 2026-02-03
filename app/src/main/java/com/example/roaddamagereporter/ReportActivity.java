package com.example.roaddamagereporter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class ReportActivity extends AppCompatActivity {

    private ImageView imageViewPhoto;
    private Button buttonTakePhoto, buttonSelectPhoto, buttonSubmit;
    private EditText editTextDescription;

    private Uri photoUri;
    private static final int REQUEST_CAMERA = 101;
    private static final int REQUEST_LOCATION = 102;
    private static final int REQUEST_GALLERY = 103;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private static final String CHANNEL_ID = "report_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // ✅ SETUP BOTTOM NAVIGATION (REPORT PAGE)
        setupBottomNavigation(R.id.nav_report);

        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        editTextDescription = findViewById(R.id.editTextDescription);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("reports");

        createNotificationChannel();

        buttonTakePhoto.setOnClickListener(v -> checkCameraPermission());
        buttonSelectPhoto.setOnClickListener(v -> openGallery());
        buttonSubmit.setOnClickListener(v -> submitReport());
    }

    // -------------------- CAMERA --------------------
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Report Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo of road damage");
        photoUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    // -------------------- GALLERY --------------------
    private void openGallery() {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), photoUri);
                imageViewPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_GALLERY
                && resultCode == RESULT_OK && data != null) {

            photoUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), photoUri);
                imageViewPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // -------------------- GPS & SUBMIT --------------------
    private void getLocationAndSubmit(String description) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        saveReport(description);
                    } else {
                        Toast.makeText(this,
                                "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitReport() {
        String description = editTextDescription.getText().toString().trim();

        if (photoUri == null || description.isEmpty()) {
            Toast.makeText(this,
                    "Photo and description required", Toast.LENGTH_SHORT).show();
            return;
        }

        getLocationAndSubmit(description);
    }

    // --- Firebase ---
    private void saveReport(String description) {
        String userId = mAuth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        Report report = new Report(userId, description, photoUri.toString(), latitude, longitude, timestamp);

        // Create report node and get reportId (push key)
        DatabaseReference newRef = dbRef.push();
        String reportId = newRef.getKey();

        newRef.setValue(report).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Report submitted", Toast.LENGTH_SHORT).show();

                // Notification that opens THIS report
                sendNotification(reportId);
// Optional: go to list after submit
                Intent i = new Intent(ReportActivity.this, ReportListActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Failed to submit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Notification ---
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Report Channel";
            String description = "Notifications for new reports";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(String reportId) {
        // Tap notification -> open ReportDetailActivity for that reportId
        Intent intent = new Intent(this, ReportDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("reportId", reportId);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                2001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle("New Road Damage Report")
                .setContentText("Your report has been submitted successfully!")
                .setPriority(PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
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