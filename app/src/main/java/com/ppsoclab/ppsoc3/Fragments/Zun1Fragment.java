package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
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
    boolean isRed = false;
    SetListener setListener;
    TextView textView;
    Button button;
    String str;
    MediaPlayer mediaPlayer;
    HandlerThread thread;
    Handler handler;
    HandlerThread workThread;
    Handler workHandler;
    Handler msgHandler;
    HandlerThread msgThread;
    Handler handlerAnim;
    HandlerThread threadAnim;
    Handler UIHandler;
    HandlerThread stopThread;
    Handler stopHandler;
    boolean stop = false;
    boolean isSent = false;
    int counter = 0;
    int test = 0;
    int position = 0;
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
    ImageView imageView;
    Button stopButton;
    boolean play = false;
    int set1, set2;

    View view;

    private PopupWindow popupWindow;

    LinearLayout background;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zun1, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser) {
            workHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UIHandler = new Handler(Looper.getMainLooper());
        stopButton = (Button) view.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stop) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopButton.setText("Stop Alarm");
                        }
                    });
                    stop = false;
                } else {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopButton.setText("Start Alarm");
                            background.setBackgroundColor(Color.WHITE);
                        }
                    });
                    stop = true;
                }
            }
        });
        imageView = (ImageView) view.findViewById(R.id.image);
        background = (LinearLayout) view.findViewById(R.id.background);
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
        threadAnim = new HandlerThread("");
        threadAnim.start();
        handlerAnim = new Handler(threadAnim.getLooper());
        handlerAnim.post(animR);
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
        ImageView imageView = (ImageView) getView().findViewById(R.id.image);
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
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
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
                test += ByteParse.sIN16From2Byte(dataP[10], dataP[11]);
                counter++;
                if(counter == 30) {
                    if (test/30 > 3840) {
                        play = true;
                    } else if (test/130 < 3840) {
                        isSent = false;
                        play = false;
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.sleep);
                                background.setBackgroundColor(Color.WHITE);
                            }
                        });
                    }
                    counter = 0;
                    test = 0;
                }
                str += "ANGLE_Z: " + ByteParse.sIN16From2Byte(dataP[12], dataP[13]) / 128 + "\n";
                str += "SUM: " + ByteParse.sIN16FromByte(dataP[14]) + "\n";
                str += "TAIL: " + ByteParse.sIN16FromByte(dataP[15]);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText(str);
//                    }
//                });
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
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    if (play && !stop) {
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource("/sdcard/warn.m4a");
                            mediaPlayer.prepare();
                            mediaPlayer.start();
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    imageView.startAnimation(anim);
//                                }
//                            });
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
                if(play && !isSent && !stop) {
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
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(), 0);
                    if(number != null) {
                        sms.sendTextMessage(number, null, "Warning! Patient is getting up.", pendingIntent, null);
                        isSent = true;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable animR = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if(play && !stop) {
                    switch (position) {
                        case 0:
                            position = 1;
                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageResource(R.drawable.sleep);
                                }
                            });
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            position = 2;
                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageResource(R.drawable.awake);
                                }
                            });
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                            position = 0;
                            UIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageResource(R.drawable.awake2);
                                }
                            });
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
        }
    };

}