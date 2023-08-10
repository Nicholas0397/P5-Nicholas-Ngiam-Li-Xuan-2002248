package com.example.mad_assignment_13;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private TextView tvWelcomeMessage;
    private EditText etTopic;
    private EditText etAmount;
    private Spinner spinnerCurrency;
    private EditText etNumPeople;
    private Button btnEqualBreakdown;
    private Button btnCustomBreakdown;
    private Button btnCombinationBreakdown;
    private SQLiteAdapter dbAdapter;
    private ImageButton btnHistory;


    private String selectedCurrency;
    private String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        btnHistory = findViewById(R.id.btnHistory); // Initialize the button
        etTopic = findViewById(R.id.etTopic);
        etAmount = findViewById(R.id.etAmount);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        etNumPeople = findViewById(R.id.etNumPeople);
        btnEqualBreakdown = findViewById(R.id.btnEqualBreakdown);
        btnCustomBreakdown = findViewById(R.id.btnCustomBreakdown);
        btnCombinationBreakdown = findViewById(R.id.btnCombinationBreakdown);
        dbAdapter = new SQLiteAdapter(this);


        // Replace "John" with the user's name (you can fetch it from preferences or other sources)
        String userName = "Friend";
        String welcomeMessage = "Welcome back, " + userName + "!";
        tvWelcomeMessage.setText(welcomeMessage);

        // Set up the currency spinner
        List<String> currencies = new ArrayList<>();
        currencies.add("Please select Currency");
        currencies.add("RM");
        currencies.add("USD");
        currencies.add("THB");
        currencies.add("SGD");
        currencies.add("GBP");
        currencies.add("CNY");

        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected currency from the spinner
                selectedCurrency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default behavior when nothing is selected
            }
        });


        // Set onClick listeners for the breakdown options
        btnEqualBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform Equal Breakdown operation
                //clicked = true;
                performEqualBreakdown();
            }
        });

        btnCustomBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform Custom Breakdown operation
                //performCustomBreakdown();
                //clicked = true;
                performCustomBreakdown();
            }
        });


        // Set an OnClickListener for the btnHistory button
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to history_activity.java
                Intent intent = new Intent(MainActivity.this, history_activity.class);
                startActivity(intent);
            }
        });

        btnCombinationBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform Combination Breakdown operation
                //performCombinationBreakdown();
                //clicked = true;
                //btnCombinationBreakdown.isSelected();
                performCombinationBreakdown();
            }
        });

        Button btnGo = findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for each input
                String topic = etTopic.getText().toString().trim(); // Remove leading and trailing spaces
                boolean noBreakdownSelected = !(btnEqualBreakdown.isSelected() || btnCustomBreakdown.isSelected() || btnCombinationBreakdown.isSelected());

                if (topic.isEmpty()) {
                    // Show a toast message for invalid topic input
                    Toast.makeText(MainActivity.this, "Please enter a valid topic", Toast.LENGTH_SHORT).show();
                } else if (etAmount.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please input the amount of the bill", Toast.LENGTH_SHORT).show();
                } else if (etNumPeople.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please input Number of Person", Toast.LENGTH_SHORT).show();
                } else if (selectedCurrency == null || selectedCurrency.equals("Please select Currency")) {
                    // Show a toast message for currency selection
                    Toast.makeText(MainActivity.this, "Select a currency", Toast.LENGTH_SHORT).show();
                } else if (noBreakdownSelected) {
                    Toast.makeText(MainActivity.this, "Please select a breakdown option", Toast.LENGTH_SHORT).show();
                } else {
                    // Redirect to the selected breakdown layout
                    // Determine the method based on selected breakdown option
                    if (btnEqualBreakdown.isSelected()) {
                        method = "Equal Breakdown";
                    } else if (btnCustomBreakdown.isSelected()) {
                        method = "Custom Breakdown";
                    } else if (btnCombinationBreakdown.isSelected()) {
                        method = "Combination Breakdown";
                    }

                    // Open the database for writing
                    dbAdapter.openToWrite();

                    // Store the data in the database
                    double amount = Double.parseDouble(etAmount.getText().toString());
                    int numPeople = Integer.parseInt(etNumPeople.getText().toString());

                    long insertedId = dbAdapter.insertData(topic, method, selectedCurrency, amount, numPeople);

                    if (insertedId != -1) {
                        // Data inserted successfully
                        Toast.makeText(MainActivity.this, "Data stored in the database", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error in inserting data
                        Toast.makeText(MainActivity.this, "Error storing data in the database", Toast.LENGTH_SHORT).show();
                    }

                    // Set up the data for the intent
                    Intent intent = null;
                    if (btnEqualBreakdown.isSelected()) {
                        redirectToEqualBreakdown();
                    } else if (btnCustomBreakdown.isSelected()) {
                        redirectToCustomBreakdown();
                    } else if (btnCombinationBreakdown.isSelected()) {
                        intent = new Intent(MainActivity.this, combination_breakdown_activity.class);
                    }

                    if (intent != null) {
                        // Add data to the intent
                        intent.putExtra("topic", topic);
                        intent.putExtra("method", method);
                        intent.putExtra("currency", selectedCurrency);
                        intent.putExtra("amount", amount);
                        intent.putExtra("numPeople", numPeople);

                        // Start the activity
                        startActivity(intent);
                    }
                }
            }
        });

        // Add a listener to the amount EditText for automatic conversion
        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // If the amount field loses focus, convert the input to a double value
                    String amountText = etAmount.getText().toString().trim();
                    if (!amountText.isEmpty()) {
                        double amountValue = Double.parseDouble(amountText);
                        // Format the value with two decimal places
                        String formattedAmount = String.format("%.2f", amountValue);
                        etAmount.setText(formattedAmount);
                    }
                }
            }
        });
    }





    // ... (existing code)
    private void redirectToEqualBreakdown() {
        // Check if all required fields are filled
        if (etTopic.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show();
        } else if (etAmount.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input the amount of the bill", Toast.LENGTH_SHORT).show();
        } else if (etNumPeople.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input Number of Person", Toast.LENGTH_SHORT).show();
        } else if (selectedCurrency.equals("Please select Currency")) {
            Toast.makeText(this, "Select a currency", Toast.LENGTH_SHORT).show();
        } else {
            // Get the data from the input fields
            /*double amount = 100;
            int numPeople = 2;*/
            String topic = etTopic.getText().toString();
            double amount = Double.parseDouble(etAmount.getText().toString());
            int numPeople = Integer.parseInt(etNumPeople.getText().toString());


            // Create an ArrayList to hold friend names (3 default names for now)
            ArrayList<String> friendNames = new ArrayList<>();
            friendNames.add("Friend A");
            friendNames.add("Friend B");
            friendNames.add("Friend C");

            // Create an Intent to start the EqualBreakdownActivity
            Intent intent = new Intent(MainActivity.this, equal_breakdown_activity.class);
            intent.putExtra("topic", topic);
            intent.putExtra("currency", selectedCurrency);
            intent.putExtra("method",method);
            intent.putExtra("amount", amount);
            intent.putExtra("numPeople", numPeople);
            intent.putStringArrayListExtra("friendNames", friendNames);

            // Start the activity
            startActivity(intent);
        }
    }

    private void redirectToCustomBreakdown() {
        // Check if all required fields are filled
        if (etTopic.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show();
        } else if (etAmount.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input the amount of the bill", Toast.LENGTH_SHORT).show();
        } else if (etNumPeople.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input Number of Person", Toast.LENGTH_SHORT).show();
        } else if (selectedCurrency.equals("Please select Currency")) {
            Toast.makeText(this, "Select a currency", Toast.LENGTH_SHORT).show();
        } else {
            // Get the data from the input fields
            String topic = etTopic.getText().toString();
            double amount = Double.parseDouble(etAmount.getText().toString());
            int numPeople = Integer.parseInt(etNumPeople.getText().toString());


            // Create an ArrayList to hold friend names (3 default names for now)
            ArrayList<String> friendNames = new ArrayList<>();
            friendNames.add("Friend A");
            friendNames.add("Friend B");
            friendNames.add("Friend C");

            // Create an Intent to start the EqualBreakdownActivity
            Intent intent = new Intent(MainActivity.this, custom_breakdown_activity.class);
            intent.putExtra("topic", topic);
            intent.putExtra("currency", selectedCurrency);
            intent.putExtra("method",method);
            intent.putExtra("amount", amount);
            intent.putExtra("numPeople", numPeople);
            intent.putStringArrayListExtra("friendNames", friendNames);

            // Start the activity
            startActivity(intent);
        }
    }

    private void performEqualBreakdown() {
        // Reset button states
        btnEqualBreakdown.setSelected(true);
        btnCustomBreakdown.setSelected(false);
        btnCombinationBreakdown.setSelected(false);
        method = "Equal Breakdown";
    }

    private void performCustomBreakdown() {
        // Reset button states
        btnEqualBreakdown.setSelected(false);
        btnCustomBreakdown.setSelected(true);
        btnCombinationBreakdown.setSelected(false);
        method = "Custom Breakdown";
    }

    private void performCombinationBreakdown() {
        // Reset button states
        btnEqualBreakdown.setSelected(false);
        btnCustomBreakdown.setSelected(false);
        btnCombinationBreakdown.setSelected(true);
        method = "Combination Breakdown";
    }
}