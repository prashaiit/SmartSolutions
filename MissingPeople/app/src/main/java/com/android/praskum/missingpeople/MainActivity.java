package com.android.praskum.missingpeople;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button rMP, fP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rMP = (Button) findViewById(R.id.MissingCaseButton);
        fP = (Button) findViewById(R.id.foundPersonButton);

        rMP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MissingPeopleListActivity.class);
                startActivity(intent);
            }
        });

        fP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoundPersonActivity.class);
                startActivity(intent);
            }
        });
    }
}
