package com.axel.datatracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Intent", "Receiver");

        Intent mIntent  = new Intent(context, DataService.class);
        if(intent.getStringExtra("--start") != null) {
            mIntent.putExtra("TOGGLE", 0);
            mIntent.putExtra("PCKG", intent.getStringExtra("--start"));
        } else if (intent.getStringExtra("--end") != null) {
            mIntent.putExtra("TOGGLE", 1);
            mIntent.putExtra("PCKG", intent.getStringExtra("--end"));
        }
        context.startService(mIntent);
    }
}