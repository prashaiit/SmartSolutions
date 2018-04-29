package com.praskum.iciciassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            SmsData.setFilePath(sFile.getAbsolutePath());
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

        loadWebView();


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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("test4", "onResume");
    }

    public void billpaymentprocess() {
        BillReminders.remindersList.remove(0);
        //BillReminders.addReminder("Bill is paid successfully");
        loadWebView();

    }

    public void loadWebView() {
        webViewAnswer = (WebView) findViewById(R.id.webviewAnswer);
        webViewAnswer.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("proceed-to-pay")) {
                    Log.i("test5", "payment proceeding");
                    billpaymentprocess();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        Log.i("test", "Interactivity : creating DB");
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlUtilities.getPrefix());
        //sb.append(HtmlUtilities.getHtmlForMessage(HtmlUtilities.getTopMessage()));

        String fileName = SmsData.getFilepath();
        Log.i("test", "isFilenameemplty = " + fileName);
        if (! fileName.isEmpty()) {
            try {
                sb.append(fillWebView(fileName));
                sb.append(fillWebViewWithStockInterests());
                sb.append(fillWebViewWithReminders());
                sb.append(fillLoanEmiReminders());
                sb.append(fillWebviewWithOffers());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sb.append(HtmlUtilities.getPostfix());
        webViewAnswer.loadData(sb.toString(), "text/html", "UTF-8");
        webViewAnswer.invalidate();
        webViewAnswer.reload();
    }

    public String fillWebView(String fileName) throws IOException {
        String data = "";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            String line = br.readLine();
            Log.i("test", "line = " + line);
            while (line != null) {
                //sb.append(line);
                //sb.append("\n");
                Log.i("test", "web view for " + line);
                data += HtmlUtilities.getHtmlForMessage(line);
                //int prevId = id;
                //addWebView(HtmlUtilities.createHtmlForMessage(line), id+1, prevId);
                line = br.readLine();
            }

            Log.i("test", "sms data = " + data);
        } finally {
            br.close();
        }
        return data;
    }

    public String fillWebViewWithStockInterests() {
        String data = "";

        for (int i = 0 ; i < StockPrice.stockInterests.length; i++) {
            if (StockPrice.stockInterests[i]) {
                data += HtmlUtilities.getHtmlForStockMessage(StockPrice.getStockInterestData(i));
            }
        }
        return data;
    }

    public String fillWebViewWithReminders() {
        List<String> reminders = BillReminders.getRemindersList();
        String returnInfo = "";

        for (int i = 0; i < reminders.size(); i++) {
            String reminderData = reminders.get(i);
            String seperator = "\\*";
            String[] data = reminderData.split(seperator);
            String category = data[0];
            String subcat = data[1];
            String date = data[2];
            String time = data[3];

            String hour = time.split(":")[0];
            String timeSuffix = "";
            if (Integer.parseInt(hour) > 12) {
                timeSuffix = "pm";
            } else {
                timeSuffix = "am";
            }

            String answer1 = "<font color=\"green\"><b>" + subcat + " " + category + " Bill</b></font>";
            String answer2 = "<br><font size=2 color=\"gray\">Scheduled at " + time + timeSuffix + " on "+date + "</font>";

            returnInfo += HtmlUtilities.getHtmlForBillMessage(answer1, answer2);
        }

        Log.i("test5", "returnInfo = " + returnInfo);
        return returnInfo;
    }

    public String fillLoanEmiReminders(){
        String returnInfo = "";
        //String url = APIToken.getLoanEMIUrl();
        //new FetchAnswer().execute(url);
        String loanAccountno = "<font color=\"green\"><b>" + LoanAccountDetails.loan_ac_no + "</b></font>";
        String amount = "<font color=\"green\"><b>" + "Rs 5500.00" + "</b></font>";
        String duedate = "<br><font size=2 color=\"gray\">Due date is  May 01,2017</font>";

        returnInfo += HtmlUtilities.getHtmlForEMIMessage(loanAccountno, amount, duedate);

        Log.i("test5", "returnInfo = " + returnInfo);
        return returnInfo;
    }

    public String fillWebviewWithOffers() {
        String returnInfo = "";

        String offer1 = "<br><font color=\"green\"><b>" + "Order Meal at Foodpanda through ICICI and get 15% OFF" + "</b></font>";
        returnInfo += HtmlUtilities.getHtmlForOfferMessage(offer1);

        offer1 = "<br><font color=\"green\"><b>" + "Book your flight ticket on Yatra and get Rs 700 OFF on Domestic Flights" + "</b></font>";
        returnInfo += HtmlUtilities.getHtmlForOfferMessage(offer1);
        return returnInfo;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.interests_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.stock:
                createStockOptionsDialog();
                break;
            case R.id.track_bills:
                createBillOptionsDialog();
                break;
            case R.id.emi:
                break;
        }
        return true;
    }

    public void createStockOptionsDialog() {
        Dialog stockOptionsDialog;
        final String[] items = StockPrice.stockNames;
        final boolean[] values = StockPrice.stockInterests;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Monitor Stock Price ");

        builder.setMultiChoiceItems(items, values,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId,
                                        boolean isSelected) {
                        Log.i("test4", "id = " + selectedItemId + " selected = " + isSelected);
                        if (isSelected) {
                            StockPrice.stockInterests[selectedItemId] = true;
                        } else {
                            StockPrice.stockInterests[selectedItemId] = false;
                        }
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Your logic when OK button is clicked
                        loadWebView();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        stockOptionsDialog = builder.create();
        stockOptionsDialog.show();
    }

    public void createBillOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bill Reminders");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bill_reminder, null);
        builder.setView(dialogView);

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.billcategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bill_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Spinner spinner2 = (Spinner) dialogView.findViewById(R.id.mobileCategory);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.mobile_category, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.reminderDate);
        datePicker.setCalendarViewShown(false);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.reminderTime);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String category = spinner.getSelectedItem().toString();
                String subCategory = spinner2.getSelectedItem().toString();

                String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int year = datePicker.getYear();

                String date = BillReminders.months[month] + " " + day + "," + year;
                String reminderData = category + "*" + subCategory + "*" + date + "*" + time;
                BillReminders.addReminder(reminderData);
                Log.i("test5", reminderData);
                try {
                    loadWebView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class FetchAnswer extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;
        private String responseAnswer = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.i("test", "doInBackground");
                ServiceHandler sh = new ServiceHandler();
                //String url = APIToken.refreshTokenUrl();
                String url = params[0];
                responseAnswer = sh.makeServiceCall(url);
                Log.i("test", "response = " + responseAnswer);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("test6", responseAnswer);

        }
    }
}
