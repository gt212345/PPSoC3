package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by User on 2015/5/20.
 */
public class Zun1Fragment extends Fragment implements ZunDataListener, View.OnClickListener {
    SetListener setListener;
    TextView textView;
    Button button;
    String str;
    ImageView imageView;
    MediaPlayer mediaPlayer;
    HandlerThread thread;
    Handler handler;
    /**
     * Views for popup window
     */
    Spinner spinnerODR,spinnerRange,spinnerAxis;
    Button confirm;
    CheckBox sys;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    Animation anim;
    Thread workThread;
    boolean play = false;
    int set1,set2;

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
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(15);
//        try {
//            fileWriter = new FileWriter("/sdcard/raw.txt");
//            bufferedWriter = new BufferedWriter(fileWriter);
//        } catch (IOException e){
//
//        }
        thread = new HandlerThread("");
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.post(warn);
        mediaPlayer = new MediaPlayer();
        imageView = (ImageView) getView().findViewById(R.id.image);
        imageView.setImageResource(R.drawable.sleep);
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
                confirm.setOnClickListener(this);
                popupWindow.showAsDropDown(v, 25, 0);
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
            play = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageResource(R.drawable.awake);
                }
            });

        } else if (ByteParse.sIN16From2Byte(data[10],data[11])<3840) {
            play = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageResource(R.drawable.sleep);
                }
            });
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
//        writeToSD(ByteParse.sIN16From2Byte(data[8], data[9]) / 128, "\r\nANGLE_X");
//        writeToSD(ByteParse.sIN16From2Byte(data[10], data[11]) / 128, "ANGLE_Y");
//        writeToSD(ByteParse.sIN16From2Byte(data[12],data[13]) / 128,"ANGLE_Z");
    }

    private void writeToSD (int i, String title) {
        try {
            bufferedWriter.append(title + ": " + i +" ");
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        String temp = "";
        switch (spinnerODR.getSelectedItemPosition()){
            case 0:
                temp += "010";
                break;
            case 1:
                temp += "011";
                break;
            case 2:
                temp += "100";
                break;
            case 3:
                temp += "101";
                break;
            case 4:
                temp += "110";
                break;
            case 5:
                temp += "111";
                break;
        }
        switch (spinnerRange.getSelectedItemPosition()) {
            case 0:
                temp += "00";
                break;
            case 1:
                temp += "01";
                break;
            case 2:
                temp += "10";
                break;
        }
        if(sys.isChecked()){
            temp += "1";
        } else {
            temp += "0";
        }
        switch (spinnerAxis.getSelectedItemPosition()) {
            case 0:
                temp += "00";
                break;
            case 1:
                temp += "01";
                break;
            case 2:
                temp += "10";
                break;
        }
        if(temp.substring(0,1).equals("1")){
            setListener.onSet((byte)Integer.parseInt(temp,2));
        } else {
            setListener.onSet(Byte.parseByte(temp,2));
        }

        popupWindow.dismiss();
    }

    private Runnable warn = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (play) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource("/sdcard/warn.mp3");
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.startAnimation(anim);
                            }
                        });
                        Thread.sleep(10000);
                    } catch (IOException e) {
                        Log.w("WelcomeActivity", e.toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void playWarn() {
        handler.post(warn);
    }
}