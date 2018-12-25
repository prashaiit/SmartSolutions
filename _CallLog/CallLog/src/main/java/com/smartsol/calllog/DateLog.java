package com.smartsol.calllog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateLog implements Serializable {
    public int IncomingCallDuration;
    public int OutgoingCallDuration;
    public Map<String, NumberWiseLog> numberLogInfo;

    public DateLog() {
        IncomingCallDuration = 0;
        OutgoingCallDuration = 0;
        numberLogInfo = new HashMap<>();
    }
}
