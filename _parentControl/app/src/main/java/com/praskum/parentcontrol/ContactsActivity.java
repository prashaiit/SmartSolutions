package com.praskum.parentcontrol;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {
    private LinkedHashMap<String, String> contactsSelected = new LinkedHashMap<>();
    private LinearLayout contactsView;
    private boolean isActivityLaunched = true;
    private boolean updateContactDatabase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);

        contactsView = (LinearLayout) findViewById(R.id.contactsView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.tilebackground)));
        actionBar.setTitle(R.string.contacts_title);
        actionBar.show();

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.tilebackground));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isActivityLaunched) {
            DatabaseActionHelper db = new DatabaseActionHelper(getApplicationContext());
            Cursor c = db.ReadAllContacts();
            if (c != null && c.getCount() > 0) {
                try {
                    if (c.moveToFirst()) {
                        do {
                            String name = c.getString(c.getColumnIndex("Name"));
                            String mobile = c.getString(c.getColumnIndex("Mobile"));

                            if (!contactsSelected.containsKey(mobile)) {
                                contactsSelected.put(mobile, name);
                            }
                        }
                        while (c.moveToNext());
                    }
                } finally {
                    c.close();
                }
            }

            isActivityLaunched = false;
        }

        UpdateSelectedContactsView();
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
                Utils utils = new Utils();
                phoneNo = utils.getNormalizedPhoneNumber(phoneNo);

                if (!contactsSelected.containsKey(phoneNo)) {
                    contactsSelected.put(phoneNo, name);
                    updateContactDatabase = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //UpdateSelectedContactsView();
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
                textView.setPadding(40, 0, 0, 0);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //params.setLayoutDirection(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);

                RelativeLayout layout = new RelativeLayout(getApplicationContext());
                layout.addView(textView, params);
                final TextView b = new TextView(getApplicationContext());
                b.setText("X");
                b.setTextColor(getResources().getColor(R.color.tileforeground));
                b.setBackgroundColor(getResources().getColor(R.color.tilebackground));
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactsSelected.remove(entry.getKey());
                        updateContactDatabase = true;
                        UpdateSelectedContactsView();
                    }
                });

                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.contact_string_height));
                // params2.setLayoutDirection(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                b.setPadding(0, 0, 40, 0);
                //params2.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY, RelativeLayout.ALIGN_TOP);
                layout.addView(b, params2);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.contact_string_height));
                layout.setBackgroundColor(getResources().getColor(R.color.tilebackground));
                layout.setPadding(0, 10, 0, 10);
                param.bottomMargin = 20;
                contactsView.addView(layout, param);
            }
        }
        else {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("   No contacts added   ");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //textView.setBackground(getDrawable(R.drawable.textviewborder));
            }
            textView.setTextColor(getResources().getColor(R.color.colorforeground));
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));

            //textView.setBackgroundColor(getResources().getColor(R.color.tilebackground));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 20;
            params.topMargin = 200;
            params.gravity = LinearLayout.VERTICAL;

            contactsView.addView(textView, params);
        }



        contactsView.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (updateContactDatabase) {
            InsertContacts();
        }

        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void InsertContacts() {
        DatabaseActionHelper db = new DatabaseActionHelper(getApplicationContext());
        db.DeleteAllContacts();

        for (final Map.Entry<String, String> entry : contactsSelected.entrySet()) {
            db.InsertContacts(entry.getValue(), entry.getKey());
        }

        Toast.makeText(ContactsActivity.this, "Contacts updated successfully", Toast.LENGTH_SHORT);
    }
}
