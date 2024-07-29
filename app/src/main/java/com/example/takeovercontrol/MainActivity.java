package com.example.takeovercontrol;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_ACTIVITY = 1;
    private static final int REQUEST_CODE_QUICK_ADD_ACTIVITY = 2;
    private static final String CHANNEL_ID = "limit_exceeded_channel";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private Button addBtn, quickAddBtn;
    private ImageButton logOutBtn, calendarBtn, statsBtn;
    private TextView todayDate;
    private String dayOfDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
    private TextView sumUnit, sumCost, sum7Unit, sum7Cost;
    private FirebaseFirestore firestore;
    private float sumToday;
    private float sumLastSevenDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = findViewById(R.id.add_button);
        quickAddBtn = findViewById(R.id.quick_add_button);
        logOutBtn = findViewById(R.id.account_image_button);
        calendarBtn = findViewById(R.id.calendar_image_button);
        todayDate = findViewById(R.id.today_text);
        todayDate.setText(dayOfDate);
        sumUnit = findViewById(R.id.units_text_edit);
        sumCost = findViewById(R.id.cost_text_edit);
        sum7Unit = findViewById(R.id.last_seven_days_units_text_edit);
        sum7Cost = findViewById(R.id.last_seven_days_cost_text_edit);
        statsBtn = findViewById(R.id.stats_image_button);

        addBtn.setOnClickListener((v) -> startActivityForResult(new Intent(MainActivity.this, AddActivity.class), REQUEST_CODE_ADD_ACTIVITY));
        quickAddBtn.setOnClickListener((v) -> startActivityForResult(new Intent(MainActivity.this, QuickAddActivity.class), REQUEST_CODE_QUICK_ADD_ACTIVITY));
        logOutBtn.setOnClickListener((v) -> showLogout());

        sumUnitsForToday(new Date());
        sumCostsForToday(new Date());
        sumUnitsForLastSevenDays();
        sumCostsForLastSevenDays();

        calendarBtn.setOnClickListener((view) -> {
            Intent intentLoadNewActivity = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intentLoadNewActivity);
        });

        statsBtn.setOnClickListener((v) -> {
            Intent intentLoadNewActivity = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intentLoadNewActivity);
        });

        createNotificationChannel();
        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void showLogout() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, logOutBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if ("Logout".equals(menuItem.getTitle())) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_ACTIVITY || requestCode == REQUEST_CODE_QUICK_ADD_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                recreate();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void sumUnitsForToday(Date date) {
        firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = Utility.getCollectionReferenceForDetails();
        if (collectionReference != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = cal.getTime();

            Query query = collectionReference
                    .whereGreaterThanOrEqualTo("timestamp", startDate)
                    .whereLessThan("timestamp", endDate);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    float sum = 0.0f;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Double unitDouble = document.getDouble("unit");
                        if (unitDouble != null) {
                            float unit = unitDouble.floatValue();
                            sum += unit;
                        }
                    }
                    sumToday = Math.round(sum * 10.0f) / 10.0f;
                    sumUnit.setText(String.valueOf(sumToday));

                    checkAndShowNotifications();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details is null", Toast.LENGTH_SHORT).show();
        }
    }

    void sumCostsForToday(Date date) {
        firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = Utility.getCollectionReferenceForDetails();
        if (collectionReference != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = cal.getTime();

            Query query = collectionReference
                    .whereGreaterThanOrEqualTo("timestamp", startDate)
                    .whereLessThan("timestamp", endDate);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    double sum = 0.0f;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Double costDouble = document.getDouble("cost");
                        if (costDouble != null) {
                            double cost = costDouble.floatValue();
                            sum += cost;
                        }
                    }
                    double roundedSum = Math.round(sum * 100.0) / 100.0;
                    sumCost.setText(String.valueOf(roundedSum));
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details is null", Toast.LENGTH_SHORT).show();
        }
    }

    void sumUnitsForLastSevenDays() {
        firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = Utility.getCollectionReferenceForDetails();

        if (collectionReference != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date endDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            Date startDate = cal.getTime();

            Query query = collectionReference
                    .whereGreaterThanOrEqualTo("timestamp", startDate)
                    .whereLessThanOrEqualTo("timestamp", endDate);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    float sum = 0.0f;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Double unitDouble = document.getDouble("unit");
                        if (unitDouble != null) {
                            float unit = unitDouble.floatValue();
                            sum += unit;
                        }
                    }
                    sumLastSevenDays = Math.round(sum * 10.0f) / 10.0f;
                    sum7Unit.setText(String.valueOf(sumLastSevenDays));

                    checkAndShowNotifications();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details is null", Toast.LENGTH_SHORT).show();
        }
    }

    void sumCostsForLastSevenDays() {
        firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = Utility.getCollectionReferenceForDetails();

        if (collectionReference != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date endDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            Date startDate = cal.getTime();

            Query query = collectionReference
                    .whereGreaterThanOrEqualTo("timestamp", startDate)
                    .whereLessThanOrEqualTo("timestamp", endDate);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    double sum = 0.0f;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Double costDouble = document.getDouble("cost");
                        if (costDouble != null) {
                            double cost = costDouble.floatValue();
                            sum += cost;
                        }
                    }
                    double roundedSum = Math.round(sum * 100.0) / 100.0;
                    sum7Cost.setText(String.valueOf(roundedSum));
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Limit Exceeded";
            String description = "Notification for exceeding limits";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Be Mindful!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void checkAndShowNotifications() {
        if (sumToday > 2 && sumLastSevenDays > 14) {
            showNotification("You have exceeded daily and weekly limits!");
        } else if (sumToday > 2) {
            showNotification("You have exceeded daily limit!");
        } else if (sumLastSevenDays > 14) {
            showNotification("You have exceeded weekly limit!");
        }
    }
}
