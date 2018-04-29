package smartsol.mcs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by praskum on 3/11/2018.
 */

public class RestartPhoneStateListenerService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PhoneStateListenerService.class);
        context.startService(i);
    }
}
