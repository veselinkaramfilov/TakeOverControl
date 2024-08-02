package com.example.takeovercontrol;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private Spinner spinnerStatDate;
    private BarChart barChart;
    private TextView textViewDate, textViewUnit, textViewCost, textViewSelectionDetails;
    private ImageButton homeBtn, logOutBtn, calendarBtn;
    private ImageView imageViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        homeBtn = findViewById(R.id.home_image_button);
        calendarBtn = findViewById(R.id.calendar_image_button);
        logOutBtn = findViewById(R.id.account_image_button);
        barChart = findViewById(R.id.bar_chart);
        spinnerStatDate = findViewById(R.id.spinner_stat);
        textViewDate = findViewById(R.id.textView_bar_date);
        textViewUnit = findViewById(R.id.textView_bar_unit);
        textViewCost = findViewById(R.id.textView_bar_cost);
        textViewSelectionDetails = findViewById(R.id.textView_selection_details);
        imageViewStatus = findViewById(R.id.imageView_status);
        ArrayAdapter<CharSequence> adapterStatDate = ArrayAdapter.createFromResource(
                this, R.array.array_stat_date, android.R.layout.simple_spinner_item);
        adapterStatDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatDate.setAdapter(adapterStatDate);
        spinnerStatDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                fetchFirestoreData(selectedOption);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        homeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(StatsActivity.this, MainActivity.class);
            startActivity(intent);
        });
        calendarBtn.setOnClickListener(view -> {
            Intent intent = new Intent(StatsActivity.this, CalendarActivity.class);
            startActivity(intent);
        });
        logOutBtn.setOnClickListener(v -> showLogout());
        configureBarChart();
    }

    private void showLogout() {
        PopupMenu popupMenu = new PopupMenu(StatsActivity.this, logOutBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getTitle().equals("Logout")) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(StatsActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void configureBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void fetchFirestoreData(String selectedOption) {
        CollectionReference detailsRef = Utility.getCollectionReferenceForDetails();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        SimpleDateFormat weekFormat = new SimpleDateFormat("w yyyy", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String formattedStartDate;

        switch (selectedOption) {
            case "Last 7 Days":
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                formattedStartDate = simpleDateFormat.format(calendar.getTime());
                break;
            case "Last Month":
                calendar.add(Calendar.MONTH, -1);
                formattedStartDate = simpleDateFormat.format(calendar.getTime());
                break;
            case "Last Year":
                calendar.add(Calendar.YEAR, -1);
                formattedStartDate = simpleDateFormat.format(calendar.getTime());
                break;
            default:
                formattedStartDate = simpleDateFormat.format(calendar.getTime());
                break;
        }

        Query query = detailsRef.whereGreaterThanOrEqualTo("date", formattedStartDate);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            switch (selectedOption) {
                case "Last 7 Days":
                    handleDailyData(queryDocumentSnapshots, simpleDateFormat, displayFormat);
                    break;
                case "Last Month":
                    handleWeeklyData(queryDocumentSnapshots, simpleDateFormat, weekFormat);
                    break;
                case "Last Year":
                    handleMonthlyData(queryDocumentSnapshots, simpleDateFormat, monthFormat);
                    break;
            }
        });
    }

    private void handleDailyData(QuerySnapshot queryDocumentSnapshots, SimpleDateFormat simpleDateFormat, SimpleDateFormat displayFormat) {
        Map<String, Float> dailyUnitSumMap = new HashMap<>();
        Map<String, Float> dailyCostSumMap = new HashMap<>();

        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            Details details = documentSnapshot.toObject(Details.class);
            String dateString = details.getDate();
            try {
                Date date = simpleDateFormat.parse(dateString);
                String formattedDate = displayFormat.format(date);
                float unitValue = details.getUnit();
                float costValue = (float) details.getCost();
                dailyUnitSumMap.merge(formattedDate, unitValue, Float::sum);
                dailyCostSumMap.merge(formattedDate, costValue, Float::sum);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        updateChart(dailyUnitSumMap, dailyCostSumMap, displayFormat);
    }

    private void handleWeeklyData(QuerySnapshot queryDocumentSnapshots, SimpleDateFormat simpleDateFormat, SimpleDateFormat weekFormat) {
        Map<String, Float> weeklyUnitSumMap = new HashMap<>();
        Map<String, Float> weeklyCostSumMap = new HashMap<>();

        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            Details details = documentSnapshot.toObject(Details.class);
            String dateString = details.getDate();
            try {
                Date date = simpleDateFormat.parse(dateString);
                String formattedWeek = weekFormat.format(date);
                float unitValue = details.getUnit();
                float costValue = (float) details.getCost();
                weeklyUnitSumMap.merge(formattedWeek, unitValue, Float::sum);
                weeklyCostSumMap.merge(formattedWeek, costValue, Float::sum);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        float totalWeeklyUnits = 0;
        for (Float weeklyUnits : weeklyUnitSumMap.values()) {
            totalWeeklyUnits += weeklyUnits;
        }
        updateChart(weeklyUnitSumMap, weeklyCostSumMap, weekFormat);
        updateStatusImage("Last Month", totalWeeklyUnits);
    }

    private void handleMonthlyData(QuerySnapshot queryDocumentSnapshots, SimpleDateFormat simpleDateFormat, SimpleDateFormat monthFormat) {
        Map<String, Float> monthlyUnitSumMap = new HashMap<>();
        Map<String, Float> monthlyCostSumMap = new HashMap<>();

        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            Details details = documentSnapshot.toObject(Details.class);
            String dateString = details.getDate();
            try {
                Date date = simpleDateFormat.parse(dateString);
                String formattedMonth = monthFormat.format(date);
                float unitValue = details.getUnit();
                float costValue = (float) details.getCost();
                monthlyUnitSumMap.merge(formattedMonth, unitValue, Float::sum);
                monthlyCostSumMap.merge(formattedMonth, costValue, Float::sum);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        float totalMonthlyUnits = 0;
        for (Float monthlyUnits : monthlyUnitSumMap.values()) {
            totalMonthlyUnits += monthlyUnits;
        }
        updateChart(monthlyUnitSumMap, monthlyCostSumMap, monthFormat);
        updateStatusImage("Last Year", totalMonthlyUnits);
    }

    private void updateChart(Map<String, Float> unitSumMap, Map<String, Float> costSumMap, SimpleDateFormat dateFormat) {
        ArrayList<Map.Entry<String, Float>> sortedUnitEntries = new ArrayList<>(unitSumMap.entrySet());
        Collections.sort(sortedUnitEntries, (e1, e2) -> {
            try {
                return dateFormat.parse(e1.getKey()).compareTo(dateFormat.parse(e2.getKey()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        float totalUnits = 0f;
        float totalCosts = 0f;
        for (Map.Entry<String, Float> entry : sortedUnitEntries) {
            totalUnits += entry.getValue();
            totalCosts += costSumMap.getOrDefault(entry.getKey(), 0f);
        }
        String formattedTotalUnits = String.format(Locale.getDefault(), "%.1f", totalUnits);
        String formattedTotalCosts = String.format(Locale.getDefault(), "%.2f", totalCosts);
        String selectedOption = spinnerStatDate.getSelectedItem().toString();
        String detailsText = "";

        switch (selectedOption) {
            case "Last 7 Days":
                detailsText = "<b>Last 7 Days Total:<br>" +
                        "<b>Units:</b> " + formattedTotalUnits + "<br>" +
                        "<b>Costs:</b> " + formattedTotalCosts + " €";
                break;
            case "Last Month":
                detailsText = "<b>Last Month Total:<br>" +
                        "<b>Units:</b> " + formattedTotalUnits + "<br>" +
                        "<b>Costs:</b> " + formattedTotalCosts + " €";
                break;
            case "Last Year":
                detailsText = "<b>Last Year Total:<br>" +
                        "<b>Units:</b> " + formattedTotalUnits + "<br>" +
                        "<b>Costs:</b> " + formattedTotalCosts + " €";
                break;
            default:
                detailsText = "<b>Total Units:</b> " + formattedTotalUnits + "<br>" +
                        "<b>Total Costs:</b> " + formattedTotalCosts + " €";
                break;
        }

        textViewSelectionDetails.setText(Html.fromHtml(detailsText));
        ArrayList<BarEntry> unitEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Float> entry : sortedUnitEntries) {
            unitEntries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }
        BarDataSet unitDataSet = new BarDataSet(unitEntries, "Units");
        unitDataSet.setColor(ContextCompat.getColor(this, R.color.my_color));
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(unitDataSet);
        BarData barData = new BarData(dataSets);
        barData.setBarWidth(0.4f);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelRotationAngle(-45);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float unitValue = e.getY();
                String label = labels.get((int) e.getX());
                float costValue = costSumMap.getOrDefault(label, 0f);
                String formattedUnitValue = String.format(Locale.getDefault(), "%.1f", unitValue);
                String formattedCostValue = String.format(Locale.getDefault(), "%.2f", costValue);
                textViewDate.setText(Html.fromHtml("<b>Selected Date:</b> " + label));
                textViewUnit.setText(Html.fromHtml("<b>Units:</b> " + formattedUnitValue));
                textViewCost.setText(Html.fromHtml("<b>Costs:</b> " + formattedCostValue + " €"));
                updateStatusImage(spinnerStatDate.getSelectedItem().toString(), unitValue);
            }

            @Override
            public void onNothingSelected() {
                textViewDate.setText("");
                textViewUnit.setText("");
                textViewCost.setText("");
                imageViewStatus.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateStatusImage(String selectedOption, float totalUnits) {
        boolean showGreenFace = false;
        boolean showRedFace = false;

        switch (selectedOption) {
            case "Last 7 Days":
                showGreenFace = totalUnits < 2;
                showRedFace = totalUnits >= 2;
                break;
            case "Last Month":
                showGreenFace = totalUnits <= 14;
                showRedFace = totalUnits > 14;
                break;
            case "Last Year":
                showGreenFace = totalUnits <= 56;
                showRedFace = totalUnits > 56;
                break;
        }

        if (showGreenFace) {
            imageViewStatus.setImageResource(R.drawable.green_face);
            imageViewStatus.setVisibility(View.VISIBLE);
        } else if (showRedFace) {
            imageViewStatus.setImageResource(R.drawable.red_face);
            imageViewStatus.setVisibility(View.VISIBLE);
        } else {
            imageViewStatus.setVisibility(View.INVISIBLE);
        }
    }
}
