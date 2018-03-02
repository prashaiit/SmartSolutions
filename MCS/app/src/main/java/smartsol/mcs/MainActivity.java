package smartsol.mcs;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }
}
