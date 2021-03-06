package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ppsoclab.ppsoc3.Interfaces.ModeChooseListener;
import com.ppsoclab.ppsoc3.ModeActivity;
import com.ppsoclab.ppsoc3.R;

/**
 * Created by heiruwu on 5/4/15.
 */
public class ConnectFragment extends Fragment implements View.OnClickListener{
    Button mode1,mode2;
    ModeChooseListener modeChooseListener;
    View rootView;
    EditText deviceName;

    private static final int MODE_1 = 0;
    private static final int MODE_2 = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect,container,false);
        rootView = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mode1 = (Button) rootView.findViewById(R.id.mode1);
        mode2 = (Button) rootView.findViewById(R.id.mode2);
        mode1.setOnClickListener(this);
        mode2.setOnClickListener(this);
        deviceName = (EditText) rootView.findViewById(R.id.deviceName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mode1:
                modeChooseListener = (ModeActivity)getActivity();
                modeChooseListener.doAfterModeChose(MODE_1, deviceName.getText().toString());
                break;
            case R.id.mode2:
                modeChooseListener = (ModeActivity)getActivity();
                modeChooseListener.doAfterModeChose(MODE_2,deviceName.getText().toString());
                break;
        }
    }

}
