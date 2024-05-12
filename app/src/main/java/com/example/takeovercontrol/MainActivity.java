package com.example.takeovercontrol;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_ACTIVITY = 1;
    private Button addButton;
    private ImageButton logOutBtn, calendarBtn;
    private TextView todayDate;
    private String dayOfDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
    private TextView sumUnit, sumCost, sum7Unit, sum7Cost;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.add_button);
        logOutBtn = findViewById(R.id.account_image_button);
        calendarBtn = findViewById(R.id.calendar_image_button);
        todayDate = findViewById(R.id.today_text);
        todayDate.setText(dayOfDate);
        sumUnit = findViewById(R.id.units_text_edit);
        sumCost = findViewById(R.id.cost_text_edit);
        sum7Unit = findViewById(R.id.last_seven_days_units_text_edit);
        sum7Cost = findViewById(R.id.last_seven_days_cost_text_edit);

        addButton.setOnClickListener((v) -> startActivityForResult(new Intent(MainActivity.this, AddActivity.class), REQUEST_CODE_ADD_ACTIVITY));
        logOutBtn.setOnClickListener((v) -> showLogout());

        sumUnitsForToday(new Date());
        sumCostsForToday(new Date());
        sumUnitsForLastSevenDays();
        sumCostsForLastSevenDays();

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoadNewActivity = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });
    }

    void showLogout() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, logOutBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_ACTIVITY) {
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
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        float sum = 0.0f;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double unitDouble = document.getDouble("unit");
                            if (unitDouble != null) {
                                float unit = unitDouble.floatValue();
                                sum += unit;
                            }
                        }
                        float roundedSum = Math.round(sum * 10.0f) / 10.0f;
                        String sumString = String.valueOf(roundedSum);
                        sumUnit.setText(sumString);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details reference is null", Toast.LENGTH_SHORT).show();
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

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        double sum = 00.0f;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double costDouble = document.getDouble("cost");
                            if (costDouble != null) {
                                double cost = costDouble.floatValue();
                                sum += cost;
                            }
                        }
                        double roundedSum = Math.round(sum * 100.0) / 100.0;
                        String sumString = String.valueOf(roundedSum);
                        sumCost.setText(sumString);
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details reference is null", Toast.LENGTH_SHORT).show();
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
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        float sum = 0.0f;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double unitDouble = document.getDouble("unit");
                            if (unitDouble != null) {
                                float unit = unitDouble.floatValue();
                                sum += unit;
                            }
                        }
                        float roundedSum = Math.round(sum * 10.0f) / 10.0f;
                        String sumString = String.valueOf(roundedSum);
                        sum7Unit.setText(sumString);
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details reference is null", Toast.LENGTH_SHORT).show();
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
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        double sum = 00.0f;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double costDouble = document.getDouble("cost");
                            if (costDouble != null) {
                                double cost = costDouble.floatValue();
                                sum += cost;
                            }
                        }
                        double roundedSum = Math.round(sum * 100.0) / 100.0;
                        String sumString = String.valueOf(roundedSum);
                        sum7Cost.setText(sumString);
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User details reference is null", Toast.LENGTH_SHORT).show();
        }
    }
}

