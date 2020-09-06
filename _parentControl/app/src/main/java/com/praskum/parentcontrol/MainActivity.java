package com.praskum.parentcontrol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
        //Toast.makeText(getApplicationContext(), "This feature will be available in upcoming version", Toast.LENGTH_LONG).show();
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
        else if (item.getItemId() == R.id.feedbackmenu) {
            TriggerEmailActivity();
        }
        else if (item.getItemId() == R.id.sharemenu) {
            TriggerShareActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    public void TriggerRatingActivity(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse("market://details?id=com.praskum.parentcontrol"));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            try {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.praskum.parentcontrol"));
                startActivity(intent);
            }
            catch (ActivityNotFoundException e1) {
                Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void TriggerEmailActivity() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:prasha.kumar.b@gmail.com")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Kid Safe Mode | Feedback");
        //intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public void TriggerShareActivity() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Kid Safe Mode(Parental Control)");

        //String shareMessage = getString(R.string.sharingIntentText) + "\n\n";
        //String shareMessage = "Kid Safe Mode(Parental Control)\n\n";
        //shareMessage += "Do you want your kid to leave the mobile device without making him/her cry ?\nYou can do that with this app\n\n";
        //shareMessage += "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharingIntentText));
        startActivity(Intent.createChooser(shareIntent, "choose one"));
    }

    public void TriggerShareActivity(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void TriggerSettingsDialog(View view) {
    }
}
