package com.praskum.iciciassistant;

import java.util.List;
import java.util.Map;

/**
 * Created by praskum on 4/16/2017.
 */

public class StockPrice {
    public static String [] stockNames = {"NTPC", "TATMOT", "INFTEC", "ICIBAN", "TATPOW", "BHEL"};
    public static String [] companyNames = {"NTPC", "Tata Motors", "Infotec", "ICICI Bank", "Tata Power", "BHEL"};
    public static boolean[] stockInterests = {false, false, true, false, true, false};
    public static String [] prices = {"165.31", "474.2", "1019.2", "514.3", "274.95", "100.9", "170.35"};
    public static String [] exchange = {"NSE", "BSE", "NSE", "BSE", "NSE", "NSE", "NSE"};
    public static String NTPC = "165.31";
    public static String NTPC_ex = "NSE";
    public static String TATMOT = "474.2";
    public static String TATMOT_ex = "BSE";
    public static String INFTEC = "1019.2";
    public static String INFTEC_ex = "NSE";
    public static String HINPET = "514.3";
    public static String HINPET_ex = "BSE";
    public static String ICIBAN = "274.95";
    public static String ICIBAN_ex = "NSE";
    public static String TAT_POW = "100.9";
    public static String TAT_POW_ex = "NSE";
    public static String BHEL = "170.35";
    public static String BHEL_ex = "NSE";

    public static String getStockPrice(String q) {
        if (q.contains("ntpc")) {
            return "NTPC_" + StockPrice.NTPC_ex + "_" + StockPrice.NTPC;
        } else if(q.contains("tatmot") || q.contains("tata motors")) {
            return "Tata Motors_" + StockPrice.TATMOT_ex + "_" + TATMOT;
        } else if (q.contains("iciban") || q.contains("icici bank")) {
            return "ICICI Bank_" + StockPrice.ICIBAN_ex + "_" + ICIBAN;
        } else if (q.contains("bhel")) {

            return "BHEL_" + StockPrice.BHEL_ex + "_" + BHEL;
        } else if (q.contains("tata power")) {

            return "Tata Power_" + TAT_POW_ex + "_" + TAT_POW;
         } else if (q.contains("infotec")) {

            return "Infotec_" + INFTEC_ex + "_" + INFTEC;
        } else {
            return "";
        }
    }

    public static String getStockInterestData(int index) {
        String data = "<b><font color=\"green\">" + companyNames[index] + "</font></b><br>" + exchange[index] + ":" + stockNames[index] + "<br>" + "Price : " + "<b><font color=\"green\">" + prices[index] + "</font></b>";
        return data;
    }
}
