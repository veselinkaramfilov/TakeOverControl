package com.example.takeovercontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {
    private String selectedType, selectedSize, selectedAlcohol;
    private Spinner spinnerType, spinnerSize, spinnerAlcohol;
    private ArrayAdapter<CharSequence> adapterType;
    private Button saveBtn, cancelBtn, deleteBtn;
    private EditText cost, place;
    private ImageButton homeBtn, logOutBtn, calendarBtn, statsBtn;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Date currentDate = new Date();
    private String formattedDate = simpleDateFormat.format(currentDate);
    private String receiveType, receiveSize, receivePlace, receiveAlcoholString, receiveCostString, receiveDocId, receiveDate;
    private float receiveAlcohol;
    private double receiveCost;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        cost = findViewById(R.id.text_cost);
        place = findViewById(R.id.text_place);
        saveBtn = findViewById(R.id.save_button);
        cancelBtn = findViewById(R.id.cancel_button);
        deleteBtn = findViewById(R.id.delete_button);
        logOutBtn = findViewById(R.id.account_image_button);
        calendarBtn = findViewById(R.id.calendar_image_button);
        homeBtn = findViewById(R.id.home_image_button);
        statsBtn = findViewById(R.id.stats_image_button);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerSize = findViewById(R.id.spinner_size);
        spinnerAlcohol = findViewById(R.id.spinner_alcohol);
        adapterType = ArrayAdapter.createFromResource(this, R.array.array_type, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        receiveType = getIntent().getStringExtra("type");
        receiveSize = getIntent().getStringExtra("size");
        receiveAlcoholString = getIntent().getStringExtra("alcohol");
        receiveCostString = getIntent().getStringExtra("cost");
        receivePlace = getIntent().getStringExtra("place");
        receiveDocId = getIntent().getStringExtra("docId");
        receiveDate = getIntent().getStringExtra("date");

        if (receiveDocId != null && !receiveDocId.isEmpty()) {
            isEditMode = true;
        }
        if (isEditMode) {
            deleteBtn.setVisibility(View.VISIBLE);
        }
        deleteBtn.setOnClickListener((v) -> deleteDetailsFromFirebase());
        {
        }
        if (receiveAlcoholString != null) {
            receiveAlcohol = Float.parseFloat(receiveAlcoholString);
        }
        if (receiveCostString != null) {
            receiveCost = Double.parseDouble(receiveCostString);
        }
        if (receiveType != null) {
            int spinnerPosition = adapterType.getPosition(receiveType);
            spinnerType.setSelection(spinnerPosition);
        }
        cost.setText(String.valueOf(receiveCost));
        place.setText(receivePlace);
        saveBtn.setOnClickListener((v) -> saveDetails());
        logOutBtn.setOnClickListener((v) -> showLogout());
        cancelBtn.setOnClickListener((v) -> {
            Intent intentLoadNewActivity;
            if (isEditMode) {
                intentLoadNewActivity = new Intent(AddActivity.this, CalendarActivity.class);
            } else {
                intentLoadNewActivity = new Intent(AddActivity.this, MainActivity.class);
            }
            startActivity(intentLoadNewActivity);
        });
        homeBtn.setOnClickListener((v) -> {
            Intent intentLoadNewActivity = new Intent(AddActivity.this, MainActivity.class);
            startActivity(intentLoadNewActivity);
        });
        calendarBtn.setOnClickListener((v) -> {
            Intent intentLoadNewActivity = new Intent(AddActivity.this, CalendarActivity.class);
            startActivity(intentLoadNewActivity);
        });
        statsBtn.setOnClickListener((v) -> {
            Intent intentLoadNewActivity = new Intent(AddActivity.this, StatsActivity.class);
            startActivity(intentLoadNewActivity);
        });
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = spinnerType.getSelectedItem().toString();
                setupSizeSpinner(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = spinnerSize.getSelectedItem().toString();
                setupAlcoholSpinner(selectedSize, selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerAlcohol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAlcohol = spinnerAlcohol.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    void showLogout() {
        PopupMenu popupMenu = new PopupMenu(AddActivity.this, logOutBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getTitle().equals("Logout")) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AddActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    void saveDetails() {
        String costText = cost.getText().toString();
        String placeText = place.getText().toString();
        if (TextUtils.isEmpty(placeText)) {
            Toast.makeText(AddActivity.this, "Please enter a place", Toast.LENGTH_SHORT).show();
            return;
        }
        double savedCost = 0.00;
        if (!TextUtils.isEmpty(costText)) {
            try {
                savedCost = Double.parseDouble(costText);
            } catch (NumberFormatException e) {
                Toast.makeText(AddActivity.this, "Invalid cost format", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        float alcoholUnits = calculateAlcoholUnits(selectedSize, selectedAlcohol);
        double roundedCost = Math.round(savedCost * 100.0) / 100.0;
        Details details = new Details();
        details.setType(selectedType);
        details.setSize(selectedSize);
        details.setAlcohol(Float.parseFloat(selectedAlcohol));
        details.setCost(roundedCost);
        details.setPlace(placeText);
        details.setUnit(alcoholUnits);
        details.setTimestamp(Timestamp.now());

        if (isEditMode) {
            details.setDate(receiveDate);
            details.setDocId(receiveDocId);
            updateDetailsInFirebase(details);
        } else {
            details.setDate(formattedDate);
            saveDetailsToFirebase(details);
        }
    }

    private float calculateAlcoholUnits(String selectedSize, String selectedAlcohol) {
        try {
            float volume = Float.parseFloat(selectedSize.replaceAll("[^\\d.]+", ""));
            float abv = Float.parseFloat(selectedAlcohol.replaceAll("[^\\d.]+", ""));
            return (volume * abv) / 1000;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    void saveDetailsToFirebase(Details details) {
        DocumentReference documentReference = Utility.getCollectionReferenceForDetails().document();
        details.setDocId(documentReference.getId());
        documentReference.set(details).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Utility.showToast(AddActivity.this, "Details saved successfully");
                setResult(RESULT_OK);
            } else {
                Utility.showToast(AddActivity.this, "Details couldn't be saved");
                setResult(RESULT_CANCELED);
            }
            finish();
        });
    }

    void updateDetailsInFirebase(Details details) {
        DocumentReference documentReference = Utility.getCollectionReferenceForDetails().document(details.getDocId());
        documentReference.set(details).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Utility.showToast(AddActivity.this, "Details updated successfully");
                setResult(RESULT_OK);
                Intent intent = new Intent("refresh_calendar");
                sendBroadcast(intent);
            } else {
                Utility.showToast(AddActivity.this, "Details couldn't be updated");
                setResult(RESULT_CANCELED);
            }
            finish();
        });
    }

    void deleteDetailsFromFirebase() {
        DocumentReference documentReference = Utility.getCollectionReferenceForDetails().document(receiveDocId);
        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Utility.showToast(AddActivity.this, "Details deleted successfully");
                setResult(RESULT_OK);
                Intent intent = new Intent("refresh_calendar");
                sendBroadcast(intent);
            } else {
                Utility.showToast(AddActivity.this, "Details couldn't be deleted");
                setResult(RESULT_CANCELED);
            }
            finish();
        });
    }

    private void setupSizeSpinner(String type) {
        ArrayAdapter<CharSequence> adapterSize;
        switch (type) {
            case "Select Type":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_default_size, android.R.layout.simple_spinner_item);
                break;
            case "Beer":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_beer_size, android.R.layout.simple_spinner_item);
                break;
            case "Cider":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_cider_size, android.R.layout.simple_spinner_item);
                break;
            case "Whiskey":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_whiskey_size, android.R.layout.simple_spinner_item);
                break;
            case "Wine":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_wine_size, android.R.layout.simple_spinner_item);
                break;
            case "Champagne":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_champagne_size, android.R.layout.simple_spinner_item);
                break;
            case "Vodka":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_vodka_size, android.R.layout.simple_spinner_item);
                break;
            case "Tequila":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_tequila_size, android.R.layout.simple_spinner_item);
                break;
            case "Cognac":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_cognac_size, android.R.layout.simple_spinner_item);
                break;
            case "Liqueur":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_liqueur_size, android.R.layout.simple_spinner_item);
                break;
            case "Cocktail":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_cocktail_size, android.R.layout.simple_spinner_item);
                break;
            case "Hot Drink":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_hot_drink_size, android.R.layout.simple_spinner_item);
                break;
            case "Other":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_other_size, android.R.layout.simple_spinner_item);
                break;
            case "Just Buy":
                adapterSize = ArrayAdapter.createFromResource(this, R.array.array_just_buy_size, android.R.layout.simple_spinner_item);
                break;
            default:
                return;
        }
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(adapterSize);

        if (receiveSize != null) {
            int spinnerPosition = adapterSize.getPosition(receiveSize);
            spinnerSize.setSelection(spinnerPosition);
        }
    }

    private void setupAlcoholSpinner(String size, String type) {
        ArrayAdapter<CharSequence> adapterAlcohol;
        if (size.equals("Select Size")) {
            adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_default_alcohol, android.R.layout.simple_spinner_item);
        } else {
            switch (type) {
                case "Beer":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_beer_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Cider":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_cider_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Whiskey":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_whiskey_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Wine":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_wine_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Champagne":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_champagne_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Vodka":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_vodka_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Tequila":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_tequila_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Cognac":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_cognac_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Liqueur":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_liqueur_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Cocktail":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_cocktail_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Hot Drink":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_hot_drink_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Other":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_other_alcohol, android.R.layout.simple_spinner_item);
                    break;
                case "Just Buy":
                    adapterAlcohol = ArrayAdapter.createFromResource(this, R.array.array_just_buy_alcohol, android.R.layout.simple_spinner_item);
                    break;
                default:
                    return;
            }
        }
        adapterAlcohol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlcohol.setAdapter(adapterAlcohol);
        if (receiveAlcoholString != null) {
            int spinnerPosition = adapterAlcohol.getPosition(receiveAlcoholString);
            spinnerAlcohol.setSelection(spinnerPosition);
        }
    }
}
