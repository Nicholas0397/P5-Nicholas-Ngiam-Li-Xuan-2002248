package com.example.mad_assignment_13;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class equal_breakdown_activity extends AppCompatActivity {

    private TextView tvEqualBreakdownTopic;
    private TextView tvTopicCurrencyAmount;
    private LinearLayout llPersonContainer;
    private Button btnCalculateEqualBreakdown;
    private TextView tvEqualBreakdownResult;
    private String currency;
    private String method = "Equal Breakdown";
    private Button btnSave;
    private Button btnDiscard;
    private Button btnEdit;
    private ImageButton btnHome;
    private ImageButton btnWhatsApp;

    private List<String> friendNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equal_breakdown_activity);

        SQLiteAdapter dbAdapter = new SQLiteAdapter(this);
        tvEqualBreakdownTopic = findViewById(R.id.tvEqualBreakdownTopic);
        tvTopicCurrencyAmount = findViewById(R.id.tvTopicCurrencyAmount);
        llPersonContainer = findViewById(R.id.llPersonContainer);
        btnCalculateEqualBreakdown = findViewById(R.id.btnCalculateEqualBreakdown);
        tvEqualBreakdownResult = findViewById(R.id.tvEqualBreakdownResult);
        btnWhatsApp = findViewById((R.id.btnWhatsApp));

        // Get data from the previous activity
        String topic = getIntent().getStringExtra("topic");
        method = getIntent().getStringExtra("method");
        currency = getIntent().getStringExtra("currency");
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        int numPeople = getIntent().getIntExtra("numPeople", 0);

        // Set the topic, currency, and amount in the TextView
        String topicCurrencyAmount = "Topic: " + topic + "  |  Currency: " + currency + "  |  Amount: " + amount;
        tvTopicCurrencyAmount.setText(topicCurrencyAmount);

        // Initialize the list of friend names with default values
        friendNames.clear();
        for (int i = 1; i <= numPeople; i++) {
            friendNames.add("Friend " + (char) (i + 64));
        }

        // Dynamically add views for each person's information
        for (int i = 0; i < numPeople; i++) {
            addPersonView(i + 1, friendNames.get(i));
        }

        // Set onClick listener for the Calculate Equal Breakdown button
        btnCalculateEqualBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateEqualBreakdown();
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

        // Set onClick listener for the "Save" button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the information to the database
                saveDataToDatabase(topic, method, currency, amount, numPeople);
                btnEdit.setVisibility(View.VISIBLE);
            }
        });

        // Set onClick listener for the "Discard" button
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return back to MainActivity
                Intent intent = new Intent(equal_breakdown_activity.this, MainActivity.class);
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
                Intent intent = new Intent(equal_breakdown_activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /*btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=queue_one_record(topic)+"\n";

                //get the data from database
                SQLiteAdapter.openToRead();
                String contentRead = SQLiteAdapter.queueRecord_1();
                String[] contentArray = contentRead.split(";");
                for (int i = 0; i < contentArray.length-1; i++) {
                    contentArray[i] = contentArray[i].trim();
                    if (i % 2 == 0) {
                        String[] contentArray2 = new String[2];
                        for (int j = 0; j < contentArray2.length; j++) {
                            contentArray2[j] = contentArray[i + j];
                        }
                        message += contentArray2[0] + ": "+String.format("%.2f",Double.parseDouble(contentArray2[1]))+"\n";
                    }
                }

                //output loading message
                Toast.makeText(EqualBreakDownPage.this,
                        "Connecting to Whatsapp API...", Toast.LENGTH_SHORT).show();

                //change to all ppl phone no
                String url = "https://api.whatsapp.com/send?text="+message;

                // Create an Intent with the ACTION_VIEW to open the URL
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(EqualBreakDownPage.this, "Error: Cannot open URL", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void saveDataToDatabase(String topic, String method, String currency, double amount, int numPeople) {
        // Save the data to the database using your SQLiteAdapter
        SQLiteAdapter dbAdapter = new SQLiteAdapter(this);
        dbAdapter.openToWrite();
        long insertedId = dbAdapter.insertData(topic, method, currency, amount, numPeople); //add percentage here

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
        LayoutInflater inflater = LayoutInflater.from(this);
        View personView = inflater.inflate(R.layout.person_item, llPersonContainer, false);

        TextView tvPersonNumber = personView.findViewById(R.id.tvPersonNumber);
        EditText etFriendName = personView.findViewById(R.id.etFriendName);

        tvPersonNumber.setText("Person " + personNumber);
        etFriendName.setHint(defaultName);

        llPersonContainer.addView(personView);
    }

    private void calculateEqualBreakdown() {
        // Get the number of people
        int numPeople = llPersonContainer.getChildCount();

        // Calculate equal breakdown amount
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        double equalAmount = amount / numPeople;

        // Calculate percentage for formatting
        double percentage = 100.0 / numPeople;

        // Display the result in the TextView
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPeople; i++) {
            View personView = llPersonContainer.getChildAt(i);
            EditText etFriendName = personView.findViewById(R.id.etFriendName);
            String friendName = etFriendName.getText().toString().trim();

            // If the friend's name is empty, use the default name
            if (friendName.isEmpty()) {
                friendName = friendNames.get(i);
            }

            // Format the output to two decimal places
            String formattedAmount = String.format("%.2f", equalAmount);
            String formattedPercentage = String.format("%.2f", percentage);

            sb.append(friendName).append(": ").append(currency).append(" ").append(formattedAmount).append(" (").append(formattedPercentage).append("%)").append("\n");
        }

        tvEqualBreakdownResult.setText(sb.toString());
    }
}
