package com.praskum.parentcontrol;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tilebackground)));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.show();

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.tilebackground));
        }
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

    public void TriggerContactsActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
        startActivity(intent);
    }

    public void TriggerPermissionActivity(View view) {
        Intent intent = new Intent(MainActivity.this, PermissionActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contactsmenu) {
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.permissionmenu) {
            Intent intent = new Intent(MainActivity.this, PermissionActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
