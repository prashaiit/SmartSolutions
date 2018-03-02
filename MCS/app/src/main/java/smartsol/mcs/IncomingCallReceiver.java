package smartsol.mcs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

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

        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                System.out.println("incomingNumber : "+incomingNumber);
                Log.i("incoming", incomingNumber);
                Log.i("incoming", "state = " + state);
            }


        },PhoneStateListener.LISTEN_CALL_STATE);

        // Get AudioManager
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Get TelephonyManager
        telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (intent.getAction().equals("android.intent.action.PHONE_STATE"))  {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))  {
                // Get incoming number
                incomingNumber =  intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                rejectCall();
                startApp(context, incomingNumber);
                Log.i("incoming", "rejected the call from " + incomingNumber);
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
