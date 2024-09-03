package com.khadas.ksettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import java.io.File;

import java.io.IOException;

public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "KSettingsBootReceiver";
	private String value;
	private int val ;
	private static final String TV_USER_SETUP_COMPLETE = "tv_user_setup_complete";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.e(TAG, "hlm5 action " + action);

		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			Log.e(TAG, "hlm5 start Kvim4ToolsService");
			skipUserSetup(context);
			context.startService(new Intent(context, Kvim4ToolsService.class));
			cam_ir_cut_control();
		}
	}

    private void skipUserSetup(Context context) {
        if (Settings.Secure.getInt(context.getContentResolver(), TV_USER_SETUP_COMPLETE, 0) == 0) {
            Log.d(TAG, "force skip tv user setup");
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
            Settings.Secure.putInt(context.getContentResolver(), TV_USER_SETUP_COMPLETE, 1);
        }
    }

	private void cam_ir_cut_control() {
        File file = new File("/sys/bus/i2c/drivers/ov08a10/2-0036");
        if (file.exists()){
			if(1 == SystemProperties.getInt("persist.sys.cam1", 0)){
				MainActivity.su_exec("echo 481 > /sys/class/gpio/export;echo out > sys/class/gpio/gpio481/direction;echo 0 > sys/class/gpio/gpio481/value");
			}else{
				MainActivity.su_exec("echo 481 > /sys/class/gpio/export;echo out > sys/class/gpio/gpio481/direction;echo 1 > sys/class/gpio/gpio481/value");
			}
		}
 	}

}
