package com.khadas.ksettings;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemProperties;
import android.provider.Settings;


import java.io.IOException;


public class Kvim4ToolsService extends Service {

    public static Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        Settings.System.putInt(mContext.getContentResolver(),"user_rotation",SystemProperties.getInt("persist.sys.user_rotation",0));

        fan_control();
        white_led_control();
        red_led_control();
        bl_control();
    }

    private void red_led_control() {
        int index = SystemProperties.getInt("persist.sys.red_led_control", 0);
        try {
            ComApi.execCommand(new String[]{"sh", "-c", "echo " + index + " >/sys/class/mcu/redled"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void white_led_control() {
        switch(SystemProperties.getInt("persist.sys.white_led_control", 1)){
            case 0:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 0 0 > /sys/class/leds/state_led/breath"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 0 255 > /sys/class/leds/state_led/state_brightness"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 0 0 > /sys/class/leds/state_led/state_brightness"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void bl_control() {
        //mipi -- /sys/class/backlight/aml-bl/brightness
        //vbo -- /sys/class/backlight/aml-bl1/brightness
        try {
            ComApi.execCommand(new String[]{"sh", "-c", "echo " + SystemProperties.getInt("persist.sys.vbo_bl_value", 102) + " > /sys/class/backlight/aml-bl1/brightness"});
            ComApi.execCommand(new String[]{"sh", "-c", "echo " + SystemProperties.getInt("persist.sys.mipi_bl_value", 102) + " > /sys/class/backlight/aml-bl/brightness"});
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fan_control() {
        switch (SystemProperties.getInt("persist.sys.fan_control", 1)){
            case 0:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 0 > /sys/class/fan/enable"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 1 > /sys/class/fan/enable"});
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 1 > /sys/class/fan/mode"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 1 > /sys/class/fan/enable"});
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 0 > /sys/class/fan/mode"});
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 1 > /sys/class/fan/level"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 1 > /sys/class/fan/enable"});
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 0 > /sys/class/fan/mode"});
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 2 > /sys/class/fan/level"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 1 > /sys/class/fan/enable"});
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 0 > /sys/class/fan/mode"});
                    ComApi.execCommand(new String[]{"sh", "-c", "echo 3 > /sys/class/fan/level"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
