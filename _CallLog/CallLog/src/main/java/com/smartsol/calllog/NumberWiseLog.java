package com.smartsol.calllog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NumberWiseLog implements Serializable {
    public int IncomingCallDuration;
    public int OutgoingCallDuration;

    public List<String> details;

    public NumberWiseLog() {
        IncomingCallDuration = 0;
        OutgoingCallDuration = 0;
        details = new ArrayList<>();
    }
}
