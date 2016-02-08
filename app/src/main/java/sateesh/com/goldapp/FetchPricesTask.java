package sateesh.com.goldapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sateesh.com.goldapp.Data.DatabaseContract;


public class FetchPricesTask extends AsyncTask<Void, Void, Void> {
    Context context;
    double startTime;

    public FetchPricesTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        startTime = System.currentTimeMillis();

//        Database Check for last record date,
        //        if exists, then incremental insert
        //        else insert everything
        //        Query for last record
        Uri uri = Uri.withAppendedPath(DatabaseContract.PriceInfo.CONTENT_URI, "2");
        Log.v("Sateesh: ", "*** URI link is: " + uri);
        Cursor cursorLastRecord = context.getContentResolver().query(uri, null, null, null, null);
        Log.v("Sateesh: ", "*** are there any records: " + (cursorLastRecord != null ? cursorLastRecord.getCount() : 0));

        String lastInsertedDate = null;

        String KEY = "1mTZIS6-dg8Hxd7akQOgi1TdxRarmA7SMvS_nabrF_t0";
        String sheetURL = null;

        try {
            if (cursorLastRecord != null && cursorLastRecord.getCount() > 0) {
                cursorLastRecord.moveToFirst();
                lastInsertedDate = cursorLastRecord.getString(cursorLastRecord.getColumnIndexOrThrow(DatabaseContract.PriceInfo.COLUMN_DATE));
                Log.v("Sateesh: ", "*** Last Inserted Date is: " + lastInsertedDate);
                sheetURL = "https://spreadsheets.google.com/feeds/list/" + KEY + "/od6/public/values?alt=json" + "&sq=date>" + lastInsertedDate;

            } else {
                Log.v("Sateesh: ", "*** No insertions till Now");
//                https://spreadsheets.google.com/feeds/list/1mTZIS6-dg8Hxd7akQOgi1TdxRarmA7SMvS_nabrF_t0/od6/public/values?alt=json
                sheetURL = "https://spreadsheets.google.com/feeds/list/" + KEY + "/od6/public/values?alt=json";
            }

        } catch (CursorIndexOutOfBoundsException e) {
            Log.v("Sateesh: ", "*** cursor Index Out of Bound");
        }


//        Establishing connection to Spreadsheet
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String sheetString = null;


        try {
            URL url = new URL(sheetURL.toString());
            /**
             * Create request to Google Spreadsheet
             */
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /**
             * Read inputStream to a String
             */
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            sheetString = buffer.toString();
            Log.v("Sateesh", "*** " + sheetString);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            getSheetDataFromJSON(sheetString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    Extract data from JSON file
    private void getSheetDataFromJSON(String sheetString) throws JSONException {


        List<ContentValues> data;
//        List<ContentValues> cityData;
        int numberOfRows;

        JSONObject mainloop = new JSONObject(sheetString);
        JSONObject feed = mainloop.getJSONObject("feed");

        JSONObject searchResultsObject = feed.getJSONObject("openSearch$totalResults");
        String searchResultsValue = searchResultsObject.getString("$t");
        int value = Integer.parseInt(searchResultsValue);
        if (value > 0) {
            Log.v("Sateesh: ", "New Data Available");
            JSONArray entry = feed.getJSONArray("entry");

            JSONObject searchTotalCount = feed.getJSONObject("openSearch$totalResults");
            numberOfRows = searchTotalCount.getInt("$t");

            data = new ArrayList<ContentValues>();
//            cityData = new ArrayList<ContentValues>();

            for (int i = 0; i < numberOfRows; i++) {
                String[] rowData = new String[numberOfRows - 1];

                String date, city, gold_22_1gm, silver_1gm, gold_change, silver_change;

                JSONObject eachRow = entry.getJSONObject(i);


                JSONObject date_Object = eachRow.getJSONObject("gsx$date");
                date = date_Object.getString("$t");


                JSONObject city_Object = eachRow.getJSONObject("gsx$city");
                city = city_Object.getString("$t");

                JSONObject gold_22_1gm_Object = eachRow.getJSONObject("gsx$gold22ct1gram");
                gold_22_1gm = gold_22_1gm_Object.getString("$t");

                JSONObject silver_1gm_Object = eachRow.getJSONObject("gsx$silver1gram");
                silver_1gm = silver_1gm_Object.getString("$t");

                JSONObject gold_Change_Object = eachRow.getJSONObject("gsx$goldchange");
                gold_change = gold_Change_Object.getString("$t");

                JSONObject silver_Change_Object = eachRow.getJSONObject("gsx$silverchange");
                silver_change = silver_Change_Object.getString("$t");

                ContentValues values = new ContentValues();
                values.put(DatabaseContract.PriceInfo.COLUMN_DATE, date);
                values.put(DatabaseContract.PriceInfo.COLUMN_CITY_NAME, city);
                values.put(DatabaseContract.PriceInfo.COLUMN_GOLD_1_GM, gold_22_1gm);
                values.put(DatabaseContract.PriceInfo.COLUMN_SILVER_1_GM, silver_1gm);
                values.put(DatabaseContract.PriceInfo.COLUMN_GOLD_CHANGE, gold_change);
                values.put(DatabaseContract.PriceInfo.COLUMN_SILVER_CHANGE, silver_change);

                data.add(values);

//                ContentValues cityValues = new ContentValues();
//                cityValues.put(DatabaseContract.CityInfo.COLUMN_CITY_NAME, city);
//                cityData.add(cityValues);
//                Log.v("Sateesh: ", "*** City Values are : " + cityData);

            }
            if (data.size() > 0) {
                Log.v("Sateesh: " , "*** Prices Records count is: " + data.size());
                ContentValues[] dataArray = new ContentValues[data.size()];
                ContentValues[] values = data.toArray(dataArray);
                Log.v("Sateesh: ", "**** content Values data " + values);

                Uri data_uri = Uri.withAppendedPath(DatabaseContract.PriceInfo.CONTENT_URI, "0");
                int insertedRecords = context.getContentResolver().bulkInsert(data_uri, dataArray);
                Log.v("Sateesh: ", "*** FetchPricesTask + Data Inserted Records: " + insertedRecords);
            }
//            if (cityData.size() > 0) {
//                ContentValues[] cityDataArray = new ContentValues[cityData.size()];
//                ContentValues[] cityValues = cityData.toArray(cityDataArray);
//                Log.v("Sateesh: ", "**** City Values data " + cityValues);
//
//                Uri city_uri = Uri.withAppendedPath(DatabaseContract.CityInfo.CONTENT_URI, "1");
//                int insertedRecords = context.getContentResolver().bulkInsert(city_uri, cityDataArray);
//                Log.v("Sateesh: ", "*** FetchPricesTask + City Inserted Records: " + insertedRecords);
//            }
            double endTime = System.currentTimeMillis();
            Log.v("Sateesh: ", "*** Time taken to insert " + ((endTime - startTime)/1000));
        } else {
            Log.v("Sateesh: ", "NO New Data Available");
            long endTime = System.currentTimeMillis();
            Log.v("Sateesh: ", "*** Time taken to Check new Data Available " + (endTime - startTime));
        }


    }
}
