package com.example.mad_assignment_13;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class custom_breakdown_activity extends AppCompatActivity {

    private TextView tvCustomBreakdownTopic;
    private TextView tvTopicCurrencyAmount;
    private LinearLayout llPersonContainer;
    private Button btnCalculatePercentage;
    private Button btnCalculateRatio;
    private TextView tvCustomBreakdownResult;
    private String currency;
    private Button btnSave;
    private Button btnDiscard;
    private Button btnEdit;
    private ImageButton btnHome, btnWhatsapp;
    private Button btnCalculateCustomBreakdown;

    private List<String> friendNames = new ArrayList<>();
    private List<EditText> etPercentages = new ArrayList<>();
    private List<EditText> etRatioUpValues = new ArrayList<>();
    private List<EditText> etRatioDownValues = new ArrayList<>();


    private double amount = 0.0;

    private SQLiteAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_breakdown_activity);

        dbAdapter = new SQLiteAdapter(this);  // Initialize the SQLiteAdapter

        llPersonContainer = findViewById(R.id.llPersonContainer);
        btnCalculatePercentage = findViewById(R.id.btnCalculatePercentage);
        btnCalculateRatio = findViewById(R.id.btnCalculateRatio);
        btnSave = findViewById(R.id.btnSave);
        btnDiscard = findViewById(R.id.btnDiscard);
        btnEdit = findViewById(R.id.btnEdit);
        tvCustomBreakdownResult = findViewById(R.id.tvCustomBreakdownResult);
        btnCalculateCustomBreakdown = findViewById(R.id.btnCalculateCustomBreakdown);

        // Get data from the previous activity
        String topic = getIntent().getStringExtra("topic");
        String method = getIntent().getStringExtra("method");
        String currency = getIntent().getStringExtra("currency");
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        int numPeople = getIntent().getIntExtra("numPeople", 0);

        // Display the topic, currency, and total amount
        TextView tvTopicCurrencyAmount = findViewById(R.id.tvTopicCurrencyAmount);
        tvTopicCurrencyAmount.setText("Topic: " + topic + " | Currency: " + currency + " | Amount: RM " + amount);

        // Initialize friend names
        for (int i = 0; i < numPeople; i++) {
            friendNames.add("Friend " + (char) (i + 65)); // A, B, C, ...
        }

        // Add views for each person
        for (int i = 0; i < numPeople; i++) {
            addPersonView(i + 1, friendNames.get(i));
        }

        // Set onClick listener for the Calculate Custom Breakdown button
        btnCalculatePercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llPersonContainer.getVisibility() == View.VISIBLE) {
                    llPersonContainer.setVisibility(View.GONE);
                } else {
                    calculatePercentage();
                    llPersonContainer.setVisibility(View.VISIBLE);
                    btnCalculateCustomBreakdown.setVisibility(View.VISIBLE);
                }
            }
        });

        btnCalculateRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llPersonContainer.getVisibility() == View.VISIBLE) {
                    llPersonContainer.setVisibility(View.GONE);
                } else {
                    calculateRatio();
                    llPersonContainer.setVisibility(View.VISIBLE);
                    btnCalculateCustomBreakdown.setVisibility(View.VISIBLE);
                }

            }
        });


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
                Intent intent = new Intent(custom_breakdown_activity.this, MainActivity.class);
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
                Intent intent = new Intent(custom_breakdown_activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnWhatsapp = findViewById(R.id.btnWhatsApp);


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
        View personView = getLayoutInflater().inflate(R.layout.person_item_custom, llPersonContainer, false);

        TextView tvPersonNumber = personView.findViewById(R.id.tvPersonNumber);
        EditText etFriendName = personView.findViewById(R.id.etFriendName);

        tvPersonNumber.setText("Person " + personNumber);
        etFriendName.setHint(defaultName);


        llPersonContainer.addView(personView);
    }

    private void calculatePercentage(){
        etPercentages.clear();
        llPersonContainer.removeAllViews();

        for (int i = 0; i < friendNames.size(); i++) {
            View personView = getLayoutInflater().inflate(R.layout.person_item_percentage, llPersonContainer, false);

            TextView tvPersonName = personView.findViewById(R.id.tvPersonNumberPercentage);
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



        btnCalculateCustomBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAndDisplayPercentageResults();
            }
        });
    }

    private void calculateRatio() {
        // Clear any previous views and data
        etRatioUpValues.clear();
        etRatioDownValues.clear();
        llPersonContainer.removeAllViews();

        for (int i = 0; i < friendNames.size(); i++) {
            View personView = getLayoutInflater().inflate(R.layout.person_item_ratio, llPersonContainer, false);

            TextView tvPersonName = personView.findViewById(R.id.tvPersonNumberRatio);
            EditText etRatioUp = personView.findViewById(R.id.etRatioUp);
            //EditText etRatioDown = personView.findViewById(R.id.etRatioDown);

            // Add a listener to the ratio EditTexts for automatic conversion
            etRatioUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // If the field loses focus, convert the input to a double value
                        String ratioUpText = etRatioUp.getText().toString().trim();
                        if (!ratioUpText.isEmpty()) {
                            double ratioUpValue = Double.parseDouble(ratioUpText);
                            // Format the value without decimal places
                            String formattedRatioUp = String.format("%.0f", ratioUpValue);
                            etRatioUp.setText(formattedRatioUp);
                        }
                    }
                }
            });


            tvPersonName.setText(friendNames.get(i));
            etRatioUp.setHint("Ratio Up");


            etRatioUpValues.add(etRatioUp);
            llPersonContainer.addView(personView);
        }

        btnCalculateCustomBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAndDisplayRatioResults();
            }
        });
    }




    private void calculateAndDisplayPercentageResults() {
        double totalPercentage = 0.0;
        boolean hasErrors = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < friendNames.size(); i++) {
            EditText etPercentage = etPercentages.get(i);
            View personView = llPersonContainer.getChildAt(i);
            EditText etFriendName = personView.findViewById(R.id.etFriendName);
            String friendName = etFriendName.getText().toString().trim();
            TextView tvPayAmount = personView.findViewById(R.id.tvPayAmount);

            double percentage = 0.0;
            try {
                percentage = Double.parseDouble(etPercentage.getText().toString());
            } catch (NumberFormatException e) {
                etPercentage.setError("Invalid input");
                hasErrors = true;
                continue;
            }

            totalPercentage += percentage;

            etPercentage.setError(null);
        }

        if (hasErrors) {
            // There are errors in the input
            tvCustomBreakdownResult.setText("");
            return; // Don't proceed to further calculations if there are errors
        } else if (totalPercentage > 100.0) {
            // Total percentage exceeds 100%
            tvCustomBreakdownResult.setText("");
            Toast.makeText(custom_breakdown_activity.this, "Total percentage cannot exceed 100%", Toast.LENGTH_SHORT).show();
            return; // Don't proceed to further calculations if totalPercentage > 100
        } else if (totalPercentage < 100.0) {
            // Total percentage is less than 100%
            tvCustomBreakdownResult.setText("");
            Toast.makeText(custom_breakdown_activity.this, "Total percentage must be equal to 100%", Toast.LENGTH_SHORT).show();
            return; // Don't proceed to further calculations if totalPercentage < 100
        }

        // Total percentage is 100%, perform calculations and display pay amounts
        double amount = getIntent().getDoubleExtra("amount", 0.0);

        sb.setLength(0); // Clear the StringBuilder

        for (int i = 0; i < friendNames.size(); i++) {
            EditText etPercentage = etPercentages.get(i);
            View personView = llPersonContainer.getChildAt(i);
            EditText etFriendName = personView.findViewById(R.id.etFriendName);
            String friendName = etFriendName.getText().toString().trim();
            TextView tvPayAmount = personView.findViewById(R.id.tvPayAmount);

            double percentage = Double.parseDouble(etPercentage.getText().toString());
            double payAmount = (percentage / 100.0) * amount;

            String result = friendNames.get(i) + ": " + percentage + "%, Pay: RM " + new DecimalFormat("0.00").format(payAmount);
            sb.append(result).append("\n");

            // Set the calculated pay amount to the corresponding TextView
            tvPayAmount.setText("Pay: RM "  + new DecimalFormat("0.00").format(payAmount));
        }

        tvCustomBreakdownResult.setText(sb.toString());
    }

    private void calculateAndDisplayRatioResults() {
        double totalRatioUp = 0.0;
        double totalRatioDown = 0.0;
        boolean hasErrors = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < friendNames.size(); i++) {
            EditText etRatioUp = etRatioUpValues.get(i);
            View personView = llPersonContainer.getChildAt(i);
            TextView tvPayAmount = personView.findViewById(R.id.tvPayAmount);

            double ratioUp = 0.0;
            //double ratioDown = 0.0;
            try {
                ratioUp = Double.parseDouble(etRatioUp.getText().toString());
                //ratioDown = Double.parseDouble(etRatioDown.getText().toString());
            } catch (NumberFormatException e) {
                etRatioUp.setError("Invalid input");
                hasErrors = true;
                continue;
            }

            totalRatioUp += ratioUp;


            etRatioUp.setError(null);
        }

        if (hasErrors) {
            // There are errors in the input
            tvCustomBreakdownResult.setText("");
            return; // Don't proceed to further calculations if there are errors
        }

        if (totalRatioUp != 20) {
            tvCustomBreakdownResult.setText("");
            Toast.makeText(custom_breakdown_activity.this, "Total Ratio values are not equal", Toast.LENGTH_SHORT).show();
            return; // Don't proceed to further calculations if totalRatioUp is not equal to totalRatioDown
        }

        // Calculate and display pay amounts for Ratio
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        sb.setLength(0); // Clear the StringBuilder

        for (int i = 0; i < friendNames.size(); i++) {
            EditText etRatioUp = etRatioUpValues.get(i);
            View personView = llPersonContainer.getChildAt(i);
            double ratioUp = Double.parseDouble(etRatioUp.getText().toString());

            double payAmountRatio = (ratioUp / 20) * amount;
            String result = friendNames.get(i) + ": " + ratioUp + " : " + 20 + ", Pay: RM " + new DecimalFormat("0.00").format(payAmountRatio);
            sb.append(result).append("\n");

            // Set the calculated pay amount to the corresponding TextView
            TextView tvPay = personView.findViewById(R.id.tvPayAmount); // Assuming you have a TextView for pay amount in your person_item_ratio layout
            tvPay.setText("Pay: RM " + new DecimalFormat("0.00").format(payAmountRatio));
        }

        tvCustomBreakdownResult.setText(sb.toString());
    }





    private void shareBillOnWhatsApp() {
        // Get the equal breakdown result text
        String breakdownResult = tvCustomBreakdownResult.getText().toString();

        // Create the WhatsApp message
        String message = "Hey friends,\nHere's the bill breakdown:\n\n" + breakdownResult;

        // Use an ACTION_SEND intent to share the message
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setPackage("com.whatsapp"); // Use the package name for WhatsApp

        // Check if there's a WhatsApp app installed on the device
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            // Start the WhatsApp activity
            startActivity(sendIntent);
        } else {
            // WhatsApp not installed. Show a message or handle the case accordingly.
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onDestroy() {
        dbAdapter.closeDatabase();  // Close the database connection when the activity is destroyed
        super.onDestroy();
    }
}
