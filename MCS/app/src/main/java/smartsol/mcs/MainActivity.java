package smartsol.mcs;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);

        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        if(b!=null){
            // Display rejected number in the TextView
            textView.setText(b.getString("number"));
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);


        Button start = (Button)findViewById(R.id.startService);
        Button end = (Button) findViewById(R.id.endService);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PhoneStateListenerService.class);
                startService(i);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PhoneStateListenerService.class);
                stopService(i);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MCS", "App Destoryed");

        Intent i = new Intent("RestartService");
        sendBroadcast(i);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i("MCS", "App Detached from window");
    }
}
