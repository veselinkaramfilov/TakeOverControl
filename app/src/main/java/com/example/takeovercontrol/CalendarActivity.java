package com.example.takeovercontrol;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    ImageButton homeBtn, logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        homeBtn = findViewById(R.id.home_image_button);
        logOutBtn = findViewById(R.id.account_image_button);
        logOutBtn.setOnClickListener((v) -> showLogout());
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoadNewActivity = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(intentLoadNewActivity);
            }
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
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date selectedDate = new Date(year - 1900, month, dayOfMonth);
                String selectedDateString = simpleDateFormat.format(selectedDate);
                collectionReference.whereEqualTo("date", selectedDateString)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    detailsList.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Details details = document.toObject(Details.class);
                                        detailsList.add(details);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(CalendarActivity.this, "Error loading details", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    void showLogout() {
        PopupMenu popupMenu = new PopupMenu(CalendarActivity.this, logOutBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(CalendarActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}
