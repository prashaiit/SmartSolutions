package smartsol.gmail1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText editextTo, edittextSubject, edittextMessage;
    Button button1;
    String TO, SUBJECT, MESSAGE ;
    Intent intent ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editextTo = (EditText)findViewById(R.id.editText);
        edittextSubject = (EditText)findViewById(R.id.editText2);
        edittextMessage = (EditText)findViewById(R.id.editText3);

        button1 = (Button)findViewById(R.id.button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetData();

                intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{TO});
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
                intent.putExtra(Intent.EXTRA_TEXT, MESSAGE);

                intent.setType("message/rfc822");

                startActivity(Intent.createChooser(intent, "Select Email Sending App :"));

            }
        });

    }

    public void GetData(){

        TO = editextTo.getText().toString() ;
        SUBJECT = edittextSubject.getText().toString();
        MESSAGE = edittextMessage.getText().toString();

    }

}


