package com.ppsoclab.ppsoc3;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;


public class SetActivity extends ActionBarActivity {
    Spinner spinnerODR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        spinnerODR = (Spinner) findViewById(R.id.ODRSpinner);
    }

}
