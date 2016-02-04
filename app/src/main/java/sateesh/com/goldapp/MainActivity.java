package sateesh.com.goldapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import sateesh.com.goldapp.Data.DatabaseContract;
import sateesh.com.goldapp.Network.ExitNoInternet;
import sateesh.com.goldapp.Network.ExitWithInternet;
import sateesh.com.goldapp.Network.InternetCheck;
import sateesh.com.goldapp.Network.LaunchNoInternet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton gold_icon, silver_icon, chart_icon, search_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gold_icon = (ImageButton) findViewById(R.id.gold_image);
        gold_icon.setOnClickListener(this);

        silver_icon = (ImageButton) findViewById(R.id.silver_image);
        silver_icon.setOnClickListener(this);

        chart_icon = (ImageButton) findViewById(R.id.chart_image);
        chart_icon.setOnClickListener(this);

        search_icon = (ImageButton) findViewById(R.id.search_image);
        search_icon.setOnClickListener(this);

        Button insert_records = (Button) findViewById(R.id.insert);
        insert_records.setOnClickListener(this);

        Button delete_records = (Button) findViewById(R.id.delete);
        delete_records.setOnClickListener(this);

        Button export_records = (Button) findViewById(R.id.export);
        export_records.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (InternetCheck.isConnectingToInternet(this) == true) {
        } else {

            LaunchNoInternet noInternet = new LaunchNoInternet();
            noInternet.show(getFragmentManager(), "LaunchNoInternet");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gold_image:
                Intent data_gold = new Intent(MainActivity.this, DataActivity.class);
                startActivity(data_gold);
                break;

            case R.id.silver_image:
                Intent data_silver = new Intent(MainActivity.this, DataSilver.class);
                startActivity(data_silver);

                break;

            case R.id.chart_image:
                Intent charts = new Intent(MainActivity.this, ChartsActivity.class);
                startActivity(charts);
                break;

            case R.id.search_image:
                Intent search = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(search);
                break;

            case R.id.insert:
                FetchPricesTask task = new FetchPricesTask(getApplicationContext());
                task.execute();
                break;

            case R.id.delete:
                Uri uri_all_records = Uri.withAppendedPath(DatabaseContract.PriceInfo.CONTENT_URI, "11");
                int delete_prices = getContentResolver().delete(uri_all_records, null, null);
                Uri city_uri = Uri.withAppendedPath(DatabaseContract.CityInfo.CONTENT_URI, "12");
                int delete_cities = getContentResolver().delete(city_uri, null, null);
                Log.v("Sateesh: ", "City Records delete: " + delete_cities + "  Price records delete: " + delete_prices);
                break;

            case R.id.export:

                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                FileChannel source = null;
                FileChannel destination;
                String SAMPLE_DB_NAME = "PriceDetails.db";
                String currentDBPath = "/data/" + "sateesh.com.goldapp" + "/databases/" + SAMPLE_DB_NAME;
                String backupDBPath = SAMPLE_DB_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                try {
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                    Toast.makeText(MainActivity.this, "DB Exported!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    }

    @Override
    public void onBackPressed() {
        if (InternetCheck.isConnectingToInternet(this) == true) {

            ExitWithInternet exitWithInternet = new ExitWithInternet();
            exitWithInternet.show(getFragmentManager(), "ExitWithInternet");

        } else {

            ExitNoInternet exitNoInternet = new ExitNoInternet();
            exitNoInternet.show(getFragmentManager(), "ExitNoInternet");
        }
    }
}
