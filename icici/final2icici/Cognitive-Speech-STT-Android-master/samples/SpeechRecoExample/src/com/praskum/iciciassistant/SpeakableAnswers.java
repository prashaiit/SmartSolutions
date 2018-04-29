package com.praskum.iciciassistant;

/**
 * Created by praskum on 4/17/2017.
 */

public class SpeakableAnswers {
    public static String getFoodOffers() {
        return "Yes, there is one offer.. Order your food through foodpanda and get 15% off";
    }

    public static String getFlightOffers() {
        return "book your flight through Yatra \n and get 700 Rupees OFF on domestic flights \n and 2000 rupees OFF on international flights";
    }

    public static String getMoneyManageVoiceDetails() {
        return "The current balance in your saving bank account is five lakh rupees.\nYou need to pay thirty thousand rupees for" +
                "recurring deposit, \n forty five thousand rupees for housing loan e m i \n, twenty thousand rupees for car loan e m i \n, twenty five thousand " +
                "rupees for i wish deposit \n, thirty five thousand rupees for credit card \n and approximately fifteen thousand rupees for other bill payments \n"+
                "your remaining balance would be approximately three lakh thirty five thousand rupees. ";
    }

    public static String getMoneyManageDetails() {
        return "" +
                "Current balance : 500000/-" + "\n" +
                "Recurring deposit : 30000/-" + "\n" +
                "Housing loan EMI : 45000/-" + "\n" +
                "Car loan EMI : 20000/-" + "\n" +
                "iWish deposit : 25000/-" + "\n" +
                "Credi Card Bill : 35000/-" + "\n" +
                "Other BillPayments (approx) : 15000/-" + "\n\n" +
                "Remaining balance would be approximately Rs.335000";

    }

    public static String getBillPayReminderDetails() {
        return "Sure.. I will remind you at 10 am tomorrow to pay the credit card bill";
    }

    public static String getiWishDepositDetails() {
        return "Recurring Deposit \n ROI : 6.9% \n Monthly Deposity : 5000/- \n Tenure : 1 year \n Maturity Amount : 62,277/-";
    }

    public static String getiWishDepositVoiceDetails() {
        return "The current rate of interest for \n Recurring Deposit is \n 6.9 percentage \n If you pay five thousand rupees every month for 1 year \n you would get the Maturity Amount of sixty two thousand two hundred and seventy seven rupees";
    }
}
