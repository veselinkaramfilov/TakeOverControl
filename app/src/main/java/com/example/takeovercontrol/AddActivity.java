package com.example.takeovercontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddActivity extends AppCompatActivity {
    private String selectedType, selectedSize, selectedAlcohol;
    private Spinner spinnerType, spinnerSize, spinnerAlcohol;
    private ArrayAdapter<CharSequence> adapterType, adapterSize, adapterAlcohol;
    Button saveBtn, cancelBtn;
    EditText cost, place;
    double savedCost;
    ImageButton homeBtn, logOutBtn, calendarBtn;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date currentDate = new Date();
    String formattedDate = simpleDateFormat.format(currentDate);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        cost = findViewById(R.id.text_cost);
        place = findViewById(R.id.text_place);
        saveBtn = findViewById(R.id.save_button);
        saveBtn.setOnClickListener((v) -> saveDetails());
        cancelBtn = findViewById(R.id.cancel_button);
        logOutBtn = findViewById(R.id.account_image_button);
        calendarBtn = findViewById(R.id.calendar_image_button);
        logOutBtn.setOnClickListener((v) -> showLogout());
        homeBtn = findViewById(R.id.home_image_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoadNewActivity = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoadNewActivity = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoadNewActivity = new Intent(AddActivity.this, CalendarActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });

        spinnerType = findViewById(R.id.spinner_type);
        adapterType = ArrayAdapter.createFromResource(this, R.array.array_type, R.layout.spinner_layout);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        spinnerSize = findViewById(R.id.spinner_size);
        spinnerAlcohol = findViewById(R.id.spinner_alcohol);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = spinnerType.getSelectedItem().toString();
                switch (selectedType) {
                    case "Select Type":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_size, R.layout.spinner_layout);
                        break;
                    case "Beer":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_beer_size, R.layout.spinner_layout);
                        break;
                    case "Cider":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cider_size, R.layout.spinner_layout);
                        break;
                    case "Whiskey":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_whiskey_size, R.layout.spinner_layout);
                        break;
                    case "Wine":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_wine_size, R.layout.spinner_layout);
                        break;
                    case "Champagne":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_champagne_size, R.layout.spinner_layout);
                        break;
                    case "Vodka":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_vodka_size, R.layout.spinner_layout);
                        break;
                    case "Tequila":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_tequila_size, R.layout.spinner_layout);
                        break;
                    case "Cognac":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cognac_size, R.layout.spinner_layout);
                        break;
                    case "Liqueur":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_liqueur_size, R.layout.spinner_layout);
                        break;
                    case "Cocktail":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cocktail_size, R.layout.spinner_layout);
                        break;
                    case "Hot Drink":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_hot_drink_size, R.layout.spinner_layout);
                        break;
                    case "Other":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_other_size, R.layout.spinner_layout);
                        break;
                    case "Just Buy":
                        adapterSize = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_just_buy_size, R.layout.spinner_layout);
                        break;
                    default:
                        break;
                }
                adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSize.setAdapter(adapterSize);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = spinnerSize.getSelectedItem().toString();
                if (selectedSize.equals("Select Size")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Beer")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_beer_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Cider")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cider_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Whiskey")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_whiskey_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Wine")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_wine_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Champagne")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_champagne_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Vodka")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_vodka_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Tequila")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_tequila_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Cognac")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cognac_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Liqueur")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_liqueur_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Cocktail")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cocktail_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Hot Drink")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_hot_drink_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Other")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_other_alcohol, R.layout.spinner_layout);
                } else if (!selectedSize.equals("Select Size") && selectedType.equals("Just Buy")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_just_buy_alcohol, R.layout.spinner_layout);
                }
                adapterAlcohol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAlcohol.setAdapter(adapterAlcohol);
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
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AddActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
    void saveDetails() {
        String costText = cost.getText().toString();
        String placeText = place.getText().toString();
        if (TextUtils.isEmpty(placeText)) {
            Toast.makeText(AddActivity.this, "Please enter place", Toast.LENGTH_SHORT).show();
            return;
        }
        savedCost = 0.00;
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
        details.setDate(formattedDate);

        saveDetailsToFirebase(details);
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
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForDetails().document();
        documentReference.set(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(AddActivity.this, "Details saved successfully");
                    setResult(RESULT_OK);
                } else {
                    Utility.showToast(AddActivity.this, "Details couldn't be saved");
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
    }
}
