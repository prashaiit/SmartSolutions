package com.smartsol.calllog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberLogActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberlog_activity);

        Intent intent = getIntent();
        NumberWiseLog numberWiseLog = (NumberWiseLog) intent.getSerializableExtra("numberLog");

        LinearLayout layout = (LinearLayout) findViewById(R.id.numberloglayout);

        for (String info : numberWiseLog.details) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(info);
            layout.addView(tv);
        }

        layout.invalidate();
    }
}
