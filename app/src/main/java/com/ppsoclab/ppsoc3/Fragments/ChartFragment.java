package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ppsoclab.ppsoc3.ByteParse;
import com.ppsoclab.ppsoc3.Interfaces.DataListener;
import com.ppsoclab.ppsoc3.R;

import java.util.ArrayList;

/**
 * Created by heiruwu on 5/10/15.
 */
public class ChartFragment extends Fragment implements DataListener{
    LineChart lineChart;
    ArrayList<Integer> x1angle;
    ArrayList<Integer> x2angle;
    ArrayList<Integer> x3angle;
    ArrayList<Integer> x4angle;
    ArrayList<Integer> x5angle;
    ArrayList<Integer> x6angle;
    ArrayList<Integer> x7angle;
    ArrayList<Integer> x8angle;
    ArrayList<Integer> y1angle;
    ArrayList<Integer> y2angle;
    ArrayList<Integer> y3angle;
    ArrayList<Integer> y4angle;
    ArrayList<Integer> y5angle;
    ArrayList<Integer> y6angle;
    ArrayList<Integer> y7angle;
    ArrayList<Integer> y8angle;
    ArrayList<Integer> z1angle;
    ArrayList<Integer> z2angle;
    ArrayList<Integer> z3angle;
    ArrayList<Integer> z4angle;
    ArrayList<Integer> z5angle;
    ArrayList<Integer> z6angle;
    ArrayList<Integer> z7angle;
    ArrayList<Integer> z8angle;
    ArrayList<Integer> x1raw;
    ArrayList<Integer> x2raw;
    ArrayList<Integer> x3raw;
    ArrayList<Integer> x4raw;
    ArrayList<Integer> x5raw;
    ArrayList<Integer> x6raw;
    ArrayList<Integer> x7raw;
    ArrayList<Integer> x8raw;
    ArrayList<Integer> y1raw;
    ArrayList<Integer> y2raw;
    ArrayList<Integer> y3raw;
    ArrayList<Integer> y4raw;
    ArrayList<Integer> y5raw;
    ArrayList<Integer> y6raw;
    ArrayList<Integer> y7raw;
    ArrayList<Integer> y8raw;
    ArrayList<Integer> z1raw;
    ArrayList<Integer> z2raw;
    ArrayList<Integer> z3raw;
    ArrayList<Integer> z4raw;
    ArrayList<Integer> z5raw;
    ArrayList<Integer> z6raw;
    ArrayList<Integer> z7raw;
    ArrayList<Integer> z8raw;
    Handler handler;
    HandlerThread handlerThread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chartInit();
        arrayReset();
        handlerThread = new HandlerThread("");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(x1angle!=null&&x1angle.size() >= 100){
                        chartDraw();
                    }
                }
            }
        });

    }

    @Override
    public void onDataReceived(byte[] data) {
        if(x1angle != null && ByteParse.sIN16FromByte(data[0]) == 170) {
            x1angle.add(ByteParse.sIN16From2Byte(data[2], data[3]));
            y1angle.add(ByteParse.sIN16From2Byte(data[4], data[5]));
            z1angle.add(ByteParse.sIN16From2Byte(data[6], data[7]));
            x1raw.add(ByteParse.sIN16From2Byte(data[8], data[9]));
            y1raw.add(ByteParse.sIN16From2Byte(data[10], data[11]));
            z1raw.add(ByteParse.sIN16From2Byte(data[12], data[13]));

            x2angle.add(ByteParse.sIN16From2Byte(data[14], data[15]));
            y2angle.add(ByteParse.sIN16From2Byte(data[16], data[17]));
            z2angle.add(ByteParse.sIN16From2Byte(data[18], data[19]));
            x2raw.add(ByteParse.sIN16From2Byte(data[20], data[21]));
            y2raw.add(ByteParse.sIN16From2Byte(data[22], data[23]));
            z2raw.add(ByteParse.sIN16From2Byte(data[24], data[25]));

            x3angle.add(ByteParse.sIN16From2Byte(data[26], data[27]));
            y3angle.add(ByteParse.sIN16From2Byte(data[28], data[29]));
            z3angle.add(ByteParse.sIN16From2Byte(data[30], data[31]));
            x3raw.add(ByteParse.sIN16From2Byte(data[32], data[33]));
            y3raw.add(ByteParse.sIN16From2Byte(data[34], data[35]));
            z3raw.add(ByteParse.sIN16From2Byte(data[36], data[37]));

            x4angle.add(ByteParse.sIN16From2Byte(data[38], data[39]));
            y4angle.add(ByteParse.sIN16From2Byte(data[40], data[41]));
            z4angle.add(ByteParse.sIN16From2Byte(data[42], data[43]));
            x4raw.add(ByteParse.sIN16From2Byte(data[44], data[45]));
            y4raw.add(ByteParse.sIN16From2Byte(data[46], data[47]));
            z4raw.add(ByteParse.sIN16From2Byte(data[48], data[49]));

            x5angle.add(ByteParse.sIN16From2Byte(data[50], data[51]));
            y5angle.add(ByteParse.sIN16From2Byte(data[52], data[53]));
            z5angle.add(ByteParse.sIN16From2Byte(data[54], data[55]));
            x5raw.add(ByteParse.sIN16From2Byte(data[56], data[57]));
            y5raw.add(ByteParse.sIN16From2Byte(data[58], data[59]));
            z5raw.add(ByteParse.sIN16From2Byte(data[60], data[61]));

            x6angle.add(ByteParse.sIN16From2Byte(data[62], data[63]));
            y6angle.add(ByteParse.sIN16From2Byte(data[64], data[65]));
            z6angle.add(ByteParse.sIN16From2Byte(data[66], data[67]));
            x6raw.add(ByteParse.sIN16From2Byte(data[68], data[69]));
            y6raw.add(ByteParse.sIN16From2Byte(data[70], data[71]));
            z6raw.add(ByteParse.sIN16From2Byte(data[72], data[73]));

            x7angle.add(ByteParse.sIN16From2Byte(data[74], data[75]));
            y7angle.add(ByteParse.sIN16From2Byte(data[76], data[77]));
            z7angle.add(ByteParse.sIN16From2Byte(data[78], data[79]));
            x7raw.add(ByteParse.sIN16From2Byte(data[80], data[81]));
            y7raw.add(ByteParse.sIN16From2Byte(data[82], data[83]));
            z7raw.add(ByteParse.sIN16From2Byte(data[84], data[85]));

            x8angle.add(ByteParse.sIN16From2Byte(data[86], data[87]));
            y8angle.add(ByteParse.sIN16From2Byte(data[88], data[89]));
            z8angle.add(ByteParse.sIN16From2Byte(data[90], data[91]));
            x8raw.add(ByteParse.sIN16From2Byte(data[92], data[93]));
            y8raw.add(ByteParse.sIN16From2Byte(data[94], data[95]));
            z8raw.add(ByteParse.sIN16From2Byte(data[96], data[97]));
        }
    }

    private void chartInit() {
        lineChart = (LineChart)getView().findViewById(R.id.chart);
        lineChart.setDescription("");
        lineChart.setNoDataText("");
        lineChart.setHighlightEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(0.95f);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setPinchZoom(true);
    }

    private void chartDraw() {
        if(x1raw.size() == y1raw.size() && x1raw.size() == z1raw.size() && y1raw.size() == z1raw.size()) {
            ArrayList<Entry> yData1 = new ArrayList<>();
            ArrayList<Entry> yData2 = new ArrayList<>();
            ArrayList<Entry> yData3 = new ArrayList<>();
            ArrayList<String> xVals = new ArrayList<>();
            for (int i = 0; i < x1raw.size()-1; i++) {
                yData1.add(new Entry(x1raw.get(i)/16, i));
                yData2.add(new Entry(y1raw.get(i)/16, i));
                yData3.add(new Entry(z1raw.get(i)/16, i));
                xVals.add((0.1 * i) + " s");
            }
            LineDataSet lineDataSet1 = new LineDataSet(yData1, "X4ANGEL");
            lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet1.setColor(ColorTemplate.getHoloBlue());
            lineDataSet1.setCircleColor(ColorTemplate.getHoloBlue());
            lineDataSet1.setLineWidth(2f);
            lineDataSet1.setCircleSize(3f);
            lineDataSet1.setFillAlpha(65);
            lineDataSet1.setFillColor(ColorTemplate.getHoloBlue());
            lineDataSet1.setHighLightColor(Color.rgb(244, 117, 117));
            lineDataSet1.setDrawCircleHole(false);
            LineDataSet lineDataSet2 = new LineDataSet(yData2, "Y4ANGEL");
            lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet2.setColor(Color.RED);
            lineDataSet2.setCircleColor(Color.RED);
            lineDataSet2.setLineWidth(2f);
            lineDataSet2.setCircleSize(3f);
            lineDataSet2.setDrawCubic(true);
            LineDataSet lineDataSet3 = new LineDataSet(yData3, "Z4ANGEL");
            lineDataSet3.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet3.setColor(Color.GREEN);
            lineDataSet3.setCircleColor(Color.GREEN);
            lineDataSet3.setLineWidth(2f);
            lineDataSet3.setCircleSize(3f);
            lineDataSet3.setDrawCubic(true);
            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet1);
            dataSets.add(lineDataSet2);
            dataSets.add(lineDataSet3);
            LineData data = new LineData(xVals, dataSets);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(9f);
            lineChart.setData(data);
            Legend l = lineChart.getLegend();
            l.setForm(Legend.LegendForm.LINE);
            l.setTextColor(Color.BLACK);
            l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setLabelsToSkip(49);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);

            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.setDrawGridLines(false);
            yAxis.setAxisMaxValue(2047f);
            yAxis.setAxisMinValue(-2048f);
            yAxis.setStartAtZero(false);
            YAxis yAxis1 = lineChart.getAxisRight();
            yAxis1.setAxisMaxValue(2047f);
            yAxis1.setAxisMinValue(-2048f);
            yAxis1.setDrawGridLines(false);
            yAxis1.setStartAtZero(false);
            arrayReset();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lineChart.animateX(200);
                }
            });
        }
    }

    private void arrayReset() {
        x1angle = new ArrayList<>();x2angle = new ArrayList<>();x3angle = new ArrayList<>();x4angle = new ArrayList<>();
        x5angle = new ArrayList<>();x6angle = new ArrayList<>();x7angle = new ArrayList<>();x8angle = new ArrayList<>();
        y1angle = new ArrayList<>();y2angle = new ArrayList<>();y3angle = new ArrayList<>();y4angle = new ArrayList<>();
        y5angle = new ArrayList<>();y6angle = new ArrayList<>();y7angle = new ArrayList<>();y8angle = new ArrayList<>();
        z1angle = new ArrayList<>();z2angle = new ArrayList<>();z3angle = new ArrayList<>();z4angle = new ArrayList<>();
        z5angle = new ArrayList<>();z6angle = new ArrayList<>();z7angle = new ArrayList<>();z8angle = new ArrayList<>();
        x1raw = new ArrayList<>();x2raw = new ArrayList<>();x3raw = new ArrayList<>();x4raw = new ArrayList<>();
        x5raw = new ArrayList<>();x6raw = new ArrayList<>();x7raw = new ArrayList<>();x8raw = new ArrayList<>();
        y1raw = new ArrayList<>();y2raw = new ArrayList<>();y3raw = new ArrayList<>();y4raw = new ArrayList<>();
        y5raw = new ArrayList<>();y6raw = new ArrayList<>();y7raw = new ArrayList<>();y8raw = new ArrayList<>();
        z1raw = new ArrayList<>();z2raw = new ArrayList<>();z3raw = new ArrayList<>();z4raw = new ArrayList<>();
        z5raw = new ArrayList<>();z6raw = new ArrayList<>();z7raw = new ArrayList<>();z8raw = new ArrayList<>();
    }
}
