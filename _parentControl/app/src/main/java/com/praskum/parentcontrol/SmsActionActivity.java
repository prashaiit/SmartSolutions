package com.praskum.parentcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SmsActionActivity extends Activity {

    private TextView timerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smsaction);

        timerView = (TextView) findViewById(R.id.timervalue);
    }


    public void TriggerAddTimerActivity(View view) {
        Intent intent = new Intent(SmsActionActivity.this, AddTimerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            timerView.setText(data.getIntExtra("timer", 0));

        }
        else {
            timerView.setText("fdskfjsdlkj");
        }
    }
}
