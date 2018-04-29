package com.prash.iciciassistant;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.Browser;

/**
 * Created by praskum on 4/11/2017.
 */

public class BrowserHistory extends Activity {


    public BrowserHistory(Context context) {

    }

    public void getLatestUrl() {
        String[] proj = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
        String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
        Cursor cursor = this.getContentResolver().query(Browser.BOOKMARKS_URI, proj, sel, null, null);
        //this.startManagingCursor(cursor);

        cursor.moveToFirst();

        String title = "";
        String url = "";

        int count = cursor.getCount();
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            while (cursor.isAfterLast() == false) {

                title = cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.TITLE));
                url = cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL));
                // Do something with title and url

                cursor.moveToNext();
            }
        }
    }
}
