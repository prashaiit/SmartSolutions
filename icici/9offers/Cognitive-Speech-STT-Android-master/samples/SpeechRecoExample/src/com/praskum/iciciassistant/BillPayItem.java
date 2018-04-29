package com.praskum.iciciassistant;

/**
 * Created by praskum on 4/8/2017.
 */

public class BillPayItem {
    private String _provider;
    private String _amount;
    private String _duedate;
    private static int _id = 0;

    public BillPayItem() {

    }
    public BillPayItem(int id, String provider, String amount, String duedate) {
        _provider = provider;
        _amount = amount;
        _duedate = duedate;
        _id = id;
    }

    public BillPayItem(String provider, String amount, String duedate) {
        _id++;
        _provider = provider;
        _amount = amount;
        _duedate = duedate;
    }

    public int getID() {return _id;}

    public String getProvider() {return _provider;}

    public String getAmount() {return _amount;}

    public String getDuedate() {return _duedate;}

    public void setId(int id) {
        _id = id;
    }
    public void setProvider(String provider) {
        _provider = provider;
    }

    public void setAmount(String amount) {
        _amount = amount;
    }

    public void setDuedate(String duedate) {
        _duedate = duedate;
    }
}
