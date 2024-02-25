package com.example.takeovercontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private String selectedType, selectedSize, selectedAlcohol; //Spinners
    private Spinner spinnerType, spinnerSize, spinnerAlcohol;
    private ArrayAdapter<CharSequence> adapterType, adapterSize, adapterAlcohol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

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

                if(selectedSize.equals("Select Size")) {
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Beer")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_beer_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Cider")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cider_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Whiskey")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_whiskey_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Wine")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_wine_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Champagne")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_champagne_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Vodka")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_vodka_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Tequila")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_tequila_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Cognac")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cognac_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Liqueur")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_liqueur_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Cocktail")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_cocktail_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Hot Drink")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_hot_drink_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Other")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_other_alcohol, R.layout.spinner_layout);
                }
                else if(!selectedSize.equals("Select Size") && selectedType.equals("Just Buy")){
                    adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_just_buy_alcohol, R.layout.spinner_layout);
                }

                adapterAlcohol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAlcohol.setAdapter(adapterAlcohol);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
