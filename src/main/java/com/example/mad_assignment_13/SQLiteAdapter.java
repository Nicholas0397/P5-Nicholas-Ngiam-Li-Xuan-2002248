package com.example.mad_assignment_13;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SQLiteAdapter {

    private static final String DATABASE_NAME = "breakdown_db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME_1 = "HISTORY";
    private static final String T1_KEY_ID = "id";
    private static final String T1_KEY_METHOD = "method";
    private static final String T1_KEY_TOPIC = "topic";
    private static final String T1_KEY_CURRENCY = "currency";
    private static final String T1_KEY_AMOUNT = "amount";
    private static final String T1_KEY_NUM_PEOPLE = "num_people";

    //TABLE2
    private static final String TABLE_NAME_2 = "PEOPLE"; //table
    private static final String T2_KEY_1 = "Name"; //column
    private static final String T2_KEY_2 = "Amount"; //column
    private static final String T2_KEY_3 = "Percentage"; //column
    private static final String T2_KEY_4 = "Status"; //column
    private static final String T2_KEY_5 = "recordID"; //column


    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME_1 + "("
                    + T1_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + T1_KEY_METHOD + " TEXT NOT NULL, "
                    + T1_KEY_TOPIC + " TEXT, "
                    + T1_KEY_CURRENCY + " TEXT NOT NULL, "
                    + T1_KEY_AMOUNT + " REAL, "
                    + T1_KEY_NUM_PEOPLE + " INTEGER)";

    private static final String SCRIPT_CREATE_TABLE2 =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_2 +
                    " (personID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    T2_KEY_1 + " text not null, " +
                    T2_KEY_2 + " float, " +
                    T2_KEY_3 + " float, " +
                    T2_KEY_4 + " text, " +
                    T2_KEY_5 + " integer, " +
                    " FOREIGN KEY(" + T2_KEY_5 + ") REFERENCES " + TABLE_NAME_2 + "(recordID));"
            ;

    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteHelper sqLiteHelper;

    public SQLiteAdapter(Context context) {
        this.context = context;

    }

    public void openToWrite() {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
    }

    public void openToRead() {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
    }


    public long insertData(String topic, String method, String currency, double amount, int numPeople) {
        ContentValues values = new ContentValues();
        values.put(T1_KEY_TOPIC, topic);
        values.put(T1_KEY_METHOD, method);
        values.put(T1_KEY_CURRENCY, currency);
        values.put(T1_KEY_AMOUNT, amount);
        values.put(T1_KEY_NUM_PEOPLE, numPeople);


        return sqLiteDatabase.insert(TABLE_NAME_1, null, values);
    }

    public long insertDataP(String content, float content_2, float content_3, String content_4, int content_5){
        ContentValues contentValues = new ContentValues();

        contentValues.put(T2_KEY_1,content);
        contentValues.put(T2_KEY_2,content_2);
        contentValues.put(T2_KEY_3,content_3);
        contentValues.put(T2_KEY_4,content_4);
        contentValues.put(T2_KEY_5,content_5);


        return sqLiteDatabase.insert(TABLE_NAME_2, null, contentValues);
    }

    //retrieve current id
    public int getCurrentID(){
        String query = "SELECT MAX(recordID) AS max_id FROM " + TABLE_NAME_1;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        int id = 0;
        if (cursor.moveToFirst())
        {
            do
            {
                id = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        return id;
    }

    public LinearLayout queueRecord_1() {
        LinearLayout cardContainer = new LinearLayout(context);
        // Create a new instance of the LinearLayout here
        LinearLayout historyCardsLayout = new LinearLayout(context);
        cardContainer.setOrientation(LinearLayout.VERTICAL);
        String result = "";
        TextView textViewBillInfo = new TextView(context);

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME_1, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getColumnIndex(T1_KEY_ID);
            int method = cursor.getColumnIndex(T1_KEY_METHOD);
            int topic = cursor.getColumnIndex(T1_KEY_TOPIC);
            int currency = cursor.getColumnIndex(T1_KEY_CURRENCY);
            double amount = cursor.getColumnIndex(T1_KEY_AMOUNT);
            int numPeople = cursor.getColumnIndex(T1_KEY_NUM_PEOPLE);

            View cardView = LayoutInflater.from(context).inflate(R.layout.history_card_view, null);
            textViewBillInfo = cardView.findViewById(R.id.textViewBillInfo);

            for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
                result = result + ("ID: " + id +
                    "\nMethod: " + method +
                    "\nTopic: " + topic +
                    "\nCurrency: " + currency +
                    "\nAmount: " + amount +
                    "\nNumber of People: " + numPeople);
                textViewBillInfo.setText(result);

        }



        }
        cardContainer.addView(textViewBillInfo);
        return cardContainer;
    }

    public String queueRecord_2(){
        String[] columns = new String[] {T2_KEY_1, T2_KEY_2, T2_KEY_3, T2_KEY_4, T2_KEY_5};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME_2, columns,
                null, null, null, null, null);
        String result = "";

        int index_CONTENT = cursor.getColumnIndex(T2_KEY_1);
        int index_CONTENT_2 = cursor.getColumnIndex(T2_KEY_2);
        int index_CONTENT_3 = cursor.getColumnIndex(T2_KEY_3);
        int index_CONTENT_4 = cursor.getColumnIndex(T2_KEY_4);
        int index_CONTENT_5 = cursor.getColumnIndex(T2_KEY_5);

        for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
            result = result + cursor.getString(index_CONTENT) + ", "
                    + cursor.getString(index_CONTENT_2) + ", "
                    + cursor.getString(index_CONTENT_3) + ", "
                    + cursor.getString(index_CONTENT_4) + ", "
                    + cursor.getString(index_CONTENT_5) + "\n";
        }

        return result;
    }

    public LinearLayout queue_one_record(String topic_1) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        String[] columns = new String[]{
                T1_KEY_ID, T1_KEY_METHOD, T1_KEY_TOPIC, T1_KEY_CURRENCY, T1_KEY_AMOUNT, T1_KEY_NUM_PEOPLE
        };

        String[] selectionArgs = new String[]{topic_1};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME_1, columns,
                T1_KEY_TOPIC + "=?", selectionArgs, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int id = cursor.getColumnIndex(T1_KEY_ID);
            int method = cursor.getColumnIndex(T1_KEY_METHOD);
            int topic = cursor.getColumnIndex(T1_KEY_TOPIC);
            int currency = cursor.getColumnIndex(T1_KEY_CURRENCY);
            double amount = cursor.getColumnIndex(T1_KEY_AMOUNT);
            int numPeople = cursor.getColumnIndex(T1_KEY_NUM_PEOPLE);

            View cardView = LayoutInflater.from(context).inflate(R.layout.history_card_view, null);
            TextView textViewBillInfo = cardView.findViewById(R.id.textViewBillInfo);
            String result = "";
            result = result + ("ID: " + id +
                    "\nMethod: " + method +
                    "\nTopic: " + topic +
                    "\nCurrency: " + currency +
                    "\nAmount: " + amount +
                    "\nNumber of People: " + numPeople);

            linearLayout.addView(cardView);

        }

        cursor.close();
        return linearLayout;
    }

    //queue record string
    public String queueRecordString(Integer recordID) {
        String[] columns = new String[]{"recordID", T1_KEY_METHOD, T1_KEY_TOPIC, T1_KEY_CURRENCY};
        String[] recordId = new String[]{recordID.toString()};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME_1, columns,
                "recordID =? OR recordID =?", recordId, null, null, null);
        String result = "";

        int index_ID = cursor.getColumnIndex("recordID");
        int index_CONTENT = cursor.getColumnIndex(T1_KEY_METHOD);
        int index_CONTENT_2 = cursor.getColumnIndex(T1_KEY_TOPIC);
        int index_DATE = cursor.getColumnIndex(T1_KEY_CURRENCY);
        Integer[] indexArr = new Integer[]{index_ID, index_CONTENT, index_CONTENT_2};
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToLast();
            do {
                result = result + "Date : " + cursor.getString(index_DATE) + "\n"
                        + "Total : RM " + cursor.getString(index_CONTENT_2) + "\n"
                        + queuePeopleString(Integer.parseInt(cursor.getString(index_ID)));
            } while (cursor.moveToPrevious());
        }

        return result;
    }

    //queue person string
    public String queuePeopleString(Integer recordID){
        String[] columns = new String[] {"personID", T2_KEY_1, T2_KEY_2, T2_KEY_3, T2_KEY_4};
        String[] recordId = new String[]{recordID.toString()};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME_2, columns,
                T2_KEY_5 + "=? OR " + T2_KEY_5 + "=?", recordId, null, null, null);
        String result = "";

        int index_ID = cursor.getColumnIndex("personID");
        int index_CONTENT = cursor.getColumnIndex(T2_KEY_1);
        int index_CONTENT_2 = cursor.getColumnIndex(T2_KEY_2);
        int index_CONTENT_3 = cursor.getColumnIndex(T2_KEY_3);
        int index_CONTENT_4 = cursor.getColumnIndex(T2_KEY_4);
        Integer[] indexArr = new Integer[] {index_ID, index_CONTENT, index_CONTENT_2, index_CONTENT_3, index_CONTENT_4};
        for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
            result = result + cursor.getString(index_CONTENT) + "     RM "
                    + cursor.getString(index_CONTENT_2) + "     "
                    + cursor.getString(index_CONTENT_4) + "\n";
        }

        return result;
    }

    public String queueRecord_3(String[] test){
        String[] columns = new String[] {T1_KEY_METHOD, T1_KEY_AMOUNT, T1_KEY_NUM_PEOPLE};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME_1, columns,
                T1_KEY_AMOUNT + "=? OR " + T1_KEY_AMOUNT + "=?", test, null, null, null);
        String result = "";

        int index_CONTENT = cursor.getColumnIndex(T1_KEY_METHOD);
        int index_CONTENT_2 = cursor.getColumnIndex(T1_KEY_AMOUNT);
        int index_CONTENT_3 = cursor.getColumnIndex(T1_KEY_NUM_PEOPLE);

        for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
            result = result + cursor.getString(index_CONTENT) + ", "
                    + cursor.getString(index_CONTENT_2) + ", "
                    + cursor.getString(index_CONTENT_3) + "\n";
        }

        return result;
    }



    public void closeDatabase() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }



    //update
    public void update(String table_name, String column_name, String id, String newName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_name, newName);

        // Specify the condition for the row to be updated.
        String selection = "personID = ?";
        String[] selectionArgs = {id};

        // Perform the update operation.
        int rowsAffected = sqLiteDatabase.update(table_name, contentValues, selection, selectionArgs);

// Check the number of rows affected to verify if the update was successful
        if (rowsAffected > 0) {
            // Update successful
            Log.d("here","successful");
        } else {
            // Update failed (no rows matched the selection criteria)
            Log.d("here","failed");
        }
    }



    private class SQLiteHelper extends SQLiteOpenHelper {

        SQLiteHelper(@Nullable Context context, @Nullable String name,
                     @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
            onCreate(db);
        }
    }
}
