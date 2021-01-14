package com.example.sleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootMesReceiver extends BroadcastReceiver {
    String action_boot="android.intent.action.BOOT_COMPLETED";
    SharedPreferences sp;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)) {
            sp = context.getSharedPreferences("lockerConfig",Context.MODE_PRIVATE);
            if(sp.getBoolean("isStart", false)) {
                Intent bootStartActivity = new Intent(context, MainActivity.class);
                bootStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bootStartActivity.putExtra("isStart",true);
                context.startActivity(bootStartActivity);
            }
        }
    }
}
