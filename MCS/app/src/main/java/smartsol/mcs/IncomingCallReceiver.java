package smartsol.mcs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by praskum on 3/2/2018.
 */

public class IncomingCallReceiver extends BroadcastReceiver {
    String incomingNumber="";
    AudioManager audioManager;
    TelephonyManager telephonyManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("incoming", intent.getAction().toString());
        Toast.makeText(context, "broadcast intent ", Toast.LENGTH_SHORT).show();

        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                super.onCallStateChanged(state, incomingNumber);
                Log.i("incomingNumber : ", " = " +incomingNumber);
                Log.i("incoming", incomingNumber);
                Log.i("incoming", "state = " + state);
            }


        },PhoneStateListener.LISTEN_CALL_STATE);

        if (intent != null && intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String phoneState = intent.hasExtra(TelephonyManager.EXTRA_STATE) ? intent.getStringExtra(TelephonyManager.EXTRA_STATE) : null;

            if (phoneState != null /*&& phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING*/) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class c = Class.forName(tm.getClass().getName());
                    Method m = c.getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    ITelephony telephonyService = (ITelephony) m.invoke(tm);
                    Bundle bundle = intent.getExtras();
                    String phoneNumber = bundle.getString("incoming_number");
                    Log.i("MCS", "Ph = " + phoneNumber);
                    if ((phoneNumber != null) && phoneNumber.contains("9959027782") ) {
                        telephonyService.silenceRinger();
                        telephonyService.endCall();
                        Log.i("MCS", "Hangup = " + phoneNumber);
                    }
                    Log.i("MCS", "ph = " + phoneNumber);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

            }
        }


    }

    private void startApp(Context context, String number){
        Intent intent=new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("number", "Rejected incoming number:"+number);
        context.startActivity(intent);
    }

    private void rejectCall(){
        try {
            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(telephonyManager);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass =Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
