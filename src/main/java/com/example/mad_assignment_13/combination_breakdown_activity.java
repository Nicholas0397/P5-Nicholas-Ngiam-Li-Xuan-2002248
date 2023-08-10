package com.example.mad_assignment_13;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class combination_breakdown_activity extends AppCompatActivity {

    private LinearLayout llPersonContainer;
    private String currency;
    private Button btnSave;
    private Button btnDiscard;
    private Button btnEdit;
    private TextView tvCombinationBreakdownResult;
    private ImageButton btnHome;
    private double totalAmount = 0.0;
    private double amount_payPercentage = 0.0;
    private double amount_payAmount = 0.0;
    private TextView tvAmount;
    private Button btnCalculateCombinationBreakdown;

    private List<String> friendNames = new ArrayList<>();
    private List<EditText> etPercentages = new ArrayList<>();
    private List<EditText> etAmount = new ArrayList<>();
    private double amount = 0.0;
    private SQLiteAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combination_breakdown_activity);

        dbAdapter = new SQLiteAdapter(this);

        llPersonContainer = findViewById(R.id.llPersonContainer);
        btnSave = findViewById(R.id.btnSave);
        btnDiscard = findViewById(R.id.btnDiscard);
        btnEdit = findViewById(R.id.btnEdit);
        btnCalculateCombinationBreakdown = findViewById(R.id.btnCalculateCombinationBreakdown);
        tvAmount = findViewById(R.id.tvCustomBreakdownResult);

        // Get data from the previous activity
        String topic = getIntent().getStringExtra("topic");
        String method = getIntent().getStringExtra("method");
        String currency = getIntent().getStringExtra("currency");
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        int numPeople = getIntent().getIntExtra("numPeople", 0);

        // Display the topic, currency, and total amount
        TextView tvTopicCurrencyAmount = findViewById(R.id.tvTopicCurrencyAmount);
        tvTopicCurrencyAmount.setText("Topic: " + topic + " | Currency: " + currency + " | Amount: " + currency + " " + amount);

        // Initialize friend names
        for (int i = 0; i < numPeople; i++) {
            friendNames.add("Friend " + (char) (i + 65)); // A, B, C, ...
        }

        // Add views for each person
        for (int i = 0; i < numPeople; i++) {
            addPersonView(i + 1, friendNames.get(i));
        }

        // Set onClick listener for the "Edit" button
        btnEdit = findViewById(R.id.btnEdit);  // Use the class-level declaration
        btnEdit.setVisibility(View.GONE);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reveal the Save and Discard buttons
                btnSave.setVisibility(View.VISIBLE);
                btnDiscard.setVisibility(View.VISIBLE);

                // Hide the Edit button
                btnEdit.setVisibility(View.GONE);
            }
        });

        // Initialize the "Save" and "Discard" buttons
        btnSave = findViewById(R.id.btnSave);
        btnDiscard = findViewById(R.id.btnDiscard);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the information to the database
                saveDataToDatabase();
            }
        });

        // Set onClick listener for the "Discard" button
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return back to MainActivity
                Intent intent = new Intent(combination_breakdown_activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Find the home button by ID
        btnHome = findViewById(R.id.btnHome);

        // Set onClick listener for the home button
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to MainActivity
                Intent intent = new Intent(combination_breakdown_activity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        // Calculate and display the combination breakdown result
        btnCalculateCombinationBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalAmount += amount;
                //amount_payPercentage =
                calculateAmountAmount();
                calculatePercentage();
                double total_pay = amount_payPercentage + amount_payAmount;

                if (total_pay > totalAmount) {
                    // Total payment exceeds total amount
                    Toast.makeText(combination_breakdown_activity.this, "Total payment exceeds total amount", Toast.LENGTH_SHORT).show();
                } else if (total_pay < totalAmount) {
                    // Total payment is less than total amount
                    Toast.makeText(combination_breakdown_activity.this, "Total payment is less than total amount", Toast.LENGTH_SHORT).show();
                } else {
                    // Total payment matches total amount
                    if (amount_payPercentage > 0 && amount_payAmount < 0) {
                        tvAmount.setText("Amount: " + currency + amount_payPercentage);
                        calculatePercentageAmount();
                    } else if (amount_payAmount > 0 && amount_payPercentage < 0) {
                        tvAmount.setText("Amount: " + currency + amount_payAmount);
                        calculateAmountAmount();
                    } else {
                        Toast.makeText(combination_breakdown_activity.this, "Only input either one of the column", Toast.LENGTH_SHORT).show();;
                    }
                }
            }
        });


    }

    /*private void calculateAmountAmount() {
        EditText etPercentage = findViewById(R.id.etPercentage);
        EditText etPayAmount = findViewById(R.id.etPayAmount);

        String percentageText = etPercentage.getText().toString();
        String amountText = etPayAmount.getText().toString();

        if (!percentageText.isEmpty() && !amountText.isEmpty()) {
            // Both fields are filled, show error toast
            Toast.makeText(this, "Please enter either percentage or amount, not both.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!amountText.isEmpty()) {
            double amount = Double.parseDouble(amountText);
            amount_payAmount = amount;
            tvAmount.setText("Amount: " + currency + " " + amount_payAmount);
        } else {
            // Clear any previously set value
            amount_payAmount = 0.0;
            tvAmount.setText("Combination Breakdown Result:");
        }
    }*/

    private void calculateAmountAmount() {
        double totalAmounts = 0.0;

        for (EditText etAmount : etAmount) {
            String amountText = etAmount.getText().toString();

            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                totalAmounts += amount;
            }
        }

        amount_payAmount = totalAmounts;

        if (amount_payAmount > totalAmount) {
            // Total payment exceeds total amount
            Toast.makeText(this, "Total payment exceeds total amount", Toast.LENGTH_SHORT).show();
            amount_payAmount = 0.0; // Reset amount_payAmount
            return;
        }

        // Display the calculated amount_payAmount
        tvAmount.setText("Amount: " + currency + " " + amount_payAmount);
    }

    private void calculatePercentage(){
        etPercentages.clear();
        llPersonContainer.removeAllViews();

        for (int i = 0; i < friendNames.size(); i++) {
            View personView = getLayoutInflater().inflate(R.layout.person_item_combination, llPersonContainer, false);

            TextView tvPersonName = personView.findViewById(R.id.tvPersonNumberCombination);
            EditText etPercentage = personView.findViewById(R.id.etPercentage);
            // Add a listener to the amount EditText for automatic conversion
            etPercentage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // If the amount field loses focus, convert the input to a double value
                        String amountText = etPercentage.getText().toString().trim();
                        if (!amountText.isEmpty()) {
                            double amountValue = Double.parseDouble(amountText);
                            // Format the value with two decimal places
                            String formattedAmount = String.format("%.2f", amountValue);
                            etPercentage.setText(formattedAmount);
                        }
                    }
                }
            });




            tvPersonName.setText(friendNames.get(i));
            etPercentage.setHint("Percentage");

            etPercentages.add(etPercentage);
            llPersonContainer.addView(personView);

        }
        calculatePercentageAmount();
    }



    private void calculatePercentageAmount() {
        double totalPercentage = 0.0;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < friendNames.size(); i++) {
            EditText etPercentage = etPercentages.get(i);
            View personView = llPersonContainer.getChildAt(i);
            EditText etFriendName = personView.findViewById(R.id.etFriendName);
            double percentage = Double.parseDouble(etPercentage.getText().toString());
            double payPercentage = (percentage / 100.0) * amount;

            // Calculate pay amount
            TextView tvPayAmount = personView.findViewById(R.id.tvPayAmount);
            tvPayAmount.setText("Pay: " + currency + " " + new DecimalFormat("0.00").format(payPercentage));

            totalPercentage += percentage;

            etPercentage.setError(null);
        }

        // Total percentage is 100%, perform calculations and display pay amounts
        sb.setLength(0); // Clear the StringBuilder

        for (int i = 0; i < friendNames.size(); i++) {
            EditText etPercentage = etPercentages.get(i);
            View personView = llPersonContainer.getChildAt(i);
            double percentage = Double.parseDouble(etPercentage.getText().toString());
            double payPercentage = (percentage / 100.0) * amount;

            String result = friendNames.get(i) + ": " + percentage + "%, Pay: "+ currency + " " + new DecimalFormat("0.00").format(payPercentage);
            sb.append(result).append("\n");
        }

        // Display the calculated pay amounts for each friend
        tvAmount.setText(sb.toString());
    }




    private void saveDataToDatabase() {
        // Get the data to save
        String topic = getIntent().getStringExtra("topic");
        String method = getIntent().getStringExtra("method");
        String currency = getIntent().getStringExtra("currency");
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        int numPeople = getIntent().getIntExtra("numPeople", 0);

        // Save the data to the database using SQLiteAdapter
        dbAdapter.openToWrite();
        long insertedId = dbAdapter.insertData(topic, method, currency, amount, numPeople); // Add percentage here

        if (insertedId != -1) {
            // Data inserted successfully
            Toast.makeText(this, "Data stored in the database", Toast.LENGTH_SHORT).show();
        } else {
            // Error in inserting data
            Toast.makeText(this, "Error storing data in the database", Toast.LENGTH_SHORT).show();
        }
        dbAdapter.closeDatabase();

        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnDiscard.setVisibility(View.GONE);
    }


    private void addPersonView(int personNumber, String defaultName) {
        View personView = getLayoutInflater().inflate(R.layout.person_item_combination, llPersonContainer, false);

        TextView tvPersonNumber = personView.findViewById(R.id.tvPersonNumberCombination);
        EditText etFriendName = personView.findViewById(R.id.etFriendName);

        tvPersonNumber.setText("Person " + personNumber);
        etFriendName.setHint(defaultName);


        llPersonContainer.addView(personView);
    }




}
