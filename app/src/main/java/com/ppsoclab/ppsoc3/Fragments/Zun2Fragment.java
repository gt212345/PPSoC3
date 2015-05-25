package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.ppsoclab.ppsoc3.ByteParse;
import com.ppsoclab.ppsoc3.Interfaces.SetListener;
import com.ppsoclab.ppsoc3.Interfaces.ZunDataListener;
import com.ppsoclab.ppsoc3.ModeActivity;
import com.ppsoclab.ppsoc3.R;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by User on 2015/5/25.
 */
public class Zun2Fragment extends Fragment implements ZunDataListener{
    SetListener setListener;
    TextView textView;
    Button button;
    String str;
    ImageView imageView;
    /**
     * Views for popup window
     */
    Spinner spinnerODR,spinnerRange,spinnerAxis;
    Button confirm;
    CheckBox sys;
    byte set1,set2;

    private PopupWindow popupWindow;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zun1,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView = (ImageView) getView().findViewById(R.id.image);
        textView = (TextView) getView().findViewById(R.id.set);
        setListener = (ModeActivity) getActivity();
        button = (Button) getView().findViewById(R.id.setButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.popup_set,null);
                spinnerODR = (Spinner)view.findViewById(R.id.ODRSpinner);
                spinnerRange = (Spinner)view.findViewById(R.id.rangeSpinner);
                spinnerAxis = (Spinner)view.findViewById(R.id.axisSpinner);
                sys = (CheckBox) view.findViewById(R.id.sys);
                confirm = (Button) view.findViewById(R.id.confirm);
                popupWindow = new PopupWindow(view , getActivity().getWindowManager().getDefaultDisplay().getWidth()-50,getActivity().getWindowManager().getDefaultDisplay().getHeight()/2-350);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAsDropDown(v,25,0);
            }
        });
    }

    @Override
    public void onDataFire(byte[] data) {
        str  = "Data set :";
        str += "Header: " + ByteParse.sIN16FromByte(data[0]) + "\n";
        str += "Count: " + ByteParse.sIN16FromByte(data[1]) + "\n";
        str += "ACC_X: " + ByteParse.sIN16From2Byte(data[2], data[3]) + "\n";
        str += "ACC_Y: " + ByteParse.sIN16From2Byte(data[4],data[5]) + "\n";
        str += "ACC_Z: " + ByteParse.sIN16From2Byte(data[6],data[7]) + "\n";
        str += "ANGLE_X: " + ByteParse.sIN16From2Byte(data[8],data[9])/128 + "\n";
        str += "ANGLE_Y: " + ByteParse.sIN16From2Byte(data[10],data[11])/128 + "\n";
        if(ByteParse.sIN16From2Byte(data[10],data[11])>3840){
            imageView.setImageResource(R.drawable.awake);
        } else if (ByteParse.sIN16From2Byte(data[10],data[11])<3840) {
            imageView.setImageResource(R.drawable.sleep);
        }
        str += "ANGLE_Z: " + ByteParse.sIN16From2Byte(data[12], data[13])/128 + "\n";
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
