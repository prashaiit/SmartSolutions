package com.praskum.iciciassistant;

import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by praskum on 4/14/2017.
 */

public final class APIToken {
    public static final String ClientId = "prashaenator@gmail.com";
    public static final String password = "C2CD3F8A";
    public static String token = "17e9c31289c4";
    public static String response = "";
    public static String queryType = "";
    public static String stockPrice = "";
    public static boolean hitUrl = true;
    public static String currentAnswer = "";
    public static int currentImage = R.drawable.foodpanda;
    public static boolean isImageAnswer = false;

    public static String getCurrentAnswer() {
        return currentAnswer;
    }


    public static void setToken(String newToken) {
        token = newToken;
    }

    public static String getToken() {
        return token;
    }

    public static String getRefreshTokenUrl() {
        String url = "https://corporateapiprojectwar.mybluemix.net/corporate_banking/mybank/authenticate_client?client_id=" +
                ClientId + "&password=" + password;
        return url;
    }

    public static String getBalanceEnquiryUrl() {
        String Url = "https://retailbanking.mybluemix.net/banking/icicibank/balanceenquiry?client_id=" +
                ClientId + "&token=" + getToken() + "&accountno=4444777755551057";
        return Url;
    }

    public static String getLoanAccountStatementUrl() {
        String Url = "https://pocketsapi.mybluemix.net/rest/Loan/getLoanDetails?param=LACRM11144450057&clientId=" +
                ClientId + "&authToken=" + getToken();
        return  Url;
    }

    public static String getStockPriceUrl () {
        String Url = "https://stockexchnage.mybluemix.net/stockexc/icicibank/stockvalue?client_id=" +
                ClientId + "&token=" + getToken();

        return Url;
    }

    public static String getLoanEMIUrl () {
        String Url = "https://pocketsapi.mybluemix.net/rest/Loan/EMIDetails?loan_no=" +
                LoanAccountDetails.getLoan_no() + "&agreeID=" + LoanAccountDetails.getAgreementId() + "&clientId=" + ClientId +
                "&authToken=" + getToken();
        return Url;
    }
}
