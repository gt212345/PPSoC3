package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.Nullable;
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

/**
 * Created by User on 2015/5/25.
 */
public class Zun2Fragment extends Fragment implements ZunDataListener{
    SetListener setListener;
    TextView textView;
    Button button;
    String str;
    ImageView imageView;
    HandlerThread thread;
    Handler handler;
    HandlerThread animThread;
    Handler animHandler;
    Animation anim;
    Vibrator vibrator;
    boolean isRed = false;
    Handler UIHandler;
    LinearLayout background;
    View rootView;
    /**
     * Views for popup window
     */
    Spinner spinnerODR,spinnerRange,spinnerAxis;
    Button confirm;
    CheckBox sys;
    byte set1,set2;
    boolean play = false;

    private PopupWindow popupWindow;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zun1,container,false);
        rootView = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        background = (LinearLayout) rootView.findViewById(R.id.background);
        animThread = new HandlerThread("");
        animThread.start();
        animHandler = new Handler(animThread.getLooper());
        animHandler.post(backgroundR);
        UIHandler = new Handler(Looper.getMainLooper());
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(15);
        imageView = (ImageView) rootView.findViewById(R.id.image);
        textView = (TextView)rootView.findViewById(R.id.set);
        setListener = (ModeActivity) getActivity();
        thread = new HandlerThread("");
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.post(vibrate);
        button = (Button) rootView.findViewById(R.id.setButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.popup_set, null);
                spinnerODR = (Spinner) view.findViewById(R.id.ODRSpinner);
                spinnerRange = (Spinner) view.findViewById(R.id.rangeSpinner);
                spinnerAxis = (Spinner) view.findViewById(R.id.axisSpinner);
                sys = (CheckBox) view.findViewById(R.id.sys);
                confirm = (Button) view.findViewById(R.id.confirm);
                popupWindow = new PopupWindow(view, getActivity().getWindowManager().getDefaultDisplay().getWidth() - 50, getActivity().getWindowManager().getDefaultDisplay().getHeight() / 2 - 350);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp = "";
                        switch (spinnerODR.getSelectedItemPosition()) {
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
                        if (sys.isChecked()) {
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
                        if (temp.substring(0, 1).equals("1")) {
                            setListener.onSet((byte) Integer.parseInt(temp, 2));
                        } else {
                            setListener.onSet(Byte.parseByte(temp, 2));
                        }

                        popupWindow.dismiss();
                    }
                });
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
        if(ByteParse.sIN16From2Byte(data[10],data[11])<7680){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    play = true;
                    imageView.setImageResource(R.drawable.fox);
                }
            });

        } else if (ByteParse.sIN16From2Byte(data[10],data[11])>7680) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    play = false;
                    imageView.clearAnimation();
                    imageView.setImageResource(R.drawable.normal);
                    background.setBackgroundColor(Color.WHITE);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        vibrator.cancel();
    }

    private Runnable vibrate = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if(play) {
                    vibrator.vibrate(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.startAnimation(anim);
                        }
                    });
                    try {
                        Thread.sleep(950);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    vibrator.cancel();
                }
            }
        }
    };

    Runnable backgroundR = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if(play) {
                    if(isRed) {
                        isRed = false;
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                background.setBackgroundColor(Color.WHITE);
                            }
                        });
                    } else {
                        isRed = true;
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                background.setBackgroundColor(Color.RED);
                            }
                        });
                    }
                }
                try {
                    Thread.sleep(950);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
