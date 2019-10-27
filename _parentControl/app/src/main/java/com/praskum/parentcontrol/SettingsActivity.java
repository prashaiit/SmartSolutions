package com.praskum.parentcontrol;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private LinkedHashMap<String, String> contactsSelected = new LinkedHashMap<>();
    private LinearLayout contactsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        contactsView = (LinearLayout) findViewById(R.id.contactsView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tilebackground)));
        actionBar.setTitle(R.string.settings_sub_title);
        actionBar.setTitle(R.string.settings_sub_title);
        actionBar.show();

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.tilebackground));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void TriggerContactsActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, Constants.REQ_CODE_CONTACT_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQ_CODE_CONTACT_PICK) {
            Cursor cursor = null;
            try {
                String phoneNo = null;
                String name = null;
                Uri uri = data.getData();
                cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                name = cursor.getString(nameIndex);
                phoneNo = cursor.getString(phoneIndex);
                phoneNo = phoneNo.replaceAll("\\s+","");
                phoneNo = phoneNo.replace("+91", "");

                if (phoneNo.startsWith("0") && phoneNo.length() == 11) {
                    phoneNo = phoneNo.substring(1);
                }

                if (!contactsSelected.containsKey(phoneNo)) {
                    contactsSelected.put(phoneNo, name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            UpdateSelectedContactsView();
        }
    }

    private void UpdateSelectedContactsView() {
        contactsView.removeAllViews();

        if (contactsSelected.size() > 0) {
            for (final Map.Entry<String, String> entry : contactsSelected.entrySet()) {
                TextView textView = new TextView(getApplicationContext());
                textView.setText(entry.getValue() + " - " + entry.getKey());
                textView.setBackgroundColor(getResources().getColor(R.color.tilebackground));
                textView.setTextColor(getResources().getColor(R.color.tileforeground));

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //params.setLayoutDirection(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);

                RelativeLayout layout = new RelativeLayout(getApplicationContext());
                layout.addView(textView, params);
                final Button b = new Button(getApplicationContext());
                b.setText("X");
                b.setTextColor(getResources().getColor(R.color.tileforeground));
                b.setBackgroundColor(getResources().getColor(R.color.tilebackground));
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactsSelected.remove(entry.getKey());
                        UpdateSelectedContactsView();
                    }
                });

                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                // params2.setLayoutDirection(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                layout.addView(b, params2);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setBackgroundResource(R.drawable.textviewborder);
                layout.setBackgroundColor(getResources().getColor(R.color.tilebackground));
                param.bottomMargin = 20;
                contactsView.addView(layout, param);
            }
        }
        else {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("   No contacts added   ");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView.setBackground(getDrawable(R.drawable.textviewborder));
            }
            textView.setTextColor(getResources().getColor(R.color.tileforeground));
            textView.setBackgroundColor(getResources().getColor(R.color.tilebackground));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 20;
            params.topMargin = 30;
            params.gravity = 1;

            contactsView.addView(textView, params);
        }



        contactsView.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
