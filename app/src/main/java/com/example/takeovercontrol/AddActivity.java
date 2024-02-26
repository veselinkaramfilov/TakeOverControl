package com.example.takeovercontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private String selectedType, selectedSize, selectedAlcohol;
    private Spinner spinnerType, spinnerSize, spinnerAlcohol;
    private ArrayAdapter<CharSequence> adapterType, adapterSize, adapterAlcohol;
    private DatePicker datePicker;
    Button saveButton;
    EditText cost, place;
    float alcoholUnits;
    String selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        cost = findViewById(R.id.text_cost);
        place = findViewById(R.id.text_place);
        datePicker = findViewById(R.id.date_picker);
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener((v)-> saveDetails());

        //Current Date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (TextUtils.isEmpty(selectedDate)) {
            selectedDate = year + "-" + (month + 1) + "-" + day;
        }
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                // You can use the selectedDate variable here or pass it to a method to handle it
            }
        });

        //Type Spinner
        spinnerType = findViewById(R.id.spinner_type);
        adapterType = ArrayAdapter.createFromResource(this, R.array.array_type, R.layout.spinner_layout);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        //Size Spinner
        spinnerSize = findViewById(R.id.spinner_size);

        //Alcohol Spinner
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
                // Handle the case where no alcohol is selected
            }
        });


    }
    void saveDetails() {
        // Get the selected values
        String costText = cost.getText().toString();
        String placeText = place.getText().toString();

        // Check if place is not empty
        if (TextUtils.isEmpty(placeText)) {
            // Handle empty place field
            Toast.makeText(AddActivity.this, "Please enter place", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse cost to Double
        double costValue = 0.00; // Default value
        if (!TextUtils.isEmpty(costText)) {
            try {
                costValue = Double.parseDouble(costText);
            } catch (NumberFormatException e) {
                // Handle invalid cost format
                Toast.makeText(AddActivity.this, "Invalid cost format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Format cost value to display with two decimal places
        String formattedCost = String.format("%.2f", costValue);

        // Calculate alcohol units
        alcoholUnits = calculateAlcoholUnits(selectedSize, selectedAlcohol);
        String formattedAlcoholUnits = String.format("%.1f", alcoholUnits); // Format alcohol units

        // Now you have all the data, you can save it to a database, file, or perform any other action
        // For demonstration, I'm just logging the values
        Log.d("SelectedData", "Type: " + selectedType);
        Log.d("SelectedData", "Size: " + selectedSize);
        Log.d("SelectedData", "Alcohol: " + selectedAlcohol);
        Log.d("SelectedData", "Cost: " + formattedCost);
        Log.d("SelectedData", "Place: " + placeText);
        Log.d("SelectedData", "Date: " + selectedDate);
        Log.d("SelectedData", "Alcohol Units: " + formattedAlcoholUnits);

        // Here you can save the data to your database or perform any other necessary action
        // For example, you can create a method to save the data to a database
        saveData(selectedType, selectedSize, selectedAlcohol, costValue, placeText, selectedDate, alcoholUnits);

        // Optionally, you can navigate back to the previous activity or perform any other action
        finish();
    }

    // Method to calculate alcohol units
    private float calculateAlcoholUnits(String selectedSize, String selectedAlcohol) {
        try {

            // Extract volume from selectedSize
            float volume = Float.parseFloat(selectedSize.replaceAll("[^\\d.]+", ""));
            // Extract ABV from selectedAlcohol
            float abv = Float.parseFloat(selectedAlcohol.replaceAll("[^\\d.]+", ""));

            // Calculate units of alcohol using the formula
            return (volume * abv) / (1000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0f; // Return default value if parsing fails
        }
    }

    // Method to save data to database
    private void saveData(String selectedType, String selectedSize, String selectedAlcohol, double costValue, String placeText, String selectedDate, float alcoholUnits) {
        // Implement your logic to save data to database here


    }
    }
