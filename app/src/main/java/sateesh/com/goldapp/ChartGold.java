package sateesh.com.goldapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sateesh on 02-02-2016.
 */
public class ChartGold extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

        public static ChartGold newInstance(int sectionNumber) {
            ChartGold fragment = new ChartGold();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v =inflater.inflate(R.layout.chart_gold,container,false);
            return v;
        }
}