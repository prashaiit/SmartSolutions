package com.prash.iciciassistant;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by praskum on 4/8/2017.
 */

public class InterestsActivity extends Activity {
    private WebView webViewAnswer;
    private RelativeLayout interestContainer;
    private int id;

    public void createSMSDataFile() {
        ContextWrapper cw =new ContextWrapper(getApplicationContext());
        File path = cw.getFilesDir();
        Log.i("test", "path == " + path);
        String datafile = path + "/smsBillData.txt";
        File sFile = new File(datafile);
        if (!sFile.exists()) {
            try {
                sFile.createNewFile();
                Log.i("test", "abs path = " + sFile.getAbsolutePath());
                SmsData.setFilePath(sFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e("test", "file not created " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i("test", "file exists . abs path = " + sFile.getAbsolutePath());
            com.prash.iciciassistant.SmsData.setFilePath(sFile.getAbsolutePath());
            Log.i("test", SmsData.getFilepath());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interests_activity);
        Log.i("test", "Interest Activity : OnCreate");

        interestContainer = (RelativeLayout) findViewById(R.id.interest_activity_container);

        createSMSDataFile();

        //addWebView(HtmlUtilities.getTopMessage(), ++id, 0);
        webViewAnswer = (WebView) findViewById(R.id.webviewAnswer);
        //webViewAnswer.loadData(HtmlUtilities.getTopMessage(), "text/html", "UTF-8");

        Log.i("test", "Interactivity : creating DB");
        /*DatabaseHandler db = SmsDbApplication.createDB(getApplicationContext());
        Log.i("test", "DB Created");
        Log.i("test", "DB = " + db + " no of bills = " + db.getBillsCount());
        if (db != null && db.getBillsCount() > 0) {
            List<BillPayItem> bills = db.getAllBills();
            Log.i("test", "Read sms bills");
            for (BillPayItem cn : bills) {
                String log = "Id: " + cn.getID() + " ,Provider: " + cn.getProvider() + " ,Amount: " + cn.getAmount() + " ,duedate : " + cn.getDuedate();
                Log.i("test", log);
                String data = HtmlUtilities.createHtmlForBill(cn.getProvider(), cn.getAmount(), cn.getDuedate());
                webViewAnswer.loadData(data, "text/html", "UTF-8");
            }
        }*/

        String fileName = SmsData.getFilepath();
        Log.i("test", "isFilenameemplty = " + fileName);
        if (! fileName.isEmpty()) {
            try {
                fillWebView(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.interest_activity_container);
        linearLayout.setOnTouchListener(new OnSwipeTouchListener(InterestsActivity.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                Log.i("test", "InterestActiviy : SwipeRight");
                Intent intent = new Intent(InterestsActivity.this, MainActivity.class);
                startActivity(intent);
            }
            public void onSwipeLeft() {
               /* Log.i("test", "InterestActiviy : SwipeLeft");
                Intent intent = new Intent(InterestsActivity.this, MainActivity.class);
                startActivity(intent);*/
            }
            public void onSwipeBottom() {

            }
        });
    }

    public void fillWebView(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlUtilities.getPrefix());
        sb.append(HtmlUtilities.getHtmlForMessage(HtmlUtilities.getTopMessage()));

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            String line = br.readLine();
            Log.i("test", "line = " + line);
            while (line != null) {
                //sb.append(line);
                //sb.append("\n");
                Log.i("test", "web view for " + line);
                sb.append(HtmlUtilities.getHtmlForMessage(line));
                //int prevId = id;
                //addWebView(HtmlUtilities.createHtmlForMessage(line), id+1, prevId);
                line = br.readLine();
            }
            sb.append(HtmlUtilities.getPostfix());
            webViewAnswer.loadData(sb.toString(), "text/html", "UTF-8");
            Log.i("test", "sms data = " + sb.toString());
        } finally {
            br.close();
        }
    }

    public void addWebView(String line, int id, int prevId) {
        WebView webView = new WebView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        webView.setId(id);
        webView.loadData(line, "text/html", "UTF-8");
        if (prevId > 0) {
            params.addRule(RelativeLayout.BELOW, prevId);
        }
        interestContainer.addView(webView, params);
        interestContainer.invalidate();
    }
}
