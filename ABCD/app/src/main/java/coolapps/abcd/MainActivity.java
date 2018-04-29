package coolapps.abcd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.GestureDetector;
import android.speech.tts.TextToSpeech;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private TextView char1, char2;
    private int position;
    private boolean isFirstChar = false;
    private TextToSpeech t1;
    private String [] chars = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        char1 = (TextView) findViewById(R.id.char1);
        char2 = (TextView) findViewById(R.id.char2);
        position = 0;

        gestureDetector = new GestureDetector(new SwipeGestureDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.setOnTouchListener(gestureListener);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String speechText = chars[position];
        if (!isFirstChar) {
            char1.setVisibility(View.VISIBLE);
            char2.setVisibility(View.GONE);
            char1.setText(speechText);
        } else {
            char2.setVisibility(View.VISIBLE);
            char1.setVisibility(View.GONE);
            char2.setText(speechText);
        }
        t1.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
    }

    public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1 == null || e2 == null) return false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
    public void onSwipeRight() {
        position--;
        if (position == -1) {
            position = 25;
        }

        String speechText = chars[position];
        if (!isFirstChar) {
            char1.setVisibility(View.VISIBLE);
            char2.setVisibility(View.GONE);
            char1.setText(speechText);
        } else {
            char2.setVisibility(View.VISIBLE);
            char1.setVisibility(View.GONE);
            char2.setText(speechText);
        }
        t1.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
       // overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
    public void onSwipeLeft() {
        position++;
        if (position == 26) {
            position = 0;
        }

        String speechText = chars[position];
        if (!isFirstChar) {
            char1.setVisibility(View.VISIBLE);
            char2.setVisibility(View.GONE);
            char1.setText(speechText);
        } else {
            char2.setVisibility(View.VISIBLE);
            char1.setVisibility(View.GONE);
            char2.setText(speechText);
        }
        t1.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
        //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return this.gestureDetector.onTouchEvent(ev);
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

}
