package com.example.mad_assignment_13;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class history_activity extends AppCompatActivity {

    private SQLiteAdapter dbHelper;
    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

       // cardContainer = findViewById(R.id.cardContainer);

        dbHelper = new SQLiteAdapter(this);
        dbHelper.openToRead();

        // Get a LinearLayout containing history cards
        //String historyCardsLayout = dbHelper.queueRecord_1();

        //String[] arr = historyCardsLayout.split("\n");

        LinearLayout ll = findViewById(R.id.ll);
        for(int i = 0; i<1; i++){
            LinearLayout horizontal = new LinearLayout(this);
            horizontal.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,1
            ));
            horizontal.setOrientation(LinearLayout.HORIZONTAL);
            for(int j = 0; j<6;j++){
                TextView tv = new TextView(this);
                tv.setTextColor(Color.RED);
                //tv.setText(arr[j]);

                horizontal.addView(tv);
            }
            ll.addView(horizontal);
        }

        /*cardContainer.addView(historyCardsLayout);*/

        dbHelper.closeDatabase();
    }
}
