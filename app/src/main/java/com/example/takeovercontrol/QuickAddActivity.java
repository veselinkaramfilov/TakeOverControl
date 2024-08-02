package com.example.takeovercontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuickAddActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private String selectedBrandType, selectedBrand;
    private Spinner spinnerBrandType, spinnerBrand;
    private Button saveBtn, cancelBtn, barcodeScannerBtn;
    private ImageButton homeBtn, logOutBtn, calendarBtn, statsBtn;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Date currentDate = new Date();
    private String formattedDate = simpleDateFormat.format(currentDate);
    private TextView textViewSelectedType, textViewSelectedBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_add);

        saveBtn = findViewById(R.id.save_button);
        cancelBtn = findViewById(R.id.cancel_button);
        barcodeScannerBtn = findViewById(R.id.barcode_scanner_button);
        logOutBtn = findViewById(R.id.account_image_button);
        calendarBtn = findViewById(R.id.calendar_image_button);
        homeBtn = findViewById(R.id.home_image_button);
        statsBtn = findViewById(R.id.stats_image_button);
        spinnerBrandType = findViewById(R.id.spinner_brand_type);
        spinnerBrand = findViewById(R.id.spinner_brand);
        textViewSelectedType = findViewById(R.id.textview_type);
        textViewSelectedBrand = findViewById(R.id.textview_brand);
        ArrayAdapter<CharSequence> adapterBrandType = ArrayAdapter.createFromResource(this, R.array.array_brand_type, android.R.layout.simple_spinner_item);
        adapterBrandType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrandType.setAdapter(adapterBrandType);
        saveBtn.setOnClickListener(v -> saveDetails());
        logOutBtn.setOnClickListener(v -> showLogout());
        cancelBtn.setOnClickListener(v -> {
            Intent intentLoadNewActivity = new Intent(QuickAddActivity.this, MainActivity.class);
            startActivity(intentLoadNewActivity);
        });
        homeBtn.setOnClickListener(v -> {
            Intent intentLoadNewActivity = new Intent(QuickAddActivity.this, MainActivity.class);
            startActivity(intentLoadNewActivity);
        });
        calendarBtn.setOnClickListener(v -> {
            Intent intentLoadNewActivity = new Intent(QuickAddActivity.this, CalendarActivity.class);
            startActivity(intentLoadNewActivity);
        });
        statsBtn.setOnClickListener(v -> {
            Intent intentLoadNewActivity = new Intent(QuickAddActivity.this, StatsActivity.class);
            startActivity(intentLoadNewActivity);
        });

        spinnerBrandType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBrandType = spinnerBrandType.getSelectedItem().toString();
                setTextViewType(selectedBrandType);
                setupBrandSpinner(selectedBrandType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBrand = spinnerBrand.getSelectedItem().toString();
                setTextViewBrand(selectedBrand);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        barcodeScannerBtn.setOnClickListener(v -> startBarcodeScanner());
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void startBarcodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedValue = result.getContents();
                Toast.makeText(this, "Scanned: " + scannedValue, Toast.LENGTH_SHORT).show();
                if (scannedValue.equals("5000213028179")) {
                    spinnerBrandType.setSelection(getIndex(spinnerBrandType, "Beer"));
                    new Handler().postDelayed(() -> {
                        spinnerBrand.setSelection(getIndex(spinnerBrand, "Guinness"));
                    }, 300);
                } else {
                    Toast.makeText(this, "Scanned value does not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No barcode found", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    private void setupBrandSpinner(String type) {
        ArrayAdapter<CharSequence> adapterBrand;
        switch (type) {
            case "Beer":
                adapterBrand = ArrayAdapter.createFromResource(this, R.array.array_brand_beer, android.R.layout.simple_spinner_item);
                break;
            case "Whiskey":
                adapterBrand = ArrayAdapter.createFromResource(this, R.array.array_brand_whiskey, android.R.layout.simple_spinner_item);
                break;
            case "Wine":
                adapterBrand = ArrayAdapter.createFromResource(this, R.array.array_brand_wine, android.R.layout.simple_spinner_item);
                break;
            case "Vodka":
                adapterBrand = ArrayAdapter.createFromResource(this, R.array.array_brand_vodka, android.R.layout.simple_spinner_item);
                break;
            case "Tequila":
                adapterBrand = ArrayAdapter.createFromResource(this, R.array.array_brand_tequila, android.R.layout.simple_spinner_item);
                break;
            case "Cocktail":
                adapterBrand = ArrayAdapter.createFromResource(this, R.array.array_brand_cocktail, android.R.layout.simple_spinner_item);
                break;
            default:
                adapterBrand = ArrayAdapter.createFromResource(this, R.array.array_default_brand, android.R.layout.simple_spinner_item);
                break;
        }
        adapterBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(adapterBrand);
    }

    private void showLogout() {
        PopupMenu popupMenu = new PopupMenu(QuickAddActivity.this, logOutBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getTitle().equals("Logout")) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(QuickAddActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void saveDetails() {
        String placeText = "unknown";
        String selectedDate = formattedDate;
        Timestamp currentTimestamp = Timestamp.now();
        if ("Beer".equals(selectedBrandType)) {
            String selectedSize = "Pint (568ml)";
            double savedCost = 7.0;
            String selectedType = "Beer";
            float selectedUnit;

            switch (selectedBrand) {
                case "Guinness":
                    selectedUnit = 2.3f;
                    saveToFirestore(4.2f, savedCost, selectedDate, placeText, selectedSize, currentTimestamp, selectedType, selectedUnit);
                    break;
                case "Heineken":
                    selectedUnit = 2.8f;
                    saveToFirestore(5.0f, savedCost, selectedDate, placeText, selectedSize, currentTimestamp, selectedType, selectedUnit);
                    break;
                default:
                    selectedUnit = 2.5f;
                    saveToFirestore(4.5f, savedCost, selectedDate, placeText, selectedSize, currentTimestamp, selectedType, selectedUnit);
                    break;
            }
        } else if ("Whiskey".equals(selectedBrandType)) {
            saveToFirestore(45.0f, 15.0, selectedDate, placeText, "Large Shot (35ml)", currentTimestamp, "Whiskey", 1.6f);
        } else if ("Wine".equals(selectedBrandType)) {
            saveToFirestore(12.0f, 10.0, selectedDate, placeText, "Medium Glass (150ml)", currentTimestamp, "Wine", 1.8f);
        } else if ("Vodka".equals(selectedBrandType)) {
            saveToFirestore(40.0f, 12.0, selectedDate, placeText, "Large Shot (35ml)", currentTimestamp, "Vodka", 1.4f);
        } else if ("Tequila".equals(selectedBrandType)) {
            saveToFirestore(42.0f, 12.0, selectedDate, placeText, "Large Shot (35ml)", currentTimestamp, "Tequila", 1.5f);
        } else if ("Cocktail".equals(selectedBrandType)) {
            saveToFirestore(40.0f, 18.0, selectedDate, placeText, "Medium Double (50ml)", currentTimestamp, "Cocktail", 2.0f);
        } else {
            Toast.makeText(QuickAddActivity.this, "Please select a valid brand type", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFirestore(float alcohol, double cost, String date, String place, String size, Timestamp timestamp, String type, float unit) {
        Details details = new Details();
        details.setAlcohol(alcohol);
        details.setCost(cost);
        details.setDate(date);
        details.setPlace(place);
        details.setSize(size);
        details.setTimestamp(timestamp);
        details.setType(type);
        details.setUnit(unit);

        DocumentReference documentReference = Utility.getCollectionReferenceForDetails().document();
        details.setDocId(documentReference.getId());
        documentReference.set(details).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Utility.showToast(QuickAddActivity.this, "Details saved successfully");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Utility.showToast(QuickAddActivity.this, "Details couldn't be saved");
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required for barcode scanner", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setTextViewType(String selectedBrandType) {
        int colorType = getResources().getColor(R.color.my_color);
        String typeText = "Type: " + selectedBrandType;
        SpannableStringBuilder builder = new SpannableStringBuilder(typeText);
        builder.setSpan(new ForegroundColorSpan(colorType), 0, "Type: ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewSelectedType.setText(builder);
    }

    private void setTextViewBrand(String selectedBrand) {
        int colorBrand = getResources().getColor(R.color.my_color);
        String brandText = "Brand: " + selectedBrand;
        SpannableStringBuilder builder = new SpannableStringBuilder(brandText);
        builder.setSpan(new ForegroundColorSpan(colorBrand), 0, "Brand: ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewSelectedBrand.setText(builder);
    }
}
