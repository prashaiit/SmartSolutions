package coolapps.rapell;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by praskum on 1/23/2018.
 */

public class AlarmService extends Service {
    private AlarmReceiver ar;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("android.intent.action.ALARM_CHANGED");
        ar = new AlarmReceiver();

        registerReceiver(ar, ifilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ar);
    }
}
