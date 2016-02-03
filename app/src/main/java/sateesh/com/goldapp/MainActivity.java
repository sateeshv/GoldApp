package sateesh.com.goldapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
                break;

            case R.id.delete:
                break;

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
