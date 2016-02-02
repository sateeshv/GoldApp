package sateesh.com.goldapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sateesh on 02-02-2016.
 */
public class DataGold extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

        public static DataGold newInstance(int sectionNumber) {
            DataGold fragment = new DataGold();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v =inflater.inflate(R.layout.data_gold,container,false);
            return v;
        }
}