package com.praskum.parentcontrol;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void TriggerTimerActionActivity(View view) {
        Intent intent = new Intent(MainActivity.this, TimerActionActivity.class);
        startActivity(intent);
    }

    public void TriggerTimerActionActivity2(View view) {
        DatabaseActionHelper dbHelper = new DatabaseActionHelper(getApplicationContext());

        Cursor c = dbHelper.ReadAllData();
        try {
            c.moveToFirst();
            Intent intent = new Intent(MainActivity.this, TimerActionActivity.class);
            intent.putExtra("AlarmId", c.getInt(0));
            startActivity(intent);
        }
        catch (Exception e) {

        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void TriggerSmsActionActivity(View view){
        Intent intent = new Intent(MainActivity.this, SmsActionList.class);
        startActivity(intent);
    }

    public void TriggerAlertActiviy(View view) {
        Intent intent = new Intent(MainActivity.this, AlertActivity.class);
        startActivity(intent);
    }
}
