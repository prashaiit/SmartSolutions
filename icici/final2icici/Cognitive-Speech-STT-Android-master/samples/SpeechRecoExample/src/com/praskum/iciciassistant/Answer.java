package com.praskum.iciciassistant;

import android.util.Log;
import android.widget.Switch;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by praskum on 4/14/2017.
 */

public class Answer {
    public static String ttsAnswer = "";

    public static String getTTSAnswer() {
        return ttsAnswer;
    }

    public static void setTTSAnswer(String ans) {
        ttsAnswer = ans;
    }

    public static String parseQuery(String question) {
        Log.i("test", "Parsing the question " + question);
        String qLowercase = question.toLowerCase();
        if (qLowercase.contains("refresh token")) {
            APIToken.queryType = "refreshToken";
            Log.i("test", "return refresh token url");
            return APIToken.getRefreshTokenUrl();
        } else if (qLowercase.contains("balance") || qLowercase.contains("account statement")
                || qLowercase.contains("account details")) {
            APIToken.queryType = "accountStatement";
            Log.i("test", "return balance enquiry url");
            return APIToken.getBalanceEnquiryUrl();
        } else if (qLowercase.contains("loan account summary") || qLowercase.contains("summary of loan account")) {
            APIToken.queryType = "loanaccountstatement";
            return APIToken.getLoanAccountStatementUrl();
        } else if (qLowercase.contains("share value of ntpc goes beyond")) {
            APIToken.hitUrl = false;
            APIToken.currentAnswer = "Sure.. I will notify you when it goes beyond 170 rupees";
            //APIToken.currentImage = R.drawable.yatra;
            APIToken.isImageAnswer = false;
        } else if (qLowercase.contains("stock price") || qLowercase.contains("share value")) {
            APIToken.queryType = "stockprice";
            APIToken.stockPrice = StockPrice.getStockPrice(qLowercase);
            return APIToken.getStockPriceUrl();
        } else if ((qLowercase.contains("food") || qLowercase.contains("meal")) && qLowercase.contains("offer")) {
            APIToken.hitUrl = false;
            APIToken.currentAnswer = SpeakableAnswers.getFoodOffers();
            APIToken.currentImage = R.drawable.foodpanda;
            APIToken.isImageAnswer = true;
        } else if (qLowercase.contains("flight") && qLowercase.contains("offer")) {
            APIToken.hitUrl = false;
            APIToken.currentAnswer = SpeakableAnswers.getFlightOffers();
            APIToken.currentImage = R.drawable.yatra;
            APIToken.isImageAnswer = true;
        } else if (qLowercase.contains("money") && qLowercase.contains("manage")) {
            APIToken.hitUrl = false;
            APIToken.currentAnswer = SpeakableAnswers.getMoneyManageVoiceDetails();
            APIToken.isImageAnswer = true;
            APIToken.currentImage = R.drawable.managemoney;
            APIToken.queryType = "moneymanagement";
        } else if (qLowercase.contains("remind me to pay credit card bill tomorrow at 10am") ||
                qLowercase.contains("remind me to pay the credit card bill tomorrow at 10am") ||
                qLowercase.contains("remind me to pay the credit card bill tomorrow at 10 am") ||
                qLowercase.contains("remind me to pay credit card bill tomorrow at 10 am")) {
            APIToken.hitUrl = false;
            APIToken.currentAnswer = SpeakableAnswers.getBillPayReminderDetails();
            APIToken.isImageAnswer = false;
            APIToken.queryType = "billpayreminder";
            String reminderData = "Credit Card*ICICI*April 19, 2017*10:00";
            BillReminders.addReminder(reminderData);
        } else if (qLowercase.contains("recurring deposit")) {
            APIToken.hitUrl = false;
            APIToken.currentAnswer = SpeakableAnswers.getiWishDepositVoiceDetails();
            APIToken.isImageAnswer = true;

            APIToken.currentImage = R.drawable.recurringdeposit;
            APIToken.queryType = "rd";
        }

        Log.i("test6", "qLowercase = " + qLowercase);
        return "";
    }
}
