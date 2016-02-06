package sateesh.com.goldapp;


import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import sateesh.com.goldapp.Data.DatabaseContract;

/**
 * Created by Sateesh on 02-02-2016.
 */
public class ChartGold extends Fragment implements OnChartValueSelectedListener, View.OnClickListener {


    private static final String ARG_SECTION_NUMBER = "section_number";

    SimpleCursorAdapter citySpinnerAdapter;
    String selectedCity, selectedDuration;

    String[] cityFilter;
    private LineChart mChart;

    public static ChartGold newInstance(int sectionNumber) {
        ChartGold fragment = new ChartGold();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chart_gold, container, false);

        final Spinner citySpinner = (Spinner) v.findViewById(R.id.chart_gold_city_spinner);
        String[] fromColumns = {DatabaseContract.CityInfo.COLUMN_CITY_NAME};
        int[] toColumns = {R.id.spinner_text_view};

        Uri city_uri = Uri.withAppendedPath(DatabaseContract.CityInfo.CONTENT_URI, "13");
        Cursor cityQuery = getActivity().getContentResolver().query(city_uri, null, null, null, null);
        Log.v("Sateesh: ", "Spinner items in cursor: " + DatabaseUtils.dumpCursorToString(cityQuery));

        citySpinnerAdapter = new SimpleCursorAdapter(getContext(), R.layout.city_spinner, cityQuery, fromColumns, toColumns, 0);
//            citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(citySpinnerAdapter);
        int count = citySpinner.getCount();


        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int location = citySpinner.getSelectedItemPosition();
                String s = citySpinner.getAdapter().getItem(position).toString();

                if (citySpinner != null && citySpinner.getChildAt(0) != null) {
                    TextView selectedTextView = (TextView) view.findViewById(R.id.spinner_text_view); // You may need to replace android.R.id.text1 whatever your TextView label id is
                    selectedCity = selectedTextView.getText().toString();
                    Log.v("Sateesh: ", "*** Selected option is: " + selectedCity);
                    Log.v("Sateesh: ", "*** Position is: " + position);

                    cityFilter = new String[1];
                    cityFilter[0] = selectedCity;
                    Log.v("Sateesh: ", "CityFilter option value is: " + cityFilter[0].toString());
//                        getLoaderManager().restartLoader(position, null, ChartGold.this);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int selectedItemId = citySpinner.getSelectedItemPosition();
        Log.v("Sateesh: ", "*** selectedItem is: " + selectedItemId);

        final Spinner durationSpinner = (Spinner) v.findViewById(R.id.chart_gold_duration_spinner);
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (durationSpinner != null && durationSpinner.getChildAt(0) != null) {
                    selectedDuration = durationSpinner.getSelectedItem().toString();
                    Log.v("Sateesh: ", "*** Selected option is: " + selectedDuration);
                    Log.v("Sateesh: ", "*** Position is: " + position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button displayData = (Button) v.findViewById(R.id.chart_gold_display_button);
        displayData.setOnClickListener(this);

        mChart = (LineChart) v.findViewById(R.id.chart1);

        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(130f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
//        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
//        ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaxValue(220f);
//        leftAxis.setAxisMinValue(-50f);
        leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        return v;
    }

    @Override
    public void onClick(View v) {
        String userSelectedDuration = this.selectedDuration;
        switch (userSelectedDuration) {
            case "Last 30 Days":
                Log.v("Sateesh: ", "*** Final Values userselected Duratoin is: Lasat 30 Days");
                Log.v("Sateesh: ", "*** Final Values userselected City is: " + cityFilter[0]);
                setData(200, 300);
                mChart.invalidate();
                break;
            case "Last 90 Days":
                Log.v("Sateesh: ", "*** Final Values userselected Duratoin is: Lasat 90 Days");
                Log.v("Sateesh: ", "*** Final Values userselected City is: " + cityFilter[0]);
                break;
            case "One Year":
                Log.v("Sateesh: ", "*** Final Values userselected Duratoin is: Lasat 365 Days");
                Log.v("Sateesh: ", "*** Final Values userselected City is: " + cityFilter[0]);
                break;
        }
    }

    private void setData(int count, float range) {


        Uri uri = Uri.withAppendedPath(DatabaseContract.PriceInfo.CONTENT_URI, "4");
        String[] Dateprojection = {DatabaseContract.PriceInfo.COLUMN_DATE};
        List<String> xVals = new ArrayList<>();

        Cursor cursorData = getActivity().getContentResolver().query(uri, Dateprojection, null, null, null);
//        Log.v("Sateesh: ", "*** Chart - Date cursor Data: " + DatabaseUtils.dumpCursorToString(cursorData));

//        cursorData.moveToFirst();

        if (cursorData != null) {
//        while (!cursorData.isAfterLast()) {
            for (cursorData.moveToFirst(); !cursorData.isAfterLast(); cursorData.moveToNext()) {
                int columnIndexOrThrow = cursorData.getColumnIndexOrThrow(DatabaseContract.PriceInfo.COLUMN_DATE);
                String rawDate = cursorData.getString(columnIndexOrThrow);
                Log.v("Sateesh: ", "*** raw Date each Value: " + rawDate);
                String finalDateValue = rawDate.split("-")[2];
                Log.v("Sateesh: ", "*** final Date each Value: " + finalDateValue);
                xVals.add(finalDateValue);
            }
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>(4);

        Uri rollNouri = Uri.withAppendedPath(DatabaseContract.PriceInfo.CONTENT_URI, "4");
        String[] rollNoprojection = {DatabaseContract.PriceInfo.COLUMN_GOLD_1_GM};

        Cursor rollNocursorData = getActivity().getContentResolver().query(rollNouri, rollNoprojection, null, null, null);
//        cursorData.moveToFirst();

        int cursorDataValue = cursorData.getCount();
        int rollNocursorDataValue = rollNocursorData.getCount();
        int i = 0;
        Log.v("Sateesh: ", "*** X and Y Column Sizes are : " + cursorDataValue + " *** " + rollNocursorDataValue);
//        List<Entry> yVals = new ArrayList<Entry>();
        for (rollNocursorData.moveToFirst(); !rollNocursorData.isAfterLast(); rollNocursorData.moveToNext()) {
            int columnIndexOrThrow = rollNocursorData.getColumnIndexOrThrow(DatabaseContract.PriceInfo.COLUMN_GOLD_1_GM);
            String rollNo = rollNocursorData.getString(columnIndexOrThrow);
            Log.v("Sateesh: ", "*** Each Roll No is: " + rollNo);
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(Float.valueOf(rollNo), i));
            i++;
            Log.v("Sateesh: ", "*** Value of I is :" + i);
        }

        Log.v("Sateesh: ", "*** Y Values are: " + yVals);
        Log.v("Sateesh: ", "*** X Values are: " + xVals);

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillColor(R.color.colorAccent);
//        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
//        set1.setFillDrawable(drawable);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets


        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("", "low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());
    }

    @Override
    public void onNothingSelected() {

    }


}