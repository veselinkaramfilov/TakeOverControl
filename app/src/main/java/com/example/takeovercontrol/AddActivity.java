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

                switch (selectedSize) {
                    case "Select Size":
                        adapterAlcohol = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_alcohol, R.layout.spinner_layout);
                        break;
                    default:
                        // Handle other cases if needed
                        break;
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
