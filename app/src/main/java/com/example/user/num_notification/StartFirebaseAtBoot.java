package com.example.user.num_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author AMarsaikhan
 *
 */
public class StartFirebaseAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(NotifyService.class.getName()));
    }
}