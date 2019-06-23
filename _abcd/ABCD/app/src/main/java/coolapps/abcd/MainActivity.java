package coolapps.abcd;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void UpperCaseAlphabets(View view) {
        Intent intent = new Intent(MainActivity.this, ShowLetterActivity.class);
        intent.putExtra("type", "uppercaseLetters");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void LowerCaseAlphabets(View view) {
        Intent intent = new Intent(MainActivity.this, ShowLetterActivity.class);
        intent.putExtra("type", "lowercaseletters");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void Numbers(View view){
        Intent intent = new Intent(MainActivity.this, ShowLetterActivity.class);
        intent.putExtra("type", "numbers");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
