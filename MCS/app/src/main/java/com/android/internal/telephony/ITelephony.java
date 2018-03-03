package com.android.internal.telephony;

/**
 * Created by praskum on 3/3/2018.
 */

public interface ITelephony {
    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}
