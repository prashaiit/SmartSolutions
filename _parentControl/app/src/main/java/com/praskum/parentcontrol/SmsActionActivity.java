package com.praskum.parentcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SmsActionActivity extends Activity {

    private TextView timerView;
    private TextView hour, min, sec;
    private int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smsaction);
        hour = (TextView) findViewById(R.id.hours);
        min = (TextView) findViewById(R.id.mintues);
        sec = (TextView) findViewById(R.id.seconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Addtimer", "smsaction resume");
        timerView = (TextView) findViewById(R.id.timervalue);
        timerView.setText(Integer.toString(value));
        UpdateTextView();
    }

    public void TriggerAddTimerActivity(View view) {
        Intent intent = new Intent(SmsActionActivity.this, AddTimerActivity.class);
        startActivityForResult(intent, 6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("addtimer", "OnActivityResult");
        Log.i("addtimer", "req = " + requestCode + " resCode " + resultCode);
        if (requestCode == 6 && resultCode == 5) {
            value = data.getIntExtra("timer", 0);
        }
        else {
            value = 0;
        }
    }

    private void UpdateTextView(){
        Utils utils = new Utils();
        sec.setText(utils.getSecValue(value));
        min.setText(utils.getMinValue(value));
        hour.setText(utils.getHourValue(value));
    }
}
