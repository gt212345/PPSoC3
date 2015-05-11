package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.ppsoclab.ppsoc3.Interfaces.DataListener;
import com.ppsoclab.ppsoc3.R;

/**
 * Created by heiruwu on 5/10/15.
 */
public class ChartFragment extends Fragment implements DataListener{

    LineChartView lineChartView;
    LineSet lineSet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lineChartView = (LineChartView)getView().findViewById(R.id.linechart);

    }

    @Override
    public void onDataReceived(byte[] data) {

    }
}
