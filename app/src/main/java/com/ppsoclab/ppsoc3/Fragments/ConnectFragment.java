package com.ppsoclab.ppsoc3.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ppsoclab.ppsoc3.R;

/**
 * Created by heiruwu on 5/4/15.
 */
public class ConnectFragment extends Fragment implements View.OnClickListener {
    Button mode1,mode2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode1 = (Button)getView().findViewById(R.id.mode1);
        mode2 = (Button)getView().findViewById(R.id.mode2);
        mode1.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect,container,false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mode1:

                break;
            case R.id.mode2:

                break;
        }
    }
}
