package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppsoclab.ppsoc3.ByteParse;
import com.ppsoclab.ppsoc3.Interfaces.SetListener;
import com.ppsoclab.ppsoc3.Interfaces.ZunDataListener;
import com.ppsoclab.ppsoc3.R;

/**
 * Created by User on 2015/5/20.
 */
public class Zun1Fragment extends Fragment implements ZunDataListener{
    SetListener setListener;
    TextView textView;
    String str;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zun1,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textView = (TextView) getView().findViewById(R.id.set);
    }

    @Override
    public void onDataFire(byte[] data) {
        str  = "Data set :";
        str += "Header: " + ByteParse.sIN16FromByte(data[0]) + "\n";
        str += "Count: " + ByteParse.sIN16FromByte(data[1]) + "\n";
        str += "ACC_X: " + ByteParse.sIN16From2Byte(data[2], data[3]) + "\n";
        str += "ACC_Y: " + ByteParse.sIN16From2Byte(data[4],data[5]) + "\n";
        str += "ACC_Z: " + ByteParse.sIN16From2Byte(data[6],data[7]) + "\n";
        str += "ANGLE_X: " + ByteParse.sIN16From2Byte(data[8],data[9]) + "\n";
        str += "ANGLE_Y: " + ByteParse.sIN16From2Byte(data[10],data[11]) + "\n";
        str += "ANGLE_Z: " + ByteParse.sIN16From2Byte(data[12], data[13]) + "\n";
        str += "SUM: " + ByteParse.sIN16FromByte(data[14]) + "\n";
        str += "TAIL: " + ByteParse.sIN16FromByte(data[15]);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(str);
            }
        });
    }
}
