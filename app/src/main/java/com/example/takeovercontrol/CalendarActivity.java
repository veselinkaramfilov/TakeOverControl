package com.example.takeovercontrol;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private String userID;
    private DetailsAdapter adapter;
    private List<Details> detailsList;
    private ImageButton homeBtn, logOutBtn, statsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        homeBtn = findViewById(R.id.home_image_button);
        logOutBtn = findViewById(R.id.account_image_button);
        statsBtn = findViewById(R.id.stats_image_button);
        logOutBtn.setOnClickListener(v -> showLogout());
        homeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
            startActivity(intent);
        });
        statsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = Utility.getCollectionReferenceForDetails();
        if (collectionReference != null) {
            userID = collectionReference.getParent().getId();
        } else {
            return;
        }

        calendarView = findViewById(R.id.calendarViewLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        detailsList = new ArrayList<>();
        adapter = new DetailsAdapter(this, detailsList);
        recyclerView.setAdapter(adapter);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date selectedDate = new Date(year - 1900, month, dayOfMonth);
            String selectedDateString = simpleDateFormat.format(selectedDate);
            loadDetailsForSelectedDate(selectedDateString);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadDetailsForSelectedDate(String selectedDateString) {
        CollectionReference collectionReference = Utility.getCollectionReferenceForDetails();
        if (collectionReference != null) {
            collectionReference.whereEqualTo("date", selectedDateString)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            detailsList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Details details = document.toObject(Details.class);
                                detailsList.add(details);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CalendarActivity.this, "Error while loading the details", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showLogout() {
        PopupMenu popupMenu = new PopupMenu(CalendarActivity.this, logOutBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if ("Logout".equals(menuItem.getTitle())) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(CalendarActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
