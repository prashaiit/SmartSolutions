package com.praskum.iciciassistant;

/**
 * Created by praskum on 4/9/2017.
 */

public class HtmlUtilities {

    public static String getPrefix() {
        String data = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "body { \n" +
                "background-color: #E6E6E6;\n" +
                "}\n" +
                "\n" +
                ".boxed {\n" +
                "\tbackground: #FFFFFF;\n" +
                "\tborder-style: solid;\n" +
                "    border-color: #FFFFFF;\n" +
                "    padding: 10px; \n" +
                "    margin-bottom: 20px; \n" +
                "    width: 285px;\n" +
                "    height: 70px; \n" +
                "}\n" +
                ".category {\n"+
                "text-align: left; \n" +
                "}\n" +
                ".paymentlink {\n"+
                "text-decoration: underline blue;\n" +
                "text-color: blue;\n"+
                "}\n"+
                ".stock {\n" +
                "\tbackground: #FFFFFF;\n" +
                "\tborder-style: solid;\n" +
                "    border-color: #FFFFFF;\n" +
                "    padding: 10px; \n" +
                "    margin-bottom: 20px; \n" +
                "    width: 285px;\n" +
                "    height: 70px; \n" +
                "    text-align: right; \n" +
                "}\n" +
                ".bill {\n" +
                "\tbackground: #FFFFFF;\n" +
                "\tborder-style: solid;\n" +
                "    border-color: #FFFFFF;\n" +
                "    padding: 10px; \n" +
                "    margin-bottom: 20px; \n" +
                "    width: 285px;\n" +
                "    height: 85px; \n" +
                "    text-align: right; \n" +
                "}\n" +
                ".emi {\n" +
                "\tbackground: #FFFFFF;\n" +
                "\tborder-style: solid;\n" +
                "    border-color: #FFFFFF;\n" +
                "    padding: 10px; \n" +
                "    margin-bottom: 20px; \n" +
                "    width: 285px;\n" +
                "    height: 105px; \n" +
                "    text-align: right; \n" +
                "}\n" +
                ".offer {\n" +
                "\tbackground: #FFFFFF;\n" +
                "\tborder-style: solid;\n" +
                "    border-color: #FFFFFF;\n" +
                "    padding: 10px; \n" +
                "    margin-bottom: 20px; \n" +
                "    width: 285px;\n" +
                "    height: 75px; \n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n";
        return data;
    }

    public static String getHtmlForMessage(String message) {
        String data = "<div class=\"boxed\" style=\"\">\n" +
                message +
                "</div>\n";
        return data;
    }

    public static String getHtmlForStockMessage(String message) {
        String data = "<div class=\"stock\" style=\"\">\n" +
                "<div class=\"category\" style=\"\">Stock</div>" +
                message +
                "</div>\n";
        return data;
    }

    public static String getHtmlForBillMessage(String message, String scheduleInfo) {
        String data = "<div class=\"bill\" style=\"\">\n" +
                "<div class=\"category\" style=\"\">Bill Payment</div>" +
                message +
                //"<div class=\"paymentlink\" style=\"\">proceed to pay</div>" +
                "<a href=\"proceed-to-pay\">\n" +
                "<div class=\"paymentlink\" style=\"\">\n" +
                "proceed to pay" +
                "</div></a>" +
                "<div class=\"footer\" style=\"\">" + scheduleInfo + "</div>" +
                "</div>\n";
        return data;
    }

    public static String getHtmlForEMIMessage(String LoanAccountNo, String emiamount, String duedate) {
        String data = "<div class=\"emi\" style=\"\">\n" +
                "<div class=\"category\" style=\"\">EMI</div>" +
                LoanAccountNo + "<br>" + emiamount +
                //"<div class=\"paymentlink\" style=\"\">proceed to pay</div>" +
                "<a href=\"proceed-to-pay\">\n" +
                "<div class=\"paymentlink\" style=\"\">\n" +
                "proceed to pay" +
                "</div></a>" +
                "<div class=\"footer\" style=\"\">" + duedate + "</div>" +
                "</div>\n";
        return data;
    }

    public static String getHtmlForOfferMessage(String message) {
        String data = "<div class=\"offer\" style=\"\">\n" +
                "<div class=\"category\" style=\"\">Offers</div>" +
                message +
                "</div>\n";
        return data;
    }

    public static String getPostfix() {
        String data = "\n" +
                "</body>\n" +
                "</html>";
        return data;
    }

    public static String getTopMessage() {
        String data = "This text is enclosed in a box";
        return data;
    }
    public static String createHtmlForBill(String provider, String amount, String duedate) {
        String data1 = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "* { \n" +
                "  margin: 0; \n" +
                "  padding: 0; \n" +
                "}\n" +
                ".boxed {\n" +
                "\tbackground: #FFFFFF;\n" +
                "\tborder-style: solid;\n" +
                "    border-color: #FFFFFF;\n" +
                "    padding: 20px; \n" +
                "    width: 275px;\n" +
                "    height: 50px; \n" +
                "    margin-bottom: 20px;\n" +
                "}\n" +
                "\n" +
                "</style>\n" +
                "</head>\n" +
                "<body bgcolor=\"#E6E6E6\">\n" +
                "<div class=\"boxed\" style=\"\">\n" +
                provider + "\n Due Amount : " + amount + "\n Due Date : " + duedate +
                "</div>\n" +
                "\n" +
                "<div class=\"boxed\" style=\"\">\n" +
                "  This text is enclosed in a box.\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";

        return  data1;
    }

    public static String createHtmlForMessage(String message) {
        String data1 = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "* { \n" +
                "  margin: 0; \n" +
                "  padding: 0; \n" +
                "}\n" +
                ".boxed {\n" +
                "\tbackground: #FFFFFF;\n" +
                "\tborder-style: solid;\n" +
                "    border-color: #FFFFFF;\n" +
                "    padding: 20px; \n" +
                "    width: 275px;\n" +
                "    height: 50px; \n" +
                "    margin-bottom: 20px;\n" +
                "}\n" +
                "\n" +
                "</style>\n" +
                "</head>\n" +
                "<body bgcolor=\"#E6E6E6\">\n" +
                "<div class=\"boxed\" style=\"\">\n" +
                message +
                "</div>\n" +
                "\n" +
                "<div class=\"boxed\" style=\"\">\n" +
                "  This text is enclosed in a box.\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";

        return  data1;
    }

    public static String getAccountStatementHtml(String bal, String accountno, String accountType) {
        String answerInfo = "Account No : " + accountno + "\n" + "Balance : " + bal;
        return getPrefix() + getHtmlForMessage(answerInfo) + getPostfix();
    }
}
