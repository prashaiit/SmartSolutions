/*
 * Copyright (c) Microsoft. All rights reserved.
 * Licensed under the MIT license.
 * //
 * Project Oxford: http://ProjectOxford.ai
 * //
 * ProjectOxford SDK GitHub:
 * https://github.com/Microsoft/ProjectOxford-ClientSDK
 * //
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 * //
 * MIT License:
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * //
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * //
 * THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.praskum.iciciassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.speech.tts.TextToSpeech;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import com.microsoft.bing.speech.Conversation;
import com.microsoft.bing.speech.SpeechClientStatus;
import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements ISpeechRecognitionServerEvents
{
    int m_waitSeconds = 0;
    DataRecognitionClient dataClient = null;
    MicrophoneRecognitionClient micClient = null;
    FinalResponseStatus isReceivedResponse = FinalResponseStatus.NotReceived;
    EditText _qText, _aText;
    RadioGroup _radioGroup;
    Button _buttonSelectMode;
    Button _startButton;
    ListView _chatListView;
    TextToSpeech textToSpeech;
    boolean isMicInput = false;
    boolean answerSpoken = false;
    private SQLiteDatabase db;
    private boolean isQTextSet = false;

    public void setMicButtonEnabled(boolean val) {
        _startButton.setEnabled(val);
    }

    public enum FinalResponseStatus { NotReceived, OK, Timeout }

    /**
     * Gets the primary subscription key
     */
    public String getPrimaryKey() {
        return this.getString(R.string.primaryKey);
    }

    /**
     * Gets the LUIS application identifier.
     * @return The LUIS application identifier.
     */
    private String getLuisAppId() {
        return this.getString(R.string.luisAppID);
    }

    /**
     * Gets the LUIS subscription identifier.
     * @return The LUIS subscription identifier.
     */
    private String getLuisSubscriptionID() {
        return this.getString(R.string.luisSubscriptionID);
    }

    /**
     * Gets a value indicating whether or not to use the microphone.
     * @return true if [use microphone]; otherwise, false.
     */
    private Boolean getUseMicrophone() {
        int id = this._radioGroup.getCheckedRadioButtonId();
        return id == R.id.micIntentRadioButton ||
                id == R.id.micDictationRadioButton ||
                id == (R.id.micRadioButton - 1);
    }

    /**
     * Gets a value indicating whether LUIS results are desired.
     * @return true if LUIS results are to be returned otherwise, false.
     */
    private Boolean getWantIntent() {
        int id = this._radioGroup.getCheckedRadioButtonId();
        return id == R.id.dataShortIntentRadioButton ||
                id == R.id.micIntentRadioButton;
    }

    /**
     * Gets the current speech recognition mode.
     * @return The speech recognition mode.
     */
    private SpeechRecognitionMode getMode() {
        /*int id = this._radioGroup.getCheckedRadioButtonId();
        if (id == R.id.micDictationRadioButton ||
                id == R.id.dataLongRadioButton) {
            return SpeechRecognitionMode.LongDictation;
        }*/

        return SpeechRecognitionMode.LongDictation;
    }

    /**
     * Gets the default locale.
     * @return The default locale.
     */
    private String getDefaultLocale() {
        return "en-IN";
    }

    /**
     * Gets the short wave file path.
     * @return The short wave file.
     */
    private String getShortWaveFile() {
        return "whatstheweatherlike.wav";
    }

    /**
     * Gets the long wave file path.
     * @return The long wave file.
     */
    private String getLongWaveFile() {
        return "batman.wav";
    }

    /**
     * Gets the Cognitive Service Authentication Uri.
     * @return The Cognitive Service Authentication Uri.  Empty if the global default is to be used.
     */
    private String getAuthenticationUri() {
        return this.getString(R.string.authenticationUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("test", "MainActivity : OnCreate");
        this._qText = (EditText) findViewById(R.id.editTextQ);
        this._aText = (EditText) findViewById(R.id.editTextA);
        _aText.setVisibility(View.INVISIBLE);
        _aText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("start speak", "mic = " + isMicInput + " answerSpoken " + answerSpoken);
                if (answerSpoken) return;
                if (Answer.getTTSAnswer().isEmpty()) {
                    textToSpeech.speak(charSequence.toString() , TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    String answerToBeSpoken = Answer.getTTSAnswer();
                    textToSpeech.speak(answerToBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                }

                answerSpoken = true;
                Answer.setTTSAnswer("");
                _startButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_microsophone, 0,0,0);
                Log.i("test", "micEnabled");
                setMicButtonEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _qText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isQTextSet) {
                    getAnswer(_qText.getText().toString());
                    isQTextSet = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //_qText.setText("");
        _qText.setHint("Search");
        this._radioGroup = (RadioGroup)findViewById(R.id.groupMode);
        this._buttonSelectMode = (Button)findViewById(R.id.buttonSelectMode);
        _buttonSelectMode.setVisibility(View.INVISIBLE);
        //_radioGroup.check(R.id.radioButton);
        _radioGroup.check(R.id.micDictationRadioButton);
        _radioGroup.setVisibility(View.INVISIBLE);
        this._startButton = (Button) findViewById(R.id.button1);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.top_container);
        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                            }
            public void onSwipeLeft() {
                Intent intent = new Intent(MainActivity.this, InterestsActivity.class);
                startActivity(intent);
            }
            public void onSwipeBottom() {

            }
        });

        Intent intent = new Intent(MainActivity.this, ReadSMSService.class);
        startService(intent);

        /*_chatListView = (ListView) findViewById(R.id.chatlistview);

        EditText welcomeText = new EditText(MainActivity.this);
        welcomeText.setText("Hello");
        welcomeText.setGravity(Gravity.LEFT);

        EditText welcomeText1 = new EditText(MainActivity.this);
        welcomeText1.setText("Hello");
        welcomeText1.setGravity(Gravity.RIGHT);

        ArrayList<EditText> arrayList = new ArrayList<EditText>();
        arrayList.add(welcomeText);
        arrayList.add(welcomeText1);

        final ArrayAdapter<EditText> arrayAdapter = new ArrayAdapter<EditText>
                (this, android.R.layout.simple_list_item_1, chat_list);

        _chatListView.setAdapter(arrayAdapter);*/

        if (getString(R.string.primaryKey).startsWith("Please")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
        }

        // setup the buttons
        final MainActivity This = this;
        this._startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                _startButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mic_clicked, 0,0,0);
                This.StartButton_Click(arg0);
            }
        });

        this._buttonSelectMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                This.ShowMenu(This._radioGroup.getVisibility() == View.INVISIBLE);
            }
        });

        this._radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                This.RadioButton_Click(rGroup, checkedId);
            }
        });

        this.ShowMenu(true);
    }

    private void ShowMenu(boolean show) {
        if (show) {
            this._radioGroup.setVisibility(View.INVISIBLE);
            this._qText.setVisibility(View.INVISIBLE);
        } else {
            this._radioGroup.setVisibility(View.INVISIBLE);
            this._qText.setText("");
            this._qText.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Handles the Click event of the _startButton control.
     */
    private void StartButton_Click(View arg0) {
        Log.i("startButton", "pressed");
        APIToken.queryType = "";
        APIToken.hitUrl = true;
        APIToken.currentAnswer = "";
        APIToken.currentImage = "";
        Answer.setTTSAnswer("");
        isMicInput = true;
        answerSpoken = false;
        //_qText.setActivated(false);
        Log.i("hello1", "isPressed = " + _startButton.isPressed());
        this._startButton.setEnabled(false);
        this._radioGroup.setEnabled(false);

        this.m_waitSeconds = this.getMode() == SpeechRecognitionMode.ShortPhrase ? 20 : 200;

        this.ShowMenu(false);

        this.LogRecognitionStart();

        if (this.getUseMicrophone()) {
            if (this.micClient == null) {
                if (this.getWantIntent()) {
                    //this.WriteLine("--- Start microphone dictation with Intent detection ----");

                    Log.i("test", "initializing mic");

                    this.micClient =
                            SpeechRecognitionServiceFactory.createMicrophoneClientWithIntent(
                                    this,
                                    this.getDefaultLocale(),
                                    this,
                                    this.getPrimaryKey(),
                                    this.getLuisAppId(),
                                    this.getLuisSubscriptionID());
                }
                else
                {
                    this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
                            this,
                            this.getMode(),
                            this.getDefaultLocale(),
                            this,
                            this.getPrimaryKey());
                }

                this.micClient.setAuthenticationUri(this.getAuthenticationUri());
            }

            this.micClient.startMicAndRecognition();
        }
        else
        {
            if (null == this.dataClient) {
                if (this.getWantIntent()) {
                    this.dataClient =
                            SpeechRecognitionServiceFactory.createDataClientWithIntent(
                                    this,
                                    this.getDefaultLocale(),
                                    this,
                                    this.getPrimaryKey(),
                                    this.getLuisAppId(),
                                    this.getLuisSubscriptionID());
                }
                else {
                    this.dataClient = SpeechRecognitionServiceFactory.createDataClient(
                            this,
                            this.getMode(),
                            this.getDefaultLocale(),
                            this,
                            this.getPrimaryKey());
                }

                this.dataClient.setAuthenticationUri(this.getAuthenticationUri());
            }

            this.SendAudioHelper((this.getMode() == SpeechRecognitionMode.ShortPhrase) ? this.getShortWaveFile() : this.getLongWaveFile());
        }
    }

    /**
     * Logs the recognition start.
     */
    private void LogRecognitionStart() {
        String recoSource;
        if (this.getUseMicrophone()) {
            recoSource = "microphone";
        } else if (this.getMode() == SpeechRecognitionMode.ShortPhrase) {
            recoSource = "short wav file";
        } else {
            recoSource = "long wav file";
        }

        //this.WriteLine("\n--- Start speech recognition using " + recoSource + " with " + this.getMode() + " mode in " + this.getDefaultLocale() + " language ----\n\n");
    }

    private void SendAudioHelper(String filename) {
        RecognitionTask doDataReco = new RecognitionTask(this.dataClient, this.getMode(), filename);
        try
        {
            doDataReco.execute().get(m_waitSeconds, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            doDataReco.cancel(true);
            isReceivedResponse = FinalResponseStatus.Timeout;
        }
    }

    public void onFinalResponseReceived(final RecognitionResult response) {
        Log.i("hello2", "startButton enabled = " + _startButton.isEnabled());
        //if (!isMicInput) return;


        boolean isFinalDicationMessage = this.getMode() == SpeechRecognitionMode.LongDictation &&
                (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        if (null != this.micClient && this.getUseMicrophone() && ((this.getMode() == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)) {
            // we got the final result, so it we can end the mic reco.  No need to do this
            // for dataReco, since we already called endAudio() on it as soon as we were done
            // sending all the data.
            this.micClient.endMicAndRecognition();
        }

        if (answerSpoken) return;

        if (isFinalDicationMessage) {
            this.isReceivedResponse = FinalResponseStatus.OK;
            isMicInput = false;
        }

        if (!isFinalDicationMessage) {
            //this.WriteLine("********* Final n-BEST Results *********");
            //for (int i = 0; i < response.Results.length; i++) {
            //this.WriteLine("[" + i + "]" + " Confidence=" + response.Results[i].Confidence +
            //" Text=\"" + response.Results[i].DisplayText + "\"");
            //}
            Log.i("h5", "writing text");
            isQTextSet = true;
            this.WriteLine(response.Results[0].DisplayText);

            Log.i("test", "Question Aksed... Fetch the answer");
            //Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
            //startActivity(intent);
            //String qtext = _qText.getText().toString();
            //getAnswer(qtext);
        }
    }

    public void getAnswer(String qText) {
        _aText.setVisibility(View.VISIBLE);
        Log.i("test", "endmicRecog");
        micClient.endMicAndRecognition();

        //APIToken.refreshToken();

        //_aText.setText(answer);
        Log.i("text", "fetching the answer");

        String url = Answer.parseQuery(qText);

        if (APIToken.hitUrl) {
            if (!url.isEmpty())
                new FetchAnswer().execute(url);
            else {

            }
        } else
            findTheAnswer();

        Log.i("hello", "isPressed = " + _startButton.isPressed());
    }

    public void findTheAnswer() {
        String ans = APIToken.getCurrentAnswer();
        Answer.setTTSAnswer(ans);
        _aText.setText(ans);
        _aText.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.foodpanda), null);
    }

    public void defaultAnswer() {
        String ans = "I will notify you when I find the right answer for your question";
        Answer.setTTSAnswer(ans);
        _aText.setText(ans);
    }

    private class FetchAnswer extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;
        private String responseAnswer = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("test", "onPreExecute");
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Fetching the answer");
            dialog.setCancelable(false);
            dialog.show();
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
            dialog.dismiss();
            Log.i("test", "queryType = " + APIToken.queryType);
            String properJsonFormat = "{\"answer\":" + responseAnswer + "}";
            Log.i("test2", "json = " + properJsonFormat);

            try {
                JSONObject object = new JSONObject(properJsonFormat);
                JSONArray jsonArray = object.getJSONArray("answer");
                Log.i("test3", "queryType = " + APIToken.queryType);
                if (APIToken.queryType.equals("refreshToken")) {
                    Log.i("test2", "refreshToken");
                    //String responseAnswer = null;

                    Log.i("test2", "array length = " + jsonArray.length());
                    JSONObject tokenObject = jsonArray.getJSONObject(0);
                    Log.i("test2", "token --> " + tokenObject.get("token"));
                    String refreshToken = tokenObject.getString("token");
                    Log.i("test2", "new refreshToken = " + refreshToken);
                    Log.i("test", "new refreshToken = ");
                    APIToken.setToken(refreshToken);

                    _aText.setText("Token refreshed");
                } else if (APIToken.queryType.equals("accountStatement")) {
                    Log.i("test3", "length of array " + jsonArray.length());
                    JSONObject tokenObject = jsonArray.getJSONObject(1);
                    String balance = tokenObject.getString("balance");
                    String accno = tokenObject.getString("accountno");
                    String acctype = tokenObject.getString("accounttype");

                    Log.i("test3", "new token " + APIToken.getToken());

                    //String htmlInfo = HtmlUtilities.getAccountStatementHtml(balance, accno, acctype);
                    String answer = "Account no : " + accno + "\n" + "Balance : " + balance;
                    Answer.setTTSAnswer("Balance amount is Five Lakh Rupees");
                    _aText.setText(answer);
                } else if (APIToken.queryType.equals("loanaccountstatement")) {
                    JSONObject tokenObject = jsonArray.getJSONObject(1);
                    String principleOutstanding = tokenObject.getString("principal_outstanding");
                    String dateofloan = tokenObject.getString("date_of_loan");
                    String roi = tokenObject.getString("roi");
                    String loanAmount = tokenObject.getString("loanAmount");
                    String loanno = tokenObject.getString("loan_no");
                    String agreementNo = tokenObject.getString("agreementId");

                    LoanAccountDetails.agreementId = agreementNo;
                    LoanAccountDetails.loan_no = loanno;

                    String answer = "Loan Account No : " + LoanAccountDetails.loan_ac_no + "\n" +
                            "Date of Loan : " + dateofloan + "\n" + "ROI : " + roi + "\n" +
                            "Loan Amount : " + loanAmount + "\n" + "Outstanding amount : " + principleOutstanding;

                    //Answer.setTTSAnswer("your principle outstanding amount is " + principleOutstanding);
                    Answer.setTTSAnswer("your principle outstanding amount is 18 lakh rupees");
                    _aText.setText(answer);
                } else if (APIToken.queryType.equals("stockprice")) {
                    String sdetails = APIToken.stockPrice;

                    String [] details = sdetails.split("_");
                    String company = details[0];
                    String symbol = details[1];
                    String price = details[2];
                    Answer.setTTSAnswer("Current stock price of " + company + " is " + price + " rupees");

                    String answer = company + "\n" + symbol + "\n" + price;
                    _aText.setText(answer);
                } else {
                    _aText.setText(responseAnswer);
                }
            } catch (JSONException e) {
                Log.i("test2", e.getMessage());
                e.printStackTrace();
            }


        }
    }

    public void onPause(){
        Log.i("test", "MainActivity : OnPause");
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("test", "MainActivity : onResume");
        textToSpeech =new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        Log.i("test", "initialized TTS");
    }

    /**
     * Called when a final response is received and its intent is parsed
     */
    public void onIntentReceived(final String payload) {
        //this.WriteLine("--- Intent received by onIntentReceived() ---");
        Log.i("h", "onIntentReceived");
        this.WriteLine(payload);
    }

    public void onPartialResponseReceived(final String response) {
        //this.WriteLine("--- Partial result received by onPartialResponseReceived() ---");
        Log.i("h", "partial response received");
        this.WriteLine(response);
    }

    public void onError(final int errorCode, final String response) {
        this._startButton.setEnabled(true);
        Log.i("h", "error received");
        this.WriteLine("--- Error received by onError() ---");
        this.WriteLine("Error code: " + SpeechClientStatus.fromInt(errorCode) + " " + errorCode);
        this.WriteLine("Error text: " + response);
    }

    /**
     * Called when the microphone status has changed.
     * @param recording The current recording state
     */
    public void onAudioEvent(boolean recording) {
        Log.i("h", "onAudioEvent");
        //this.WriteLine("--- Microphone status change received by onAudioEvent() ---");
        //this.WriteLine("********* Microphone status: " + recording + " *********");
        if (recording) {
            //this.WriteLine("Please start speaking.");
        }

        if (!recording) {
            this.micClient.endMicAndRecognition();
            this._startButton.setEnabled(true);
        }
    }

    /**
     * Writes the line.
     * @param text The line to write.
     */
    private void WriteLine(String text) {
        if (answerSpoken) return;;
        Log.i("hello3", "writeline " + text);
        this._qText.setText(text + "\n");
    }

    /**
     * Handles the Click event of the RadioButton control.
     * @param rGroup The radio grouping.
     * @param checkedId The checkedId.
     */
    private void RadioButton_Click(RadioGroup rGroup, int checkedId) {
        // Reset everything
        if (this.micClient != null) {
            this.micClient.endMicAndRecognition();
            try {
                this.micClient.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            this.micClient = null;
        }

        if (this.dataClient != null) {
            try {
                this.dataClient.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            this.dataClient = null;
        }

        this.ShowMenu(false);
        this._startButton.setEnabled(true);
    }

    /*
     * Speech recognition with data (for example from a file or audio source).
     * The data is broken up into buffers and each buffer is sent to the Speech Recognition Service.
     * No modification is done to the buffers, so the user can apply their
     * own VAD (Voice Activation Detection) or Silence Detection
     *
     * @param dataClient
     * @param recoMode
     * @param filename
     */
    private class RecognitionTask extends AsyncTask<Void, Void, Void> {
        DataRecognitionClient dataClient;
        SpeechRecognitionMode recoMode;
        String filename;

        RecognitionTask(DataRecognitionClient dataClient, SpeechRecognitionMode recoMode, String filename) {
            this.dataClient = dataClient;
            this.recoMode = recoMode;
            this.filename = filename;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Note for wave files, we can just send data from the file right to the server.
                // In the case you are not an audio file in wave format, and instead you have just
                // raw data (for example audio coming over bluetooth), then before sending up any
                // audio data, you must first send up an SpeechAudioFormat descriptor to describe
                // the layout and format of your raw audio data via DataRecognitionClient's sendAudioFormat() method.
                // String filename = recoMode == SpeechRecognitionMode.ShortPhrase ? "whatstheweatherlike.wav" : "batman.wav";
                InputStream fileStream = getAssets().open(filename);
                int bytesRead = 0;
                byte[] buffer = new byte[1024];

                do {
                    // Get  Audio data to send into byte buffer.
                    bytesRead = fileStream.read(buffer);

                    if (bytesRead > -1) {
                        // Send of audio data to service.
                        dataClient.sendAudio(buffer, bytesRead);
                    }
                } while (bytesRead > 0);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            finally {
                dataClient.endAudio();
            }

            return null;
        }
    }
}
