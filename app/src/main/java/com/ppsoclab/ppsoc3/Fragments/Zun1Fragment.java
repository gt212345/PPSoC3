package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.ArrayList;

/**
 * Created by User on 2015/5/20.
 */
public class Zun1Fragment extends Fragment implements ZunDataListener {
    SetListener setListener;
    TextView textView;
    Button button;
    String str;
    ImageView imageView;
    MediaPlayer mediaPlayer;
    HandlerThread thread;
    Handler handler;
    HandlerThread workThread;
    Handler workHandler;
    Handler msgHandler;
    HandlerThread msgThread;
    private String number;
    byte[] dataP;
    boolean isVisible = false;
    /**
     * Views for popup window
     */
    Spinner spinnerODR, spinnerRange, spinnerAxis;
    Button confirm;
    CheckBox sys;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    Animation anim;
    SmsManager sms;
    EditText phone;
    boolean play = false;
    int set1, set2;
    ArrayList<Integer> buffer1;
    ArrayList<Integer> buffer2;
    ArrayList<Integer> buffer3;
    int test;

    private PopupWindow popupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zun1, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buffer1 = new ArrayList<>();
        buffer2 = new ArrayList<>();
        buffer3 = new ArrayList<>();
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
        sms = SmsManager.getDefault();
        msgThread = new HandlerThread("");
        msgThread.start();
        msgHandler = new Handler(msgThread.getLooper());
        msgHandler.post(msgDetect);
        thread = new HandlerThread("");
        thread.start();
        handler = new Handler(thread.getLooper());
        workThread = new HandlerThread("");
        workThread.start();
        workHandler = new Handler(workThread.getLooper());
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
                View view = layoutInflater.inflate(R.layout.popup_set, null);
                phone = (EditText) view.findViewById(R.id.phone);
                spinnerODR = (Spinner) view.findViewById(R.id.ODRSpinner);
                spinnerRange = (Spinner) view.findViewById(R.id.rangeSpinner);
                spinnerAxis = (Spinner) view.findViewById(R.id.axisSpinner);
                sys = (CheckBox) view.findViewById(R.id.sys);
                confirm = (Button) view.findViewById(R.id.confirm);
                popupWindow = new PopupWindow(view, getActivity().getWindowManager().getDefaultDisplay().getWidth() - 50, getActivity().getWindowManager().getDefaultDisplay().getHeight() / 2 - 200);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(phone.getText().toString().equals("")){

                        } else {
                            number = phone.getText().toString();
                        }
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
                popupWindow.setFocusable(true);
                popupWindow.update();
                popupWindow.showAsDropDown(v, 25, 0);
            }
        });
    }

    @Override
    public void onDataFire(byte[] data) {
        dataP = data;
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                str = "Data set :";
                str += "Header: " + ByteParse.sIN16FromByte(dataP[0]) + "\n";
                str += "Count: " + ByteParse.sIN16FromByte(dataP[1]) + "\n";
                str += "ACC_X: " + ByteParse.sIN16From2Byte(dataP[2], dataP[3]) + "\n";
                str += "ACC_Y: " + ByteParse.sIN16From2Byte(dataP[4], dataP[5]) + "\n";
                str += "ACC_Z: " + ByteParse.sIN16From2Byte(dataP[6], dataP[7]) + "\n";
                str += "ANGLE_X: " + ByteParse.sIN16From2Byte(dataP[8], dataP[9]) / 128 + "\n";
                str += "ANGLE_Y: " + ByteParse.sIN16From2Byte(dataP[10], dataP[11]) / 128 + "\n";
                if(buffer1.size() <= 10) {
                    buffer1.add(ByteParse.sIN16From2Byte(dataP[10], dataP[11]) / 128);
                } else if (buffer1.size() == 10 && buffer2.size() <= 10){
                    buffer2.add(ByteParse.sIN16From2Byte(dataP[10], dataP[11]) / 128);
                } else if (buffer1.size() == 10 && buffer2.size() == 10 && buffer3.size() <= 10) {
                    buffer3.add(ByteParse.sIN16From2Byte(dataP[10], dataP[11]) / 128);
                }
                if(buffer1.size()==10&&buffer2.size()==10&&buffer3.size()==10){
                    for (int i : buffer1){

                    }
                    buffer1.clear();
                    buffer2.clear();
                    buffer3.clear();
                }
                if (ByteParse.sIN16From2Byte(dataP[10], dataP[11]) > 3840) {
                    play = true;
                } else if (ByteParse.sIN16From2Byte(dataP[10], dataP[11]) < 3840) {
                    play = false;
                    if(mediaPlayer!=null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.clearAnimation();
                            imageView.setImageResource(R.drawable.sleep);
                        }
                    });
                }
                str += "ANGLE_Z: " + ByteParse.sIN16From2Byte(dataP[12], dataP[13]) / 128 + "\n";
                str += "SUM: " + ByteParse.sIN16FromByte(dataP[14]) + "\n";
                str += "TAIL: " + ByteParse.sIN16FromByte(dataP[15]);
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
        });
    }

    private void writeToSD(int i, String title) {
        try {
            bufferedWriter.append(title + ": " + i + " ");
        } catch (Exception e) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

    private Runnable warn = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (mediaPlayer != null) {
                    if (play && buffer.size() >= 100) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.awake);
                            }
                        });
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
                        } catch (IOException e) {
                            Log.w("WelcomeActivity", e.toString());
                        }
                    }
                }
            }
        }
    };

    Runnable msgDetect = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if(play && buffer.size() > 100) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(), 0);
                    if(number != null) {
                        sms.sendTextMessage(number, null, "Warning! Patient is getting up.", pendingIntent, null);
                    }
                    try {
                        Thread.sleep(600000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}