package com.prash.iciciassistant;

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
}
