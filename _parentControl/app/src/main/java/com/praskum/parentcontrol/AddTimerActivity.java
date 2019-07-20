package com.praskum.parentcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddTimerActivity extends Activity {
    private int value = 0;
    TextView hour, min, sec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addtimer);
        hour = (TextView) findViewById(R.id.hours);
        min = (TextView) findViewById(R.id.mintues);
        sec = (TextView) findViewById(R.id.seconds);

        Button backButton = (Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = value / 10;
                UpdateTextView();
            }

        });

        backButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                value = 0;
                return false;
            }


        });
    }


    public void numberClicked(View view) {
        if (value / 100000 > 0)
        {
            return;
        }

        switch (view.getId()) {
            case (R.id.zero):
                value = value * 10;
                break;
            case (R.id.one):
                value = value * 10 + 1;
                break;
            case (R.id.two):
                value = value * 10 + 2;
                break;
            case (R.id.three):
                value = value * 10 + 3;
                break;
            case (R.id.four):
                value = value * 10 + 4;
                break;
            case (R.id.five):
                value = value * 10 + 5;
                break;
            case (R.id.six):
                value = value * 10 + 6;
                break;
            case (R.id.seven):
                value = value * 10 + 7;
                break;
            case (R.id.eight):
                value = value * 10 + 8;
                break;
            case (R.id.nine):
                value = value * 10 + 9;
                break;

            default:
                break;
        }

        UpdateTextView();
    }

    private void UpdateTextView(){
        int v = value % 100;
        String vv = Integer.toString(v);
        if (v < 10) {
            vv = "0" + vv;
        }
        sec.setText(vv);

        v = (value % 10000)/100;
        vv = Integer.toString(v);
        if (v < 10) {
            vv = "0" + vv;
        }
        min.setText(vv);

        v = (value % 1000000)/10000;
        vv = Integer.toString(v);
        if (v < 10) {
            vv = "0" + vv;
        }
        hour.setText(vv);
    }

    public void Cancel(View view) {
        this.finish();
    }

    public void Done(View view) {
        Intent intent = new Intent();
        intent.putExtra("timer", value);
        Log.i("addtimer",  " value " + value);
        setResult(5, intent);
        finish();
    }
}
