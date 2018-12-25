package com.smartsol.softlock;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SelfAdActivity extends Activity {
    private Button installButton;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfad);

        this.installButton = (Button)findViewById(R.id.installButton);
        this.webView = (WebView)findViewById(R.id.webView);

        this.installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.microsoft.android.smsorganizer"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = "<html>\n" +
                "<body>\n" +
                "<h6>Hello<h6>" +
                "\n" +
                "<h1>Hello<h1>" +
                "\n" +
                "<img src=\"https://images.pexels.com/photos/36764/marguerite-daisy-beautiful-beauty.jpg?auto=compress&cs=tinysrgb&dpr=2&h=50&w=50\">\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        this.webView.loadDataWithBaseURL("", text, "text/html", "UTF-8", "");
    }
}
